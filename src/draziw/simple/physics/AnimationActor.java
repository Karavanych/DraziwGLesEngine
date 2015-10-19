package draziw.simple.physics;

import android.util.Log;
import draziw.gles.objects.GLESObject;

public class AnimationActor {
	
	public interface AnimationActorListener {
			public void onAnimationEnd(AnimationActor actor);
		}
			

	public enum AnimationTypes {
		LINE_MOVE, LINE_MOVE_BACK, LINE_SCALE, LINE_ROTATE
	};

	private GLESObject obj;
	private AnimationTypes type;
	private AnimationActorListener listener;
	public boolean isRun = false;
	public boolean destroy=false;

	public float startPosition;
	public float endPosition;

	// start
	private float sx;
	private float sy;
	private float sz;

	// end
	private float ex;
	private float ey;
	private float ez;

	private float normX;
	private float normY;
	private float normZ;
	private float speed;

	public AnimationActor(GLESObject mGLESObject, AnimationTypes animationType,AnimationActorListener listener) {
		this.obj = mGLESObject;
		this.type = animationType;
		this.listener=listener;
	}

	public void moveTo(float x, float y, float z, float speed) {
		this.ex = x;
		this.ey = y;
		this.ez = z;
		this.speed = speed;
	}

	public void start() {
		this.sx = obj.position[0];
		this.sy = obj.position[1];
		this.sz = obj.position[2];

		final float a = ex - sx;
		final float b = ey - sy;
		final float c = ez - sz;

		float vectorLenght = (float) Math.sqrt(a * a + b * b + c * c);
		normX = a / vectorLenght * speed;
		normY = b / vectorLenght * speed;
		normZ = c / vectorLenght * speed;		

		this.isRun = true;

	}

	public void run(float timer) {	
			switch (type) {
			case LINE_MOVE: {
				final float ax = normX * timer;
				final float ay = normY * timer;
				final float az = normZ * timer;				

				obj.translateI(ax, ay, az);
				// аккумулируем сколько уже переместили ( в квадрате)

				// если перемещение линейное, то можно проверять только одну
				// координату
				if ((normX > 0 && obj.position[0] > ex)
						|| (normX < 0 && obj.position[0] < ex)) {
					obj.setPositionI(ex, ey, ez);
					// isRun=false;
					type = AnimationTypes.LINE_MOVE_BACK;
				}
				break;
			}
			case LINE_MOVE_BACK: {
				final float ax = -normX * timer;
				final float ay = -normY * timer;
				final float az = -normZ * timer;				

				obj.translateI(ax, ay, az);
				// аккумулируем сколько уже переместили ( в квадрате)

				// если перемещение линейное, то можно проверять только одну
				// координату
				if ((normX > 0 && obj.position[0] < sx)
						|| (normX < 0 && obj.position[0] > sx)) {
					obj.setPositionI(sx, sy, sz);
					isRun=false;	
					listener.onAnimationEnd(this);
					destroy=true;
				}
				break;
			}
			case LINE_SCALE: {
				break;
			}
			case LINE_ROTATE: {
				break;
			}
			}		
	}

}
