package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.engine.ShaderManager;
import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;

public class PointLight3D extends GLESObject {

	private FloatBuffer vertextBuffer;
	private FloatBuffer colorBuffer;

	private float[] modelPosition;
	
	private float luminance=0.05f;
	

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
	

/*	@Override
	public void initializeShaderParam() {
		
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aColorHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aColor");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		
		if (-1==aPositionHolder || -1==aColorHolder || -1==uObjectMatrixHandler) {
			Log.e("MyLogs", "Shader Point3D atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aColorHolder+","+uObjectMatrixHandler);
		}
		else { 			
		}
		
	}*/

	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);	
		 		
	     GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
		
	}
	
	public float[] getMVPosition(float[] viewMatrix) {
		
		//mObjectMVPMatrix здесь mpv матрица использована как буфер для преобразования,
		// она же уже всеравно существует, 
		// в ней мы положим результат преобразования сначала - координат позиций, на матрицу объекта
		// потом добавили вид.
		
		Matrix.multiplyMV(mObjectMVPMatrix, 0, mObjectMatrix, 0, modelPosition, 0);
		Matrix.multiplyMV(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMVPMatrix, 0);		
		
		return mObjectMVPMatrix;
	}


	public float getLuminance() {		
		return luminance;
	}

	public void setLuminance(float luminance) {
		this.luminance=luminance;		
	}

}
