package draziw.gles.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class ShaderProgram {
	
	public int programHandler;
	public String name;
	
	
	public ShaderProgram(String name,Context context) {
			this.name=name;

			// load from file resource
			String shaderStr="";			
	        Resources res = context.getResources();
	        String packageName = context.getApplicationContext().getPackageName();	        
	        //+ ":raw/" + name;
	        int res_id = res.getIdentifier(packageName+":raw/" + name, null, packageName);
	        //Log.d("MyLogs", fileName);
	        //int res_id = res.getIdentifier(this.getClass().getPackage().getName() + ":raw/" + name+".shr", null, null);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(res.openRawResource(res_id)));
	        String sline ="";	        
	        while(sline!=null){
	            try {
	                sline = reader.readLine();
	                if(sline!=null)shaderStr+=sline+"\n";
	            } catch (IOException e) {}
	        }
       
		
	        String[] split_vs_fr = shaderStr.split(Pattern.quote("[FRAGMENT]"));
	        String o_vs=split_vs_fr[0];
	        String o_fr=split_vs_fr[1];
	        
	        attachProgram(o_vs,o_fr);
		
	}
	
	public ShaderProgram(String mVertexShaderCode, String mFragmentShaderCode) {
		attachProgram(mVertexShaderCode,mFragmentShaderCode);		
	}
	
	
	public void attachProgram(String mVertexShaderCode, String mFragmentShaderCode) {
		int pointVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
		int pointFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
		programHandler = GLES20.glCreateProgram();
		GLES20.glAttachShader(programHandler, pointVertexShader);
		GLES20.glAttachShader(programHandler, pointFragmentShader);
		GLES20.glLinkProgram(programHandler);	
		
		  // Получаем ссылку на программу.
	    final int[] linkStatus = new int[1];
	    GLES20.glGetProgramiv(programHandler, GLES20.GL_LINK_STATUS, linkStatus, 0);
	 
	    // Если ссылку не удалось получить, удаляем программу.
	    if (linkStatus[0] == 0)
	    {
	        GLES20.glDeleteProgram(programHandler);
	        programHandler = 0;
	    }
	    
	    if (programHandler == 0)
	    {
	        throw new RuntimeException("Error creating program.");
	    }
	    
	}	
	
	private int loadShader(int type, String source) {
		 int shader = GLES20.glCreateShader(type);
		 GLES20.glShaderSource(shader, source);
		 GLES20.glCompileShader(shader);
		 
         int[] compiled = new int[1];
         GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
         if (compiled[0] == 0) {
        	 Log.e("MyLogs","compile shader Error");
        	 Log.e("Error", "Could not compile shader name:" + source + " =>" +type+ ":");
             Log.e("Error", GLES20.glGetShaderInfoLog(shader));
             GLES20.glDeleteShader(shader);
             shader = 0;
         }
		 
		 return shader;
		}
	
	public static void onAllLoaded() {
		GLES20.glReleaseShaderCompiler();
	}
	
}
