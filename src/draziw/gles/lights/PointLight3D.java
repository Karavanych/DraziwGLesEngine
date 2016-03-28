package draziw.gles.lights;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;

public class PointLight3D extends GLESLight {

	private FloatBuffer vertextBuffer;

	private float[] modelPosition;
	

	public PointLight3D() {
		this(null,null);
	}
	public PointLight3D(Texture texture,Material material) {
		super(texture,material);
		
		// по умолчанию координаты света 0,0,0, 1f - это для работы с 4x Матрицами
		modelPosition = new float[]{0f,0f,0f,1f}; 
		 
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(modelPosition.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(modelPosition);
				vertextBuffer.position(0); 				
				
	}
	

	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);	
		 		
	     GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
		
	}
	
	@Override
	public float[] getMV(float[] viewMatrix) {
		
		//mObjectMVPMatrix здесь mpv матрица использована как буфер для преобразования,
		// она же уже всеравно существует, 
		// в ней мы положим результат преобразования сначала - координат позиций, на матрицу объекта
		// потом добавили вид.
		
		Matrix.multiplyMV(mObjectMVPMatrix, 0, mObjectMatrix, 0, modelPosition, 0);
		Matrix.multiplyMV(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMVPMatrix, 0);		
		
		return mObjectMVPMatrix;
	}


	@Override
	public int getType() {
		return POINT_LIGHT;
	}



}
