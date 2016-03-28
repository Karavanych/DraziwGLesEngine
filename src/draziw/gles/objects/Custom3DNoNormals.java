package draziw.gles.objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import draziw.gles.engine.Texture;
import draziw.gles.game.ResourceManager;
import draziw.gles.materials.Material;

public class Custom3DNoNormals extends Custom3D {

	public Custom3DNoNormals(Texture texture,
			Material material, ResourceManager resources, String modelName) {
		super(texture,material, resources, modelName);		
	}
	
	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {		     	                       

		 
		 Matrix.setIdentityM(mMVMatrix,0);				
		 				 
		 Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);		 		
		 					  		 
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 mTexture.use(material.uBaseMap);
		 
		 GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vntBufferHolder);
		 GLES20.glEnableVertexAttribArray(material.aPosition);
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE, 0);		 				 
		 
		 GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, ResourceManager.VNT_STRIDE,ResourceManager.TEXTURE_OFFSET); // 24 - offset (3+3)*4float		 		
			 
	     //GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);
	     GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,indexCount);	 	     
	     
	    // Если буфер неотключить - то его начинают использовать другие объекты - например cubeMap
		// Надо подумать куда это вставлять
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	     		
	}
	
	

}
