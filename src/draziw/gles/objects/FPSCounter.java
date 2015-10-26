package draziw.gles.objects;

import draziw.gles.engine.GLES20Renderer;
import draziw.gles.engine.ShaderManager;
import draziw.gles.engine.Texture;

public class FPSCounter extends Font2D {
	
	int frameCount=0;
	double frameTime=0;

	public FPSCounter(Texture mTexture,ShaderManager shaders) {
		super(mTexture,shaders.getShader("font"), "0");		
	}
	
	@Override
	public boolean isGUI() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void draw(float[] viewMatrix, float[] projectionMatrix, float timer) {
		frameTime+=timer;
		frameCount++;
		if (frameTime>1000000000*GLES20Renderer.GAME_SPEED) {
			frameTime=0;
			this.setText(Integer.toString(frameCount));
			frameCount=0;
		}
		super.draw(viewMatrix, projectionMatrix, timer);
	}
	

}
