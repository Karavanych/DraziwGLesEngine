package dont.touch.white;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

public class GLES20Renderer implements Renderer {
	
	private Context context;
	
	Sprite2D sprite1;
	Font2D font1;
	
	//private int _textureId;
	int texturesId[];
	
	// зададим матрицы
	private float[] mViewMatrix					= new float[16]; // вид
	private float[] mProjectionMatrix			= new float[16]; // проекци€
	private float[] mMVPMatrix					= new float[16]; // модель мира
	
	//делаем таймер как у usnavii
	private float timeAnimationInterval=.5f;
	
	
	public GLES20Renderer(Context cc) {
		context=cc;		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);		
		matrixInitialization(width,height);// инициализации матриц вида, проекций и мира
		
		TextureLoader textureLoader= new TextureLoader(context);
		ArrayList<Texture> textures = textureLoader.loadTexture(); // сначала загружаем текстуры, потом вс€кие вертексы и шейдеры
		
		
		ShaderProgram shProg=new ShaderProgram(Sprite2D.vertexShaderCode,Sprite2D.fragmentShaderCode);		
		//shProg.pointProgram; - теперь указатель на шейдерную программу здесь.
		//атрибуты и униформы, получим в спрайте
		
		sprite1=new Sprite2D(shProg,textures.get(0));
		sprite1.setRelativeTextureBounds(6,1,3,0);
		sprite1.setAnimation(0, 6, new float[]{0.166666666f,0f});
		//sprite1.scale(0.2f, 0.2f, 1f);
		//sprite1.translate(0,0,0);
		
		ShaderProgram shProg2=new ShaderProgram(Font2D.vertexShaderCode,Font2D.fragmentShaderCode);	
		font1=new Font2D(shProg2, textures.get(1),"Custom text");		
						
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		float timer = getTime(timeAnimationInterval);
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		 GLES20.glUseProgram(sprite1.shProg.programHandler);
			 //sprite1.rotate(360.0f / 10000.0f,0f,0f,1f);		
		 sprite1.draw(mMVPMatrix,timer);
		 
		 GLES20.glUseProgram(font1.shProg.programHandler);
		 font1.draw(mMVPMatrix, timer);
		 		
	}
	
	
	   private float getTime(float oppa){
	        return (SystemClock.uptimeMillis() % (1000*oppa))/(1000*oppa);
	    }
	   
	   
	   
	   
	   private void matrixInitialization(int width, int height) {
		   // ѕоложение глаза, точки наблюдени€ в пространстве.
		    final float eyeX = 0.0f;
		    final float eyeY = 0.0f;
		    final float eyeZ = 2.5f;
		 
		    // Ќа какое рассто€ние мы можем видеть вперед. ќграничивающа€ плоскость обзора.
		    final float lookX = 0.0f;
		    final float lookY = 0.0f;
		    final float lookZ = -5.0f;
		 
		    // ”станавливаем вектор. ѕоложение где наша голова находилась бы если бы мы держали камеру.
		    final float upX = 0.0f;
		    final float upY = 1.0f;
		    final float upZ = 0.0f;
			
			
		    float aspectRatio = width > height ?
		    		(float) width / (float) height :
		    		(float) height / (float) width;
		    		
		    
			float zNear = 0.1f;
			float zFar = 1000;
			float fov = 0.75f; // 0.2 to 1.0
			float size = (float) (zNear * Math.tan(fov / 2));
			Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
			if (width > height) {
				Matrix.frustumM(mProjectionMatrix, 0, -size*aspectRatio, size*aspectRatio, -size , size , zNear, zFar);
			} else {
				Matrix.frustumM(mProjectionMatrix, 0, -size, size, -size *aspectRatio, size *aspectRatio, zNear, zFar);
			}
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
	   }
	   
	   private void matrixInitializationOrthographic(int width, int height) {
		
		    // ћатрица mViewMatrix дл€ ортографической проекции не нужна, делаем ее единичной
		    Matrix.setIdentityM(mViewMatrix, 0);
		    
		    final float aspectRatio = width > height ?
		    		(float) width / (float) height :
		    		(float) height / (float) width;
		    		if (width > height) {
		    		// Landscape
		    			Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
		    		} else {
		    		// Portrait or square
		    			Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
		    		}
		    		
		    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);		   
	   }
	   	   	
}
