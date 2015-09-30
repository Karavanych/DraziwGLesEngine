package draziw.gles.game;

import draziw.gles.engine.MyMatrix;
import draziw.gles.game.GameControllers.Controller;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.Player;
import android.opengl.Matrix;
import android.util.Log;

public class GLESCamera {

	// зададим матрицы
	private float[] mProjectionMatrix = new float[16]; // проекци€
	private float[] mOrthoGUI = new float[16]; // проекци€ дл€ gui
	private float[] viewGUI = new float[16];

	// напр€мую обращатьс€ нельз€, потому что результирующа€ матрица получаетс€,
	// только в
	// медоте getViewMatrix
	private float[] viewMatrix = new float[16]; // вид
	
	//private float[] rotationMatrix = new float[16]; // по идее нужно пересмотреть forward,up, right на rotation matrix

	public float[] position = new float[3];

	/*public float[] forward = new float[3];

	public float[] up = new float[3];

	public float[] right = new float[3];*/
		
	int width;
	int height;	
	
	public float[] glScreenSize=new float[2];
	
	private float toPlayerDistanceZ=2.5f;
	private float toPlayerDistanceU=2f;

	public GLESCamera(int width, int height) {
		this.width = width;
		this.height = height;

		/*
		 * uAxis[0] = 0.0f; uAxis[1] = 1.0f; uAxis[2] = 0.0f; vAxis[0] = 0.0f;
		 * vAxis[1] = 0.0f; vAxis[2] = 1.0f; nAxis[0] = 1.0f; nAxis[1] = 0.0f;
		 * nAxis[2] = 0.0f;
		 */

		MyMatrix.vec3set(position, 0.0f, 0.0f, 15.0f);
		
		Matrix.setIdentityM(viewMatrix, 0);
		
/*
		MyMatrix.vec3set(forward, 0.0f, 0.0f, 1.0f);

		MyMatrix.vec3set(up, 0.0f, 1.0f, 0.0f);

		MyMatrix.vec3set(right, 1.0f, 0.0f, 0.0f);
*/
		projectionInitialization();
	}

	private void projectionInitialization() {

		/*
		 * // ѕоложение глаза, точки наблюдени€ в пространстве. final float eyeX
		 * = 0.0f; final float eyeY = 0.0f; final float eyeZ = 15.0f;
		 * 
		 * // Ќа какое рассто€ние мы можем видеть вперед. ќграничивающа€
		 * плоскость // обзора. final float lookX = 0.0f; final float lookY =
		 * 0.0f; final float lookZ = -40.0f;
		 * 
		 * // ”станавливаем вектор. ѕоложение где наша голова находилась бы если
		 * бы // мы держали камеру. final float upX = 0.0f; final float upY =
		 * 1.0f; final float upZ = 0.0f;
		 * 
		 * Matrix.setLookAtM(oldViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY,
		 * lookZ, upX, upY, upZ);
		 */

		/*
		 * float aspectRatio = width > height ? (float) width / (float) height :
		 * (float) height / (float) width;
		 * 
		 * float zNear = 1f; float zFar = 50; // float fov = 0.75f; // 0.2 to
		 * 1.0 // float size = (float) (zNear * Math.tan(fov / 2)); float size =
		 * 1;
		 * 
		 * if (width > height) { Matrix.frustumM(mProjectionMatrix, 0, -size *
		 * aspectRatio, size * aspectRatio, -size, size, zNear, zFar);
		 * screenSize = new float[] { size * aspectRatio, size }; } else {
		 * Matrix.frustumM(mProjectionMatrix, 0, -size, size, -size *
		 * aspectRatio, size * aspectRatio, zNear, zFar); screenSize = new
		 * float[] { size, size * aspectRatio }; }
		 */

		/*final float ratio = (float) width / height;
		
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 40.0f;*/
		
		
		//final float ratio = (float) width / height;
		final float minDispRatio=(1.0f/Math.min(width,height));
		
		final float left = -width*minDispRatio;
		final float right = width*minDispRatio;
		final float bottom = -height*minDispRatio;
		final float top = height*minDispRatio;
		final float near = 1.0f;
		final float far = 500.0f;
		
		glScreenSize=new float[]{right, top};

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near,
				far);
		
		
		
		Matrix.orthoM(mOrthoGUI, 0, left,right, bottom, top, near, far);
				
		//Matrix.multiplyMM(mOrthoGUI, 0, mOrthoGUI, 0, getViewMatrix(), 0);
		
		viewGUI=getViewMatrix().clone();

