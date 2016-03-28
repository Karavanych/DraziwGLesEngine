package draziw.gles.engine;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

public class TextureLoader {
/*	
	public static final String DEFAULT_BASE="DEFAULT_BASE_TEXTURE";
	public static final String DEFAULT_NORMAL_MAP="DEFAULT_NORMAL_TEXTURE";
	public static final String DEFAULT_CUBE_MAP="DEFAULT_CUBE_MAP";
	public static final String DEFAULT_GUI="DEFAULT_GUI_TEXTURE";
	public static final String DEFAULT_FONT="DEFAULT_FONT";*/
	
	public ArrayList<Texture> textures=new ArrayList<Texture>();
	//public int[] texturesId;
	private Context context;	
	
	public TextureLoader(Context cc) {
		context=cc;
	}	
			
	
	public void loadTextures() {					
		
			/*if (textures.size()>0) textures.clear();
		
			int[] texturesId = new int[3];
		
			GLES20.glGenTextures(texturesId.length, texturesId, 0);					
			
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
			     
			// 1ая текстура
			Texture mTexture = 	new Texture(GLES20.GL_TEXTURE_CUBE_MAP,
										texturesId[0],
										GLES20.GL_TEXTURE0,
										0);						        
			mTexture.load(context);
			textures.add(mTexture);
	        	        
	        // 2ая текстура	 
			mTexture = 			new Texture(GLES20.GL_TEXTURE_2D,
										texturesId[1],
										GLES20.GL_TEXTURE1,
										R.drawable.basetexture1);
			mTexture.load(context);
			textures.add(mTexture);
			
	        // 3я текстура	 
			mTexture = 			new Texture(GLES20.GL_TEXTURE_2D,
										texturesId[2],
										GLES20.GL_TEXTURE2,
										R.drawable.texturegui);
			mTexture.load(context);
			textures.add(mTexture);	*/        
	        										
	}
	
	public void deleteSingleTexture(int position) {
		Texture delTex = textures.get(position);
		int[] texturesId = new int[]{delTex.id};			
		GLES20.glDeleteTextures(1,texturesId,0);
		textures.remove(position);
		delTex=null;
	}
	
	public Texture loadCubeMapTexture(int front,int left,int back,int right,int up, int down,int slotId,int compression) {
		
		int[] texturesId = new int[1];
		
		GLES20.glGenTextures(texturesId.length, texturesId, 0);					
		
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		
		Texture mTexture = 	new Texture(compression,
				texturesId[0],
				slotId,
				front,left,back,right,up,down);						        
		mTexture.load(context);
		textures.add(mTexture);
		
		return mTexture;
	
}
	
	public Texture loadSingleTexture(int resId,int slotId,int compression) {
				
			int[] texturesId = new int[1];
			
			GLES20.glGenTextures(texturesId.length, texturesId, 0);					
			
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
			
			Texture mTexture = 	new Texture(compression,
					texturesId[0],
					slotId,
					resId);						        
			mTexture.load(context);
			textures.add(mTexture);	
			
			return mTexture;
		
	}
	
	public Texture loadSingleTexture(Bitmap bitmap,int slotId) {				
		
			int[] texturesId = new int[1];
			
			GLES20.glGenTextures(texturesId.length, texturesId, 0);					
			
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
			
			Texture mTexture = 	new Texture(texturesId[0],
					slotId,
					bitmap);						        
			mTexture.load(context);
			textures.add(mTexture);	
			
			return mTexture;
		
	}
	
	public void confirmTextures() {	
		
		if (textures!=null && textures.size()>0) {
			
			for (Texture each:textures) {
				if (!GLES20.glIsTexture(each.id)) {
					Log.d("MyLogs", "texture restored "+each.id);
					int[] texturesId = new int[]{each.id};					
					GLES20.glDeleteTextures(1,texturesId,0);					
					GLES20.glGenTextures(1, texturesId,0);
					
					each.reload(context, texturesId[0]);					
				}
			}
		}										
	}

	public Texture getTexture(int i) {		
		return textures.get(i);
	}


	public void clearAll() {
		if (textures != null && textures.size() > 0) {
			int[] texturesId = new int[textures.size()];
			for (int i = 0; i < textures.size(); i++) {
				texturesId[i] = textures.get(i).id;
			}
			GLES20.glDeleteTextures(texturesId.length, texturesId, 0);
		}
	}

}
