package draziw.gles.materials;

import android.opengl.GLES20;
import android.opengl.Matrix;
import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.PointLight3D;

public class MaterialNormalMapOld extends Material {

	public int uLightPos;
	public int uLuminance;
		
	private PointLight3D glPointLight;
	private float mLuminance;
	
	private int uvi;
	
	private float[] mViewInverse = new float[16];

	public MaterialNormalMapOld(ShaderManager shaders) {
		super(shaders, "basenormal");		
	}

	@Override
	public void initializeShaderParam() {
				
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aTextureCoord = glGetAttribLocation(shaderProgramHandler, "aTextureCoord");
		aNormal = glGetAttribLocation(shaderProgramHandler,"aNormal");
		aTangent = glGetAttribLocation(shaderProgramHandler,"aTangent");
		aBitangent = glGetAttribLocation(shaderProgramHandler,"aBinormal");		
		

		umvp=glGetUniformLocation(shaderProgramHandler, "mvp");
		um=glGetUniformLocation(shaderProgramHandler,"m");
		uv=glGetUniformLocation(shaderProgramHandler,"v");
		uNormalMatrix=glGetUniformLocation(shaderProgramHandler,"uNormalMatrix");		
		
		uBaseMap = glGetUniformLocation(shaderProgramHandler, "uBaseMap");
		uNormalMap = glGetUniformLocation(shaderProgramHandler,"uNormalMap");
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
