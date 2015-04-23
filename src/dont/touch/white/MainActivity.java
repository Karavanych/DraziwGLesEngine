package dont.touch.white;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {
	private GLSurfaceView _surfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// убираем заголовок
				requestWindowFeature(Window.FEATURE_NO_TITLE);

				// устанавливаем полноэкранный режим
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		 _surfaceView = new GLSurfaceView(this);
		 _surfaceView.setEGLContextClientVersion(2);
		 _surfaceView.setRenderer(new GLES20Renderer((Context)this));
		 setContentView(_surfaceView);
	}
}
