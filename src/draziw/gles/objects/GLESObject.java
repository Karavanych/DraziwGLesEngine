package draziw.gles.objects;

import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import android.opengl.Matrix;
import android.util.Log;

public abstract class GLESObject {
		
		
	public Texture mTexture;
	public float[] mGeometry=new float[3];
	
	public float[] mObjectMatrix = new float[16];
	public float[] mObjectMVPMatrix = new float[16];
	
	public float[] position = new float[3];

	public int shaderProgramHandler;
	
	public abstract ShaderProgram getShaderProgramInstance();
	
	
	public abstract void initializeShaderParam();
	public abstract void draw(float[] viewMatrix, float[] projectionMatrix, float timer); // timer для анимации кадров	
	
	public GLESObject(Texture texture) {
		shaderProgramHandler = getShaderProgramInstance().programHandler;
		this.mTexture=texture;
		Matrix.setIdentityM(mObjectMatrix,0);
		this.initializeShaderParam();
	}
	
	
	// вращение по умолчанию
	public void rotateM(float angleInDegrees,float x,float y,float z) {
		Matrix.rotateM(mObjectMatrix, 0, angleInDegrees, x, y,z);	
	}
	
	public void scaleM(float x,float y,float z) {		
		Matrix.scaleM(mObjectMatrix, 0, x, y, z);			
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
		// геометрия в единицах в которых экран, т.е. -1,1.6 - до +1,-1,6
		mGeometry[0]=x;
		mGeometry[1]=y;
		mGeometry[2]=z;
	}
	
	public void scaleByGeometri() {
		Matrix.scaleM(mObjectMatrix, 0, mGeometry[0]*0.5f, mGeometry[1]*0.5f,mGeometry[2]*0.5f);		
	}
	
	public void resetMatrix() {
		Matrix.setIdentityM(mObjectMatrix,0);
	}
	
	public boolean isGUI() {
		return false;
	}
	

}
