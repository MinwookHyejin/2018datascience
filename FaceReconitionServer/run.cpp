
// Standard Library
#include <iostream>
#include <direct.h>
#include <stdio.h>

// Third Party Library
#include "glog/logging.h"
#include "nam_ds_cppVersion.h"
#include "TCP/tcp_header.h"
#include "JSon/JsonWrapper.h"
#include "opencv2/opencv.hpp"

int loadLibrary();

int main(int argc, char* argv[])
{
	//121.166.184.64 15000
	//192.168.0.8 15000
	if (argc != 3)
	{
		std::cout << "[Error] .exe vmsIP vmsPort" << std::endl;
		std::cout << argc << std::endl;
		getchar();
		return 1;
	}

	HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
	google::InitGoogleLogging(argv[0]);

	SetConsoleTextAttribute(hConsole, 12);
	char buf[256];
	GetCurrentDirectoryA(256, buf);
	printf("Working Directory: %s\n\n", buf);

	SetConsoleTextAttribute(hConsole, 14);
	if (loadLibrary())
	{
		std::cout << "[Error] dll not found" << std::endl;
		getchar();
		return EXIT_FAILURE;
	}

	nam_ds_SetParameter();



	while (1)
	{
		bool serverReconnect = false;
		SetConsoleTextAttribute(hConsole, 11);
		Client *client = new Client();

		std::vector<cv::Mat> frame_directory;
		frame_directory.clear();

		char* vmsName = "DS_2ndProject_Connection";
		char* vmsIP = argv[1];
		char* vmsPort = argv[2];

		while (!client->initialize(vmsIP, vmsPort, vmsName));


		Json::StyledWriter writer;
		// 3. 상황실 -> 서버 : CLIENT_REQUEST 보냄[JSON]( crop_width, crop_height 채워서 보냄)
		int bufSize = 1024;
		//char* part3_json = client->recv_JSON(bufSize, vmsName);
		//std::cout << "Part 3\n" << part3_json << std::endl;

		//4. 서버->상황실 : CLIENT_RESPONSE 보냄[JSON](받았던 crop_width, crop_height 채워서 보냄)
		Json::Value part4_SendJson;
		part4_SendJson["status"] = "4";
		part4_SendJson["crop_width"] = "400";
		part4_SendJson["crop_height"] = "400";
		std::string part4_SendString = writer.write(part4_SendJson);
		char part4_SendBuf[1024];
		strcpy_s(part4_SendBuf, part4_SendString.c_str());
		std::cout << "Part 4\n" << part4_SendBuf << std::endl;
		//client->sendData(part4_SendBuf, 1024, vmsName);		// send json

		int requested_crop_w, requested_crop_h;
		int limit = 9;
		do
		{
			int w = 400;
			int h = 400;
			int bytes_per_line = 3;

			std::cout << "\n" << w*h*bytes_per_line << std::endl;
			unsigned char* receiveData = client->recv_IMAGE(w*h*bytes_per_line, vmsName);
			int inner_receiveSize = client->getCurrent_PacketSize();
			std::cout << "packet size: " << inner_receiveSize << std::endl;

			cv::Mat frame = cv::Mat(h, w, CV_8UC3);

			if (receiveData == NULL)
			{
				serverReconnect = true;
				break;
			}

			for (int r = 0; r < h; r++)
				for (int c = 0; c < w; c++)
					for (int channel = 0; channel < bytes_per_line; channel++)
						frame.at<cv::Vec3b>(r, c)[channel] = receiveData[r*w*bytes_per_line + c*bytes_per_line + channel];

			frame_directory.push_back(frame);
			std::cout << "Get Frame Succcess " << limit << std::endl;
		} while (client->getCurrent_PacketSize() > 0 && limit--);

		if (serverReconnect)
			continue;

		SetConsoleTextAttribute(hConsole, 13);
		int totalFrame = frame_directory.size();
		float maxScore = -1;
		int maxIndex = -1;
		unsigned char* retImage = NULL;
		int retW, retH;
		cv::Mat retRealImage;

		Json::Value root;

		for (int frame_idx = 0; frame_idx < totalFrame; frame_idx++)
		{
			std::string filename = std::to_string(frame_idx) + ".png";
			cv::imwrite(filename, frame_directory[frame_idx]);
			std::cout << "Save Frame Success " << frame_idx + 1 << std::endl;

			cv::Mat faceRecogFrame = frame_directory[frame_idx];
			nam_ds_LoadImageFromBuffer(faceRecogFrame.data, faceRecogFrame.cols, faceRecogFrame.rows, faceRecogFrame.channels());
			int detNum = nam_ds_DetectFaces(0, 0, 0, 0);
			std::cout << "Detection Faces: " << detNum << std::endl;

			if (detNum > 0)
			{
				std::cout << " detection success " << frame_idx + 1 << std::endl;
				std::cout << " Start Estimate Gender, Age, Face Score" << std::endl;

				int estimated_age = nam_ds_EstimateGender(0);
				int estimated_gender = nam_ds_EstimateAge(0);
				float* v_estimated_score = nam_ds_GetDetectedFaceScore();
				float m_estimated_score = v_estimated_score[0];

				m_estimated_score *= 1000;
				m_estimated_score = std::abs(m_estimated_score);

				std::cout << " Write Json message" << std::endl;
				std::cout << " Score: " << m_estimated_score << "\n" << std::endl;
				if (maxScore < m_estimated_score)
				{
					retImage = NULL;
					retImage = nam_ds_GetDetectFaces();
					retW = nam_ds_ExtractFaceWidth(0);
					retH = nam_ds_ExtractFaceHeight(0);

					std::string ageIndex;
					std::string genIndex;

					maxScore = m_estimated_score;
					maxIndex = frame_idx;
					root["status"] = "5";
					root["person_face_score"] = std::to_string(m_estimated_score).c_str();
					root["crop_width"] = "400";
					root["crop_height"] = "400";


					//GEN: 0=man 1=woman
					
					genIndex = "man";
					root["person_gender"] = genIndex.c_str();

					//AGE: 0-2 4-6 8-13 15-20 25-32 38-43 48-53 60-
					ageIndex = "25-32";
					root["person_age"] = ageIndex.c_str();

					std::cout << " age, gender, score: " << ageIndex << " " << genIndex << " " << maxScore << "\n" << std::endl;
					std::cout << " image info: " << retW << " " << retH << " " << maxScore << "\n" << std::endl;
				}
			}
			else
				std::cout << " detection failed " << frame_idx + 1 << std::endl;
		}

		if (maxScore == -1)
		{
			root["status"] = "5";
			root["person_face_score"] = "0";
			root["crop_width"] = "Detection Failed";
			root["crop_height"] = "Detection Failed";
			root["person_gender"] = "Detection Failed";
			root["person_age"] = "Detection Failed";
			maxIndex = 0;
			retRealImage = frame_directory[maxIndex];
		}
		else
		{
			retRealImage = cv::imread("DLL.png");
			cv::resize(retRealImage, retRealImage, cv::Size(400, 400));
		}

		std::string returnJsonMessage = writer.write(root);
		char sendbuf[1024];
		strcpy_s(sendbuf, returnJsonMessage.c_str());
		client->sendData(retRealImage.data, sizeof(char) * 400 * 400 * 3, vmsName);	// send image
		client->sendData(sendbuf, returnJsonMessage.length(), vmsName);		// send json

		if (maxScore != -1)
			Sleep(1000);

		client->closeClient(vmsName);
		delete client;
	}

	return 0;
}

