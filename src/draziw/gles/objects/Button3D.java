package draziw.gles.objects;

import draziw.gles.engine.Texture;
import draziw.gles.game.ResourceManager;
import draziw.gles.materials.Material;
import draziw.gles.objects.Custom3D;


public class Button3D extends Custom3D {

	public Button3D(Texture texture, Texture normalMap, Material material,
			ResourceManager resources, String modelName) {
		super(texture, normalMap, material, resources, modelName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isCollidePoint(float[] mPos) {
		if (mPos[0]>position[0]-geometry[0]*0.5f && mPos[0]<position[0]+geometry[0]*0.5f
				&& 	mPos[1]>position[1]-geometry[1]*0.5f && mPos[1]<position[1]+geometry[1]*0.5f) {
			return true;
		} else {
			return false;
		}		
	}

}
