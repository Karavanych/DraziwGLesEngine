package draziw.gles.controllers;

import draziw.gles.controllers.GameControllers.ControllersListener;

public abstract class Controller {
	private boolean enable=false;
	public int type;
	public int actionId;
	public float sx, sy; // start x, start y
	public float x, y, z; // current x, current y, z- for accelerometer
	ControllersListener listener;
	
	private GameControllers manager;


	public Controller(GameControllers manager,int controllerType) {
		this.type=controllerType;
		this.manager=manager;
		
	}

	public Controller(GameControllers manager,int controllersType, ControllersListener mListener) {
		this(manager,controllersType);
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
		return new float[] {manager.glKoefWidth * x - manager.glWidth,
				- manager.glKoefHeight * y + manager.glHeight,
				0f};
	}

	public void move(float x2, float y2) {
		this.x = x2;
		this.y = y2;
	}

	public float getMovementX() {
		if (enable) {
			return (x-sx)*manager.glSensitivity;
		} else {
			return 0f;
		}
					
	}
	
	public float getMovementY() {
		if (enable) {				
			return (y-sy)*manager.glSensitivity;
		} else {
			return 0f;
		}			
	}

	public void setListener(ControllersListener mListener) {
		this.listener=mListener;		
	}
}