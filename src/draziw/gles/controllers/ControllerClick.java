package draziw.gles.controllers;

import draziw.gles.controllers.GameControllers.ControllersListener;

public class ControllerClick extends Controller {

	public ControllerClick(GameControllers manager, int controllerType) {
		super(manager, controllerType);		
	}
	
	public ControllerClick(GameControllers manager,int controllersType, ControllersListener mListener) {
		super(manager, controllersType,mListener);
	}

}
