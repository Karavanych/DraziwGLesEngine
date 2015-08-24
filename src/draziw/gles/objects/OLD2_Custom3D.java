package draziw.gles.objects;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;

public class OLD2_Custom3D extends GLESObject {
	
	public static ShaderProgram sShaderProgram;
	
	// повертексное освещение
	public static final String VERTEX_SHADER_CODE_VERTEX_LIGHT = 
			  "attribute vec4 aPosition;         		   \n" // объявляем входящие данные
			 + "attribute vec2 aTextureCoord;	           \n" // объявляем входящие данные
			 + "attribute vec3 aNormal;                   \n"		// Per-vertex normal information we will pass in.
			 + "uniform vec3 uLightPos;                   \n"	
			 + "varying vec2 vTextureCoord;            	   \n" // для передачи во фрагментный шейдер
			 + "varying float vDiffuse;"
			 + "uniform float uLuminance;"
			 + "uniform mat4 uObjectMatrix;		           \n"
			 + "uniform mat4 uMVMatrix;                    \n"
			 + "void main() { " +
			 "                   		   \n"
			 	// Для реализации освещения
				// Трансформируем вертексы в позицию вида
			 + "   vec3 modelViewVertex = vec3(uMVMatrix * aPosition);          \n"
			 	// Трансформируем нормали в позицию вида
			 + "   vec3 modelViewNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));     \n"
			 	// Расчитываем дисстанцию для затухания освещения
			 + "   float distance = length(uLightPos - modelViewVertex);             \n"
			 	// Получим нормализованный вектор направления  от источника света к вертексам
			 + "   vec3 lightVector = normalize(uLightPos - modelViewVertex);        \n"
			 	// Расчитаем скаларное произведение (dot product) вектора света и вектора нормали
   			    // Максимальная яркость будет в случае если эти ветора однонаправленные , 0.1 - минимальная освещенность
			 + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n" 	  		  													  
			 	// Рассчитаем угасание в зависимости от дистанции
			 + "   vDiffuse = diffuse * (1.0 / (0.9 + (uLuminance * distance * distance)));  \n"
			 //	+ "   vDiffuse = diffuse;  \n"		 
			 +	" gl_Position = uObjectMatrix*aPosition;	\n"			
			 +  " vTextureCoord = aTextureCoord;     \n" 					
		+	"}"	;
		
	
	public static final String FRAGMENT_SHADER_CODE_VERTEX_LIGHT = 
			"precision highp float;"
		+	"varying vec2 vTextureCoord;                        \n" 
		+   "varying float vDiffuse;"
		+	"uniform sampler2D uSampler;                 \n"
		+	"void main() {							\n"
		+	" gl_FragColor = texture2D(uSampler,vTextureCoord)*vDiffuse;	\n"
		+	"}"	;
	
	
	
	//попиксельное освещение
	public static final String VERTEX_SHADER_CODE_PIXEL_LIGHT = 
			   "attribute vec4 aPosition;         		   \n" // объявляем входящие данные
			 + "attribute vec2 aTextureCoord;	           \n" // объявляем входящие данные
			 + "attribute vec3 aNormal;                    \n"		// Per-vertex normal information we will pass in.
			
			 + "varying vec2 vTextureCoord;            	   \n" // для передачи во фрагментный шейдер
			 + "varying vec3 vPosition;                   \n"
			 + "varying vec3 vNormal;        			   \n"
			
			 + "uniform mat4 uObjectMatrix;		           \n"
			 + "uniform mat4 uMVMatrix;                    \n"
			 + "void main() { " +
			 "                   		   \n"
				// Transform the vertex into eye space.
			 + "   vPosition = vec3(uMVMatrix * aPosition);             \n"
				// Pass through the color.
			 + "   vTextureCoord = aTextureCoord;                         \n"
				// Transform the normal's orientation into eye space.
			 + "   vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));      \n"
				// gl_Position is a special variable used to store the final position.
				// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
			 + "   gl_Position = uObjectMatrix * aPosition;              \n"      		  
			 + "} ";
		
	
	public static final String FRAGMENT_SHADER_CODE_PIXEL_LIGHT = 
	
