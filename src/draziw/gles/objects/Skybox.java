package draziw.gles.objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import draziw.gles.engine.Texture;
import draziw.gles.game.GLESCamera;
import draziw.gles.game.ResourceManager;

public class Skybox extends Custom3D {
	
	public static float[] tmpView=new float[16];
	private float[] newProjection;

	public Skybox(Texture texture, MaterialSkybox material,
			ResourceManager resources) {
		super(texture, material, resources, "sphere32");		
		
		tmpView[3] = 0.0f;
		tmpView[7] = 0.0f;
		tmpView[11] = 0.0f;
		tmpView[15] = 1.0f;
		
	}
	
	public Skybox(Texture texture, MaterialSkybox material,
			ResourceManager resources,float[] newProjection) {
		this(texture,material,resources);
		
		this.newProjection=newProjection;
				
	}
		
	@Override
	public void draw(float[] viewMatrix, float[] projectionMatrix, float timer) {
		tmpView[0]=viewMatrix[0];
		tmpView[4]=viewMatrix[4];
		tmpView[8]=viewMatrix[8];
		tmpView[1]=viewMatrix[1];
		tmpView[5]=viewMatrix[5];
		tmpView[9]=viewMatrix[9];
		tmpView[2]=viewMatrix[2];
		tmpView[6]=viewMatrix[6];
		tmpView[10]=viewMatrix[10];
		
         

		 
		Matrix.setIdentityM(mMVMatrix,0);				
		
		GLES20.glUniformMatrix4fv(material.um, 1, false, mObjectMatrix, 0);//передаем матрицу M в шейдер
		
		Matrix.multiplyMM(mMVMatrix, 0, tmpView, 0, mObjectMatrix, 0);					
				
		if (newProjection==null) {
			Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
		} else {
			Matrix.multiplyMM(mObjectMVPMatrix, 0, newProjection, 0, mMVMatrix, 0);
		}
		
		GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		
		mTexture.use(material.uBaseMap);
		//GLES20.glActiveTexture(mTexture.slot); // активируем текстуру, которой собрались рисовать		 
		//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		//GLES20.glUniform1i(uSamplerHolder,mTexture.slot);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему
		
		
		// в шейдере достаточно только координаты
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vntBufferHolder);
		GLES20.glEnableVertexAttribArray(material.aPosition);
		GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, 0);
				
			
		//GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);	 
		
		
		// Если буфер неотключить - то его начинают использовать другие объекты - например cubeMap
		// Надо подумать куда это вставлять
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);



	}

}
