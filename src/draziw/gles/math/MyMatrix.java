package draziw.gles.math;

public class MyMatrix {
	
	public static final int M00 = 0;
	public static final int M01 = 4;
	public static final int M02 = 8;
	public static final int M03 = 12;
	public static final int M10 = 1;
	public static final int M11 = 5;
	public static final int M12 = 9;
	public static final int M13 = 13;
	public static final int M20 = 2;
	public static final int M21 = 6;
	public static final int M22 = 10;	
	public static final int M23 = 14;	
	public static final int M30 = 3;	
	public static final int M31 = 7;	
	public static final int M32 = 11;	
	public static final int M33 = 15;
	
	
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
	
	public static String Vector3ToString(float[] mVec) {
		String txt="";
		for (int each=0;each<mVec.length;each++) {
			txt+=","+mVec[each];
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
	
	public static float normalize(float[] vec3) {
		/*final float len2 = this.len2();
		if (len2 == 0f || len2 == 1f) return this;
		return this.scl(1f / (float)Math.sqrt(len2));*/
		final float len2=vec3[0]*vec3[0]+vec3[1]*vec3[1]+vec3[2]*vec3[2];
		if (len2 == 0f || len2 == 1f) return 0f;
		final float len1 = (float)Math.sqrt(len2);
		final float oneDivLen1=1f/len1;
		vec3[0]*=oneDivLen1;
		vec3[1]*=oneDivLen1;
		vec3[2]*=oneDivLen1;	
		return len1;
	}
	
	public static float[] cross(float[] a,float[] b) {		
		return new float[]{
				a[1]*b[2]-a[2]*b[1],
				a[2]*b[0]-a[0]*b[2],
				a[0]*b[1]-a[1]*b[0]
		};
	}
	
	public static float[] cross(float a0,float a1,float a2,float b0,float b1,float b2) {		
		return new float[]{
				a1*b2-a2*b1,
				a2*b0-a0*b2,
				a0*b1-a1*b0
		};
	}
	
	public static void setFromQuaternion (float[] matrix4,float translationX, float translationY, float translationZ, Quaternion quaternion) {
		final float xs = quaternion.x * 2f, ys = quaternion.y * 2f, zs = quaternion.z * 2f;
		final float wx = quaternion.w * xs, wy = quaternion.w * ys, wz = quaternion.w * zs;
		final float xx = quaternion.x * xs, xy = quaternion.x * ys, xz = quaternion.x * zs;
		final float yy = quaternion.y * ys, yz = quaternion.y * zs, zz = quaternion.z * zs;

		matrix4[M00] = (1.0f - (yy + zz));
		matrix4[M01] = (xy - wz);
		matrix4[M02] = (xz + wy);
		matrix4[M03] = translationX;

		matrix4[M10] = (xy + wz);
		matrix4[M11] = (1.0f - (xx + zz));
		matrix4[M12] = (yz - wx);
		matrix4[M13] = translationY;

		matrix4[M20] = (xz - wy);
		matrix4[M21] = (yz + wx);
		matrix4[M22] = (1.0f - (xx + yy));
		matrix4[M23] = translationZ;

		matrix4[M30] = 0.f;
		matrix4[M31] = 0.f;
		matrix4[M32] = 0.f;
		matrix4[M33] = 1.0f;			
	}

	public void setFromQuaternion (float[] matrix4,float translationX, float translationY, float translationZ,Quaternion quaternion, float scaleX, float scaleY, float scaleZ) {
			final float xs = quaternion.x * 2f, ys = quaternion.y * 2f, zs = quaternion.z * 2f;
			final float wx = quaternion.w * xs, wy = quaternion.w * ys, wz = quaternion.w * zs;
			final float xx = quaternion.x * xs, xy = quaternion.x * ys, xz = quaternion.x * zs;
			final float yy = quaternion.y * ys, yz = quaternion.y * zs, zz = quaternion.z * zs;
	
			matrix4[M00] = scaleX * (1.0f - (yy + zz));
			matrix4[M01] = scaleY * (xy - wz);
			matrix4[M02] = scaleZ * (xz + wy);
			matrix4[M03] = translationX;
	
			matrix4[M10] = scaleX * (xy + wz);
			matrix4[M11] = scaleY * (1.0f - (xx + zz));
			matrix4[M12] = scaleZ * (yz - wx);
			matrix4[M13] = translationY;
	
			matrix4[M20] = scaleX * (xz - wy);
			matrix4[M21] = scaleY * (yz + wx);
			matrix4[M22] = scaleZ * (1.0f - (xx + yy));
			matrix4[M23] = translationZ;
	
			matrix4[M30] = 0.f;
			matrix4[M31] = 0.f;
			matrix4[M32] = 0.f;
			matrix4[M33] = 1.0f;			
		}

	
}