int loadLibrary()
{
	HINSTANCE hGetProcIDDLL = LoadLibrary("DS_DLL");

	if (hGetProcIDDLL == NULL)
	{
		std::cout << "cannot locate the .dll file" << std::endl;
		return -1;
	}
	else
		std::cout << "find .dll file" << std::endl;

	nam_ds_SetParameter = (CPP_nam_ds_SetParameter)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_SetParameter");
	nam_ds_LoadImageFromBuffer = (Cpp_nam_ds_LoadImageFromBuffer)GetProcAddress(hGetProcIDDLL, "Cpp_nam_ds_LoadImageFromBuffer");
	nam_ds_DetectFaces = (CPP_nam_ds_DetectFaces)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_DetectFaces");
	nam_ds_ExtractFeature = (CPP_nam_ds_ExtractFeature)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_ExtractFeature");
	nam_ds_ExtractFaceWidth = (CPP_nam_ds_ExtractFaceWidth)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_ExtractFaceWidth");
	nam_ds_ExtractFaceHeight = (CPP_nam_ds_ExtractFaceHeight)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_ExtractFaceHeight");
	nam_ds_ExtractFaceCenter = (CPP_nam_ds_ExtractFaceCenter)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_ExtractFaceCenter");
	nam_ds_VerifyFeature = (CPP_nam_ds_VerifyFeature)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_VerifyFeature");
	nam_ds_EstimateGender = (CPP_nam_ds_EstimateGender)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_EstimateGender");
	nam_ds_EstimateAge = (CPP_nam_ds_EstimateAge)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_EstimateAge");
	nam_ds_GetDetectedFaceScore = (CPP_nam_ds_GetDetectedFaceScore)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_GetDetectedFaceScore");
	nam_ds_GetDetectFaces = (CPP_nam_ds_GetDetectFaces)GetProcAddress(hGetProcIDDLL, "CPP_nam_ds_GetDetectFaces");

	if (!nam_ds_SetParameter || !nam_ds_LoadImageFromBuffer || !nam_ds_DetectFaces 
		|| !nam_ds_ExtractFeature || !nam_ds_ExtractFaceWidth || !nam_ds_ExtractFaceHeight
		|| !nam_ds_ExtractFaceCenter || !nam_ds_VerifyFeature)
	{
		std::cout << "could not locate the function" << std::endl;
		return -1;
	}



	return 0;
}