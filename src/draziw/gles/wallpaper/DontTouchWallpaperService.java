package draziw.gles.wallpaper;

import draziw.gles.engine.GLES20Renderer;
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;


public class DontTouchWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return new GLES20Renderer((Context)this);
	}
}
