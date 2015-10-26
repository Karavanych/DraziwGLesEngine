package draziw.gles.animation;

import draziw.gles.objects.GLESObject;

public abstract class AnimationActor {

	public interface AnimationActorListener {
		public void onAnimationEnd(AnimationActor actor);
	}


	protected GLESObject obj;	
	protected AnimationActorListener listener;
	public boolean isRun = false;
	public boolean destroy = false;


	// start
	protected float sx;
	protected float sy;
	protected float sz;

	// end
	protected float ex;
	protected float ey;
	protected float ez;
	
	float timeLeft;
	float timeEnd;
	float delay=0;
	
	boolean reverse=false;


	public AnimationActor(GLESObject mGLESObject,
			AnimationActorListener listener) {
		this.obj = mGLESObject;		
		this.listener = listener;
		
		mGLESObject.setAnimated(true);
	}

	public void setDelay(float delay) {
		this.delay=delay;		
	}

	public void start() {
		this.sx = obj.position[0];
		this.sy = obj.position[1];
		this.sz = obj.position[2];		

		this.isRun = true;
		timeLeft=0;

	}

	public abstract void run(float timer);

	public void end() {
		obj.setAnimated(false);
		isRun = false;
		destroy=true;		
	}

}
