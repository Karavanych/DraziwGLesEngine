package draziw.gles.materials;

import android.opengl.GLES20;
import draziw.gles.engine.ShaderManager;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.PointLight3D;

public class MaterialNormalMap extends Material {

	public int uLightPos;
		
	private PointLight3D glPointLight;

	private int uKa;
	private int uKs;
	private int uShininess;
	private int uLightIntensity;

	private float[] materialsParams; // ambientRED,aG,aB,specularRED,sG,sB
	private float[] lightParams;// light Intensity RED, iG,iB, Shininess 

	public MaterialNormalMap(ShaderManager shaders) {
		super(shaders, "normapmappong");		
	}

	@Override
	public void initializeShaderParam() {
				
		aPosition = glGetAttribLocation(shaderProgramHandler, "aPosition");// �������� ��������� ��� ���������� ��������� aPosition
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
		
		uKa = glGetUniformLocation(shaderProgramHandler,"uKa");
		uKs = glGetUniformLocation(shaderProgramHandler,"uKs");; //specular
		uShininess = glGetUniformLocation(shaderProgramHandler,"uShininess");
		uLightIntensity = glGetUniformLocation(shaderProgramHandler,"uLightIntensity");
	}
	
	public void setMaterialParams(float aR,float aG,float aB,float sR,float sG,float sB) {
		materialsParams=new float[] {aR,aG,aB,sR,sG,sB};
	}
	
	public void setLightParams(float iR,float iG,float iB,float shininess) {
		lightParams=new float[] {iR,iG,iB,shininess};		
	}

	public void setLight(PointLight3D glPointLight) {
		this.glPointLight=glPointLight;				
	}	

	@Override
	public void applyMaterialParams(float[] viewMatrix, float[] projectionMatrix) {
		float[] lightPos = glPointLight.getMVPosition(viewMatrix); 
		
		GLES20.glUniform3f(uLightPos, lightPos[0], lightPos[1], lightPos[2]);
		GLES20.glUniform3f(uKa, materialsParams[0], materialsParams[1], materialsParams[2]);
		GLES20.glUniform3f(uKs, materialsParams[3], materialsParams[4], materialsParams[5]);
		
		GLES20.glUniform3f(uLightIntensity, lightParams[0], lightParams[1], lightParams[2]);
		GLES20.glUniform1f(uShininess, lightParams[3]);
		
		GLES20.glUniformMatrix4fv(uv, 1, false, viewMatrix, 0);//�������� ������� V � ������
		
	}

	@Override
	public void applyObjectParams(float[] viewMatrix, float[] projectionMatrix,
			GLESObject mObject) {
		// TODO Auto-generated method stub
		
	}

}
