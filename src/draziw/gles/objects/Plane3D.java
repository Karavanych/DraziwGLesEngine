package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Plane3D extends GLESObject {
	
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;
	ShortBuffer verticesIndex;
		
	public Plane3D(Texture mTexture,Material material,float[] planeVector,boolean ccw) {
		 super(mTexture,material);						
		 
		 float[] pointVFA = {
				 planeVector[0],0.0f,0.0f,
				 0.0f,0.0f,0.0f,
				 planeVector[0],planeVector[1],planeVector[2],
				 0.0f,planeVector[1],planeVector[2]
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
		
		short[] planeISA;
		if (ccw) {
			planeISA = new short[]{
							2,3,1,
							0,2,1,
					};
		} else {
			planeISA =new short[] {
					1,3,2,
					1,2,0,
			};
		}
		
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
	
	//устанавливаем границы текстуры как сумму векторов вектора отступа и вектора текстуры vec2
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
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		
		 mTexture.use(material.uBaseMap);		
		 //GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTexture.index); // активируем текстуру, которой собрались рисовать		 
		 //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		 //GLES20.glUniform1i(uSamplerHolder, mTexture.index);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему
		 
		 //GLES20.glUniform2f(uAnimVectorHolder,animationVector[0],animationVector[1]);// вектор сдвига анимации в текстурных координатах {1/6,0} где 6 - число кадров в спрайте		 
		 //GLES20.glUniform1f(uFrameHolder,tekFrame);// - номер кадра
		 
		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);	
		 
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 		
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}
	
}
