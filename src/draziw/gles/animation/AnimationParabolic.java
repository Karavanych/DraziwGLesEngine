package draziw.gles.animation;

import draziw.gles.objects.GLESObject;

public class AnimationParabolic extends AnimationActor{

	private float speed;
	private float normX;
	private float normY;
	private float normZ;

	public AnimationParabolic(GLESObject mGLESObject,
			AnimationActorListener listener) {
		super(mGLESObject, listener);
		
	}
	
	public void moveTo(float x, float y, float z, float speed) {
		this.ex = x;
		this.ey = y;
		this.ez = z;
		this.speed = speed;
		
		

		this.isRun = true;
	}
	
	@Override
	public void start() {		
		super.start();
		/*
		 * x(t)=(Qx-2Rx+Px)*t*t+2(Rx-Px)*t + Px
		 * y(t)=(Qy-2Ry+Py)*t*t+2(Ry-Py)*t + Py
		 * z(t)=(Qz-2Rz+Pz)*t*t+2(Rz-Pz)*t + Pz
		 */
		
		final float a = ex - sx;
		final float b = ey - sy;
		final float c = ez - sz;

		float vectorLenght = (float) Math.sqrt(a * a + b * b + c * c);
		normX = a / vectorLenght * speed;
		normY = b / vectorLenght * speed;
		normZ = c / vectorLenght * speed;
		
		this.timeEnd=a/normX;
	}

	@Override
	public void run(float timer) {
		timeLeft+=timer;
		if (timeLeft<=timeEnd && timeLeft>=0) {
			obj.setPositionI((float)(normX*timeLeft+sx),(float)(normY*timeLeft+sy),(float)(normZ*timeLeft+sy));
		} else if (timeLeft<0) {
			obj.setPositionI(sx, sy, sz);
			end();
		} else {
			obj.setPositionI(ex, ey, ez);
		}		
	}	

}
