package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import draziw.gles.engine.GLES20Renderer;
import draziw.gles.engine.Texture;
import draziw.gles.materials.MaterialSprite;

public class FPSCounter extends GLESObject {
	
	int frameCount=0;
	double frameTime=0;
	
	
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;
	ShortBuffer verticesIndex1;
	ShortBuffer verticesIndex2;
	
	private float[] leftTopRightBottom1;
	private float[] leftTopRightBottom2;
	
	private int frame0;
	private int frame1;
	
	private float[] animationVector;

	public FPSCounter(Texture mTexture,MaterialSprite material) {
		super(mTexture,material);	
		
		animationVector=new float[]{0.0625f,0f};
		
		// по умолчанию координаты на весь экран, нужно будет реализовать сдвиг и скалирование
		float[] pointVFA = {
				 0f,-1f,0.0f,
				 -1f,-1f,0.0f,
				 0f,1f,0.0f,
				-1f,1f,0.0f,
				
				 1f,-1f,0.0f,
				 0f,-1f,0.0f,
				 1f,1f,0.0f,
				 0f,1f,0.0f
				};
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(pointVFA.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(pointVFA);
				vertextBuffer.position(0); 				
										
				// по умолчанию вся текстура на полигон, нужен вектор смещения, вектор определяющий размер текстуры и вектор анимации
		setTextureCoord();
				
		//зададим индексы, предидущие массивы определяли просто координаты вершин и текстурные координаты, соответствующие вершинам
		// а здесь мы определим порядок следования вершин через индексы					
				
		short[] planeISA = {
						2,3,1,
						0,2,1
				};
		
		ByteBuffer planeIBB = ByteBuffer.allocateDirect(planeISA.length * 2);
		planeIBB.order(ByteOrder.nativeOrder());
		verticesIndex1 = planeIBB.asShortBuffer();
		verticesIndex1.put(planeISA);
		verticesIndex1.position(0);
		
		
		planeISA = new short[]{
				6,7,5,
				4,6,5
		};

		planeIBB = ByteBuffer.allocateDirect(planeISA.length * 2);
		planeIBB.order(ByteOrder.nativeOrder());
		verticesIndex2 = planeIBB.asShortBuffer();
		verticesIndex2.put(planeISA);
		verticesIndex2.position(0);
		
	}
	
	
	public void setTextureCoord() {
		//float[] leftTopRightBottom=new float[]{0.0f,0.9375f,0.0625f,1.0f};
		int number=0;
		int number2=0;
		
		leftTopRightBottom1=new float[]{number*0.0625f,0.9375f,(number+1)*0.0625f,1.0f};
		
		leftTopRightBottom2=new float[]{number2*0.0625f,0.9375f,(number2+1)*0.0625f,1.0f};
		
		float[] planeTFA = {
				// 1f,1f, 0,1f, 1f,0,0,0
				leftTopRightBottom1[2],leftTopRightBottom1[3],
				leftTopRightBottom1[0],leftTopRightBottom1[3],
				leftTopRightBottom1[2],leftTopRightBottom1[1],
				leftTopRightBottom1[0],leftTopRightBottom1[1],
				
				leftTopRightBottom2[2],leftTopRightBottom2[3],
				leftTopRightBottom2[0],leftTopRightBottom2[3],
				leftTopRightBottom2[2],leftTopRightBottom2[1],
				leftTopRightBottom2[0],leftTopRightBottom2[1]
		};

		ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
		planeTBB.order(ByteOrder.nativeOrder());
		textureCoordBuffer = planeTBB.asFloatBuffer();
		textureCoordBuffer.put(planeTFA);
		textureCoordBuffer.position(0);	
		
	}
	
	@Override
	public boolean isGUI() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void draw(float[] viewMatrix, float[] projectionMatrix, float timer) {
		frameTime+=timer;
		frameCount++;
		if (frameTime>1000000000*GLES20Renderer.GAME_SPEED) {
			frameTime=0;
			
			frame0=frameCount-(frameCount/10)*10;
			frame1=Math.min((frameCount-frame0)/10,9);
			
			frameCount=0;
		}
	
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//передаем кумулятивную матрицы MVP в шейдер
		
		 mTexture.use(material.uBaseMap);		
		 //GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTexture.index); // активируем текстуру, которой собрались рисовать		 
		 //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // прикрепляем текстуру, которой собираемся сейчас рисовать		 
		 //GLES20.glUniform1i(uSamplerHolder, mTexture.index);//передаем индекс текстуры в шейдер... index текстуры и id текстуры различаются, я хз пока почему		 
		 
		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);	
		 
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 		
	     GLES20.glUniform2f(((MaterialSprite)material).uAnimVector,animationVector[0],animationVector[1]);// вектор сдвига анимации в текстурных координатах {1/6,0} где 6 - число кадров в спрайте
	     
		 GLES20.glUniform1f(((MaterialSprite)material).uFrame,frame1);
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex1);
	     
	     GLES20.glUniform1f(((MaterialSprite)material).uFrame,frame0);
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex2);
	
	}
	
	public void setGeometriByScaling() {
		setGeometry(mObjectMatrix[0]*Sprite2D.BASE_SPRITE_WIDTH,mObjectMatrix[5]*Sprite2D.BASE_SPRITE_HEIGHT,mObjectMatrix[10]);		
	}
	

}
