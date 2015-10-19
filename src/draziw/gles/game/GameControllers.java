package draziw.gles.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import android.util.Log;
import android.view.MotionEvent;

public class GameControllers {
	
	public interface ControllersListener {
		public void controllerEnable(Controller controller);
		public void controllerDisable(Controller controller);
	}

	public static int CONTROLLER_NO = 0x0;
	public static int CONTROLLER_LEFT = 0x1;
	public static int CONTROLLER_RIGHT = 0x2;
	public static int CONTROLLER_CLICK = 0x4;
		

	private int width;
	private int height;
	private float glWidth;
	private float glHeight;
	float glKoefWidth = 1;
	float glKoefHeight = 1;
	float glSensitivity=1;
	
	public int enabledControllers=CONTROLLER_NO;

	//ArrayList<Controller> controlles = new ArrayList<Controller>();
	HashMap<Integer, Controller> controllersByType=new HashMap<Integer,Controller>();
	HashMap<Integer, Controller> controllersById=new HashMap<Integer,Controller>();
	
	

	public GameControllers(int width, int height, float glWidth,float glHeight) {
		
		this.width = width;
		this.height = height;
		this.glWidth = glWidth;
		this.glHeight= glHeight;
		this.glKoefWidth = 2*glWidth / width;
		this.glKoefHeight = 2*glHeight / height;
		this.glSensitivity = Math.min(glKoefWidth,glKoefHeight)*5;
		
		// создаем сразу контроллеры нужных типов
		enableController(CONTROLLER_LEFT);
		enableController(CONTROLLER_RIGHT);
		
		//controllersByType.put(CONTROLLER_LEFT, new Controller(CONTROLLER_LEFT));
		//controllersByType.put(CONTROLLER_RIGHT, new Controller(CONTROLLER_RIGHT));
	}
	
	public void enableController(int controllersType) {
		enabledControllers|=controllersType;
		if (controllersByType.get(controllersType)==null) {
			controllersByType.put(controllersType, new Controller(controllersType));
		}
	}
	
	public void enableController(int controllersType,ControllersListener mListener) {
		enabledControllers|=controllersType;
		if (controllersByType.get(controllersType)==null) {
			controllersByType.put(controllersType, new Controller(controllersType,mListener));
		}
	}
	
	public boolean isEnable(int controllersType) {
		return ((enabledControllers & controllersType) == controllersType);
	}
	
	public int count() {
		return controllersByType.size();
	}
	
/*	public float[] getPosition(int index) {
		return controlles.get(index).getPositions();
	}*/

	public int getControllerZone(float x, float y) {
		if (x > width * 0.5) {
			return CONTROLLER_RIGHT;
		} else {
			return CONTROLLER_LEFT;
		}
	}

	public void add(int actionIndex, float x, float y) {

		// Все контроллеры уже созданы зараннее,
		// в этом методе мы только включаем контроллеры и ассоциируем их с точкой касания 
					
		int controllerType = getControllerZone(x, y);
				
		Controller currentController = controllersByType.get(controllerType);				
		
		if (currentController.isEnable()) return;
						
		currentController.enable(actionIndex, x, y);
		controllersById.put(actionIndex,currentController);
		
		if (isEnable(CONTROLLER_CLICK)) {
			currentController = controllersByType.get(CONTROLLER_CLICK);
			// клик это всегда последнее касание...и первое поднятие... не зависит от actionIndex
			currentController.enable(0, x, y);			
		}
		
		
		
	}

	public void remove(int pointerIndex) {
		
		Controller current=controllersById.get(pointerIndex);
		if (current!=null) {
			current.disable();
			controllersById.remove(pointerIndex);			
		}
		
		if (isEnable(CONTROLLER_CLICK)) {
			Controller currentController = controllersByType.get(CONTROLLER_CLICK);
			if (currentController.isEnable()) {
				currentController.disable();
			}			
		}
		
		/*Iterator<Entry<Integer, Controller>> iterator = controllers.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Controller> pair = iterator.next();
			Controller current = pair.getValue();
			if (current.actionId == pointerIndex) {
				current.disable();
				break;
			}
		}*/						
	}

	public void removeAll() {
		
		// Итератор делаем по типу контролеера, все выключаем
		Iterator<Entry<Integer, Controller>> iterator = controllersByType.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Controller> pair = iterator.next();
			Controller current = pair.getValue();
			if (current.isEnable()) current.disable();			
		}
		// а затем очищаем полностью мап по id
		controllersById.clear();
	}

	public void move(MotionEvent event) {
		int pointerCount = event.getPointerCount();
		for (int i = 0; i < pointerCount; i++) {
			Controller current = getControllerById(event.getPointerId(i));
			if (current != null)
				current.move(event.getX(i), event.getY(i));
		}
	}

	public Controller getControllerById(int id) {
		return controllersById.get(id);
	}

	public Controller getControllerByType(int controllerType) {
		return controllersByType.get(controllerType);		
	}

	/*public float[] getGlPosition(int controllerType) {
		Controller current = getControllerByType(controllerType);

		return new float[] { glKoefWidth * current.x - 1.0f,
				glKoefHeight * current.y - 1.0f,
				0f};
	}*/

	public class Controller {
		private boolean enable=false;
		public int type;
		public int actionId;
		public float sx, sy; // start x, start y
		public float x, y; // current x, current y
		private ControllersListener listener;

		/*public Controller(int controllerType, int actionIndex, float startX,
				float startY) {
			this.type = controllerType;
			this.id = actionIndex;
			this.sx = startX;
			this.sy = startY;
		}*/

		public Controller(int controllerType) {
			this.type=controllerType;
			
		}

		public Controller(int controllersType, ControllersListener mListener) {
			this(controllersType);
			this.listener=mListener;
		}

		public void disable() {
			this.enable=false;	
			if (listener!=null) listener.controllerDisable(this);
		}

		public void enable(int actionId, float startX, float startY) {
			
			this.actionId = actionId;
			this.sx = startX;
			this.sy = startY;
			this.x = startX;
			this.y = startY;
			this.enable=true;
			
			if (listener!=null) listener.controllerEnable(this);
		}

		public boolean isEnable() {			
			return enable;
		}

		public float[] getPositions() {
			return new float[]{x,y};
		}
		
		public float[] getGlPositions() {
			return new float[] {glKoefWidth * x - glWidth,
					- glKoefHeight * y + glHeight,
					0f};
		}

		public void move(float x2, float y2) {
			this.x = x2;
			this.y = y2;
		}

		public float getMovementX() {
			if (enable) {
				return (x-sx)*glSensitivity;
			} else {
				return 0f;
			}
						
		}
		
		public float getMovementY() {
			if (enable) {				
				return (y-sy)*glSensitivity;
			} else {
				return 0f;
			}			
		}
	}
}
