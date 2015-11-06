package draziw.gles.materials;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Color;
import android.opengl.GLES20;
import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;

public class MaterialPoint extends Material {

	public int aColor;

	private FloatBuffer colorBuffer;

	public MaterialPoint(ShaderManager shaders) {
		super(shaders, "point");
		setColor(Color.YELLOW);
	}

	@Override
	public void initializeShaderParam() {
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aColor = glGetAttribLocation(shaderProgramHandler, "aColor");		
		umvp=glGetUniformLocation(shaderProgramHandler, "mvp");		
	}
	
	public void setColor(int color) {
		
		float r = ((color >> 16) & 0xFF)/255f;
		float g = ((color >> 8) & 0xFF)/255f;
		float b = ((color >> 0) & 0xFF)/255f;
		
		//  R,G,B,A
		float[] planeTFA = {
				
		r,g,b,1f
			
		};

		ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
		planeTBB.order(ByteOrder.nativeOrder());
		colorBuffer = planeTBB.asFloatBuffer();
		colorBuffer.put(planeTFA);
		colorBuffer.position(0);
	}

	@Override
	public void applyMaterialParams(float[] viewMatrix, float[] projectionMatrix) {
		
		 GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
	     GLES20.glEnableVertexAttribArray(aColor);
		
	}

	@Override
	public void applyObjectParams(float[] viewMatrix, float[] projectionMatrix,
			GLESObject mObject) {		
		
	}

}
