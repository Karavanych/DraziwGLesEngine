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

import draziw.gles.controllers.Controller;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import draziw.gles.game.ResourceManager;
import draziw.gles.materials.Material;

public class ControllerView extends GLESObject {
	
	private FloatBuffer vntBuffer;	

	private int indexCount;	
	
	Controller controller;
	
	//���������
	float[] mMVMatrix = new float[16];
	

	public ControllerView(Texture texture,Material material,Context context,String modelName) {
		super(texture,material);				
		
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

/*	@Override
	public void initializeShaderParam() {
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// �������� ��������� ��� ���������� ��������� aPosition		
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uBaseMap");
		
		if (-1==aPositionHolder || -1==aTextureCoordHolder || -1==uObjectMatrixHandler || -1==uSamplerHolder) {
			Log.e("MyLogs", "Shader ControllerView atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uObjectMatrixHandler+","+uSamplerHolder);
		}else { 
			
		}
		
	}*/

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
			 
			 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//�������� ������������ ������� MVP � ������
			 
			 mTexture.use(material.uBaseMap);
			 
			 //GLES20.glUniform1i(uSamplerHolder, mTexture.index);//�������� ������ �������� � ������... index �������� � id �������� �����������, � �� ���� ������
			 
			 vntBuffer.position(0);
			 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, vntBuffer);
			 GLES20.glEnableVertexAttribArray(material.aPosition);			 
			 
			 vntBuffer.position(6);// ResourceManager.TEXTURE_OFFSET/4float
			 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, vntBuffer);
		     GLES20.glEnableVertexAttribArray(material.aTextureCoord);
			 		
		     //GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		     GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);
		     
		     GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}
		
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
