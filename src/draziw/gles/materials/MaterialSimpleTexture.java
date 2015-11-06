package draziw.gles.materials;

import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;

public class MaterialSimpleTexture extends Material {

	public MaterialSimpleTexture(ShaderManager shaders) {
		super(shaders, "simple_texture");		
	}

	@Override
	public void initializeShaderParam() {
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition		
		aTextureCoord = glGetAttribLocation(shaderProgramHandler, "aTextureCoord");		
		umvp=glGetUniformLocation(shaderProgramHandler, "mvp");
		uBaseMap = glGetUniformLocation(shaderProgramHandler, "uBaseMap");
	}

	@Override
	public void applyMaterialParams(float[] viewMatrix, float[] projectionMatrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyObjectParams(float[] viewMatrix, float[] projectionMatrix,
			GLESObject mObject) {
		// TODO Auto-generated method stub
		
	}

}
