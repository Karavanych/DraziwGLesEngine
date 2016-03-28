package draziw.gles.math;

public class MyMath {

	static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
	
	static public final float PI = 3.1415927f;
	static public final float PI2 = PI * 2;
	
	static public final float radiansToDegrees = 180f / PI;
	
	static public final float degreesToRadians = PI / 180;
	
	static public float clamp (float value, float min, float max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	
	static public boolean isEqual (float a, float b) {
		return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}
	
	static public boolean isEqual (float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}
	
	static public boolean isZero (float value) {
		return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
	}
	
	static public boolean isZero (float value, float tolerance) {
		return Math.abs(value) <= tolerance;
	}
	
	
	public static float vector3Len (final float x, final float y, final float z) {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public static float vector3Dot (float[] vector1,float[] vector2) {
		return vector1[0] * vector2[0] + vector1[1] * vector2[1] + vector1[2] * vector2[2];
	}
	
	/** @return The dot product between the two vectors */
	public static float vector3Dot (float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}
	
	public static long randomT(int i) {
		return System.currentTimeMillis()%i;
	}
	
	
}
