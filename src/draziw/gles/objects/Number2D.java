package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.util.LinkedList;

import android.graphics.Paint.Align;
import android.opengl.GLES20;
import android.opengl.Matrix;

import draziw.gles.engine.Texture;
import draziw.gles.materials.MaterialSprite;

public class Number2D extends GLESObject {
		
	
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;	
		
	
	private float[] animationVector;
	
	LinkedList<Integer> numbers;
	private int lenght;
	
	private float fixedWidth;
	private float fixedHeight;
	private Align align;

	public Number2D(Texture mTexture,MaterialSprite material,int number) {
		this(mTexture,material,number,0,0,Align.LEFT);		
	}
	
	public Number2D(Texture mTexture,MaterialSprite material,int number,float width,float height,Align align) {
		
		super(mTexture,material);
		
		this.fixedWidth=width;
		this.fixedHeight=height;
		this.align=align;
		
		this.lenght=splitNumber(number);
		
		animationVector=new float[]{0.0625f,0f};
		
		align=Align.LEFT;
		
		initBuffers();
		
	}
	
	public void setFixedSize(float width,float height,Align align) {
		this.fixedWidth=width;
		this.fixedHeight=height;
		this.align=align;
		initBuffers();		
	}
	
	public void setNumber(int number) {
		int newlenght = splitNumber(number);
		if (newlenght!=lenght) {
			lenght=newlenght;
			initBuffers();
		}
		
	}
	
	
	public void initBuffers() {
		// вертексы
				int stride=18;
				
				float[] pointVFA=new float[lenght*stride];
				// весь массив -1 до 1
				// ширина - 2/lenght
				// высота - 2/lenght
				float width;
				float height;
				if (fixedWidth==0) {
					width = 2f/lenght;				
					height = 2f/lenght;
				} else {
					width = fixedWidth;
					height = fixedHeight;
				}
				
				for (int i=0;i<lenght;i++) {
					float left;
					float right;
					switch (align) {
					case CENTER:
						left = -width*lenght*0.5f + width*i;
						right = -width*lenght*0.5f + width*(i+1);
						break;
					case RIGHT:						
						left = -width*lenght + width*i;
						right = -width*lenght + width*(i+1);
						break;
					default:
						left = -1 + width*i;
						right = -1 + width*(i+1);
					}
					
					// право низ - -1 + width*(i+1)/ 0 - 0.5f*height
					// лево низ = -1 + width*i		/ 0 - 0.5f*height
					// право верх =-1 + width*(i+1)/ 0 + 0.5f*height
					// лево верх = -1 + width*i	/ 0 + 0.5f*height						
					
					pointVFA[0+i*stride]= right;
					pointVFA[1+i*stride]= 0 + 0.5f*height;
					pointVFA[2+i*stride]= 0f;
					
					pointVFA[3+i*stride] = left;
					pointVFA[4+i*stride]= 0 + 0.5f*height;
					pointVFA[5+i*stride]= 0f;
					
					pointVFA[6+i*stride]= left;
					pointVFA[7+i*stride]= 0 - 0.5f*height;
					pointVFA[8+i*stride]= 0f;
					
					pointVFA[9+i*stride]= right;
					pointVFA[10+i*stride]= 0 - 0.5f*height;
					pointVFA[11+i*stride]= 0f;
					
					//2
					pointVFA[12+i*stride]= right;
					pointVFA[13+i*stride]= 0 + 0.5f*height;
					pointVFA[14+i*stride]= 0f;
					
					//1
					pointVFA[15+i*stride]= left;
					pointVFA[16+i*stride]= 0 - 0.5f*height;
					pointVFA[17+i*stride]= 0f;
								
				}
				
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(pointVFA.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(pointVFA);
				vertextBuffer.position(0); 	
				
				// текстуры
				stride=12;
				float[] leftTopRightBottom = new float[]{0.0f,0.9375f,0.0625f,1.0f};
				
				float[] planeTFA= new float[lenght*stride];
				
				for (int i=0;i<lenght;i++) {			
					
					//2
					planeTFA[0+i*stride]=leftTopRightBottom[2];
					planeTFA[1+i*stride]=leftTopRightBottom[1];
					
					//3
					planeTFA[2+i*stride]=leftTopRightBottom[0];
					planeTFA[3+i*stride]=leftTopRightBottom[1];
					
					//1
					planeTFA[4+i*stride]=leftTopRightBottom[0];
					planeTFA[5+i*stride]=leftTopRightBottom[3];
					
					//0
					planeTFA[6+i*stride]=leftTopRightBottom[2];
					planeTFA[7+i*stride]=leftTopRightBottom[3];
					
					//2
					planeTFA[8+i*stride]=leftTopRightBottom[2];
					planeTFA[9+i*stride]=leftTopRightBottom[1];
					
					//1
					planeTFA[10+i*stride]=leftTopRightBottom[0];
					planeTFA[11+i*stride]=leftTopRightBottom[3];
				}
				
				
				ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
				planeTBB.order(ByteOrder.nativeOrder());
				textureCoordBuffer = planeTBB.asFloatBuffer();
				textureCoordBuffer.put(planeTFA);
				textureCoordBuffer.position(0);	
		
	}
	
	
	private int splitNumber(int number) {
		// linked - потому что push заполнит в обратном порядке!, а array - хер заполнишь так.
		numbers = new LinkedList<Integer>();
		while (number > 0) {
			numbers.push( number % 10 );			
		    number = number / 10;
		}		
		return numbers.size();
	}


	@Override
	public boolean isGUI() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void draw(float[] viewMatrix, float[] projectionMatrix, float timer) {		
	
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
	     				 		 
		 for (int i=0;i<lenght;i++) {
			 GLES20.glUniform1f(((MaterialSprite)material).uFrame,numbers.get(i));			 
			 GLES20.glDrawArrays(GLES20.GL_TRIANGLES,i*6,6);	
		 }		 
	     	    	
	}
	
	public void setGeometriByScaling() {
		setGeometry(mObjectMatrix[0]*Sprite2D.BASE_SPRITE_WIDTH,mObjectMatrix[5]*Sprite2D.BASE_SPRITE_HEIGHT,mObjectMatrix[10]);		
	}
	

}
