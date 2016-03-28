package draziw.gles.materials;

import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;

public class MaterialCubeMap extends Material {

	public MaterialCubeMap(ShaderManager shaders) {
		super(shaders, "cubemap");		
	}

	@Override
	public void initializeShaderParam() {
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// получаем указатель для переменной программы aPosition
		aTextureCoord = glGetAttribLocation(shaderProgramHandler, "aTextureCoord");		
		umvp=glGetUniformLocation(shaderProgramHandler, "mvp");		
		uBaseMap = glGetUniformLocation(shaderProgramHandler, "uBaseMap");		
	}

	@Override
	public void applyMaterialParams(float[] viewMatrix, float[] projectionMatrix,float timer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyObjectParams(float[] viewMatrix, float[] projectionMatrix,
			GLESObject mObject) {
		// TODO Auto-generated method stub
		
	}

}
