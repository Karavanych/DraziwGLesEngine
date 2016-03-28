package draziw.gles.objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import draziw.gles.engine.Texture;
import draziw.gles.game.ResourceManager;
import draziw.gles.materials.Material;

public class Custom3D extends GLESObject {

	protected static float[] tmpNormalMatrix = new float[9];
	
	protected int vntBufferHolder;	
	protected int indexCount;	
	
	boolean isGui=false;
	
	//временные
	protected float[] mMVMatrix = new float[16];

	
	public Texture normalMap;
	

	public Custom3D(Texture texture,Material material,ResourceManager resources,String modelName) {
		super(texture,material);	
		
		if (!resources.isLoaded(modelName)) {
			resources.loadSingleModelData(modelName, false);
		}
		
		indexCount=resources.getVertexCount(modelName);	
		
		vntBufferHolder=resources.getBufferHolder(modelName);				
		
	}
	
	public Custom3D(Texture texture,Texture normalMap,Material material,ResourceManager resources,String modelName) {
		this(texture,material,resources,modelName);
		this.normalMap=normalMap;
		
	}

	/*@Override
	public void initializeShaderParam() {
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aNormalHandler = GLES20.glGetAttribLocation(shaderProgramHandler,"aNormal");
		aTangent = GLES20.glGetAttribLocation(shaderProgramHandler,"aTangent");
		aBinormal = GLES20.glGetAttribLocation(shaderProgramHandler,"aBinormal");		
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");

		uMVPHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "mvp");
		uMHandler=GLES20.glGetUniformLocation(shaderProgramHandler,"m");
		uVHandler=GLES20.glGetUniformLocation(shaderProgramHandler,"v");
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uBaseMap");
		uNormalMap = GLES20.glGetUniformLocation(shaderProgramHandler,"uNormalMap");
		uLightPosHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "uLightPos");
		uLuminance = GLES20.glGetUniformLocation(shaderProgramHandler, "uLuminance");
		
		
		if (uNormalMap==-1 || aTangent==-1 || aBinormal==-1 || -1==aPositionHolder || -1==aTextureCoordHolder || -1==uMVPHandler || -1==uSamplerHolder ||
				-1==aNormalHandler || -1==uMHandler || -1==uLightPosHandler || -1==uLuminance) {
			Log.e("MyLogs", "Shader Custom3D atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uMVPHandler+","+uSamplerHolder+","+uMHandler+","+aNormalHandler+","+uLightPosHandler+","+uLuminance);
		}
		else { 
			
		}
		
	}*/

	/*public void draw(float[] viewMatrix,float[] projectionMatrix, float timer)  {
		 // задаем позицию источника света    
		
		if (lightObject!=null) {
			draw(viewMatrix,projectionMatrix,timer,lightObject.getMVPosition(viewMatrix),lightObject.getLuminance());
		} else {
			draw(viewMatrix,projectionMatrix,timer,new float[]{0,0,0},.05f);
		}
	}*/
	
	
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {		     	                       

		 
		 Matrix.setIdentityM(mMVMatrix,0);				
		 
		 GLES20.glUniformMatrix4fv(material.um, 1, false, mObjectMatrix, 0);//передаем матрицу M в шейдер
		 
		 Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 
		 setNormalMatrix();
		 
			
		 GLES20.glUniformMatrix3fv(material.uNormalMatrix, 1, false, tmpNormalMatrix, 0);

		 
		 
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 mTexture.use(material.uBaseMap);
		 //GLES20.glActiveTexture(mTexture.slot); // активируем текстуру, которой собрались рисовать		 
		 //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		 //GLES20.glUniform1i(uSamplerHolder,mTexture.slot);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему
		 
		 GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vntBufferHolder);
		 GLES20.glEnableVertexAttribArray(material.aPosition);
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, 0);
		 
		 
		 GLES20.glEnableVertexAttribArray(material.aNormal);
		 GLES20.glVertexAttribPointer(material.aNormal, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.NORMAL_OFFSET); // 12 - offset 3*4float
		 
		 
		 GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.TEXTURE_OFFSET); // 24 - offset (3+3)*4float
		 		
		 	
		 if (normalMap!=null) {
			 		 
			 GLES20.glEnableVertexAttribArray(material.aTangent);
			 GLES20.glVertexAttribPointer(material.aTangent, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.TANGENT_OFFSET); // 24 - offset (3+3)*4float

			 
			 GLES20.glEnableVertexAttribArray(material.aBitangent);
			 GLES20.glVertexAttribPointer(material.aBitangent, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.BITANGENT_OFFSET); // 24 - offset (3+3)*4float			 			 			 					
			 
			 normalMap.use(material.uNormalMap);

		 } 
			 
	     //GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
	     GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);	 
	     
	     
	    // Если буфер неотключить - то его начинают использовать другие объекты - например cubeMap
		// Надо подумать куда это вставлять
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	     
		
	}

	protected void setNormalMatrix() {
		/*float[] normalMatrix=new float[] {	mMVMatrix[0],mMVMatrix[1],mMVMatrix[2],
				mMVMatrix[4],mMVMatrix[5],mMVMatrix[6],
				mMVMatrix[8],mMVMatrix[9],mMVMatrix[10]
		};// no position, only rotation */
		tmpNormalMatrix[0]=mMVMatrix[0];
		tmpNormalMatrix[1]=mMVMatrix[1];
		tmpNormalMatrix[2]=mMVMatrix[2];
		tmpNormalMatrix[3]=mMVMatrix[4];
		tmpNormalMatrix[4]=mMVMatrix[5];
		tmpNormalMatrix[5]=mMVMatrix[6];
		tmpNormalMatrix[6]=mMVMatrix[8];
		tmpNormalMatrix[7]=mMVMatrix[9];
		tmpNormalMatrix[8]=mMVMatrix[10];	
	}
	
	public void setGui(boolean mGui) {
		isGui=mGui;
	}
	
	@Override
	public boolean isGUI() {		
		return isGui;
	}

}
