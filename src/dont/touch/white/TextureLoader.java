package dont.touch.white;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureLoader {
	
	public ArrayList<Texture> textures=new ArrayList<Texture>();
	public int[] texturesId;
	private Context mContext;
	
	public TextureLoader(Context cc) {
		mContext=cc;
	}

	public ArrayList<Texture> loadTexture() {
		texturesId = new int[2];		
		
		GLES20.glGenTextures(1, texturesId, 0);		
	
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[0]);
		
        InputStream is1 = mContext.getResources().openRawResource(R.drawable.itachi);
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
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img1, 0);

		textures.add(new Texture(texturesId[0],textures.size(),img1.getWidth(),img1.getHeight()));		// add (id, index, sizeX,SizeY
		
		img1.recycle();
		
		// параметры текстуры
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_LINEAR
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		
		
		// 2ая текстура
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[1]);
		
	    /*is1 = mContext.getResources().openRawResource(R.drawable.girl);
        Bitmap img2;
        try {
        	img2 = BitmapFactory.decodeStream(is1);
        } finally {
        	try {
        		is1.close();
        	} catch(IOException e) {
        		//e.printStackTrace();
        	}
        }*/
		
		Bitmap img2=Font2D.generateFontAtlas();
        
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img2, 0);	
		
		textures.add(new Texture(texturesId[1],textures.size(),img2.getWidth(),img2.getHeight()));		// add (id, index, sizeX,SizeY
		img2.recycle();
		
		// параметры текстуры
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_LINEAR
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		
		return textures;
	}

}
