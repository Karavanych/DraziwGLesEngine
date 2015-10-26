package draziw.gles.engine;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import draziw.gles.game.GLESCamera;
import draziw.gles.game.GameControllers;
import draziw.gles.game.GameScene;
import draziw.gles.game.ResourceManager;
import draziw.test.project.TestScene;

import android.content.Context;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

import android.util.Log;
import android.view.MotionEvent;

public class GLES20Renderer implements Renderer {

	private Context context;

	// private VideoCapture videoCapture;
	// private Rect videoViewport;

	int width = 0;
	int height = 0;

	boolean wasActionDown = false;

	public static final float GAME_SPEED= 0.000000002f;

	// делаем таймер как у usnavii
	private float timeAnimationInterval = .5f;
	private long lastTime;

	GameScene gameScene;
	GameControllers gameController;

	public TextureLoader textureLoader;

	public ResourceManager resources;

	private ShaderManager shaderManager;

	public GLES20Renderer(Context cc) {
		context = cc;
		// this.videoCapture = new VideoCapture(context,new
		// MyIpProgressListener());
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {		
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

		
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK); GLES20.glFrontFace(GLES20.GL_CCW);		 

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		textureLoader= new TextureLoader(context);
		textureLoader.loadTextures();
		
		resources = new ResourceManager(context);
		shaderManager = new ShaderManager(context);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		

		GLES20.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
			
		//а че тут проверять, если ее нет, пусть падает, без нее всеравно смысла нет запускать
		//if (gameScene != null) {
			
			if (gameScene.isReady()) {
				textureLoader.confirmTextures();
				resources.confirmBuffers();
			} else {			
				GLESCamera camera=new GLESCamera(width, height);
				gameController = new GameControllers(width, height,camera.getGlScreenSize()[0],camera.getGlScreenSize()[1]);
				gameScene.init(context,camera, gameController,textureLoader,resources,shaderManager);
			}

			
		//} 
			

		// сначала загружаем текстуры, потом всякие вертексы и шейдеры

		/*
		 * videoViewport = new Rect();
		 * 
		 * videoViewport.left = 0; videoViewport.top = 0;
		 * 
		 * final float ratioDisplay = (float) width / height;
		 * 
		 * // Landscape if (ratioDisplay > 1.0f) { videoViewport.right =
		 * videoCapture.getFrameWidth(); videoViewport.bottom = (int)
		 * (videoCapture.getFrameWidth() / ratioDisplay); } else {
		 * videoViewport.bottom = videoCapture.getFrameHeight();
		 * videoViewport.right = (int) (videoCapture.getFrameHeight() *
		 * ratioDisplay); }
		 * 
		 * videoViewport.offsetTo( (videoCapture.getFrameWidth() -
		 * videoViewport.right) / 2, (videoCapture.getFrameHeight() -
		 * videoViewport.bottom) / 2);
		 */
			
		getDeltaTime();// для инициализации

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		//float timer = getAnimationTime(timeAnimationInterval);
		float timer=getDeltaTime();

		/*
		 * if (MainActivity.CAPTURE_VIDEO) { // если видеозахват делаем, то
		 * придется вьюпорт переключать внутри рендера кадра
		 * GLES20.glViewport(0,0,width,height); }
		 */

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		gameScene.onDrawFrame(timer);
		
		/*int error = GLES20.glGetError();
		  if (error != GLES20.GL_NO_ERROR)
	        {         	
	            Log.d("MyLogs", " on draw Error: " + GLU.gluErrorString(error));            
	        }*/
		

	}

	private float getAnimationTime(float oppa) {
		return (SystemClock.uptimeMillis() % (1000 * oppa)) / (1000 * oppa);
	}
	
	private float getDeltaTime() {
		long deltaTime = System.nanoTime()-lastTime;
		lastTime+=deltaTime;
		return deltaTime*GAME_SPEED;		
	}
	
	

	/*
	 * public void startCapturing(StreamingParameters params) throws IOException
	 * { if (videoCapture == null) { return; } synchronized (videoCapture) {
	 * videoCapture.start(params); } }
	 * 
	 * public void startCapturing(String videoPath) throws IOException { if
	 * (videoCapture == null) { return; } synchronized (videoCapture) {
	 * videoCapture.start(videoPath); } }
	 * 
	 * public void stopCapturing() { if (videoCapture == null) { return; }
	 * synchronized (videoCapture) { if (videoCapture.isStarted()) {
	 * videoCapture.stop(); } } }
	 * 
	 * public boolean isCapturingStarted() { return videoCapture.isStarted(); }
	 */

	public void onTouchEvent(MotionEvent event) {
		// будем работать только с двумя касаниями
		// одно справа координаты косаний width/2-0
		// одно слева координаты косаний 0-width/2

		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		// int pointerCount = event.getPointerCount();
		

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:// при одновременном нажатии 2х пальцев это событие
										// не срабатывает
			gameController.add(pointerId, event.getX(pointerId),event.getY(pointerId));
			wasActionDown = true;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (!wasActionDown) {
				pointerId = event.getPointerId(0);
				wasActionDown = true;
			}			
			gameController.add(pointerId, event.getX(pointerId),event.getY(pointerId));
			break;
		case MotionEvent.ACTION_POINTER_UP:
			gameController.remove(pointerId);
			break;
		case MotionEvent.ACTION_UP:
			gameController.removeAll();
			wasActionDown = false;
			break;
		case MotionEvent.ACTION_CANCEL:
			gameController.removeAll();
			wasActionDown = false;
			break;
		case MotionEvent.ACTION_MOVE: // движение
			gameController.move(event);
			break;
		}

	}

	public void setScene(GameScene scene) {
		//TODO если мы будем менять сцену принудительно, надо переделать добавление камеры и контроллера
		
		this.gameScene = scene;
		
	}		

}
