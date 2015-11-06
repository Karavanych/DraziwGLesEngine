package draziw.gles.materials;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.opengl.GLES20;
import android.util.Log;
import draziw.gles.engine.ShaderManager;
import draziw.gles.engine.ShaderProgram;
import draziw.gles.objects.GLESObject;

public abstract class Material {
	
	public int aPosition;
	public int aNormal;
	public int aTangent;
	public int aBitangent;
	public int aTextureCoord;
	public int umvp;
	public int um;
	public int uv;
	public int uBaseMap;
	public int uNormalMap;
	
	public static final boolean CONFIRMATION=true;
	
	public int shaderProgramHandler;
		
	public abstract void initializeShaderParam();
	
	public abstract void applyMaterialParams(float[] viewMatrix,float[] projectionMatrix);
	public abstract void applyObjectParams(float[] viewMatrix,float[] projectionMatrix,GLESObject mObject);
	
	private HashMap<String,Integer> confirmationMap;// uniforms and attributes holders	
	
	public Material(ShaderManager shaders,String shaderName) {		
		this(shaders.getShader(shaderName));								
	}		

	public Material(ShaderProgram shader) {
		if (CONFIRMATION) {
			confirmationMap=new HashMap<String,Integer>();
		}
		this.shaderProgramHandler=shader.programHandler;		
		this.initializeShaderParam();
		if (CONFIRMATION) {
			this.confirmShader(shader.name);
		}
	}
	
	public int glGetAttribLocation(int shaderHandler,String name) {
		int handler=GLES20.glGetAttribLocation(shaderHandler, name);
		if (CONFIRMATION) confirmationMap.put(name,handler);
		return handler;
	}
	
	public int glGetUniformLocation(int shaderHandler,String name) {
		int handler=GLES20.glGetUniformLocation(shaderHandler, name);
		if (CONFIRMATION) confirmationMap.put(name,handler);
		return handler;
	}
	
	public boolean confirmShader(String shaderName) {
		if (CONFIRMATION) {
			StringBuilder errorLog=new StringBuilder();
			Iterator<Entry<String, Integer>> it = confirmationMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry<String, Integer> pair = (HashMap.Entry<String,Integer>)it.next();
		        if (pair.getValue()==-1) {
		        	errorLog.append("\n"+pair.getKey());
		        }
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    
		    if (errorLog.length()>0) {
		    	errorLog.insert(0, "shader = "+shaderName+" unifor or attribute error");
		    	Log.e("MyLogs",errorLog.toString());
		    	return false;
		    }
		}
		return true;		
	}

}
