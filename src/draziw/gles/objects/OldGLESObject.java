package draziw.gles.objects;

import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import android.opengl.Matrix;
import android.util.Log;

public abstract class OldGLESObject {
	
	float currentPosition[]=new float[]{0f,0f,0f}; //current position
		
	public Texture mTexture;
	public float[] mGeometry=new float[3];
	
	public float[] mObjectMatrix = new float[16];
	public float[] mObjectMVPMatrix = new float[16];

	public int shaderProgramHandler;
	
	public abstract ShaderProgram getShaderProgramInstance();
	
	
	public abstract void initializeShaderParam();
	public abstract void draw(float[] viewMatrix, float[] projectionMatrix, float timer); // timer для анимации кадров	
	
	public OldGLESObject(Texture texture) {
		shaderProgramHandler = getShaderProgramInstance().programHandler;
		this.mTexture=texture;
		Matrix.setIdentityM(mObjectMatrix,0);
		this.initializeShaderParam();
	}
	
	public void rotate(float angleInDegrees,float x,float y,float z) {
		Matrix.rotateM(mObjectMatrix, 0, angleInDegrees, x, y,z);		
	}
	
	public void scale(float x,float y,float z) {		
		Matrix.scaleM(mObjectMatrix, 0, x, y, z);			
	}

	public void translate(float x,float y,float z) {
		Matrix.translateM(mObjectMatrix, 0, x, y, z);
	}
	
	public void moveTo(float x,float y,float z) {
		// this work only if geometry set
		
		float dx=x-currentPosition[0];
		float dy=currentPosition[1]-y;
		float dz=z-currentPosition[2];
		
		
		// мы двигаем левый верхний угол, его координаты условно -1,1,1 значит
		// геометрию по x - прибавляем, а геометрию по y,z - вычитаем
		
		translate(dx+mGeometry[0]*0.5f,dy-mGeometry[1]*0.5f,dz-mGeometry[2]*0.5f);
		
		currentPosition[0]=x;
		currentPosition[1]=y;
		currentPosition[2]=z;
	}	
	
	public void moveBack() {
		translate(currentPosition[0]+mGeometry[0]*0.5f,currentPosition[1]-mGeometry[1]*0.5f,currentPosition[2]-mGeometry[2]*0.5f);
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
