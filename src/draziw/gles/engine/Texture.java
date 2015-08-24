package draziw.gles.engine;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	private int resId;
	protected int id;
	private int slot;
	private int type;
	private int sizeX,sizeY = 0;	
	
	public Texture(int mType,int mId,int mSlot,int mResId) {
		this.id=mId;
		this.slot=mSlot;
		this.type=mType;
		this.resId=mResId;
	}
	
	public void setSize(int mSizeX, int mSizeY) {
		this.sizeX=mSizeX;
		this.sizeY=mSizeY;
	}
	
	public void use(int uSamplerHolder) {
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
	    GLES20.glUniform1i(uSamplerHolder,this.slot-GLES20.GL_TEXTURE0);
	}
	
	public void reload(Context mContext,int mTextureId){
		this.id=mTextureId;
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
		
		
        InputStream is1 = mContext.getResources().openRawResource(this.resId);
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
            
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img1, 0);
		
		//textures.add(new Texture(GLES20.GL_TEXTURE_2D,mTextureId,mTextureSlot,img1.getWidth(),img1.getHeight(),resId));		// add (id, index, sizeX,SizeY
		this.setSize(img1.getWidth(),img1.getHeight());		
		img1.recycle();
		
	}
	
	public void loadTextureCubeMap(Context mContext) {
		
		//загружаем CUBEMAP текстуру
		
		GLES20.glActiveTexture(this.slot);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, this.id);		
		
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
        
        this.setSize(img1.getWidth(),img1.getHeight());
        
        img1.recycle();
		
	}
}
