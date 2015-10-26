package draziw.gles.animation;

import draziw.gles.objects.GLESObject;

public class AnimationLinear extends AnimationActor{

	private float speed;
	private float normX;
	private float normY;
	private float normZ;

	public AnimationLinear(GLESObject mGLESObject,
			AnimationActorListener listener) {
		super(mGLESObject, listener);		
	}
	
	
	public void moveAt(float x, float y, float z, float speed) {
		this.sx = obj.position[0];
		this.sy = obj.position[1];
		this.sz = obj.position[2];	
		
		this.ex = sx+x;
		this.ey = sy+y;
		this.ez = ez+z;
		
		this.speed = speed;		
	}
	
	public void moveTo(float x, float y, float z, float speed) {
		this.ex = x;
		this.ey = y;
		this.ez = z;
		this.speed = speed;		
	}
	
	@Override
	public void start() {		
		super.start();
		
		final float a = ex - sx;
		final float b = ey - sy;
		final float c = ez - sz;

		float vectorLenght = (float) Math.sqrt(a * a + b * b + c * c);
		normX = a / vectorLenght * speed;
		normY = b / vectorLenght * speed;
		normZ = c / vectorLenght * speed;
		
		//Log.d("MyLogs", "normX="+normX+" , normY="+normY);
		
		this.timeEnd=a/normX;
	}

	@Override
	public void run(float timer) {
		if (delay>0) delay-=timer; 
		else {
			if (reverse) timeLeft-=timer;
			else timeLeft+=timer;
			
			if (timeLeft<=timeEnd && timeLeft>=0) {			
				obj.setPositionI(normX*timeLeft+sx,normY*timeLeft+sy,normZ*timeLeft+sy);
			} else if (timeLeft<0) {
				obj.setPositionI(sx, sy, sz);
				end();			
			} else {
				obj.setPositionI(ex, ey, ez);
				reverse=true;
			}	
		}
	}
}
