package draziw.gles.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureLoader {
	
	public ArrayList<Texture> textures=new ArrayList<Texture>();
	public int[] texturesId;
	private Context mContext;	
	
	public TextureLoader(Context cc) {
		mContext=cc;
	}	
	
	
	public ArrayList<Texture> loadTexture() {
		
			if (textures.size()>0) textures.clear();
		
			texturesId = new int[2];
		
			GLES20.glGenTextures(2, texturesId, 0);					
			
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
			     
			
			//��������� CUBEMAP ��������
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, texturesId[0]);		
			
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GLES20.GL_LINEAR
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			
	
	        InputStream is1 = mContext.getResources().openRawResource(R.drawable.posx);
	        
	        Bitmap img1;
			try {
	        	img1 = BitmapFactory.decodeStream(is1);
	        	
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, img1, 0);
	        img1.recycle();
	        
	
	        
	        is1 = mContext.getResources().openRawResource(R.drawable.negx);
	        
	        try {
	        	img1 = BitmapFactory.decodeStream(is1);	        	
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, img1, 0);
	        img1.recycle();
	        
	        is1 = mContext.getResources().openRawResource(R.drawable.posy);
	       
	        try {
	        	img1 = BitmapFactory.decodeStream(is1);
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, img1, 0);
	        img1.recycle();
	        
	        is1 = mContext.getResources().openRawResource(R.drawable.negy);
	        
	        try {
	        	img1 = BitmapFactory.decodeStream(is1);
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, img1, 0);
	        img1.recycle();
	        
	        is1 = mContext.getResources().openRawResource(R.drawable.posz);
	       
	        try {
	        	img1 = BitmapFactory.decodeStream(is1);
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, img1, 0);
	        img1.recycle();
	        
	        is1 = mContext.getResources().openRawResource(R.drawable.negz);
	        
	        try {
	        	img1 = BitmapFactory.decodeStream(is1);
	        	
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, img1, 0);
	        
	        textures.add(new Texture(texturesId[0],textures.size(),img1.getWidth(),img1.getHeight()));		// add (id, index, sizeX,SizeY
	        
	        img1.recycle();
	        
	
	        
	        
	        // 2�� ��������
	        
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[1]);
			
			
			
	        is1 = mContext.getResources().openRawResource(R.drawable.texture1);
	
	        try {
	        	img1 = BitmapFactory.decodeStream(is1);
	        } finally {
	        	try {
	        		is1.close();
	        	} catch(IOException e) {
	        		//e.printStackTrace();
	        	}
	        }                        
	        
	        //GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img1, 0);
	
			textures.add(new Texture(texturesId[1],textures.size(),img1.getWidth(),img1.getHeight()));		// add (id, index, sizeX,SizeY
			
			img1.recycle();
			
			// ��������� ��������
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_LINEAR
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
	        						
		
		return textures;
	}
	
	public void confirmTextures() {
		boolean needReload=false;
		
		if (texturesId!=null && texturesId.length>0) {
			
			for (int each:texturesId) {
				needReload=(needReload || !GLES20.glIsTexture(each));
				Log.d("MyLogs", "texture "+each+" is OK!");
			}
			
			if (needReload) {
				GLES20.glDeleteTextures(texturesId.length, texturesId,0);
			}			
						
		} else {
			needReload=true;
		}
		
		if (needReload) {
			this.loadTexture();
		}
	}


	public Texture getTexture(int i) {		
		return textures.get(i);
	}

}
