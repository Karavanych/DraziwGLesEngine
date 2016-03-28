package draziw.gles.objects;


import android.opengl.Matrix;
import draziw.gles.controllers.ControllerAccelerometer;
import draziw.gles.controllers.GameControllers;
import draziw.gles.controllers.Controller;
import draziw.gles.engine.Texture;
import draziw.gles.game.ResourceManager;
import draziw.gles.materials.Material;
import draziw.gles.math.MyMatrix;
import draziw.gles.math.Quaternion;
import draziw.simple.physics.Collision;
import draziw.gles.engine.ShaderProgram;

public class Player extends Custom3D {
	
	public Collision collision;	
	
	public float[] mRotationMatrix = new float[16];

	private float maxDistance=9999f;
	
	protected float[] vec3temp=new float[3];

	public Player(Texture texture,Material material, ResourceManager resources, String modelName) {
		super(texture,material,resources, modelName);		
		
		/* Для напоминания как соотносятся компоненты матриц
		 
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
	
	public Player(Texture texture,Texture normalMap,Material material, ResourceManager resources, String modelName) {
		super(texture,normalMap,material,resources, modelName);	
		Matrix.setIdentityM(mRotationMatrix, 0);
	}
	
	
	public void setMaxMovement(float mDistance) {
		this.maxDistance=mDistance;
		
	}
	
	// независимый поворот, от позиции
	// rotateIndependent
	public void rotateI(float angleInDegrees,float x,float y,float z) {				
		
		Matrix.rotateM(mRotationMatrix, 0, angleInDegrees, x, y,z);
		
	}	
	
	public void setPositionI(float x,float y,float z) {						
		MyMatrix.vec3set(position, x, y, z);						
	}	
	
	public void moveForward(float distance) {
		
		if (distance>0) distance=Math.min(distance, maxDistance); else distance=Math.max(distance, -maxDistance);
		
		position[0]+=mRotationMatrix[8]*distance;
		position[1]+=mRotationMatrix[9]*distance;
		position[2]+=mRotationMatrix[10]*distance;	
	}

	public void moveRight(float distance) {	
		
		if (distance>0) distance=Math.min(distance, maxDistance); else distance=Math.max(distance, -maxDistance);
		
		position[0]+=mRotationMatrix[0]*distance;
		position[1]+=mRotationMatrix[1]*distance;
		position[2]+=mRotationMatrix[2]*distance;
	}

	public void moveUp(float distance) {
		
		if (distance>0) distance=Math.min(distance, maxDistance); else distance=Math.max(distance, -maxDistance);
		
		position[0]+=mRotationMatrix[4]*distance;
		position[1]+=mRotationMatrix[5]*distance;
		position[2]+=mRotationMatrix[6]*distance;
	}	
	
	public float[] actualizeObjectMatrix() {
		Matrix.setIdentityM(mObjectMVPMatrix,0);
		Matrix.translateM(mObjectMVPMatrix, 0, position[0], position[1], position[2]);		
		
		Matrix.multiplyMM(mObjectMatrix, 0,mObjectMVPMatrix , 0,mRotationMatrix , 0);
		
		return mObjectMatrix;
	}
	
	@Override
	public void draw(float[] viewMatrix, float[] projectionMatrix, float timer) {	
		
		// mObjectMVPMatrix используется просто как вспомогательная матрица
		// для создания матрицы положения
		//actualizeObjectMatrix();
		actualizeObjectMatrix();

		/*Matrix.setIdentityM(mTranslationMatrix,0);
		Matrix.translateM(mTranslationMatrix, 0, position[0], position[1], position[2]);
		
		Matrix.setIdentityM(mObjectMatrix, 0);		
		
		Matrix.multiplyMM(mObjectMatrix, 0,mRotationMatrix , 0,mObjectMatrix , 0);
		Matrix.multiplyMM(mObjectMatrix, 0,mTranslationMatrix , 0,mObjectMatrix , 0);*/
				
						
		super.draw(viewMatrix, projectionMatrix, timer);
	}
	
	public float[] getRotation() {
		return mRotationMatrix;
	}	
	
	public void moveByController(float timer, GameControllers controllers,int controlType) {
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
				rotateI(-controllersRight.getMovementX() * timer*5,0, 1, 0);
			}
		}
		
	}
	
	public void moveByAccelerometr(float timer,ControllerAccelerometer controller) {
		if (controller.isEnable()) {			
			controller.getValues(vec3temp);
			/*if (vec3temp[0]>0) {
				vec3temp[0]=Math.max(vec3temp[0], vec3temp[2]);
			} else {
				vec3temp[0]=Math.min(vec3temp[0], vec3temp[2]);
			}*/
		
			moveForward(vec3temp[0] * timer);
			
			moveRight(vec3temp[1] * timer);
		}
	}
	
	
	
	public void enableCollision(Collision mCollision) {
		if (mCollision!=null) {
			this.collision=mCollision;
		} else {
			this.collision=new Collision();
		}
	}
	
	public void clearCollisions() {
		collision.clearCollisions();
	}

}
