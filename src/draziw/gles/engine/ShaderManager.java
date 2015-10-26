package draziw.gles.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Context;
import android.opengl.GLES20;

public class ShaderManager {
	HashMap<String,ShaderProgram> shaders=new HashMap<String, ShaderProgram>();
	private Context context;
	
	
	public ShaderManager(Context mContext) {
		this.context=mContext;		
		
	}
	
	public ShaderProgram getShader(String name) {
		ShaderProgram curShader = shaders.get(name);
		if (curShader==null) {
			curShader= new ShaderProgram(name,context);
			shaders.put(name, curShader);
		}
		return curShader;						
	}
	
	public int getShaderHandler(String name) {
		return getShader(name).programHandler;
	}
	
	public void clear() {
		if (shaders.size()>0) {
			Iterator<Entry<String, ShaderProgram>> it = shaders.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<String,ShaderProgram> pair = (HashMap.Entry<String,ShaderProgram>)it.next();
		        GLES20.glDeleteProgram(pair.getValue().programHandler);	        
		        it.remove();
		    }
		}
	}

}
