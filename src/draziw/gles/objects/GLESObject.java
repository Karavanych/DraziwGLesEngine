package draziw.gles.objects;

import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import android.opengl.Matrix;

public abstract class GLESObject {
					
	public Texture mTexture;
	
	public float[] mObjectMatrix = new float[16];
	public float[] mObjectMVPMatrix = new float[16];
	
	public float[] position = new float[3];
	public float[] geometry=new float[3];

	public int shaderProgramHandler;
	
	public abstract ShaderProgram getShaderProgramInstance();
	
	
	public abstract void initializeShaderParam();
	public abstract void draw(float[] viewMatrix, float[] projectionMatrix, float timer); // timer ��� �������� ������	
	
	public GLESObject(Texture texture) {
		shaderProgramHandler = getShaderProgramInstance().programHandler;
		this.mTexture=texture;
		Matrix.setIdentityM(mObjectMatrix,0);
		this.initializeShaderParam();
	}
	
	
	// �������� �� ���������
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
		// ��������� � �������� ����, ������ �� �������� ������ :)))
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
	
	
	

}
