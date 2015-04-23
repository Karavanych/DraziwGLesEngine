package dont.touch.white;

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

}