		  "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a 
			// precision in the fragment shader.
		+ "uniform vec3 uLightPos;       \n"	    // The position of the light in eye space.
		+ "uniform float uLuminance;"
		
		+ "varying vec3 vPosition;		\n"		// Interpolated position for this fragment.
		+ "varying vec2 vTextureCoord;          \n"		// This is the color from the vertex shader interpolated across the 			
		+ "varying vec3 vNormal;         \n"		// Interpolated normal for this fragment.
		
		+ "uniform sampler2D uSampler;                 \n"
		
		// The entry point for our fragment shader.
		+ "void main()                    \n"		
		+ "{                              \n"
		// Will be used for attenuation.
		+ "   float distance = length(uLightPos - vPosition);                          \n"
		// Get a lighting direction vector from the light to the vertex.
		+ "   vec3 lightVector = normalize(uLightPos - vPosition);                     \n" 	
		// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
		// pointing in the same direction then it will get max illumination.
		+ "   float diffuse = max(dot(vNormal, lightVector), 0.1);                     \n" 	  		  													  
		// Add attenuation. 
		+ "   diffuse = diffuse * (1.0 / (1.0 + (uLuminance * distance * distance)));  \n"
		// Multiply the color by the diffuse illumination level to get final output color.
		+ "   gl_FragColor = texture2D(uSampler,vTextureCoord)*diffuse;               \n"		
		+ "}                                                                           \n";

	
	//shader holder
	private int aPositionHolder;
	private int aNormalHandler;
	private int aTextureCoordHolder;
	private int uObjectMatrixHandler;
	private int uMVMatrixHandler;
	private int uSamplerHolder;
	private int uLightPosHandler;
	private int uLuminance;



	private int vntBufferHandler;
	private int vntStride=8*4;

	private int indexCount;	
	
	
	//временные
	float[] mMVMatrix = new float[16];
	
	PointLight3D lightObject;

	

	public OLD2_Custom3D(Texture texture,Context context,String modelName) {
		super(texture);		
		
		FloatBuffer vntBuffer; // vertex+ normals + texture Buffer, stride 8*4 (8*float)
						
		
		AssetManager assManager = context.getAssets();
		InputStream is = null;
		ByteBuffer pointVBB = null;
		try {
		        is = assManager.open(modelName+".vrt");
		        pointVBB = readToByteBuffer(is);
		        
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		
				
		vntBuffer=pointVBB.asFloatBuffer();		
		vntBuffer.position(0);
		
		
		indexCount=(int) (vntBuffer.remaining()*4/vntStride);	
		
		
		// для тестов сгенерируем буферы здесь
		final int buffers[] = new int[1];
		GLES20.glGenBuffers(1, buffers, 0);
		
		vntBufferHandler=buffers[0];
		
		// Bind to the buffer. Future commands will affect this buffer specifically.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		 
		// Transfer data from client memory to the buffer.
		// We can release the client memory after this call.
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vntBuffer.capacity() * 4,
				vntBuffer, GLES20.GL_STATIC_DRAW);
		
		// IMPORTANT: Unbind from the buffer when we're done with it.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		// clear buffer from appmemory		
		vntBuffer.limit(0);
		vntBuffer = null;
		
	}

