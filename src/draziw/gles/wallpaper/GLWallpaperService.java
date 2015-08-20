package draziw.gles.wallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {
	boolean loggerConfig=false;
	int FPS = 30;
	
	public class GLEngine extends Engine {
		class WallpaperGLSurfaceView extends GLSurfaceView {
			private static final String TAG = "WallpaperGLSurfaceView";

			WallpaperGLSurfaceView(Context context) {
				super(context);

				if (loggerConfig) {
					Log.d(TAG, "WallpaperGLSurfaceView(" + context + ")");
				}
			}

			@Override
			public SurfaceHolder getHolder() {
				if (loggerConfig) {
					Log.d(TAG, "getHolder(): returning " + getSurfaceHolder());
				}

				return getSurfaceHolder();
			}

			public void onDestroy() {
				if (loggerConfig) {
					Log.d(TAG, "onDestroy()");
				}

				super.onDetachedFromWindow();
			}
		}

		private static final String TAG = "GLEngine";

		private WallpaperGLSurfaceView glSurfaceView;
		
		private boolean rendererHasBeenSet;	
			
		
		private Handler mHandler = new Handler();
		private Boolean mPause = false;		

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			if (loggerConfig) {
				Log.d(TAG, "onCreate(" + surfaceHolder + ")");
			}

			super.onCreate(surfaceHolder);

			glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
						
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (loggerConfig) {
				Log.d(TAG, "onVisibilityChanged(" + visible + ")");
			}

			super.onVisibilityChanged(visible);

			if (rendererHasBeenSet) {
				if (visible) {
					glSurfaceView.onResume();
					mPause=false;
					reqRend();
				} else {					
					glSurfaceView.onPause();	
					mPause=true;
				}
			}
		}			
		
		void reqRend() {			
			mHandler.removeCallbacks(mDrawRa);
			if (!mPause) {
				// отложенный вызов mDrawRa
				mHandler.postDelayed(mDrawRa, 1000 / FPS);
				glSurfaceView.requestRender();
			}
		}

		private final Runnable mDrawRa = new Runnable() {
			public void run() {
				reqRend();
			}
		};

		@Override
		public void onDestroy() {
			if (loggerConfig) {
				Log.d(TAG, "onDestroy()");
			}

			super.onDestroy();
			glSurfaceView.onDestroy();
		}
		
		protected void setRenderer(Renderer renderer) {
			if (loggerConfig) {
				Log.d(TAG, "setRenderer(" + renderer + ")");
			}

			glSurfaceView.setRenderer(renderer);		
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			
			rendererHasBeenSet = true;
		}
		
		protected void setPreserveEGLContextOnPause(boolean preserve) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				if (loggerConfig) {
					Log.d(TAG, "setPreserveEGLContextOnPause(" + preserve + ")");
				}
	
				glSurfaceView.setPreserveEGLContextOnPause(preserve);
			}
		}		

		protected void setEGLContextClientVersion(int version) {
			if (loggerConfig) {
				Log.d(TAG, "setEGLContextClientVersion(" + version + ")");
			}

			glSurfaceView.setEGLContextClientVersion(version);
		}
	}
}
