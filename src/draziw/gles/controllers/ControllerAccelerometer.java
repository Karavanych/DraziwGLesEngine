package draziw.gles.controllers;

import android.util.Log;
import draziw.gles.controllers.GameControllers.ControllersListener;

public class ControllerAccelerometer extends Controller {
	

	private float cx;		
	private float cy;
	private float sensitivity=0.1f;
	// калибровку по z не делаем, она будет у нас добавочной осью, при переполнении координаты x
	
	boolean needCalibrate=false;
	
	private float accelerometerMaxRange;

	public ControllerAccelerometer(GameControllers manager, int controllerType) {
		super(manager, controllerType);	
		enable(0,0,0);
		this.accelerometerMaxRange=manager.accelerometerMaxRange;
	}
	
	public ControllerAccelerometer(GameControllers manager,int controllersType, ControllersListener mListener) {
		super(manager, controllersType,mListener);
		enable(0,0,0);
		this.accelerometerMaxRange=manager.accelerometerMaxRange;
	}
	
	public void calibrate() {
		cx = x;
		cy = y;		
		
		needCalibrate=false;
	}
	
	public void calibrateNext() {
		needCalibrate=true;
	}

	public void event(float[] values) {
		x = values[0];
        y = values[1];
        z = values[2];	
        if (needCalibrate) calibrate();
        if (listener!=null) listener.controllerEnable(this);
	}	
	
	public void getValues(float[] values) {
		//Log.d("MyLogs", "("+x+","+y+","+z+")"+" cx="+cx+" cy="+cy);
	
		values[0]=x-cx;
		
		/*if (z<0) { // переворот оси z, нуждно добавочное движение из-за того что мы калибровали
			if (values[0]>0) {
				values[0]-=z;
			} else {
				values[0]+=z;
			}
		}*/
		
		if (z>0) {			
			values[0]=x-cx;
		} else {
			if (x>0) {
				values[0]=accelerometerMaxRange-x-cx;
			} else {
				values[0]=-accelerometerMaxRange+x-cx;
			}
		}
		
		values[1]=y-cy;
		values[2]=z;
		
		//Log.d("MyLogs", " results "+values[0]+","+values[1]+","+values[2]+"   accelerometerMaxRange="+accelerometerMaxRange);
		
		if (Math.abs(values[0])<sensitivity) values[0]=0;
		if (Math.abs(values[1])<sensitivity) values[1]=0;		
		
	}
	
	public void setSensitivity(float sensitivity) {
		this.sensitivity = sensitivity;
	}
	

}
