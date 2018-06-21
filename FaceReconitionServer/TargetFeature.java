package nam.kwan.woo;

public class TargetFeature {
	/**
	 * Face Index
	 */
	private int faceId;
	
	/**
	 * Feature vector
	 */
	private float[] feature;
	
	/**
	 * Similarity score
	 */
	private int score;
	
	/**
	 * Return similarity socre
	 * @return Similarity score
	 */
	public int getScore(){
		return score;
	}
	
	/**
	 * Set similarity score
	 * @param score Similarity score
	 */
	public void setScore(int score){
		this.score = score;
	}
	
	/**
	 * Return face index
	 * @return Face index
	 */
	public int getFaceId(){
		return faceId;
	}
	
	/**
	 * Set face index
	 * @param faceId Face index
	 */
	public void setFaceId(int faceId){
		this.faceId = faceId;
	}
	
	/**
	 * Return feature vector
	 * @return Feature vector
	 */
	public float[] getFeature(){
		return feature;
	}
	
	/**
	 * Set feature vector
	 * @param feature Feature vector
	 */
	public void setFeature(float[] feature){
		this.feature = feature;
	}
}
