package draziw.gles.engine;

public class MyMatrix {
	
	public static void matrix2dScale(float[] mTFA,float[] mAVector) { // умножает float[] 2x2 матрицу на float[] 2d вектор		
		for (int each=0;each<mTFA.length;each+=2) {			
			mTFA[each]*=mAVector[0];				
			mTFA[each+1]*=mAVector[1];
		}		
	}
	
	public static void matrix2dTranslate(float[] mTFA,float[] mAVector) { // складывает float[] 2x2 матрицу на float[] 2d вектор		
		for (int each=0;each<mTFA.length;each+=2) {
			mTFA[each]+=mAVector[0];	
			mTFA[each+1]+=mAVector[1];				
		}		
	}
	
	  /* Union of multiple arrays */
    public static float[] unionArrays(float[]... arrays)
    {
        int maxSize = 0;
        int counter = 0;

        for(float[] array : arrays) maxSize += array.length;
        float[] accumulator = new float[maxSize];

        for(float[] array : arrays)
            for(float i : array)               
                    accumulator[counter++] = i;       

        return accumulator;
    }
    
    
	public static void matrix3dScale(float[] mTFA,float[] mAVector) { // умножает float[] 3x3 матрицу на float[] 3d вектор		
		for (int each=0;each<mTFA.length;each+=3) {			
			mTFA[each]*=mAVector[0];				
			mTFA[each+1]*=mAVector[1];
			mTFA[each+2]*=mAVector[2];
		}		
	}
	
	public static void matrix3dTranslate(float[] mTFA,float[] mAVector) { // складывает float[] 3x3 матрицу на float[] 3d вектор			
		for (int each=0;each<mTFA.length;each+=3) {			
			mTFA[each]+=mAVector[0];				
			mTFA[each+1]+=mAVector[1];
			mTFA[each+2]+=mAVector[2];
		}		
	}
	
	public static String Matrix4ToString(float[] mTFA) {
		String txt="";
		for (int each=0;each<mTFA.length;each+=4) {
			txt+="\n"+mTFA[each]+","+mTFA[each+1]+","+mTFA[each+2]+","+mTFA[each+3];				
		}
		return txt;		
	}
	
	public static void ArrayFill(float[] array, float value) {
        for (int i = 0; i < array.length; i++) {
            array[i] = value;
        }
    }
	
	public static void ArrayFill(float[] array, float[] values) {
		if (values.length>0 && array.length%values.length==0) {
	        for (int i = 0; i < array.length; i+=values.length) {
	        	for (int o=0;o<values.length;o++) {
	        		array[i+o] = values[o];
	        	}	            
	        }
		}
    }
	
	public static void vec3set(float[] vec3, float x, float y, float z) {
		vec3[0] = x;
		vec3[1] = y;
		vec3[2] = z;
	}
	
	public static void vec3add(float[] base, float[] add, float koef) {
		base[0] += add[0] * koef;
		base[1] += add[1] * koef;
		base[2] += add[2] * koef;
	}

	public static void vec3add(float[] vec3, float x, float y, float z) {
		vec3[0] += x;
		vec3[1] += y;
		vec3[2] += z;
	}
	
}
