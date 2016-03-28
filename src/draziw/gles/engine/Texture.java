package draziw.gles.engine;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {
	
	public static final int PNG =0x0;
	public static final int ETC1=0x1;
	public static final int ETC2=0x2;	
	
	private Bitmap img1;
	private int resId;
	protected int id;
	private int slot;
	
	private int type;
	
	/// Cubemap
	private int negz;
	private int posz;
	private int negy;
	private int posy;
	private int negx;
	private int posx;
	
	private int compressType;	
	
	public Texture(int compressType,int mId,int mSlot,int mResId) {
		this.id=mId;
		this.slot=mSlot;
		this.type=GLES20.GL_TEXTURE_2D;
		this.resId=mResId;
		
		this.compressType=compressType;
	}
	
	public Texture(int mId,int mSlot, Bitmap mBitmap) {
		this.id=mId;
		this.slot=mSlot;
		this.type=GLES20.GL_TEXTURE_2D;
		this.img1=mBitmap;
		
		this.compressType=PNG;
	}

	public Texture(int compressType,int mId,int mSlot, int front,
			int left, int back, int right, int up, int down) {
		
		this.id=mId;
		this.slot=mSlot;
		this.type=GLES20.GL_TEXTURE_CUBE_MAP;


		setCubemapResId(front,left,back,right,up,down);
		
		this.compressType=compressType;
	}
	
	public void setCubemapResId(int front,
			int left, int back, int right, int up, int down) {
		
		this.posz=front;
		this.negx=right;
		this.negz=back;
		this.posx=left;
		this.posy=up;
		this.negy=down;
		
	}
	
	/*public Texture(int compressType,int mId,int mSlot, int posz,
			int negx, int negz, int posx, int posy, int negy) {
		
		this.id=mId;
		this.slot=mSlot;
		this.type=GLES20.GL_TEXTURE_CUBE_MAP;
		this.posz=posz;
		this.negx=negx;
		this.negz=negz;
		this.posx=posx;
		this.posy=posy;
		this.negy=negy;
	}*/

	
	public void use(int uSampler) {
		/* Active and Bind texture not needed if you dont replace it or changeю
		 we active and bind it in TextureLoader, all your texture in memory and shader can
		 get in by index from uSampler
		
		 Нет необходимости активировать и биндить текстуру каждый раз если мы их не меняем.
		 На данный момент все текстуры загружены в textureLoadere.
		 Шейдер имеет ко всем доступ через индекс переданный в uSampler
		 т.е. достаточно просто передать номер слота и он будет использовать текстуру, которая в нем 
		 забинджена.
		
		 Вот когда мы для отрисовки одного объекта будем использовать например 8 текстурных слотов,
		 тогда нам нужен будет перекомпоновывать текстурные слоты.
		 и надо будет не забывать очищать предыдущие слоты, потому что биндить одну текстуру в 2 разных слота нельзя 
		 */
		
		//GLES20.glActiveTexture(this.slot);
	    //GLES20.glBindTexture(this.type, this.id);
		GLES20.glUniform1i(uSampler,this.slot-GLES20.GL_TEXTURE0);
	    
	}
	
	public void reload(Context mContext,int imgId){
		this.resId=imgId;
		this.load(mContext);
	}
	
	public void reload(Context mContext,Bitmap img){
		
		img1=img;
		this.load(mContext);
	}
	
	
	public void load(Context mContext) {
		
		if (this.type==GLES20.GL_TEXTURE_2D) {
			loadTexture2D(mContext);												
		} else if (this.type==GLES20.GL_TEXTURE_CUBE_MAP) {
			loadTextureCubeMap(mContext);
		}		
	}
	
	
	public void loadTexture2D(Context mContext) {
		
		GLES20.glActiveTexture(this.slot);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.id);
        
        // параметры текстуры
     	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_LINEAR
     	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
     	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
     	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);		
		
     	loadByType(mContext, GLES20.GL_TEXTURE_2D, this.resId);
		
	}
	
	public void loadTextureCubeMap(Context mContext) {
		
		//загружаем CUBEMAP текстуру
						
		GLES20.glActiveTexture(this.slot);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, this.id);		
		
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR); // GLES20.GL_LINEAR
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);		
		
		loadByType(mContext, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, posx);
       /* InputStream is1 = mContext.getResources().openRawResource(posx);
        
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
        img1.recycle();*/
        

		loadByType(mContext, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, negx);
        /*is1 = mContext.getResources().openRawResource(negx);
        
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
        img1.recycle();*/
        
		loadByType(mContext, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, posy);
        /*is1 = mContext.getResources().openRawResource(posy);
       
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
        img1.recycle();*/
        
		loadByType(mContext, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, negy);
        /*is1 = mContext.getResources().openRawResource(negy);
        
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
        img1.recycle();*/
        
		loadByType(mContext, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, posz);
        /*is1 = mContext.getResources().openRawResource(posz);
       
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
        img1.recycle();*/
        
		loadByType(mContext, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, negz);
       /* is1 = mContext.getResources().openRawResource(negz);
        
        try {
        	img1 = BitmapFactory.decodeStream(is1);
        	
        } finally {
        	try {
        		is1.close();
        	} catch(IOException e) {
        		//e.printStackTrace();
        	}
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, img1, 0); */               
        
        //this.setSize(img1.getWidth(),img1.getHeight());
        
        //img1.recycle();
		
	}
	
	
	public void loadByType(Context mContext,int target,int imgId) {
		
		InputStream is1;     	
     	switch (compressType) {
     		case PNG:						       
			     if (img1==null) {   
			    	is1 = mContext.getResources().openRawResource(imgId);
			        try {			        	
			        	img1 = BitmapFactory.decodeStream(is1);
			        } finally {
			        	try {
			        		is1.close();
			        	} catch(IOException e) {
			        		//e.printStackTrace();
			        	}
			        }
			    }
		            
				GLUtils.texImage2D(target, 0, img1, 0);
			
		
				img1.recycle();
				img1=null;
				break;
				
     		case ETC1:
     			     		
				try {
					is1 = mContext.getResources().openRawResource(imgId);
					ETC1Util.loadTexture(target, 0, 0,  GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, is1);
					is1.close();
				} catch (NotFoundException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				} 
				break;     						
     		
     	}		
	}
}
