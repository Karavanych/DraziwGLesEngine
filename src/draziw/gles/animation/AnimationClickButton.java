package draziw.gles.animation;

import android.opengl.Matrix;
import android.util.Log;
import draziw.gles.math.MyMatrix;
import draziw.gles.math.Quaternion;
import draziw.gles.objects.GLESObject;

public class AnimationClickButton extends AnimationActor{

	private float speed;
	
	private float[] defaultMatrix;

	private float stepX;

	private float stepY;

	private float stepZ;

	public AnimationClickButton(GLESObject mGLESObject,
			AnimationActorListener listener) {
		super(mGLESObject, listener);		
	}
	
	
	// speed  (0 - 1] 
	public void scaleTo(float x, float y, float z, float speed) {

		
		this.ex = x;
		this.ey = y;
		this.ez = z;
		
		this.speed=speed;				
		
	}
	
	@Override
	public void start() {		
		super.start();
		
		this.sx = MyMatrix.getScaleX(obj.mObjectMatrix);
		this.sy = MyMatrix.getScaleY(obj.mObjectMatrix);
		this.sz = MyMatrix.getScaleZ(obj.mObjectMatrix);
		
		final float a = ex - 1f;
		final float b = ey - 1f;
		final float c = ez - 1f;

		// speed  (0 - 1] 
		float vectorLenght = (float) Math.sqrt(a * a + b * b + c * c);
		
		stepX = a/vectorLenght * speed;
		stepY = b/vectorLenght * speed;
		stepZ = c /vectorLenght * speed;				
		
		this.timeEnd=a/stepX;
		
		defaultMatrix=obj.mObjectMatrix.clone();
				
	}

	@Override
	public void run(float timer) {
		if (delay>0) delay-=timer; 
		else {
			if (reverse) timeLeft-=timer;
			else timeLeft+=timer;
			
			if (timeLeft<=timeEnd && timeLeft>=0) {			
				setScale();
			} else if (timeLeft<0) {
				obj.mObjectMatrix=defaultMatrix;
				end();			
			} else {
				setScale();
				reverse=true;
			}	
		}
	}
	
	public void setScale() {
				
		MyMatrix.setFromTranslateScale(obj.mObjectMatrix,
				obj.position[0],obj.position[1],obj.position[2],				
				(1f+stepX*timeLeft)*sx,
				(1f+stepY*timeLeft)*sy,
				(1f+stepZ*timeLeft)*sz);				
		
	}
}