		return;
	}
	
	public float[] getGUIMatrix() {
		return mOrthoGUI;
	}
	
	public float[] getGUIView() {
		return viewGUI;
	}

	public float[] getViewMatrix() {

		// uvn camera - u - right, v - up, n - forward				

		/*viewMatrix[0] = rotationMatrix[0];//right[0];
		viewMatrix[4] = rotationMatrix[4];//right[1];
		viewMatrix[8] = rotationMatrix[8];//right[2];

		viewMatrix[1] = rotationMatrix[1];//up[0];
		viewMatrix[5] = rotationMatrix[5];//up[1];
		viewMatrix[9] = rotationMatrix[9];//up[2];

		viewMatrix[2] = rotationMatrix[2];//forward[0];
		viewMatrix[6] = rotationMatrix[6];//forward[1];
		viewMatrix[10] = rotationMatrix[10];//forward[2];

		viewMatrix[3] = 0.0f;
		viewMatrix[7] = 0.0f;
		viewMatrix[11] = 0.0f;
		viewMatrix[15] = 1.0f;*/
		
		viewMatrix[12] = -(viewMatrix[0] * position[0] + viewMatrix[4]
					* position[1] + viewMatrix[8] * position[2]);
		viewMatrix[13] = -(viewMatrix[1] * position[0] + viewMatrix[5]
					* position[1] + viewMatrix[9] * position[2]);
		viewMatrix[14] = -(viewMatrix[2] * position[0] + viewMatrix[6]
					* position[1] + viewMatrix[10] * position[2]);		

		return viewMatrix;

	}

	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
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
				rotate(controllersRight.getMovementX() * timer*5,0, 1, 0);
			}
		}
	}

	public void moveForward(float distance) {
		position[0]+=viewMatrix[2]*distance;
		position[1]+=viewMatrix[6]*distance;
		position[2]+=viewMatrix[10]*distance;
		//MyMatrix.vec3add(position, forward, distance);
	}

	public void moveRight(float distance) {
		position[0]+=viewMatrix[0]*distance;
		position[1]+=viewMatrix[4]*distance;
		position[2]+=viewMatrix[8]*distance;
		//MyMatrix.vec3add(position, right, distance);
	}

	public void moveUp(float distance) {
		position[0]+=viewMatrix[1]*distance;
		position[1]+=viewMatrix[5]*distance;
		position[2]+=viewMatrix[9]*distance;
		//MyMatrix.vec3add(position, up, distance);
	}

	public void rotate(float angle,float x, float y, float z) { // x - right, y - up , z - forward
		// получим viewMatrix с позицией 0,0,0
		
		/*rotationMatrix[0] = right[0];
		rotationMatrix[4] = right[1];
		rotationMatrix[8] = right[2];

		rotationMatrix[1] = up[0];
		rotationMatrix[5] = up[1];
		rotationMatrix[9] = up[2];

		rotationMatrix[2] = forward[0];
		rotationMatrix[6] = forward[1];
		rotationMatrix[10] = forward[2];

		rotationMatrix[3] = 0.0f;
		rotationMatrix[7] = 0.0f;
		rotationMatrix[11] = 0.0f;
		rotationMatrix[15] = 1.0f;
		
		rotationMatrix[12] = 0;
		rotationMatrix[13] = 0;
		rotationMatrix[14] = 0;	*/	
		
		viewMatrix[12]=0;
		viewMatrix[13]=0;
		viewMatrix[14]=0;
		
		Matrix.rotateM(viewMatrix, 0, angle, x, y, z);

		/*// восстановим вектора осей из повернутой матрицы
		right[0] = rotationMatrix[0];
		right[1] = rotationMatrix[4];
		right[2] = rotationMatrix[8];

		up[0] = rotationMatrix[1];
		up[1] = rotationMatrix[5];
		up[2] = rotationMatrix[9];

		forward[0] = rotationMatrix[2];
		forward[1] = rotationMatrix[6];
		forward[2] = rotationMatrix[10];*/

	}
	
	public float[] getGlScreenSize() {
		return glScreenSize;
	}
	
	public void setPositionByGLESObject(GLESObject mObj) {
		// optimal camera 0, 7 , 1 
		//player position = 0.0,4.0,-2.0
		
		position[0]=mObj.position[0];
		position[1]=mObj.position[1]+3;
		position[2]=mObj.position[2]+3;		
	}
	
	
	public void setByPlayerTranslateRotation(Player mPlayer) {
		float[] mPlRt = mPlayer.getRotation();		
		
		viewMatrix[0] = mPlRt[0];//right[0];
		viewMatrix[4] = mPlRt[1];//right[1];
		viewMatrix[8] = mPlRt[2];//right[2];

		viewMatrix[1] = mPlRt[4];//up[0];
		viewMatrix[5] = mPlRt[5];//up[1];
		viewMatrix[9] = mPlRt[6];//up[2];

		viewMatrix[2] = mPlRt[8];//forward[0];
		viewMatrix[6] = mPlRt[9];//forward[1];
		viewMatrix[10] = mPlRt[10];//forward[2];
		
		
		position[0]=mPlayer.position[0]+mPlRt[8]*toPlayerDistanceZ+mPlRt[4]*toPlayerDistanceU;
		position[1]=mPlayer.position[1]+mPlRt[9]*toPlayerDistanceZ+mPlRt[5]*toPlayerDistanceU;
		position[2]=mPlayer.position[2]+mPlRt[10]*toPlayerDistanceZ+mPlRt[6]*toPlayerDistanceU;		
				
	}
	
	public void lookAtGLESObject(GLESObject mObj) {
		
	}

}
