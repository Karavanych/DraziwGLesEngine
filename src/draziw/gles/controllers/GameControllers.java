package draziw.gles.controllers;

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

	public static final int CONTROLLER_NO = 0x0;
	public static final int CONTROLLER_LEFT = 0x1;
	public static final int CONTROLLER_RIGHT = 0x2;
	public static final int CONTROLLER_CLICK = 0x4;
	public static final int CONTROLLER_ACCELEROMETER = 0x8;
		

	int width;
	int height;
	float glWidth;
	float glHeight;
	
	float glKoefWidth = 1;
	float glKoefHeight = 1;
	float glSensitivity=1;
	float accelerometerMaxRange=0;
	
	public int enabledControllers=CONTROLLER_NO;

	//ArrayList<Controller> controlles = new ArrayList<Controller>();
	HashMap<Integer, Controller> controllersByType=new HashMap<Integer,Controller>();
	HashMap<Integer, Controller> controllersById=new HashMap<Integer,Controller>();
	
	

	public GameControllers(float accelMaxRange) {
		this.accelerometerMaxRange=accelMaxRange;		
	}
	
	public void init(int width, int height, float glWidth,float glHeight) {
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
	}
	
	public Controller enableController(int controllersType,ControllersListener mListener) {
		
		enabledControllers|=controllersType;
		Controller curContr=controllersByType.get(controllersType);
		if (curContr==null) {
			switch (controllersType) {
				case CONTROLLER_LEFT:
					curContr=new ControllerStick(this,controllersType,mListener);					
					break;
				case CONTROLLER_RIGHT:
					curContr=new ControllerStick(this,controllersType,mListener);
					break;
				case CONTROLLER_CLICK:
					curContr=new ControllerClick(this,controllersType,mListener);
					break;
				case CONTROLLER_ACCELEROMETER:
					curContr=new ControllerAccelerometer(this,controllersType,mListener);
					break;
			}
			controllersByType.put(controllersType,curContr);
		} else {
			curContr.setListener(mListener);
		}
		return curContr;
	}
	
	public Controller enableController(int controllersType) {
		return enableController(controllersType,null);
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

		// ¬се контроллеры уже созданы зараннее,
		// в этом методе мы только включаем контроллеры и ассоциируем их с точкой касани€ 
					
		int controllerType = getControllerZone(x, y);
				
		Controller currentController = controllersByType.get(controllerType);				
		
		if (currentController.isEnable()) return;
						
		currentController.enable(actionIndex, x, y);
		controllersById.put(actionIndex,currentController);
		
		if (isEnable(CONTROLLER_CLICK)) {			
			currentController = controllersByType.get(CONTROLLER_CLICK);									
			// клик это всегда последнее касание...и первое подн€тие... не зависит от actionIndex
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

	public void removeAllSticks(int pointerId, float x, float y) {		
		
		/*// »тератор делаем по типу контролеера, все выключаем
		Iterator<Entry<Integer, Controller>> iterator = controllersByType.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Controller> pair = iterator.next();
			Controller current = pair.getValue();
			if (current.isEnable()) current.disable();			
		}*/
		
		if (controllersById.get(pointerId)==null && isEnable(CONTROLLER_CLICK)) {
				// ≈сли включен контроллер на клик, но не найден указатель на pointer,
			    // значит это быстрый клик одновременно с поъемом,
				// поэтому надо клик отработать
				controllersByType.get(CONTROLLER_CLICK).enable(0, x, y);																
			
		}
		 
		controllersByType.get(CONTROLLER_LEFT).disable();
		controllersByType.get(CONTROLLER_RIGHT).disable();
		// а затем очищаем полностью мап по id
		controllersById.clear();
	}

	public void move(MotionEvent event) {
		int pointerCount = event.getPointerCount();	
		int pointerId;
		int pointerIdx;
		for (int i = 0; i < pointerCount; i++) {
			pointerId = event.getPointerId(i);
			
			Controller current = getControllerById(pointerId);
			if (current != null) {
				current.move(event.getX(i), event.getY(i));
			}
			else { // а вот бывает что есть просто move без down
				if (pointerCount<2) {
					pointerIdx = event.getActionIndex();
					add(pointerId, event.getX(pointerIdx),event.getY(pointerIdx));
				} else {
					add(pointerId, event.getX(pointerId),event.getY(pointerId));
				}
					
			}
		}
	}

	public Controller getControllerById(int id) {
		return controllersById.get(id);
	}

	public Controller getControllerByType(int controllerType) {
		return controllersByType.get(controllerType);		
	}

	public void clearAll() {
		controllersByType.clear();
		controllersById.clear();		
	}

	/*public float[] getGlPosition(int controllerType) {
		Controller current = getControllerByType(controllerType);

		return new float[] { glKoefWidth * current.x - 1.0f,
				glKoefHeight * current.y - 1.0f,
				0f};
	}*/

}
