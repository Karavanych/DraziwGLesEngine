package draziw.gles.objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import draziw.gles.game.ResourceManager;
import draziw.gles.game.GameControllers.Controller;

public class ControllerView extends GLESObject {
	
	public static ShaderProgram sShaderProgram;
	
	// повертексное освещение
	public static final String VERTEX_SHADER_CODE = 
			   "attribute vec4 aPosition;         		   \n" // объ€вл€ем вход€щие данные
			 + "attribute vec2 aTextureCoord;	           \n" // объ€вл€ем вход€щие данные			 			 	
			 + "varying vec2 vTextureCoord;            	   \n" // дл€ передачи во фрагментный шейдер
			 + "uniform mat4 uObjectMatrix;		           \n"
			 + "void main() { "			 			 			 
			 +	" gl_Position = uObjectMatrix*aPosition;	\n"			
			 +  " vTextureCoord = aTextureCoord;     \n" 					
		+	"}"	;
		
	
	public static final String FRAGMENT_SHADER_CODE = 
			"precision highp float;"
		+	"varying vec2 vTextureCoord;                        \n" 		
		+	"uniform sampler2D uSampler;            		     \n"
		+	"void main() {										\n"
		+	" gl_FragColor = texture2D(uSampler,vTextureCoord);	\n"
		+	"}"	;		

	
	//shader holder
	private int aPositionHolder;	
	private int aTextureCoordHolder;
	private int uObjectMatrixHandler;	
	private int uSamplerHolder;


	private FloatBuffer vntBuffer;	

	private int indexCount;	
	
	Controller controller;
	
	//временные
	float[] mMVMatrix = new float[16];
	

	public ControllerView(Texture texture,Context context,String modelName) {
		super(texture);				
		
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
		
		indexCount = (int) (vntBuffer.remaining()*4/ResourceManager.VNT_STRIDE);
		
	}

	@Override
	public void initializeShaderParam() {
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель дл€ переменной программы aPosition		
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uSampler");
		
		if (-1==aPositionHolder || -1==aTextureCoordHolder || -1==uObjectMatrixHandler || -1==uSamplerHolder) {
			Log.e("MyLogs", "Shader ControllerView atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uObjectMatrixHandler+","+uSamplerHolder);
		}else { 
			
		}
		
	}

	public void relocateToController() {
		
		float[] mPos = controller.getGlPositions();		
		this.setPositionM(mPos[0], mPos[1],0);
		
	}
	
	
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		
		if (controller!=null && controller.isEnable()) {
				relocateToController();								
				
			 GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			
			 Matrix.setIdentityM(mMVMatrix,0);		
			 
			 Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);		 		 
			 
			 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
			 
			 //Matrix.multiplyMM(mObjectMVPMatrix, 0,projectionMatrix, 0,mObjectMatrix , 0);				 				 
			 
			 GLES20.glUniformMatrix4fv(uObjectMatrixHandler, 1, false, mObjectMVPMatrix, 0);//передаем кумул€тивную матрицы MVP в шейдер
			 
			 mTexture.use(uSamplerHolder);
			 
			 //GLES20.glUniform1i(uSamplerHolder, mTexture.index);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаютс€, € хз пока почему
			 
			 vntBuffer.position(0);
			 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, vntBuffer);
			 GLES20.glEnableVertexAttribArray(aPositionHolder);			 
			 
			 vntBuffer.position(6);// ResourceManager.TEXTURE_OFFSET/4float
			 GLES20.glVertexAttribPointer(aTextureCoordHolder, 2, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, vntBuffer);
		     GLES20.glEnableVertexAttribArray(aTextureCoordHolder);
			 		
		     //GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		     GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);
		     
		     GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}
		
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
	    byteData.position(0);	   	 	  
	    
	    return byteData;
	  }	
	
	@Override	
	public boolean isGUI() {
		return true;
	}

	public void setController(Controller controller) {
		this.controller=controller;		
	}

}
