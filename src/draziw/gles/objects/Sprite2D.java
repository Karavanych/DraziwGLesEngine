package draziw.gles.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import draziw.gles.engine.ShaderProgram;
import draziw.gles.engine.Texture;
import draziw.gles.game.GameControllers.Controller;
import draziw.gles.game.GameControllers.ControllersListener;
import draziw.gles.materials.Material;
import draziw.gles.materials.MaterialSprite;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Sprite2D extends GLESObject {
	
	public static float BASE_SPRITE_WIDTH=2f; // ��� ������ �� ��������� ��������� �������
	public static float BASE_SPRITE_HEIGHT=2f; // ��. pointVFA 
	
	public boolean isGUI=false;
		
	FloatBuffer vertextBuffer;
	FloatBuffer textureCoordBuffer;
	ShortBuffer verticesIndex;
	
	
	// for animation
	private int tekFrame=0;
	private int startFrame=0;
	private int countFrames=0;
	public float[] animationVector=new float[]{0f,0f};
	
	
	public Sprite2D(Texture mTexture,MaterialSprite material,float[] leftTopRightBottomTextureCoords) {
		super(mTexture,material);
		
		// �� ��������� ���������� �� ���� �����, ����� ����� ����������� ����� � ������������
				float[] pointVFA = {
						 1f,-1f,0.0f,
						 -1f,-1f,0.0f,
						 1f,1f,0.0f,
						-1f,1f,0.0f
						};
						ByteBuffer pointVBB = ByteBuffer.allocateDirect(pointVFA.length * 4);
						pointVBB.order(ByteOrder.nativeOrder());
						vertextBuffer = pointVBB.asFloatBuffer();
						vertextBuffer.put(pointVFA);
						vertextBuffer.position(0); 				
												
						// �� ��������� ��� �������� �� �������, ����� ������ ��������, ������ ������������ ������ �������� � ������ ��������
				setTextureCoord(leftTopRightBottomTextureCoords);
						
				//������� �������, ���������� ������� ���������� ������ ���������� ������ � ���������� ����������, ��������������� ��������
				// � ����� �� ��������� ������� ���������� ������ ����� �������					
						
				short[] planeISA = {
								2,3,1,
								0,2,1,
						};
				
				ByteBuffer planeIBB = ByteBuffer.allocateDirect(planeISA.length * 2);
				planeIBB.order(ByteOrder.nativeOrder());
				verticesIndex = planeIBB.asShortBuffer();
				verticesIndex.put(planeISA);
				verticesIndex.position(0);
		
	}
	
	public Sprite2D(Texture mTexture,MaterialSprite material) {
		 this(mTexture,material,new float[]{0f,0f,1f,1f});		
	}
	
	// ������������� ���������� ���������� ��� ���������� ��������� ��������� �� scaleX ������ �� ����������� � scaleY ������ �� ���������
	public void setRelativeTextureBounds(float scaleX,float scaleY,int offsetFrameX,int offsetFrameY) {
		setTextureBounds(new float[] {1/scaleX,1/scaleY},new float[]{offsetFrameX/scaleX,offsetFrameY/scaleY});
	}
	
	public void setTextureCoord(float[] leftTopRightBottom) {
		float[] planeTFA = {
				// 1f,1f, 0,1f, 1f,0,0,0
				leftTopRightBottom[2],leftTopRightBottom[3],
				leftTopRightBottom[0],leftTopRightBottom[3],
				leftTopRightBottom[2],leftTopRightBottom[1],
				leftTopRightBottom[0],leftTopRightBottom[1]
		};

		ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
		planeTBB.order(ByteOrder.nativeOrder());
		textureCoordBuffer = planeTBB.asFloatBuffer();
		textureCoordBuffer.put(planeTFA);
		textureCoordBuffer.position(0);	
		
	}
	
	//������������� ������� �������� ��� ����� �������� ������� ������� � ������� �������� vec2
	public void setTextureBounds(float[] textureVector,	float[] offsetTextureVector) {			
		
		if (textureVector!=null && textureVector.length != 2) {try {
			throw new Exception("texture vector have to be length=2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}}
		
		if (offsetTextureVector!=null && offsetTextureVector.length != 2) {try {
			throw new Exception("offset vector have to be length=2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}}				
		
		float s=textureVector[0];
		float t=textureVector[1];
		
		float sOf=0;				
		float tOf=0;
		
		if (offsetTextureVector!=null) {
			sOf=offsetTextureVector[0];
			tOf=offsetTextureVector[1];
		}
		
		float[] planeTFA = {
					// 1f,1f, 0,1f, 1f,0,0,0
			s+sOf,t+tOf,
			0+sOf,t+tOf,
			s+sOf,0+tOf,
			0+sOf,0+tOf
			};
	
			ByteBuffer planeTBB = ByteBuffer.allocateDirect(planeTFA.length * 4);
			planeTBB.order(ByteOrder.nativeOrder());
			textureCoordBuffer = planeTBB.asFloatBuffer();
			textureCoordBuffer.put(planeTFA);
			textureCoordBuffer.position(0);								
	}
	
	@Override
	public void draw(float[] viewMatrix,float[] projectionMatrix, float timer) {
		if (isAnimated()) animationFrameUpdate(timer);// update frame, if is animated Sprite
		
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, viewMatrix, 0, mObjectMatrix, 0);
		 Matrix.multiplyMM(mObjectMVPMatrix, 0, projectionMatrix, 0, mObjectMVPMatrix, 0);
		 		 
		 GLES20.glUniformMatrix4fv(material.umvp, 1, false, mObjectMVPMatrix, 0);//�������� ������������ ������� MVP � ������
		
		 mTexture.use(material.uBaseMap);		
		 //GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTexture.index); // ���������� ��������, ������� ��������� ��������		 
		 //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.id); // ����������� ��������, ������� ���������� ������ ��������		 
		 //GLES20.glUniform1i(uSamplerHolder, mTexture.index);//�������� ������ �������� � ������... index �������� � id �������� �����������, � �� ���� ������
		 
		 GLES20.glUniform2f(((MaterialSprite)material).uAnimVector,animationVector[0],animationVector[1]);// ������ ������ �������� � ���������� ����������� {1/6,0} ��� 6 - ����� ������ � �������		 
		 GLES20.glUniform1f(((MaterialSprite)material).uFrame,tekFrame);// - ����� �����
		 
		 
		 GLES20.glVertexAttribPointer(material.aPosition, 3, GLES20.GL_FLOAT, false, 0, vertextBuffer);
		 GLES20.glEnableVertexAttribArray(material.aPosition);	
		 
		 GLES20.glVertexAttribPointer(material.aTextureCoord, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
	     GLES20.glEnableVertexAttribArray(material.aTextureCoord);
		 		
	     GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, verticesIndex);
		
	}


	public void animationFrameUpdate(double ctime) {		  		   
	        if(ctime < 0){ctime++;}
	        tekFrame=startFrame+(int)(ctime*(countFrames));	                	                 	  	
	}
	
	public void setAnimation(int mStartFrame,int mCountFrames,float[] mAnimVector) {
		startFrame=mStartFrame;
		countFrames=mCountFrames;
		animationVector=mAnimVector;					
	}
	
	public boolean isSpriteAnimated() {
		if (countFrames>0) return true; else return false;
	}
	
	public void setGeometriByScaling() {
		setGeometry(mObjectMatrix[0]*BASE_SPRITE_WIDTH,mObjectMatrix[5]*BASE_SPRITE_HEIGHT,mObjectMatrix[10]);		
	}
	
	@Override
	public boolean isCollidePoint(float[] mPos) {
		if (mPos[0]>position[0]-geometry[0]*0.5f && mPos[0]<position[0]+geometry[0]*0.5f
				&& 	mPos[1]>position[1]-geometry[1]*0.5f && mPos[1]<position[1]+geometry[1]*0.5f) {
			return true;
		} else {
			return false;
		}		
	}
	
	public void setGUI(boolean gui) {
		this.isGUI=gui;
	}
	
	@Override
	public boolean isGUI() {		
		return isGUI;
	}
	
}
