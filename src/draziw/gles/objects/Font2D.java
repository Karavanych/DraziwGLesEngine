package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import draziw.gles.engine.MyMatrix;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Font2D extends GLESObject{
	
	public static ShaderProgram sShaderProgram;
	
	public static final String VERTEX_SHADER_CODE = 
			  "attribute vec4 aPosition;         		   \n" // ��������� �������� ������
			 + "attribute vec2 aTextureCoord;	         		   \n" // ��������� �������� ������
			 + "varying vec2 vTextureCoord;             		   \n" // ��� �������� �� ����������� ������			
			 +	"uniform mat4 uObjectMatrix;			\n"
			 + "void main() {                    		   \n"			
			 +	" gl_Position = uObjectMatrix*aPosition;	\n"			
			 + " vTextureCoord = aTextureCoord;     \n" // ���������� ���������� ���������, ��� ������ ����� ������ �� �������					
		+	"}"	;
		
	
	public static final String FRAGMENT_SHADER_CODE = 
			"precision highp float;"
+			"varying vec2 vTextureCoord;                        \n" +
			"uniform sampler2D uSampler;                 \n"
		+	"void main() {							\n"
		+	" gl_FragColor = texture2D(uSampler,vTextureCoord);	\n"
		+	"}"	;
	
	
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;
	int drawVertex=0;
	
	//shader holder
	int aPositionHolder;
	int aTextureCoordHolder;
	int uSamplerHolder;
	int uObjectMatrixHandler;
			
	@Override
	public void initializeShaderParam() {
		
		aPositionHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aPosition");// �������� ��������� ��� ���������� ��������� aPosition
		aTextureCoordHolder = GLES20.glGetAttribLocation(shaderProgramHandler, "aTextureCoord");
		uSamplerHolder = GLES20.glGetUniformLocation(shaderProgramHandler, "uSampler");		
		uObjectMatrixHandler=GLES20.glGetUniformLocation(shaderProgramHandler, "uObjectMatrix");
		
		if (-1==aPositionHolder || -1==aTextureCoordHolder || -1==uSamplerHolder || -1==uObjectMatrixHandler) {
			Log.d("MyLogs", "Shader atributs or uniforms not found.");
			Log.d("MyLogs",""+aPositionHolder+","+aTextureCoordHolder+","+uSamplerHolder+","+uObjectMatrixHandler);
		}
		else { 
			
		}
	}

	
	public Font2D(Texture mTexture,String str) {
		super(mTexture);
		
		drawVertex=6;								
		setText(str);		
	}		
	
	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {		
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 
		 GLES20.glUniformMatrix4fv(uObjectMatrixHandler, 1, false, mObjectMVPMatrix, 0);//�������� ������������ ������� MVP � ������
		
		// GLES20.GL_TEXTURE_2D - �� �������� ����� ��� ��� �����, ����� ������������ �������� � ��������� �� � �����.
		 GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTexture.index); // ���������� ��������, ������� ��������� ��������		 
		 GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // ����������� ��������, ������� ���������� ������ ��������		 
		 GLES20.glUniform1i(uSamplerHolder, mTexture.index);//�������� ������ �������� � ������... index �������� � id �������� �����������, � �� ���� ������		 		
		 		 
		 GLES20.glVertexAttribPointer(aPositionHolder, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(aPositionHolder);	
		 
		 GLES20.glVertexAttribPointer(aTextureCoordHolder, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(aTextureCoordHolder);
		 	
	     GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,drawVertex);
	     //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}
	
	public void setAtlas() {
		drawVertex=6;
		
		// ������� ������ ��������� ������ ������� ��������� ���� �����	
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
		
		// ������ ��������� ������ �����, ��� ������ ����� ������ 2 ������������, � 6 ��������� , ��� �� ������� ����������...
		// ����� ������������ ����������� ������������ ��� ������.
				
		drawVertex=str.length()*6;
		
		// ������� ������ ��������� ������ ������� ��������� ���� �����	
		float[] pointVFA = {
				  1, 1,0,
				 -1, 1,0,
				 -1,-1,0,
				  1,-1,0,
				  1, 1,0,
				 -1,-1,0			 
				};
		
		MyMatrix.matrix3dScale(pointVFA, new float[]{1f/str.length(),1f/str.length(),1f});// �������� ������ ������� �� ���������� �������� � ������
		float firstMatrixMoveLeft=-(1-1f/str.length());//���������� �������� ������ �� ������ ������ ���������
		MyMatrix.matrix3dTranslate(pointVFA, new float[]{firstMatrixMoveLeft,0,0}); // �������� � ����������� ���������
		
		float matrixStep=2f/str.length();//���������� ��� ������ � ����� ��� ������ ��������� �������		
		float[] tekMatrix=pointVFA.clone(); // ������ ������� ������� ��� ����� ������
		float[] unionVFA=pointVFA; // ������ ������� ������� ����� ���������� ��� ����������, �������� � ��� ������
		for (int i=0;i<str.length();i++) {
			MyMatrix.matrix3dTranslate(tekMatrix,new float[]{matrixStep,0,0});// �������� ������� ������� ������ �� ������ �������
			unionVFA=MyMatrix.unionArrays(unionVFA,tekMatrix);// ��������� ������� ������� � ������������ �������
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
	
	
	
	
	
	
	// ����������� ������ ��� ��������� �������� ������
	
	
	public static float[] animationVector;
	public static String[] mFont=new String[] {
		"ABCDEFGHIJKLMNOP",
		"QRSTUVWXYZabcdef",
		"ghijklmnopqrstuv",
		"wxyz0123456789 #"				
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

		// �������� �� ������
		Rect bounds = new Rect();
		paint.getTextBounds(str, 0, str.length(), bounds);

		while (bounds.height() > maxHeigth) {
			paint.setTextSize(--size);
			paint.getTextBounds(str, 0, str.length(), bounds);
		}

		return size;
	}


	@Override
	public ShaderProgram getShaderProgramInstance() {		
		if (sShaderProgram==null) {
			sShaderProgram=new ShaderProgram(VERTEX_SHADER_CODE,FRAGMENT_SHADER_CODE);
		}
		return sShaderProgram;
	}


	public static void reset() {
		sShaderProgram=null;		
	}

}