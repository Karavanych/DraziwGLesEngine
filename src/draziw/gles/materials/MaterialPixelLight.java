package draziw.gles.materials;

import android.opengl.GLES20;
import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.PointLight3D;

public class MaterialPixelLight extends Material {

	public int uLightPos;
	public int uLuminance;
		
	private PointLight3D glPointLight;
	private float mLuminance;

	public MaterialPixelLight(ShaderManager shaders) {
		super(shaders, "pixel_light");		
	}

	@Override
	public void initializeShaderParam() {
				
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aNormal = glGetAttribLocation(shaderProgramHandler,"aNormal");
		//aTangent = glGetAttribLocation(shaderProgramHandler,"aTangent");
		//aBinormal = glGetAttribLocation(shaderProgramHandler,"aBinormal");		
		aTextureCoord = glGetAttribLocation(shaderProgramHandler, "aTextureCoord");

		umvp=glGetUniformLocation(shaderProgramHandler, "mvp");
		um=glGetUniformLocation(shaderProgramHandler,"m");
		uv=glGetUniformLocation(shaderProgramHandler,"v");
		uBaseMap = glGetUniformLocation(shaderProgramHandler, "uBaseMap");
		//uNormalMap = glGetUniformLocation(shaderProgramHandler,"uNormalMap");
		uLightPos = glGetUniformLocation(shaderProgramHandler, "uLightPos");
		uLuminance = glGetUniformLocation(shaderProgramHandler, "uLuminance");		
	}

	public void setLight(PointLight3D glPointLight) {
		this.glPointLight=glPointLight;		
		this.mLuminance=glPointLight.getLuminance();
	}	
	
	public void setLuminance(float luminance) {
		this.mLuminance=luminance;
	}

	@Override
	public void applyMaterialParams(float[] viewMatrix, float[] projectionMatrix) {
		float[] lightPos = glPointLight.getMVPosition(viewMatrix); 
		GLES20.glUniform3f(uLightPos, lightPos[0], lightPos[1], lightPos[2]);
		GLES20.glUniform1f(uLuminance,mLuminance);
		
		GLES20.glUniformMatrix4fv(uv, 1, false, viewMatrix, 0);//передаем матрицу V в шейдер
		
	}

	@Override
	public void applyObjectParams(float[] viewMatrix, float[] projectionMatrix,
			GLESObject mObject) {
		// TODO Auto-generated method stub
		
	}

}
