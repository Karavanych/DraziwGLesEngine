package draziw.gles.animation;

import java.util.ArrayList;

import draziw.gles.objects.GLESObject;

public abstract class AnimationActor {

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
	
	protected float timeLeft;
	protected float timeEnd;
	protected float delay=0;
	
	protected boolean reverse=false;
	
	protected String tag;


	public AnimationActor(GLESObject mGLESObject,
			AnimationActorListener listener) {
		this.obj = mGLESObject;		
		this.listener = listener;
		
		mGLESObject.setAnimated(true);				
	}

	/* @param delay - задает задержку перед анимацией
	 * 
	 */
	
	public void setTag(String tag) {
		this.tag=tag;
	}
	
	public boolean isTag(String tag) {
		return tag==this.tag;	
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
		if (listener!=null) listener.onAnimationEnd(tag,obj);
	}
	
	public boolean isObject(GLESObject mObj) {
		return obj==mObj?true:false;		
	}
	
	public void destroy() {
		obj=null;
		listener=null;
	}

}
