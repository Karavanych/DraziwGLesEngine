package draziw.gles.engine;

public class Texture {
	public int id;
	public int index;
	public int sizeX,sizeY = 0;
	
	public Texture(int mId,int mIdx,int mSizeX, int mSizeY) {
		id=mId;
		index=mIdx;
		sizeX=mSizeX;
		sizeY=mSizeY;	
	}
}
