package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Sprite2D extends GLESObject {
	
	public static ShaderProgram sShaderProgram;
	
	public static final String VERTEX_SHADER_CODE = 
			  "attribute vec4 aPosition;         		   \n" // объявляем входящие данные
			 + "attribute vec2 aTextureCoord;	         		   \n" // объявляем входящие данные
			 + "varying vec2 vTextureCoord;             		   \n" // для передачи во фрагментный шейдер
			 + "uniform vec2 uAnimVector;       		   \n" // вектор на который сдвигаем текстуру			 
			 + "uniform float uFrame;      		   \n" //номер кадра
			 + "uniform mat4 uObjectMatrix;			\n"
			 + "void main() {                    		   \n"
			 //+	" gl_PointSize = 15.0;					\n"
			 +	" gl_Position = uObjectMatrix*aPosition;	\n"			
			 + " vTextureCoord = aTextureCoord+(uAnimVector*uFrame);     \n" 					
		+	"}"	;
		
	
	public static final String FRAGMENT_SHADER_CODE = 
			"precision highp float;"
+			"varying vec2 vTextureCoord;                        \n" +
			"uniform sampler2D uSampler;                 \n"
		+	"void main() {							\n"
		+	" gl_FragColor = texture2D(uSampler,vTextureCoord);	\n"
		+	"}"	;
	
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;
	ShortBuffer verticesIndex;
	
	//shader holder
	int aPositionHolder;
	int aTextureCoordHolder;
	int uSamplerHolder;
	int uAnimVectorHolder;
	int uFrameHolder;
	int uObjectMatrixHandler;
	
	
	// for animation
	private int tekFrame=0;
	private int startFrame=0;
	private int countFrames=0;
	public float[] animationVector=new float[]{0f,0f};
	
	@Override
	public void initializeShaderParam() {
		
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uSampler");
		uAnimVectorHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uAnimVector");
		uFrameHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uFrame");
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		
		if (-1==aPositionHolder || -1==aTextureCoordHolder || -1==uSamplerHolder || -1==uAnimVectorHolder || -1==uFrameHolder || -1==uObjectMatrixHandler) {
			Log.d("MyLogs", "Shader atributs or uniforms not found.");
			Log.d("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uSamplerHolder+","+uAnimVectorHolder+","+uFrameHolder+","+uObjectMatrixHandler);
		}
		else { 
			
		}
	}
	
	
	public Sprite2D(Texture mTexture) {
		 super(mTexture);
						
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
				
				

				// по умолчанию вся текстура на полигон, нужен вектор смещения, вектор определяющий размер текстуры и вектор анимации
		float[] planeTFA = {
						// 1f,1f, 0,1f, 1f,0,0,0
				1f,1f,
				0f,1f,
				1f,0,
				0,0
				};
		
				ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
				planeTBB.order(ByteOrder.nativeOrder());
				textureCoordBuffer = planeTBB.asFloatBuffer();
				textureCoordBuffer.put(planeTFA);
				textureCoordBuffer.position(0);
				
		//зададим индексы, предидущие массивы определяли просто координаты вершин и текстурные координаты, соответствующие вершинам
		// а здесь мы определим порядок следования вершин через индексы	
		
				
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
	
	// устанавливаем текстурные координаты для спрайтовой текструры состоящей их scaleX кадров по горизонтали и scaleY кадров по вертикали
	public void setRelativeTextureBounds(float scaleX,float scaleY,int offsetFrameX,int offsetFrameY) {
		setTextureBounds(new float[] {1/scaleX,1/scaleY},new float[]{offsetFrameX/scaleX,offsetFrameY/scaleY});
	}
	
	//устанавливаем границы текстуры как сумму векторов вектора отступа и вектора текстуры
	public void setTextureBounds(float[] textureVector,	float[] offsetTextureVector) {			
		
		if (textureVector!=null && textureVector.length != 2) {try {
			throw new Exception("texture vector have to be length=2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}}
		
		if (offsetTextureVector!=null && offsetTextureVector.length != 2) {try {
			throw new Exception("offset vector have to be length=2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}}				
		
		float s=textureVector[0];
		float t=textureVector[1];
		
		float sOf=0;				
		float tOf=0;
		
		if (offsetTextureVector!=null) {
			sOf=offsetTextureVector[0];
			tOf=offsetTextureVector[1];
		}
		
		float[] planeTFA = {
					// 1f,1f, 0,1f, 1f,0,0,0
			s+sOf,t+tOf,
			0+sOf,t+tOf,
			s+sOf,0+tOf,
			0+sOf,0+tOf
			};
	
			ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
			planeTBB.order(ByteOrder.nativeOrder());
			textureCoordBuffer = planeTBB.asFloatBuffer();
			textureCoordBuffer.put(planeTFA);
			textureCoordBuffer.position(0);								
	}
	
	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		if (isAnimated()) animationFrameUpdate(timer);// update frame, if is animated Sprite
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 		 
		 GLES20.glUniformMatrix4fv(uObjectMatrixHandler, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		
		// GLES20.GL_TEXTURE_2D - по большому счету это как кисть, нужно активировать текстуру и привязать ее к кисти.
		 GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTexture.index); // активируем текстуру, которой собрались рисовать		 
		 GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		 GLES20.glUniform1i(uSamplerHolder, mTexture.index);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему
		 
		 GLES20.glUniform2f(uAnimVectorHolder,animationVector[0],animationVector[1]);// вектор сдвига анимации в текстурных координатах {1/6,0} где 6 - число кадров в спрайте		 
		 GLES20.glUniform1f(uFrameHolder,tekFrame);// - номер кадра
		 
		 
		 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(aPositionHolder);	
		 
		 GLES20.glVertexAttribPointer(aTextureCoordHolder, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(aTextureCoordHolder);
		 		
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}


	public void animationFrameUpdate(float ctime) {		  		   
	        if(ctime < 0){ctime++;}
	        tekFrame=startFrame+(int)(ctime*(countFrames));	                	                 	  	
	}
	
	public void setAnimation(int mStartFrame,int mCountFrames,float[] mAnimVector) {
		startFrame=mStartFrame;
		countFrames=mCountFrames;
		animationVector=mAnimVector;					
	}
	
	public boolean isAnimated() {
		if (countFrames>0) return true; else return false;
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
