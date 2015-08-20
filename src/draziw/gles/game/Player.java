package draziw.gles.game;

import android.opengl.Matrix;
import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.Texture;
import draziw.gles.objects.Custom3D;

public class Player extends Custom3D {	

	public Player(Texture texture, ResourceManager resources, String modelName) {
		super(texture, resources, modelName);
		
	}
	
	// независимый поворот, от позиции
	// rotateIndependent
	public void rotateI(float angleInDegrees,float x,float y,float z) {
		
		// clear position, then update, then restore position
		mObjectMatrix[12] = 0;
		mObjectMatrix[13] = 0;
		mObjectMatrix[14] = 0;
		
		Matrix.rotateM(mObjectMatrix, 0, angleInDegrees, x, y,z);
		
		updatePosition();
	}
	
	private void updatePosition() {
		mObjectMatrix[12] = (mObjectMatrix[0] * position[0] + mObjectMatrix[4]
				* position[1] + mObjectMatrix[8] * position[2]);
		mObjectMatrix[13] = (mObjectMatrix[1] * position[0] + mObjectMatrix[5]
				* position[1] + mObjectMatrix[9] * position[2]);
		mObjectMatrix[14] = (mObjectMatrix[2] * position[0] + mObjectMatrix[6]
				* position[1] + mObjectMatrix[10] * position[2]);			
	}
	
	public void setPosition(float x,float y,float z) {
		
		//Matrix.translateM(mObjectMatrix, 0, -position[0], -position[1], -position[2]);
		
		MyMatrix.vec3set(position, x, y, z);
		
		//Matrix.translateM(mObjectMatrix, 0, position[0], position[1], position[2]);
		
		updatePosition();
		
	}	

}
