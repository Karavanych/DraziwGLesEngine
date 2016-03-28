package draziw.gles.game;

import android.content.Context;
import draziw.gles.engine.GLES20Renderer;
import draziw.gles.engine.TextureLoader;

public abstract class SceneManager {
	
	public GameScene currentScene;
	
	public GLES20Renderer renderer;

	public TextureLoader textureLoader;
	
	public String currentSceneTag;
	public String oldSceneTag;
	

	public abstract GameScene getMainMenu(Context context);	
	public abstract void onBackPressed();
	
	public SceneManager() {
		
	}
	
	public void setRenderer(GLES20Renderer render) {
		this.renderer=render;
	}
	
	
	public void finish() {
		if (currentScene!=null) {
			currentScene.destroy();
			currentScene=null;
		}
		renderer.finish();
	}

	public void setTextureLoader(TextureLoader textureLoader, boolean loadDefaults) {
		this.textureLoader=textureLoader;
	}
	
	public void onSceneExit(GameScene scene,String nextSceneTag) {
		scene.destroy();
		scene=null;
		this.oldSceneTag=this.currentSceneTag;
		this.currentSceneTag=nextSceneTag;		
	}

}
