package draziw.gles.controllers;

public class SliderActor {
	
	public interface SliderActions {
		public void onSlideLeft();
		public void onSlideRight();
		public void onSlideCancel(float x);
		public void onSlideChanges(float x);
	}
	
	public float posX=0;
	
	private float sensitivity=2;
	
	private SliderActions listener;
	
	public SliderActor(float sens,SliderActions listener) {
		this.sensitivity=sens;
		this.listener=listener;
	}
	

		
	public void moveByController(float timer, GameControllers controllers) {
		Controller controllersLeft = controllers
				.getControllerByType(GameControllers.CONTROLLER_LEFT);
		
		boolean hasEnabled=false;
		
		if (controllersLeft.isEnable()) {
			hasEnabled=true;

			if (Math.abs(controllersLeft.getMovementX()) > 0.02) {
				posX+=controllersLeft.getMovementX();
			}
		}

		Controller controllersRight = controllers
				.getControllerByType(GameControllers.CONTROLLER_RIGHT);

		if (controllersRight.isEnable()) {
			hasEnabled=true;

			if (Math.abs(controllersRight.getMovementX()) > 0.02) {
				posX+=controllersRight.getMovementX();
			}
		}
		
		if (posX>sensitivity) {
			posX=0;
			listener.onSlideRight();
		}
		
		if (posX<-sensitivity) {
			posX=0;
			listener.onSlideLeft();
		}
		
		if (posX!=0) {
			if (hasEnabled) {
				listener.onSlideChanges(posX);
			} else {				
				listener.onSlideCancel(posX);
				posX=0;
			}
		}
		
	}

}
