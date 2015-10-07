package draziw.test.project;

import android.os.Bundle;
import draziw.gles.engine.MainGLESActivity;
import draziw.gles.game.GameScene;

public class TestActivity extends MainGLESActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		GameScene scene = new TestScene();
		setScene(scene);
	}

}
