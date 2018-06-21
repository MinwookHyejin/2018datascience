package nam.kwan.woo;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;


/*import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;*/

//0111 Test
import java.io.ByteArrayOutputStream;

public class MultiThread_SampleCode implements Runnable {

	static {
		System.loadLibrary("caffe_FRS");
	}

	private Thread t;
	private int tid;
	private int totalThread;

	MultiThread_SampleCode(int idx, int totalThread) {
		this.tid = idx;
		this.totalThread = totalThread;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (new Run().SetParameter(tid, totalThread, "./model22/") == 0)
			System.out.println("[Success]");
		else {
			System.out.println("[Fail]");
			return;
		}
		
		// Nam Test 
		BufferedImage bufferedImg = null;
		try {
			bufferedImg = ImageIO.read(new File("./SampleImage/C.jpg"));
		} catch (IOException e) {
			System.out.println("The image was not loaded.");
		}
		byte[] pixels = ((DataBufferByte) bufferedImg.getRaster().getDataBuffer()).getData();

		if (new Run().LoadImageFromBuffer(pixels, bufferedImg.getWidth(), bufferedImg.getHeight(), 3, tid) == 0)
			System.out.println("Load image from buffer[Success]");
		else {
			System.out.println("Load image from buffer[Fail]");
			return;
		}
		
		System.out.println("Detected face number : " + new Run().DetectFaces(0, 0, 0, 0, tid));

		// Nam Test 

		InstagramModule instagramModule = new InstagramModule();
		instagramModule.initializeInstagramModule("jojem40@gmail.com", "jj5649", "suhwan7155");
		//instagramModule.initializeInstagramModule("hyunj00915@naver.com", "so1989", "hyunj0092");
		instagramModule.parsingGivenUserName();

		List<BufferedImage> userfaceBuffers = instagramModule.getListByteBufferInstagramUser();
		List<String> userNames = instagramModule.getListStringBufferUserID();

		List<float[]> featureList = new ArrayList<float[]>();
		
		int index = 0;

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("test.csv"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (BufferedImage face : userfaceBuffers) {
			StringBuilder sb = new StringBuilder();

			byte[] bgrPixel = ((DataBufferByte) face.getRaster().getDataBuffer()).getData();
			if (new Run().LoadImageFromBuffer(bgrPixel, face.getWidth(), face.getHeight(), 3, tid) == 0)
				System.out.println("Load image from buffer[Success]");
			else {
				System.out.println("Load image from buffer[Fail]");
				return;
			}

			int detNum = new Run().DetectFaces(0, 0, 0, 0, tid);
			System.out.println("Detected face number : " + detNum);
			
			sb.append(userNames.get(index));
			sb.append(',');
			
			
			if (detNum == 0) {
				sb.append("detection failed");
				sb.append("\n");
				index++;
				continue;
			}

			float[] detectionScore_1 = new Run().GetDetectedFaceScore(tid);
			System.out.println("Number of scores: " + detectionScore_1.length);

			if (detectionScore_1.length != 0)
				for (int i = 0; i < detectionScore_1.length; i++)
					System.out.println("Detected face Score : " + detectionScore_1[i]);
			else
				System.out.println("fail to get detected face score");

			long start_Time = System.currentTimeMillis();
			float[] ef_1 = new Run().ExtractFeature(0, tid);
			featureList.add(ef_1);

			long endTime = System.currentTimeMillis();
			System.out.println("\n[Java] extractFeatureTime: " + (endTime - start_Time) + " milliseconds\n");
			
			/*sb.append(',');
			sb.append("Feature,");*/
			for (int i = 0; i < 512; i++)
			{
				//System.out.println("feat: " + ef_1[i]);
				sb.append(ef_1[i]);
				sb.append(',');
			}
			sb.append("\n");
			
			
			int age, gender;
			float w,h,cx,cy;
			float score = new Run().VerifyFeature(ef_1, featureList.get(0), tid);
			
			age = new Run().EstimateAge(0, tid);
			gender = new Run().EstimateGender(0, tid);
			w = new Run().ExtractFaceWidth(0, tid);
			h = new Run().ExtractFaceHeight(0, tid);
			cx = new Run().ExtractFaceCenter(0, tid)[0];
			cy = new Run().ExtractFaceCenter(0, tid)[1];
			
			System.out.println("Age : " + age);
			
			/*sb.append(",age," + age + "\n");
			sb.append(",gender," + gender + "\n");
			sb.append(",width," + w + ",height," + h + ",center x y," + cx +"," + cy + "\n");
			sb.append(",Score," + score + "\n");*/
			
			System.out.println("Gender : " + gender);

			// Extract Face width, height, center
			System.out.println("Width : " + w);
			System.out.println("Height : " + h);
			System.out.println("CenterX : " + cx + " CenterY : "
					+ cy);
			
			System.out.println("\nScore: " + score + "\n");
			pw.write(sb.toString());
			
			// Save face image to file
			if (new Run().SaveFaceImageToFile(0, "FaceImageToFile_" + ++index + ".jpg", 300, tid) == 0)
				System.out.println("Save face image to file[Success]");
			else {
				System.out.println("Save face image to file[Fail]");
				return;
			}
		}

		System.out.println("Feature List Size: " + featureList.size());
		pw.close();
		
		// // TODO Auto-generated method stub
		// // Load image from buffer
		// BufferedImage bufferedImg = null;
		// try {
		// bufferedImg = ImageIO.read(new File("./SampleImage/ef1.jpg"));
		// } catch (IOException e) {
		// System.out.println("The image was not loaded.");
		// }
		// byte[] pixels = ((DataBufferByte)
		// bufferedImg.getRaster().getDataBuffer()).getData();
		//
		// if (new NFSFRS().LoadImageFromBuffer(pixels, bufferedImg.getWidth(),
		// bufferedImg.getHeight(), 3, tid) == 0)
		// System.out.println("Load image from buffer[Success]");
		// else {
		// System.out.println("Load image from buffer[Fail]");
		// return;
		// }
		//
		// // Detect face & Detected face score & Extract feature
		// long start_Time = System.currentTimeMillis();
		// System.out.println("Detected face number : " + new NFSFRS().DetectFaces(0, 0,
		// 0, 0, tid));
		// //System.out.println("[application thread]: " + t.currentThread().getId());
		// long endTime = System.currentTimeMillis();
		// System.out.println("\n[Java] detectFace_matalignment: " + (endTime -
		// start_Time) + " milliseconds\n");
		//
		// float[] detectionScore_1 = new NFSFRS().GetDetectedFaceScore(tid);
		// System.out.println("Number of scores: " + detectionScore_1.length);
		//
		// if(detectionScore_1.length != 0)
		// for(int i=0; i<detectionScore_1.length; i++)
		// System.out.println("Detected face Score : " + detectionScore_1[i]);
		// else
		// System.out.println("fail to get detected face score");
		//
		// start_Time = System.currentTimeMillis();
		// float[] ef_1 = new NFSFRS().ExtractFeature(0, tid);
		// endTime = System.currentTimeMillis();
		// System.out.println("\n[Java] extractFeatureTime: " + (endTime - start_Time) +
		// " milliseconds\n");
		//
		// System.out.println("Age : " + new NFSFRS().EstimateAge(0, tid));
		// System.out.println("Gender : " + new NFSFRS().EstimateGender(0, tid));
		//
		// // Extract Face width, height, center
		// System.out.println("Width : " + new NFSFRS().ExtractFaceWidth(0, tid));
		// System.out.println("Height : " + new NFSFRS().ExtractFaceHeight(0, tid));
		// System.out.println("CenterX : " + new NFSFRS().ExtractFaceCenter(0, tid)[0] +
		// " CenterY : "
		// + new NFSFRS().ExtractFaceCenter(0, tid)[1]);
		//
		//
		// // Save face image to file
		// if (new NFSFRS().SaveFaceImageToFile(0, "FaceImageToFile_" + tid + ".jpg",
		// 100, tid) == 0)
		// System.out.println("Save face image to file[Success]");
		// else {
		// System.out.println("Save face image to file[Fail]");
		// return;
		// }
		//
		// // Save face image to buffer
		// byte[] buf_FaceImg = new NFSFRS().SaveFaceImageToBuffer(0, 100, tid);
		//
		// if (buf_FaceImg != null)
		// System.out.println("Save face image to buffer[Success]");
		// else {
		// System.out.println("Save face image to buffer[Fail]");
		// return;
		// }
		//
		// // Save face jpeg image to buffer
		// byte[] buf_JpegFaceImg = new NFSFRS().SaveFaceImageToJpegStreamBuffer(0, 100,
		// tid);
		// if (buf_JpegFaceImg != null)
		// System.out.println("Save face Jpeg image to buffer[Success]");
		// else {
		// System.out.println("Save face Jpeg image to buffer[Fail]");
		// return;
		// }
		//
		// // Jpeg file save test
		// try {
		// FileOutputStream fos=new FileOutputStream("_FaceImageToJpegStream_" + tid +
		// ".jpg");
		//
		// fos.write(buf_JpegFaceImg);
		// fos.close();
		// }
		// catch (java.io.IOException e) {
		// e.printStackTrace();
		// }
		//
		// // Jpegfile save test end
		//
		// ////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// // Load image from file
		// if (new NFSFRS().LoadImageFromFile("./SampleImage/4k1.jpg", tid) == 0)
		// System.out.println("Load image from file[Success]");
		// else {
		// System.out.println("Load image from file[Fail]");
		// return;
		// }
		//
		//
		// start_Time = System.currentTimeMillis();
		// // Detect face & Detected face score & Extract feature
		// System.out.println("Detected face number : " + new NFSFRS().DetectFaces(0, 0,
		// 0, 0, tid));
		// //System.out.println("[application thread]: " + t.currentThread().getId());
		// endTime = System.currentTimeMillis();
		// System.out.println("\n[Java] detectFace_matalignment: " + (endTime -
		// start_Time) + " milliseconds\n");
		//
		// float[] detectionScore_2 = new NFSFRS().GetDetectedFaceScore(tid);
		//
		// if(detectionScore_2.length != 0)
		// for(int i=0; i<detectionScore_2.length; i++)
		// System.out.println("Detected face Score : " + detectionScore_2[i]);
		// else
		// System.out.println("fail to get detected face score");
		//
		//
		//
		// if (new NFSFRS().SaveFaceImageToFile(0, "FaceImageToFile_" + tid + ".jpg",
		// 100, tid) == 0)
		// System.out.println("Save face image to file[Success]");
		// else {
		// System.out.println("Save face image to file[Fail]");
		// return;
		// }
		//
		// start_Time = System.currentTimeMillis();
		// float[] ef_2 = new NFSFRS().ExtractFeature(0, tid);
		// endTime = System.currentTimeMillis();
		// System.out.println("\n[Java] extractFeatureTime: " + (endTime - start_Time) +
		// " milliseconds\n");
		//
		// // Save face image to buffer
		// byte[] buf_FaceImg2 = new NFSFRS().SaveFaceImageToBuffer(0, 100, tid);
		// if (buf_FaceImg2 != null)
		// System.out.println("Save face image to buffer[Success]");
		// else {
		// System.out.println("Save face image to buffer[Fail]");
		// return;
		// }
		//
		//
		//
		//
		// // Save face jpeg image to buffer
		// byte[] buf_JpegFaceImg2 = new NFSFRS().SaveFaceImageToJpegStreamBuffer(0,
		// 100, tid);
		// if (buf_JpegFaceImg2 != null)
		// System.out.println("Save face Jpeg image to buffer[Success]");
		// else {
		// System.out.println("Save face Jpeg image to buffer[Fail]");
		// return;
		// }
		//
		// ByteArrayInputStream inputStream = new
		// ByteArrayInputStream(buf_JpegFaceImg2);
		//
		// try {
		// BufferedImage bufferedImage = ImageIO.read(inputStream);
		// ImageIO.write(bufferedImage, "jpg", new File("jpgTest.jpg"));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // Verify Feature
		// System.out.println("Score of First and Second images : " + new
		// NFSFRS().VerifyFeature(ef_1, ef_2, tid));
		//
		// // Verify FeatureList
		// List<TargetFeature> FeatureList = new ArrayList<TargetFeature>();
		// for (int i = 0; i < 10; i++) {
		// new NFSFRS().LoadImageFromFile("./SampleImage/list_ef_" + i + ".jpg", tid);
		// new NFSFRS().DetectFaces(0, 0, 0, 0, tid);
		// TargetFeature tf = new TargetFeature();
		// tf.setFaceId(i + 10);
		//
		// start_Time = System.currentTimeMillis();
		// tf.setFeature(new NFSFRS().ExtractFeature(0, tid));
		// endTime = System.currentTimeMillis();
		// System.out.println("\n[Java] extractFeatureTime: " + (endTime - start_Time) +
		// " milliseconds\n");
		//
		// FeatureList.add(tf);
		// }
		//
		// List<TargetFeature> result = new NFSFRS().VerifyFeatureList(ef_1,
		// FeatureList, 0, 10, tid);
		// for (int i = 0; i < result.size(); i++)
		// System.out.println(
		// "Similar faceId : " + result.get(i).getFaceId() + " / score : " +
		// result.get(i).getScore());
		// System.out.println("");
		// System.out.println("");
		// System.out.println("");
	}

	public void start() {

		// TODO Auto-generated method stub
		if (t == null) {
			t = new Thread(this);

			t.start();
		}
	}
}