	@Override
	public void initializeShaderParam() {
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aNormalHandler = GLES20.glGetAttribLocation(shaderProgramHandler,"aNormal");
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		uMVMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler,"uMVMatrix");
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uSampler");
		uLightPosHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "uLightPos");
		uLuminance = GLES20.glGetUniformLocation(shaderProgramHandler, "uLuminance");
		
		if (-1==aPositionHolder || -1==aTextureCoordHolder || -1==uObjectMatrixHandler || -1==uSamplerHolder ||
				-1==aNormalHandler || -1==uMVMatrixHandler || -1==uLightPosHandler || -1==uLuminance) {
			Log.d("MyLogs", "Shader Custom3D atributs or uniforms not found.");
			Log.d("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uObjectMatrixHandler+","+uSamplerHolder+","+uMVMatrixHandler+","+aNormalHandler+","+uLightPosHandler+","+uLuminance);
		}
		else { 
			
		}
		
	}

	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer)  {
		 // задаем позицию источника света    
		
		if (lightObject!=null) {
			draw(viewMatrix,projectionMatrix,timer,lightObject.getMVPosition(viewMatrix),lightObject.getLuminance());
		} else {
			draw(viewMatrix,projectionMatrix,timer,new float[]{0,0,0},.05f);
		}
	}
	
	
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer,float[] lightPos,float luminance) {		     	                       

		 GLES20.glUniform3f(uLightPosHandler, lightPos[0], lightPos[1], lightPos[2]);
		 GLES20.glUniform1f(uLuminance,luminance);
		
		 
		 Matrix.setIdentityM(mMVMatrix,0);		
		 
		 Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(uMVMatrixHandler, 1, false, mMVMatrix, 0);//передаем кумулятивную матрицы MV в шейдер
		 
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(uObjectMatrixHandler, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 mTexture.use(uSamplerHolder);
		 //GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTexture.index); // активируем текстуру, которой собрались рисовать		 
		 //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		 //GLES20.glUniform1i(uSamplerHolder, mTexture.index);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему
		 
		 GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vntBufferHandler);
		 GLES20.glEnableVertexAttribArray(aPositionHolder);
		 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, vntStride, 0);
		 
		 
		 GLES20.glEnableVertexAttribArray(aNormalHandler);
		 GLES20.glVertexAttribPointer(aNormalHandler, 3, GLES20.GL_FLOAT, false, vntStride,12); // 12 - offset 3*4float
		 
		 
		 GLES20.glEnableVertexAttribArray(aTextureCoordHolder);
		 GLES20.glVertexAttribPointer(aTextureCoordHolder, 2, GLES20.GL_FLOAT, false, vntStride,24); // 24 - offset (3+3)*4float
		 		
		 		
	     //GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
	     GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);	 
	     
	     
	    // Если буфер неотключить - то его начинают использовать другие объекты - например cubeMap
		// Надо подумать куда это вставлять
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	     
		
	}

	@Override
	public ShaderProgram getShaderProgramInstance() {		
		if (sShaderProgram==null) {
			//sShaderProgram=new ShaderProgram(VERTEX_SHADER_CODE_VERTEX_LIGHT,FRAGMENT_SHADER_CODE_VERTEX_LIGHT);
			sShaderProgram=new ShaderProgram(VERTEX_SHADER_CODE_PIXEL_LIGHT,FRAGMENT_SHADER_CODE_PIXEL_LIGHT);
		}
		return sShaderProgram;
	}

	public static void reset() {
		sShaderProgram=null;		
	}
	
	public ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
		int bufferSize = 0x20000;
	    byte[] buffer = new byte[bufferSize];
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
	    int read;
	    while (true) {
	      read = inStream.read(buffer);
	      if (read == -1)
	        break;
	      outStream.write(buffer, 0, read);
	    }	    	    
	    

	    ByteBuffer byteData=ByteBuffer.allocateDirect(outStream.size());
	    byteData.order(ByteOrder.nativeOrder());
	    byteData.put(outStream.toByteArray());
	    //ByteBuffer byteData = ByteBuffer.wrap(outStream.toByteArray());
	    byteData.position(0);	   	 	  
	    
	    return byteData;
	  }
	
	public void setLight(PointLight3D mLight) {
		lightObject=mLight;
	}

}
