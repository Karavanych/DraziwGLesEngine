package draziw.gles.lights;

import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.math.MyMatrix;

public class DirectionLight3D extends GLESLight {


	private float[] lightVector;
	private float[] tmpVec=new float[]{0,0,0,1f};
	

	public DirectionLight3D(float x,float y,float z) {
		super(null,null);
		
		lightVector=new float[]{x,y,z,1.0f};		
		MyMatrix.normalize(lightVector);
		//Log.d("MyLogs", " dirLight="+MyMatrix.MatrixToString(lightVector));
						 				
	}
	

	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {				
		
	}
	
	@Override
	public float[] getMV(float[] viewMatrix) {
		
		//Matrix.multiplyMV(tmpVec, 0, viewMatrix, 0, lightVector, 0);		
		
		return lightVector;
	}


	@Override
	public int getType() {
		return DIRECTION_LIGHT;
	}

}
