package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;
import draziw.gles.math.MyMatrix;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Font2D extends GLESObject{
	
	
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;
	int drawVertex=0;
	
	//shader holder
	int aPositionHolder;
	int aTextureCoordHolder;
	int uSamplerHolder;
	int uObjectMatrixHandler;
			

	
	public Font2D(Texture mTexture,Material material,String str) {
		super(mTexture,material);
		
		drawVertex=6;								
		setText(str);		
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
		 		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);	
		 
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 	
	     GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,drawVertex);
	     //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}
	
	public void setAtlas() {
		drawVertex=6;
		
		// сначала делаем единичный массив который описывает одну букву	
		float[] unionVFA = {
				  1, 1,0,
				 -1, 1,0,
				 -1,-1,0,
				  1,-1,0,
				  1, 1,0,
				 -1,-1,0			 
				};
		
		ByteBuffer pointVBB = ByteBuffer.allocateDirect(unionVFA.length * 4);
		pointVBB.order(ByteOrder.nativeOrder());
		vertextBuffer = pointVBB.asFloatBuffer();
		vertextBuffer.put(unionVFA);
		vertextBuffer.position(0); 
		
		float[] unionTFA = {						
				1f,0,
				0,0,
				0,1f,
				1f,1f,
				1f,0,
				0,1f				
				};	
		
		ByteBuffer planeTBB = ByteBuffer.allocateDirect(unionTFA.length * 4);
		planeTBB.order(ByteOrder.nativeOrder());
		textureCoordBuffer = planeTBB.asFloatBuffer();
		textureCoordBuffer.put(unionTFA);
		textureCoordBuffer.position(0);
		
	}
	
	public void setText(String str) {
		
		// логика нанесения текста такая, для каждой буквы задаем 2 треугольника, с 6 вершинами , две из которых одинаковые...
		// текст определяется текстурными координатами для вершин.
				
		drawVertex=str.length()*6;
		
		// сначала делаем единичный массив который описывает одну букву	
		float[] pointVFA = {
				  1, 1,0,
				 -1, 1,0,
				 -1,-1,0,
				  1,-1,0,
				  1, 1,0,
				 -1,-1,0			 
				};
		
		MyMatrix.matrix3dScale(pointVFA, new float[]{1f/str.length(),1f/str.length(),1f});// изменяем размер матрицы по количеству символов в строке
		float firstMatrixMoveLeft=-(1-1f/str.length());//определяем величину сдвига до крайне левого положения
		MyMatrix.matrix3dTranslate(pointVFA, new float[]{firstMatrixMoveLeft,0,0}); // сдвигаем в крайнелевое положение
		
		float matrixStep=2f/str.length();//определяем шаг сдвига в право для каждой следующей матрицы		
		float[] tekMatrix=pointVFA.clone(); // задаем текущую матрицу как копию первой
		float[] unionVFA=pointVFA; // задаем матрицу которая будет объединять все предыдущие, помещаем в нее первую
		for (int i=0;i<str.length();i++) {
			MyMatrix.matrix3dTranslate(tekMatrix,new float[]{matrixStep,0,0});// сдвигаем текущую матрицу вправо на длинну матрицы
			unionVFA=MyMatrix.unionArrays(unionVFA,tekMatrix);// добавляем текущую матрицу к объединяющей матрице
		}
		tekMatrix=null;
		pointVFA=null;								
		
				ByteBuffer pointVBB = ByteBuffer.allocateDirect(unionVFA.length * 4);
				pointVBB.order(ByteOrder.nativeOrder());
				vertextBuffer = pointVBB.asFloatBuffer();
				vertextBuffer.put(unionVFA);
				vertextBuffer.position(0); 		
												
		
		
		float[] planeTFA = {						
				1f,0,
				0,0,
				0,1f,
				1f,1f,
				1f,0,
				0,1f				
				};		
		
		MyMatrix.matrix2dScale(planeTFA,animationVector);
		tekMatrix=planeTFA.clone();
		float[] unionTFA = null;
		for (int i=0;i<str.length();i++) {
			String tekSimbol=str.substring(i, i+1);
			int[] simbolPos = getSimbolPosition(tekSimbol);
			tekMatrix=planeTFA.clone();	
			MyMatrix.matrix2dTranslate(tekMatrix,new float[]{animationVector[0]*simbolPos[0],animationVector[1]*simbolPos[1]});
			if (unionTFA==null) {
				unionTFA = tekMatrix;
			} else {
				unionTFA = MyMatrix.unionArrays(unionTFA, tekMatrix);
			}			
		}				
		
				ByteBuffer planeTBB = ByteBuffer.allocateDirect(unionTFA.length * 4);
				planeTBB.order(ByteOrder.nativeOrder());
				textureCoordBuffer = planeTBB.asFloatBuffer();
				textureCoordBuffer.put(unionTFA);
				textureCoordBuffer.position(0);
		
	}	
	
	
	
	
	
	
	// статические методы для генерации текстуры шрифта
	
	
	public static float[] animationVector;
	public static String[] mFont=new String[] {
		"ABCDEFGHIJKLMNOP",
		"QRSTUVWXYZabcdef",
		"ghijklmnopqrstuv",
		"wxyz0123456789 #",
		",.-+!@$%^&*()=~`"
		};
	
	public static int[] getSimbolPosition(String simbol) {
		for (int i=0;i<mFont.length;i++) {
			int simIdx = mFont[i].indexOf(simbol);
			if (simIdx!=-1) return new int[]{simIdx,i};
		}
		return new int[]{mFont[0].length(),mFont.length};		
	}
	
	public static Bitmap generateFontAtlas() {
		int fontWidth=64;// fontWidth x fontHeight - 1 simpol size
		int fontHeight=64;		
		
		
		int widthBmp=fontWidth*mFont[0].length();
		int heightBmp=fontHeight*mFont.length;
		
		animationVector =new float[]{(float)fontWidth/widthBmp,(float)fontHeight/heightBmp};		
		
		
		// new bitmap
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap mBitmap = Bitmap.createBitmap(widthBmp, heightBmp, conf); // this
										   
		Canvas mCanvas = new Canvas(mBitmap);
				
		Paint mPaint = new Paint();
		
		mPaint.setAntiAlias(true);
		
		float mSize = determineMaxTextSize("W",fontWidth,fontHeight);// fontWidth x fontHeight - 1 simpol size
		mPaint.setTextSize(mSize);
		
		Rect bounds = new Rect();
		mPaint.getTextBounds("W",0,1,bounds);



		for (int sIdx=0;sIdx<mFont[0].length();sIdx++)
			for (int sIdy=0;sIdy<mFont.length;sIdy++) {	
				
				mPaint.setColor(Color.BLACK);
				mCanvas.drawRect(sIdx*fontWidth,sIdy*fontHeight,sIdx*fontWidth+ fontWidth,sIdy*fontHeight+fontHeight, mPaint);
				mPaint.setColor(Color.WHITE);
				mCanvas.drawRect(sIdx*fontWidth+1, sIdy*fontHeight+1, sIdx*fontWidth+ fontWidth-2, sIdy*fontHeight+fontHeight-2, mPaint);
				mPaint.setColor(Color.BLACK);
				
				float xTextOffset=(fontWidth-bounds.right)/2;
				float yTextOffset=fontHeight-(fontHeight+bounds.top)/2;
				float posX=sIdx*fontWidth;
				float posY=sIdy*fontHeight;
				String mStr = mFont[sIdy].substring(sIdx,sIdx+1);				
				mCanvas.drawText(mStr,posX+xTextOffset,posY+yTextOffset,mPaint);
			}
		
		return mBitmap;
	}
		
	public static int determineMaxTextSize(String str, float maxWidth,
			float maxHeigth) {
		int size = (int) (maxWidth / str.length());
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		do {
			paint.setTextSize(++size);
		} while (paint.measureText(str) < maxWidth * 5 / 6);

		// проверка на высоту
		Rect bounds = new Rect();
		paint.getTextBounds(str, 0, str.length(), bounds);

		while (bounds.height() > maxHeigth) {
			paint.setTextSize(--size);
			paint.getTextBounds(str, 0, str.length(), bounds);
		}

		return size;
	}
	
	@Override
	public boolean isGUI() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void setGeometriByScaling() {
		setGeometry(mObjectMatrix[0]*Sprite2D.BASE_SPRITE_WIDTH,mObjectMatrix[5]*Sprite2D.BASE_SPRITE_HEIGHT,mObjectMatrix[10]);		
	}

}
