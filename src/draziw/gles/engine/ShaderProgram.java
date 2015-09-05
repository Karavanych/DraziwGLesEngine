package draziw.gles.engine;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderProgram {
	
	public int programHandler;
	
	public ShaderProgram(String mVertexShaderCode, String mFragmentShaderCode) {
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
