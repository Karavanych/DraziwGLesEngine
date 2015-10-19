package draziw.simple.physics;

import draziw.gles.objects.GLESObject;

public class AnimationActor {

	public enum AnimationTypes{LINE_MOVE,LINE_SCALE,LINE_ROTATE};
	
	private GLESObject obj;
	private AnimationTypes type;	
	public boolean isRun=false;
	
	public float startPosition;
	public float endPosition;
	
	//start
	private float sx;
	private float sy;
	private float sz;
	
	//end
	private float ex;
	private float ey;
	private float ez;
	
	private float normX;
	private float normY;
	private float normZ;
	private float endMovement2;
	private float curMovement2;
	
	public AnimationActor(GLESObject mGLESObject,AnimationTypes animationType) {
		this.obj=mGLESObject;
		this.type=animationType;
	}		
	
	public void moveTo(float x,float y,float z,float speed) {
		this.ex=x;
		this.ey=y;
		this.ez=z;		
		this.endMovement2=x*x+y*y+z*z;
		
		final float a = ex - sx;
		final float b = ey - sy;
		final float c = ez - sz;
		
		float vectorLenght = (float)Math.sqrt(a*a+b*b+c*c);
		normX=a/vectorLenght*speed;
		normY=b/vectorLenght*speed;
		normZ=c/vectorLenght*speed;		
		
	}
	
	public void start() {
		this.sx=obj.position[0];
		this.sy=obj.position[1];
		this.sz=obj.position[2];
				
		this.isRun=true;
		this.curMovement2=0;
		
	}

	public void run(float timer) {
		if (isRun) {
			switch (type) {
			case LINE_MOVE:
				final float ax=normX*timer;
				final float ay=normY*timer;
				final float az=normZ*timer;
				
				obj.translateI(ax,ay,az);
				// аккумулируем сколько уже переместили ( в квадрате)
				curMovement2+=ax*ax+ay*ay+az*az;
				//сравниваем с квадратом конечно длинны
				if (endMovement2>curMovement2) {
					obj.setPositionI(ex, ey, ez);
					isRun=false;
				}
				break;
			case LINE_SCALE:
				break;
			case LINE_ROTATE:
				break;
			}		
		}
	}
	
	
}
