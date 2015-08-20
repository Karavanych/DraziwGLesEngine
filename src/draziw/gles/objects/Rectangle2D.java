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

public class Rectangle2D extends GLESObject {
	
	public static ShaderProgram sShaderProgram;
	
	public static final String VERTEX_SHADER_CODE = 
			  "attribute vec4 aPosition;         		   \n" // объ€вл€ем вход€щие данные
			 + "attribute vec4 aColor;	         		   \n" // объ€вл€ем вход€щие данные
			 + "varying vec4 vColor;             		   \n" // дл€ передачи во фрагментный шейдер			
			 +	"uniform mat4 uObjectMatrix;			\n"
			 + "void main() {                    		   \n"
			 //+	" gl_PointSize = 15.0;					\n"
			 +	" gl_Position = uObjectMatrix*aPosition;	\n"			
			 + " vColor = aColor;     \n" 					
		+	"}"	;
		
	
	public static final String FRAGMENT_SHADER_CODE = 
			"precision highp float;"
		+   "varying vec4 vColor;" // получаем цвет из вертексного шейдера
		+	"void main() {							\n"
		+	" gl_FragColor = vColor;	\n" // задаем цвет текстуры
		+	"}"	;


	private int aPositionHolder;


	private int aColorHolder;


	private int uObjectMatrixHandler;



	private FloatBuffer vertextBuffer;
	private FloatBuffer colorBuffer;
	private ShortBuffer verticesIndex;
	

	public Rectangle2D(Texture texture) {
		super(texture);
		
		// по умолчанию координаты на весь экран, нужно будет реализовать сдвиг и скалирование
		float[] pointVFA = {
				 1f,-1f,0.0f,
				 -1f,-1f,0.0f,
				 1f,1f,0.0f,
				-1f,1f,0.0f
				};
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(pointVFA.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(pointVFA);
				vertextBuffer.position(0); 				
				
				

				//  R,G,B,A
		float[] planeTFA = {
						
				1f,1f,1f,1f,
				1f,1f,1f,1f,
				1f,1f,1f,1f,
				1f,1f,1f,1f				
				};
		
				ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
				planeTBB.order(ByteOrder.nativeOrder());
				colorBuffer = planeTBB.asFloatBuffer();
				colorBuffer.put(planeTFA);
				colorBuffer.position(0);
				
		//зададим индексы, предидущие массивы определ€ли просто координаты вершин и текстурные координаты, соответствующие вершинам
		// а здесь мы определим пор€док следовани€ вершин через индексы	
		
				
		short[] planeISA = {
						2,3,1,
						0,2,1,
				};
		
		ByteBuffer planeIBB = ByteBuffer.allocateDirect(planeISA.length * 2);
		planeIBB.order(ByteOrder.nativeOrder());
		verticesIndex = planeIBB.asShortBuffer();
		verticesIndex.put(planeISA);
		verticesIndex.position(0);
		
		
	}
	

	@Override
	public void initializeShaderParam() {
		
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель дл€ переменной программы aPosition
		aColorHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aColor");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		
		if (-1==aPositionHolder || -1==aColorHolder || -1==uObjectMatrixHandler) {
			Log.d("MyLogs", "Shader Rectangle atributs or uniforms not found.");
			Log.d("MyLogs",""+aPositionHolder+","+aColorHolder+","+uObjectMatrixHandler);
		}
		else { 			
		}
		
	}

	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(uObjectMatrixHandler, 1, false, mObjectMVPMatrix, 0);//передаем кумул€тивную матрицы MVP в шейдер
		 
		 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(aPositionHolder);	
		 
		 GLES20.glVertexAttribPointer(aColorHolder, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
	     GLES20.glEnableVertexAttribArray(aColorHolder);
		 		
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}
	
	@Override
	public ShaderProgram getShaderProgramInstance() {		
		if (sShaderProgram==null) {
			sShaderProgram=new ShaderProgram(VERTEX_SHADER_CODE,FRAGMENT_SHADER_CODE);
		}
		return sShaderProgram;
	}


	public static void reset() {
		sShaderProgram=null;		
	}


}
