package draziw.gles.controllers;

import draziw.gles.controllers.GameControllers.ControllersListener;

public class ControllerStick extends Controller {

	public ControllerStick(GameControllers manager, int controllerType) {
		super(manager, controllerType);		
	}
	
	public ControllerStick(GameControllers manager,int controllersType, ControllersListener mListener) {
		super(manager,controllersType,mListener);
	}
	

}
