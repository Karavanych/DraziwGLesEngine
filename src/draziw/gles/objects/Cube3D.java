package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;

public class Cube3D extends GLESObject {

	private int aPositionHolder;


	private int aColorHolder;


	private int uObjectMatrixHandler;



	private FloatBuffer vertextBuffer;
	private FloatBuffer colorBuffer;
	private ShortBuffer verticesIndex;

	private int indexCount;
	

	public Cube3D(Texture texture,ShaderProgram shader) {
		super(texture,shader);
		
		// по умолчанию координаты на весь экран, нужно будет реализовать сдвиг и скалирование
		float[] pointVFA = {
				 -1f,-1f,-1f,
				 -1f,1f,-1f,
				 1f,1f,-1f,
				 1f,-1f,-1f, // forward
				 -1f,-1f,1f,
				 -1f,1f,1f,
				 1f,1f,1f,
				 1f,-1f,1f, // back
				 
				};
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(pointVFA.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(pointVFA);
				vertextBuffer.position(0); 				
				
				

				//  R,G,B,A
		float[] planeTFA = {
						
				1f,1f,1f,1f,
				0f,1f,1f,1f,
				1f,0f,1f,1f,
				1f,1f,0f,1f,
				0.5f,0.5f,1f,1f,
				1f,0.5f,0.5f,1f,
				0.5f,1f,0.5f,1f,
				0.5f,0f,1f,1f
				};
		
				ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
				planeTBB.order(ByteOrder.nativeOrder());
				colorBuffer = planeTBB.asFloatBuffer();
				colorBuffer.put(planeTFA);
				colorBuffer.position(0);
				
		//зададим индексы, предидущие массивы определяли просто координаты вершин и текстурные координаты, соответствующие вершинам
		// а здесь мы определим порядок следования вершин через индексы	
		
				
		short[] planeISA = {
						0,1,3,
						3,1,2,//front
						3,2,7,
						7,2,6,//right
						4,7,6,
						6,5,4,//back
						5,1,4,
						4,1,0, //left
						0,3,4,
						4,3,7, // bottom
						1,5,6,
						6,2,1 // top
						
				};
		indexCount=planeISA.length;
		ByteBuffer planeIBB = ByteBuffer.allocateDirect(planeISA.length * 2);
		planeIBB.order(ByteOrder.nativeOrder());
		verticesIndex = planeIBB.asShortBuffer();
		verticesIndex.put(planeISA);
		verticesIndex.position(0);		
		
	}

	@Override
	public void initializeShaderParam() {
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aColorHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aColor");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		
		if (-1==aPositionHolder || -1==aColorHolder || -1==uObjectMatrixHandler) {
			Log.e("MyLogs", "Shader Rectangle atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aColorHolder+","+uObjectMatrixHandler);
		}
		else { 
			
		}
		
	}

	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(uObjectMatrixHandler, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(aPositionHolder);	
		 
		 GLES20.glVertexAttribPointer(aColorHolder, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
	     GLES20.glEnableVertexAttribArray(aColorHolder);
		 		
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}

}
