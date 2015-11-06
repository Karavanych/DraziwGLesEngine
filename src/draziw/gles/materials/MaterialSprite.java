package draziw.gles.materials;

import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;

public class MaterialSprite extends Material {

	public int uAnimVector;
	public int uFrame;

	public MaterialSprite(ShaderManager shaders) {
		super(shaders, "sprite");		
	}

	@Override
	public void initializeShaderParam() {
		
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aTextureCoord = glGetAttribLocation(shaderProgramHandler, "aTextureCoord");
		uBaseMap = glGetUniformLocation(shaderProgramHandler, "uBaseMap");
		uAnimVector = glGetUniformLocation(shaderProgramHandler, "uAnimVector");
		uFrame = glGetUniformLocation(shaderProgramHandler, "uFrame");
		umvp=glGetUniformLocation(shaderProgramHandler, "mvp");

	}

	@Override
	public void applyMaterialParams(float[] viewMatrix, float[] projectionMatrix) {
		
		
	}

	@Override
	public void applyObjectParams(float[] viewMatrix, float[] projectionMatrix,
			GLESObject mObject) {
		// TODO Auto-generated method stub
		
	}

}
