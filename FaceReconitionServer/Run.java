package nam.kwan.woo;
import java.util.List;

public class Run  {

	/**
	 * Enter parameters need to be set before start recognition engine
	 * @param threadIdx Current thread index
	 * @param totalThreadNum Total number of thread to run
 	 * @param modelDirectory Model Directory
	 * @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 4: NOFILE, 7: EXPIRED
	 */
	public native int SetParameter(int threadIdx, int totalThreadNum, String modelDirectory);

	/**
	 * Get byte[], width, and height of image as inputs and save them to the engine. 
	 * @param img Image pixel data in byte[].
	 * @param width Width of image
	 * @param height Height of image
	 * @param bytes_per_line Channel(Gray:1, Color:3)
	 * @param tid index of thread 
	 * @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 3: UNHANDLEDPARAM, 7: EXPIRED
	 */
	public native int LoadImageFromBuffer(byte[] img, int width, int height, int bytes_per_line, int tid);
	
	/**
	 * Get file path as a input and save that image to engine
	 * @param filename File path
	 * @param tid index of thread
	 * @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 4: NOFILE, 7: EXPIRED
	 */
	public native int LoadImageFromFile(String filename, int tid);
	
	/**
	 * Detect face area from saved image in the engine
	 * @param x Top-left point of the frame to be checked
	 * @param y Top-left point of the frame to be checked
	 * @param width Width of frame to be checked
	 * @param height Height of frame to be checked
	 * @param tid index of thread 
	 * @return Total number of detected faces 
	 */
	public native int DetectFaces(int x, int y, int width, int height, int tid);
	
	/**
	 * Get Face Detection Score
	 * @param tid index of thread
	 * @return Face Detection Score
	 */
	public native float[] GetDetectedFaceScore(int tid);
	
	/**
	 * Extract features from detected face
	 * @param idx_face Index of one of the detected faces
	 * @param tid index of thread
	 * @return Feature vector of that face number
	 */
	public native float[] ExtractFeature(int idx_face, int tid);
	
	/**
	 * Get age of face from detected face
	 * @param idx_face Index of one of the detected faces
	 * @param tid index of thread
	 * @return Age of face
	 */
	public native int EstimateAge(int idx_face, int tid);
	
	/**
	 * Get gender of face from detected face
	 * @param idx_face Index of one of the detected faces
	 * @param tid index of thread
	 * @return Gender of face
	 */
	public native int EstimateGender(int idx_face, int tid);
	
	/**
	 * Get width of face from detected face
	 * @param idx_face Index of one of the detected faces
	 * @param tid index of thread
	 * @return Width of face
	 */
	public native int ExtractFaceWidth(int idx_face, int tid);
	
	/**
	 * Get Height of face from detected face  
	 * @param idx_face Index of one of the detected faces
	 * @param tid index of thread 
	 * @return Width of face 
	 */
	public native int ExtractFaceHeight(int idx_face, int tid);
	
	/**
	 * Get center point from detected face 
	 * @param idx_face Index of one of the detected faces
	 * @param tid index of thread 
	 * @return Center point of that face, int[0] = x, int[1] = y  
	 */
	public native int[] ExtractFaceCenter(int idx_face, int tid);
	
	/**
	 * Save detected face to image file 
	 * @param idx_face Index of one of the detected faces  
	 * @param filename File name with path to save 
	 * @param width Width of face image 
	 * @param tid index of thread
	 * @return Error code, 0: SUCCESS, 1: INITFAIL, 2: INVALIDPARAM, 5: HAVENOIMGFILE, 7: EXPIRED
	 */
	public native int SaveFaceImageToFile(int idx_face, String filename, int width, int tid);
	
	/**
	 * Save detected face to byte[] 
	 * @param idx_face Index of one of the detected faces 
	 * @param width Width of face image 
	 * @param tid index of thread
	 * @return Image pixel data in byte[] byte[] 
	 */
	public native byte[] SaveFaceImageToBuffer(int idx_face, int width, int tid);
	
	/**
	 * Save detected face jpeg stream to byte[] 
	 * @param idx_face Index of one of the detected faces 
	 * @param width Width of face image 
	 * @param tid index of thread
	 * @return Image pixel data in byte[] byte[] 
	 */
	public native byte[] SaveFaceImageToJpegStreamBuffer(int idx_face, int width, int tid);
	
	/**
	 * Calculate similarity(0~100) comparing two feature vectors 
	 * @param feat1 Feature vector to be compared
	 * @param feat2 Anote feature vector to be compared
	 * @param tid index of thread
	 * @return Similarity score 
	 */
	public native int VerifyFeature(float[] feat1, float[] feat2, int tid);
	
	/**
	 * Calculate similarity comparing a source feature vector with many target feature vectors and retrun them in descending order
	 * @param feat Source feature vector source
	 * @param featList Target feature vectors list
	 * @param threshold To make it return the only feature vectors over the threshold
	 * @param limitCount To make it return up to limitCount
	 * @param tid index of thread
	 * @return See TargetFeature object structure
	 */
	public native List<TargetFeature> VerifyFeatureList(float[] feat, List<TargetFeature> featList,int threshold, int limitCount, int tid);
	
	public native void LoadImageFromPointer(long bufferPointer, int imageFormat, int size, int tid);
	public native void LoadBGR24FromPointer(long bufferPointer, int width, int height, int bytesPerLine, int tid);
	
	static {
		System.loadLibrary("caffe_FRS");
	}


	
	
	/**
	 * Example code
	 * @param args System input(Unused)
	 */
	public static void main(String[] args) {
		//Set parameters
			
		int threadNum = 1;

		for (int i = 0; i < threadNum; i++) {
			MultiThread_SampleCode R1 = new MultiThread_SampleCode(i, threadNum);
			R1.start();
		}
	}
}
