package draziw.gles.engine;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLES20;

public class TextureLoader {
	
	public ArrayList<Texture> textures=new ArrayList<Texture>();
	//public int[] texturesId;
	private Context context;	
	
	public TextureLoader(Context cc) {
		context=cc;
	}	
			
	
	public void loadTextures() {
		
			if (textures.size()>0) textures.clear();
		
			int[] texturesId = new int[2];
		
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
										R.drawable.texture1);
			mTexture.load(context);
			textures.add(mTexture);
	        
	        										
	}
	
	public void confirmTextures() {	
		
		if (textures!=null && textures.size()>0) {
			
			for (Texture each:textures) {
				if (!GLES20.glIsTexture(each.id)) {
					
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

}
