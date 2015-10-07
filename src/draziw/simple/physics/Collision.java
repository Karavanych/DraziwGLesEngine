package draziw.simple.physics;

public class Collision {
	
	// direction x = left, right - +
	// direction y = bottom, top - +
	// direction z = forward, back - +
	
	
	
	public static final int NO_COLLISION=0x0000;
	
	public static final int X_INCR=0x0001;
	public static final int X_DECR=0x0002;
	
	public static final int Y_INCR=0x0004;
	public static final int Y_DECR=0x0008;
		
	public static final int Z_INCR=0x0010;
	public static final int Z_DECR=0x0020;
	
	public float xIncrCol;
	public float xDecrCol;
	
	public float yIncrCol;
	public float yDecrCol;
	
	public float zIncrCol;
	public float zDecrCol;
	
	public int colDirection;
	
	
	public void clearCollisions() {		
		xIncrCol=-Float.MAX_VALUE;
		yIncrCol=-Float.MAX_VALUE;
		zIncrCol=-Float.MAX_VALUE;
		
		xDecrCol=Float.MAX_VALUE;
		yDecrCol=Float.MAX_VALUE;
		zDecrCol=Float.MAX_VALUE;	
		
		colDirection=NO_COLLISION;
	}
	
	public void add(int direction,float position) {
		colDirection|=direction;
		switch (direction) {
			case X_INCR:
				xIncrCol=Math.max(xIncrCol, position);
				break;
			case X_DECR:
				xDecrCol=Math.min(xDecrCol, position);
				break;
			case Y_INCR:
				yIncrCol=Math.max(yIncrCol, position);
				break;
			case Y_DECR:
				yDecrCol=Math.min(xDecrCol, position);
				break;
			case Z_INCR:
				zIncrCol=Math.max(zIncrCol, position);
				break;
			case Z_DECR:
				zDecrCol=Math.min(zDecrCol, position);
				break;				
		}	
	}
	
	public void add(int direction) {
		colDirection|=direction;
	}
	
	public boolean isCollision(int direction) {
		return (colDirection & direction)==direction;
	}
}
