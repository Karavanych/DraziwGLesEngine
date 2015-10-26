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
import draziw.gles.game.ResourceManager;

public class Custom3D extends GLESObject {

	
	//shader holder
	private int aPositionHolder;
	private int aNormalHandler;
	private int aTextureCoordHolder;
	private int uObjectMatrixHandler;
	private int uMVMatrixHandler;
	private int uSamplerHolder;
	private int uLightPosHandler;
	private int uLuminance;



	private int vntBufferHolder;	
	private int indexCount;		
	
	
	//временные
	float[] mMVMatrix = new float[16];
	
	PointLight3D lightObject;

	

	public Custom3D(Texture texture,ShaderProgram shader,ResourceManager resources,String modelName) {
		super(texture,shader);		
		
		indexCount=resources.getVertexCount(modelName);	
		
		vntBufferHolder=resources.getBufferHolder(modelName);				
		
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
			Log.e("MyLogs", "Shader Custom3D atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uObjectMatrixHandler+","+uSamplerHolder+","+uMVMatrixHandler+","+aNormalHandler+","+uLightPosHandler+","+uLuminance);
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
		 //GLES20.glActiveTexture(mTexture.slot); // активируем текстуру, которой собрались рисовать		 
		 //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		 //GLES20.glUniform1i(uSamplerHolder,mTexture.slot);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему
		 
		 GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vntBufferHolder);
		 GLES20.glEnableVertexAttribArray(aPositionHolder);
		 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, 0);
		 
		 
		 GLES20.glEnableVertexAttribArray(aNormalHandler);
		 GLES20.glVertexAttribPointer(aNormalHandler, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.NORMAL_OFFSET); // 12 - offset 3*4float
		 
		 
		 GLES20.glEnableVertexAttribArray(aTextureCoordHolder);
		 GLES20.glVertexAttribPointer(aTextureCoordHolder, 2, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.TEXTURE_OFFSET); // 24 - offset (3+3)*4float
		 		
		 		
	     //GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
	     GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);	 
	     
	     
	    // Если буфер неотключить - то его начинают использовать другие объекты - например cubeMap
		// Надо подумать куда это вставлять
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	     
		
	}

	
	public void setLight(PointLight3D mLight) {
		lightObject=mLight;
	}

}
