package draziw.gles.objects;

import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;
import draziw.gles.math.MyMatrix;
import android.opengl.Matrix;

public abstract class GLESObject {
					
	public Texture mTexture;
	public Material material;
	
	public float[] mObjectMatrix = new float[16];
	public float[] mObjectMVPMatrix = new float[16];
	
	public float[] position = new float[3];
	public float[] geometry=new float[3];

	public boolean isAnimated=false;
	
	//public abstract void initializeShaderParam();
	public abstract void draw(float[] viewMatrix, float[] projectionMatrix, float timer); // timer для анимации кадров	
	
	public GLESObject(Texture texture,Material material) {
		this.material = material;
		this.mTexture=texture;
		Matrix.setIdentityM(mObjectMatrix,0);		
	}
	
	
	// вращение по умолчанию
	public void rotateM(float angleInDegrees,float x,float y,float z) {
		Matrix.rotateM(mObjectMatrix, 0, angleInDegrees, x, y,z);	
	}
	
	public void scaleM(float x,float y,float z) {		
		Matrix.scaleM(mObjectMatrix, 0, x, y, z);			
	}
	
	
	public void translateI(float x,float y,float z) {
		mObjectMatrix[12]+=x;
		mObjectMatrix[13]+=y;
		mObjectMatrix[14]+=z;
		MyMatrix.vec3add(position, x, y, z);
	}
	
	public void setPositionI(float x,float y,float z) {
		mObjectMatrix[12]=x;
		mObjectMatrix[13]=y;
		mObjectMatrix[14]=z;
		MyMatrix.vec3set(position, x, y, z);
	}

	public void translateM(float x,float y,float z) {
		MyMatrix.vec3add(position, x, y, z);
		
		Matrix.translateM(mObjectMatrix, 0, x, y, z);
			
	}
	
	public void setPositionM(float x,float y,float z) {
				
		Matrix.translateM(mObjectMatrix, 0, x-position[0],y-position[1], z-position[2]);
		
		MyMatrix.vec3set(position, x, y, z);
	}		
	
	public void setGeometry(float x,float y,float z) {
		// геометрия в единицах мира, раньше мы страдали фигней :)))
		geometry[0]=x;
		geometry[1]=y;
		geometry[2]=z;
	}
	
	public void setRadius(float r) {
		geometry[0]=r;
		geometry[1]=r;
		geometry[2]=r;
	}
	
	public float getRadius() {
		return geometry[0];
	}
	
	public void resetMatrix() {
		Matrix.setIdentityM(mObjectMatrix,0);
	}
	
	public boolean isGUI() {
		return false;
	}
	
	public boolean isCollidePoint(float[] mPos) {
		return false;
	}
	
	public void setAnimated(boolean mEnable) {
		isAnimated=mEnable;
	}
	
	public boolean isAnimated() {
		return isAnimated;
	}
	
	
	
	
	

}
