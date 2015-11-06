package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.engine.ShaderManager;
import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;

public class CubeMap3D extends GLESObject {
	
	private FloatBuffer vertextBuffer;
	private FloatBuffer textureCoordBuffer;
	private ShortBuffer verticesIndex;

	private int indexCount;	
		
	//временные
	float[] mMVMatrix = new float[16];	

	public CubeMap3D(Texture texture,Material material,Context context) {
		super(texture,material);	
		
		float[] faces   =  {  1.0f,  1.0f,  1.0f,    -1.0f,  1.0f,  1.0f,    -1.0f, -1.0f,  1.0f,     1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,     1.0f, -1.0f,  1.0f,     1.0f, -1.0f, -1.0f,     1.0f,  1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,    -1.0f, -1.0f, -1.0f,    -1.0f,  1.0f, -1.0f,     1.0f,  1.0f, -1.0f,
               -1.0f,  1.0f,  1.0f,    -1.0f,  1.0f, -1.0f,    -1.0f, -1.0f, -1.0f,    -1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,     1.0f,  1.0f, -1.0f,    -1.0f,  1.0f, -1.0f,    -1.0f,  1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,    -1.0f, -1.0f,  1.0f,    -1.0f, -1.0f, -1.0f,     1.0f, -1.0f, -1.0f,
             };
		
		
		/*short[] indices  = {  0,  1,  2,     0,  2,  3,
		                4,  5,  6,     4,  6,  7,
		                8,  9, 10,     8, 10, 11,
		               12, 13, 14,    12, 14, 15,
		               16, 17, 18,    16, 18, 19,
		               20, 21, 22,    20, 22, 23
             };*/
		
		short[] indices  = {  0,  2,  1,     0,  3,  2,
                4,  6,  5,     4,  7,  6,
                8,  10, 9,     8, 11, 10,
               12, 14, 13,    12, 15, 14,
               16, 18, 17,    16, 19, 18,
               20, 22, 21,    20, 23, 22
		};
		
		float[] vertices = {  1.0f,  1.0f,  1.0f,    -1.0f,  1.0f,  1.0f,    -1.0f, -1.0f,  1.0f,     1.0f, -1.0f,  1.0f,
		                1.0f,  1.0f,  1.0f,     1.0f, -1.0f,  1.0f,     1.0f, -1.0f, -1.0f,     1.0f,  1.0f, -1.0f,
		                1.0f, -1.0f, -1.0f,    -1.0f, -1.0f, -1.0f,    -1.0f,  1.0f, -1.0f,     1.0f,  1.0f, -1.0f,
		               -1.0f,  1.0f,  1.0f,    -1.0f,  1.0f, -1.0f,    -1.0f, -1.0f, -1.0f,    -1.0f, -1.0f,  1.0f,
		                1.0f,  1.0f,  1.0f,     1.0f,  1.0f, -1.0f,    -1.0f,  1.0f, -1.0f,    -1.0f,  1.0f,  1.0f,
		                1.0f, -1.0f,  1.0f,    -1.0f, -1.0f,  1.0f,    -1.0f, -1.0f, -1.0f,     1.0f, -1.0f, -1.0f
		             };
		
		ByteBuffer pointVBB = ByteBuffer.allocateDirect( 4 * faces.length );
		pointVBB.order( ByteOrder.nativeOrder( ));
		vertextBuffer = pointVBB.asFloatBuffer( );
		vertextBuffer.put( faces );
		vertextBuffer.position( 0 );

		ByteBuffer pointVTB = ByteBuffer.allocateDirect( 4 * vertices.length );
		pointVTB.order( ByteOrder.nativeOrder( ));
		textureCoordBuffer = pointVTB.asFloatBuffer( );
		textureCoordBuffer.put( vertices );
		textureCoordBuffer.position( 0 );
		
		ByteBuffer pointIBB = ByteBuffer.allocateDirect( 4 * indices.length );
		pointIBB.order( ByteOrder.nativeOrder( ));
		verticesIndex = pointIBB.asShortBuffer( );
		verticesIndex.put( indices );
		verticesIndex.position( 0 );
		indexCount=verticesIndex.remaining();		
		verticesIndex.position(0);
		
	}

/*	@Override
	public void initializeShaderParam() {
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");		
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uBaseMap");		
		
		if (-1==aPositionHolder || -1==aTextureCoordHolder || -1==uObjectMatrixHandler || -1==uSamplerHolder 
				  ) {
			Log.e("MyLogs", "Shader CubeMap atributs or uniforms not found.");
			Log.e("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uObjectMatrixHandler+","+uSamplerHolder);
		}
		else { 
			
		}
		
	}*/
	
	
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {		     	                       
		 
		 Matrix.setIdentityM(mMVMatrix,0);		
		 
		 Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);		 		 
		 
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		 
		 mTexture.use(material.uBaseMap);
		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);			 
		 
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 3, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 		
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT, verticesIndex);	    
		
	}

}
