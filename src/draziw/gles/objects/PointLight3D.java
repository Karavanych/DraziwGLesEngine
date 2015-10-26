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

public class PointLight3D extends GLESObject {
	
	/*public static ShaderProgram sShaderProgram;
	
	public static final String VERTEX_SHADER_CODE = 
			  "attribute vec4 aPosition;         		   \n" // объ€вл€ем вход€щие данные
			 + "attribute vec4 aColor;	         		   \n" // объ€вл€ем вход€щие данные
			 + "varying vec4 vColor;             		   \n" // дл€ передачи во фрагментный шейдер			
			 + "uniform mat4 uObjectMatrix;			\n"
			 + "void main() {                    		   \n"
			 +	" gl_PointSize = 5.0;					\n"
			 +	" gl_Position = uObjectMatrix*aPosition;	\n"			
			 + " vColor = aColor;     \n" 					
		+	"}"	;
		
	
	public static final String FRAGMENT_SHADER_CODE = 
			"precision highp float;"
		+   "varying vec4 vColor;" // получаем цвет из вертексного шейдера
		+	"void main() {							\n"
		+	" gl_FragColor = vColor;	\n" // задаем цвет текстуры
		+	"}"	;*/


	private int aPositionHolder;


	private int aColorHolder;


	private int uObjectMatrixHandler;



	private FloatBuffer vertextBuffer;
	private FloatBuffer colorBuffer;

	private float[] modelPosition;
	
	private float luminance=0.05f;
	

	public PointLight3D(Texture texture,ShaderManager shaders) {
		super(texture,shaders.getShader("point"));
		
		// по умолчанию координаты света 0,0,0, 1f - это дл€ работы с 4x ћатрицами
		modelPosition = new float[]{0f,0f,0f,1f}; 
		 
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(modelPosition.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(modelPosition);
				vertextBuffer.position(0); 				
				
				

				//  R,G,B,A
		float[] planeTFA = {
						
				1f,1f,1f,1f
					
				};
		
				ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
				planeTBB.order(ByteOrder.nativeOrder());
				colorBuffer = planeTBB.asFloatBuffer();
				colorBuffer.put(planeTFA);
				colorBuffer.position(0);
	
				
	}
	

	@Override
	public void initializeShaderParam() {
		
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель дл€ переменной программы aPosition
		aColorHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aColor");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		
		if (-1==aPositionHolder || -1==aColorHolder || -1==uObjectMatrixHandler) {
			Log.e("MyLogs", "Shader Point3D atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aColorHolder+","+uObjectMatrixHandler);
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
		 		
	     GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
		
	}
	
	public float[] getMVPosition(float[] viewMatrix) {
		
		//mObjectMVPMatrix здесь mpv матрица использована как буфер дл€ преобразовани€,
		// она же уже всеравно существует, 
		// в ней мы положим результат преобразовани€ сначала - координат позиций, на матрицу объекта
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
