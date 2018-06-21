#ifndef _nam_ds_CPP_H
#define _nam_ds_CPP_H

#include "TargetFeature.h"


//more about this in reference 1
#ifdef _nam_ds_EXPORT
#define NFSDLL  __declspec(dllexport)   // export DLL information

#else
#define NFSDLL  __declspec(dllimport)   // import DLL information

#endif 

//Error code
#define NFS_SUCCESS 0
#define NFS_INITFAIL 1
#define NFS_INVALIDPARAM 2
#define NFS_UNHANDLEDPARAM 3
#define NFS_NOFILE 4
#define NFS_HAVENOIMGFILE 5
#define NFS_UNATHORIZED 6
#define NFS_EXPIRED 7

#ifdef __cplusplus
extern "C" {
#endif

#ifdef _nam_ds_EXPORT
	/**
	* Enter parameters need to be set before start recognition engine
	* @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 4: NOFILE, 7: EXPIRED
	*/
	NFSDLL int CPP_nam_ds_SetParameter();

	/**
	* Get char*, width, and height of image as inputs and save them to the engine.
	* @param buffer BGR Image pixel data of image
	* @param w Width of image
	* @param h Height of image
	* @param bytes_per_line Channel of image(Gray:1, Color:3)
	* @return Error code, 0: SUCCESS, 2: INVALIDPARAM, 3: UNHANDLEDPARAM, 7: EXPIRED
	*/
	NFSDLL int Cpp_nam_ds_LoadImageFromBuffer(unsigned char *buffer, int w, int h, int bytes_per_line);

	/**
	* Get file path as a input and save that image to engine
	* @param filename File path
	* @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 4: NOFILE, 7: EXPIRED
	*/
	NFSDLL int CPP_nam_ds_LoadImageFromFile(char* filepath);

	/**
	* Detect face area from saved image in the engine
	* @param x Top-left point of the frame to be checked
	* @param y Top-left point of the frame to be checked
	* @param width Width of frame to be checked
	* @param height Height of frame to be checked
	* @return Total number of detected faces
	*/
	NFSDLL int CPP_nam_ds_DetectFaces(int x, int y, int w, int h);

	NFSDLL int CPP_nam_ds_ExtractDistanceBetweenEyes(int idx_face);

	/**
	* Extract features from detected face
	* @param idx_face Index of one of the detected faces
	* @return Feature vector of that face number
	*/
	NFSDLL float* CPP_nam_ds_ExtractFeature(int idx_face);

	NFSDLL int* CPP_nam_ds_ExtractShape(int idx_face);
	/**
	* Extract features from detected face
	* @param idx_face Index of one of the detected faces
	* @return Feature vector of that face number
	* Usage for Quadruplet
	* 0929
	*/
	NFSDLL float* CPP_nam_ds_ExtractFeature_Quadruplet(char* imgFile, char* imgFile_p);
	/**
	* Get width of face from detected face
	* @param idx_face Index of one of the detected faces
	* @return Width of face
	*/
	NFSDLL int CPP_nam_ds_ExtractFaceWidth(int idx_face);

	/**
	* Get Height of face from detected face
	* @param idx_face Index of one of the detected faces
	* @return Width of face
	*/
	NFSDLL int CPP_nam_ds_ExtractFaceHeight(int idx_face);

	/**
	* Get center point from detected face
	* @param idx_face Index of one of the detected faces
	* @return Center point of that face, int[0] = x, int[1] = y
	*/
	NFSDLL int* CPP_nam_ds_ExtractFaceCenter(int idx_face);

	/**
	* Save detected face to image file
	* @param idx_face Index of one of the detected faces
	* @param filename File name with path to save
	* @param width Width of face image
	* @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 5: HAVENOIMGFILE, 7: EXPIRED
	*/
	NFSDLL int CPP_nam_ds_SaveFaceImageToFile(int idx_face, char* filepath, int width);

	/**
	* Calculate similarity(0~100) comparing two feature vectors
	* @param feat1 Feature vector to be compared
	* @param feat2 Anote feature vector to be compared
	* @return Similarity score
	*/
	NFSDLL float CPP_nam_ds_VerifyFeature(float* f1, float* f2, int m_NormType);

	// 0912 return type int* to float*
	NFSDLL float* CPP_nam_ds_VerifyFeature_CroppedFeature(float* f1, float* f2, int m_NormType);
	/**
	* Calculate similarity comparing a source feature vector with many target feature vectors and retrun them in descending order
	* @param feat Source feature vector
	* @param tf Target feature pointer
	* @param targetFeatureSize size of targetfeature
	* @param limitCount To make it return up to limitCount
	* @return See TargetFeature object structure
	*/
	NFSDLL TargetFeature* CPP_nam_ds_VerifyFeatureList(float* feat, TargetFeature* tf, int targetFeatureSize, int limitCount);

	/*
	* Release feature
	* @param f Feature to delete
	*/
	NFSDLL void CPP_nam_ds_ReleaseFeature(float* f);

	/*
	* Release feature class
	* @param tf Feature class to delete
	*/
	NFSDLL void CPP_nam_ds_ReleaseFeatureClass(TargetFeature* tf);

	/*
	* Release coordinate
	* @param i Coordinate to delete
	*/
	NFSDLL void CPP_nam_ds_ReleaseCoord(int* i);

	NFSDLL int CPP_nam_ds_EstimateGender(int idx_face);
	NFSDLL int CPP_nam_ds_EstimateAge(int idx_face);
	NFSDLL float* CPP_nam_ds_GetDetectedFaceScore();
	NFSDLL unsigned char* CPP_nam_ds_GetDetectFaces();

#else
	typedef int(__stdcall *CPP_nam_ds_SetParameter)();
	typedef int(__stdcall *Cpp_nam_ds_LoadImageFromBuffer)(unsigned char *buffer, int w, int h, int bytes_per_line);
	typedef int(__stdcall *CPP_nam_ds_LoadImageFromFile)(char* filepath);
	typedef int(__stdcall *CPP_nam_ds_DetectFaces)(int x, int y, int w, int h);
	typedef int(__stdcall *CPP_nam_ds_DetectFaces_with_FaceShape)(int x, int y, int w, int h, std::vector<int>& shape_Points);
	typedef float*(__stdcall *CPP_nam_ds_ExtractFeature)(int idx_face);
	typedef int*(__stdcall *CPP_nam_ds_ExtractShape)(int idx_face);
	typedef float*(__stdcall *CPP_nam_ds_ExtractFeature_Quadruplet)(char* img_a, char* img_b);	//1010
	typedef int(__stdcall *CPP_nam_ds_ExtractFaceWidth)(int idx_face);
	typedef int(__stdcall *CPP_nam_ds_ExtractFaceHeight)(int idx_face);
	typedef int*(__stdcall *CPP_nam_ds_ExtractFaceCenter)(int idx_face);
	typedef int(__stdcall *CPP_nam_ds_SaveFaceImageToFile)(int idx_face, char* filepath, int width);
	typedef float(__stdcall *CPP_nam_ds_VerifyFeature)(float* f1, float* f2, int m_NormType);
	typedef TargetFeature*(__stdcall *CPP_nam_ds_VerifyFeatureList)(float* feat, TargetFeature* tf, int targetFeatureSize, int limitCount);
	typedef void(__stdcall *CPP_nam_ds_ReleaseFeature)(float* f);
	typedef void(__stdcall *CPP_nam_ds_ReleaseFeatureClass)(TargetFeature* tf);
	typedef void(__stdcall *CPP_nam_ds_ReleaseCoord)(int* i);
	typedef int(__stdcall *CPP_nam_ds_EstimateGender)(int idx_face);
	typedef int(__stdcall *CPP_nam_ds_EstimateAge)(int idx_face);
	typedef float*(__stdcall *CPP_nam_ds_GetDetectedFaceScore)();
	typedef unsigned char* (__stdcall *CPP_nam_ds_GetDetectFaces)();

	CPP_nam_ds_SetParameter nam_ds_SetParameter;
	Cpp_nam_ds_LoadImageFromBuffer nam_ds_LoadImageFromBuffer;
	CPP_nam_ds_LoadImageFromFile nam_ds_LoadImageFromFile;
	CPP_nam_ds_DetectFaces nam_ds_DetectFaces;
	CPP_nam_ds_DetectFaces_with_FaceShape nam_ds_DetectFaces_with_FaceShape;
	CPP_nam_ds_ExtractFeature nam_ds_ExtractFeature;
	CPP_nam_ds_ExtractShape nam_ds_ExtractShape;
	CPP_nam_ds_ExtractFeature_Quadruplet nam_ds_ExtractFeature_Quadruplet;
	CPP_nam_ds_ExtractFaceWidth nam_ds_ExtractFaceWidth;
	CPP_nam_ds_ExtractFaceHeight nam_ds_ExtractFaceHeight;
	CPP_nam_ds_ExtractFaceCenter nam_ds_ExtractFaceCenter;
	CPP_nam_ds_SaveFaceImageToFile nam_ds_SaveFaceImageToFile;
	CPP_nam_ds_VerifyFeature nam_ds_VerifyFeature;
	CPP_nam_ds_VerifyFeatureList nam_ds_VerifyFeatureList;
	CPP_nam_ds_ReleaseFeature nam_ds_ReleaseFeature;
	CPP_nam_ds_ReleaseFeatureClass nam_ds_ReleaseFeatureClass;
	CPP_nam_ds_ReleaseCoord nam_ds_ReleaseCoord;
	CPP_nam_ds_EstimateGender nam_ds_EstimateGender;
	CPP_nam_ds_EstimateAge nam_ds_EstimateAge;
	CPP_nam_ds_GetDetectedFaceScore nam_ds_GetDetectedFaceScore;
	CPP_nam_ds_GetDetectFaces nam_ds_GetDetectFaces;
#endif

#ifdef __cplusplus
}
#endif


#endif


