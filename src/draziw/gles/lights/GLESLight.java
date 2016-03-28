package draziw.gles.lights;

import draziw.gles.engine.Texture;
import draziw.gles.materials.Material;
import draziw.gles.objects.GLESObject;

public abstract class GLESLight extends GLESObject {
	
	public static final int POINT_LIGHT=0x0;
	public static final int DIRECTION_LIGHT=0x1;

	public GLESLight(Texture texture, Material material) {
		super(texture, material);
	}
	
	public abstract float[] getMV(float[] viewMatrix);
	
	public abstract int getType();



}
