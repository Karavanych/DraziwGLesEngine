package draziw.gles.objects;

import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.Texture;
import draziw.gles.game.GameControllers;
import draziw.gles.game.ResourceManager;
import draziw.gles.game.GameControllers.Controller;

public class Player extends Custom3D {
	
	private float[] mRotationMatrix = new float[16];		

	public Player(Texture texture, ResourceManager resources, String modelName) {
		super(texture, resources, modelName);
		
		
		/* ��� ����������� ��� ����������� ���������� ������
		 
		MyMatrix.vec3set(right, 1.0f, 0.0f, 0.0f);
		
		MyMatrix.vec3set(up, 0.0f, 1.0f, 0.0f);
		
		MyMatrix.vec3set(forward, 0.0f, 0.0f, 1.0f);

		mRotationMatrix[0] = right[0];
		mRotationMatrix[4] = right[1];
		mRotationMatrix[8] = right[2];

		mRotationMatrix[1] = up[0];
		mRotationMatrix[5] = up[1];
		mRotationMatrix[9] = up[2];

		mRotationMatrix[2] = forward[0];
		mRotationMatrix[6] = forward[1];
		mRotationMatrix[10] = forward[2];				

		mRotationMatrix[3] = 0.0f;
		mRotationMatrix[7] = 0.0f;
		mRotationMatrix[11] = 0.0f;
		mRotationMatrix[15] = 1.0f;
		
		mRotationMatrix[12] = -(mRotationMatrix[0] * position[0] + mRotationMatrix[4]* position[1] + mRotationMatrix[8] * position[2]);
		mRotationMatrix[13] = -(mRotationMatrix[1] * position[0] + mRotationMatrix[5]* position[1] + mRotationMatrix[9] * position[2]);
		mRotationMatrix[14] = -(mRotationMatrix[2] * position[0] + mRotationMatrix[6]* position[1] + mRotationMatrix[10] * position[2]);
		*/				
		
		Matrix.setIdentityM(mRotationMatrix, 0);
		//Matrix.setIdentityM(mTranslationMatrix, 0);
		
	}
	
	// ����������� �������, �� �������
	// rotateIndependent
	public void rotateI(float angleInDegrees,float x,float y,float z) {				
		
		Matrix.rotateM(mRotationMatrix, 0, angleInDegrees, x, y,z);				
		
	}	
	
	public void setPositionI(float x,float y,float z) {						
		MyMatrix.vec3set(position, x, y, z);						
	}	
	
	public void moveForward(float distance) {
		position[0]-=mRotationMatrix[2]*distance;
		position[1]+=mRotationMatrix[6]*distance;
		position[2]+=mRotationMatrix[10]*distance;	
	}

	public void moveRight(float distance) {		
		position[0]-=mRotationMatrix[0]*distance;
		position[1]+=mRotationMatrix[4]*distance;
		position[2]+=mRotationMatrix[8]*distance;
	}

	public void moveUp(float distance) {		
		position[0]-=mRotationMatrix[1]*distance;
		position[1]+=mRotationMatrix[5]*distance;
		position[2]+=mRotationMatrix[9]*distance;
	}
	
	@Override
	public void draw(float[] viewMatrix, float[] projectionMatrix, float timer) {	
		
		// mObjectMVPMatrix ������������ ������ ��� ��������������� �������
		// ��� �������� ������� ���������
		
		Matrix.setIdentityM(mObjectMVPMatrix,0);
		Matrix.translateM(mObjectMVPMatrix, 0, position[0], position[1], position[2]);		
		
		Matrix.multiplyMM(mObjectMatrix, 0,mObjectMVPMatrix , 0,mRotationMatrix , 0);

		/*Matrix.setIdentityM(mTranslationMatrix,0);
		Matrix.translateM(mTranslationMatrix, 0, position[0], position[1], position[2]);
		
		Matrix.setIdentityM(mObjectMatrix, 0);		
		
		Matrix.multiplyMM(mObjectMatrix, 0,mRotationMatrix , 0,mObjectMatrix , 0);
		Matrix.multiplyMM(mObjectMatrix, 0,mTranslationMatrix , 0,mObjectMatrix , 0);*/
				
						
		super.draw(viewMatrix, projectionMatrix, timer);
	}
	
	public void moveByController(float timer, GameControllers controllers) {
		Controller controllersLeft = controllers
				.getControllerByType(GameControllers.CONTROLLER_LEFT);
		if (controllersLeft.isEnable()) {
			if (Math.abs(controllersLeft.getMovementY()) > 0.02) {
				moveForward(controllersLeft.getMovementY() * timer);
			}

			if (Math.abs(controllersLeft.getMovementX()) > 0.02) {
				moveRight(controllersLeft.getMovementX() * timer);
			}
		}

		Controller controllersRight = controllers
				.getControllerByType(GameControllers.CONTROLLER_RIGHT);

		if (controllersRight.isEnable()) {
			if (Math.abs(controllersRight.getMovementY()) > 0.02) {
				moveUp(-controllersRight.getMovementY() * timer);
			}

			if (Math.abs(controllersRight.getMovementX()) > 0.02) {
				rotateI(controllersRight.getMovementX() * timer*5,0, 1, 0);
			}
		}
	}

}