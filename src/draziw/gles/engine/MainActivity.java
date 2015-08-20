package draziw.gles.engine;



import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {
	private GLSurfaceView mSurfaceView;
	private GLES20Renderer mRender;
	
	private Handler mHandler = new Handler();
	private Boolean RPause = false;
	private int FPS = 60;
	
	public static boolean CAPTURE_VIDEO=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		      
    
		// ������� ���������
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// ������������� ������������� �����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mSurfaceView = new GLSurfaceView(this);

		// ���� ������ API ���� 11 � ���� ������������� ������ GLContext
		if (Build.VERSION.SDK_INT > 10)
			mSurfaceView.setPreserveEGLContextOnPause(true);
		
		

		int pixelFormat = PixelFormat.RGBA_8888;
		
		mSurfaceView.getHolder().setFormat(pixelFormat);
		mSurfaceView.setEGLContextClientVersion(2);

		
		// ���������� ���� ���������� EGLConfigChooser
		Config3D888MSAA configChooser=new Config3D888MSAA();
		mSurfaceView.setEGLConfigChooser(configChooser);
		
		if (pixelFormat!=configChooser.getPixelFormat()) {
			mSurfaceView.getHolder().setFormat(pixelFormat);
		}

		// �������������� ���� ���������� Renderer
		mRender = new GLES20Renderer(this);

		mSurfaceView.setRenderer(mRender);

		// ������������� ����� ������ �� ������
		mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		// ������ ��� glSurfaceView ��� �������� View ��������.
		setContentView(mSurfaceView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		/*if (CAPTURE_VIDEO) {
			try {
				mRender.startCapturing(videoPath + lastFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	
	@Override
	public void onBackPressed() {
		/*if (CAPTURE_VIDEO) {
			mRender.stopCapturing();
		}*/
		super.onBackPressed();
	}
	
	void reqRend() {
		mHandler.removeCallbacks(mDrawRa);
		if (!RPause) {
			// ���������� ����� mDrawRa
			mHandler.postDelayed(mDrawRa, 1000 / FPS);
			mSurfaceView.requestRender();
		}
	}

	private final Runnable mDrawRa = new Runnable() {
		public void run() {
			reqRend();
		}
	};

	@Override
	// �������� onTouchEvent � ����� Renderer
	public boolean onTouchEvent(final MotionEvent event) {
		mSurfaceView.queueEvent(new Runnable() {
			public void run() {
				mRender.onTouchEvent(event);
			}
		});
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSurfaceView.onPause();
		RPause = true; // ���� �����
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSurfaceView.onResume();
		// ���� �����
		RPause = false;
		// ��������� ���������
		reqRend();

	}

	@Override
	protected void onStop() {
		super.onStop();
		RPause = true;		
	}
	
	
	
}
