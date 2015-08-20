package draziw.gles.game;

import java.util.ArrayList;


import android.content.Context;
import android.opengl.GLES20;



import draziw.gles.engine.TextureLoader;
import draziw.gles.objects.ControllerView;
import draziw.gles.objects.Cube3D;
import draziw.gles.objects.Custom3D;
import draziw.gles.objects.CubeMap3D;
import draziw.gles.objects.Font2D;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.PointLight3D;
import draziw.gles.objects.Rectangle2D;
import draziw.gles.objects.Sprite2D;

public class GameScene {
	
	
	
	Context context;
	
	
	
	public ArrayList<GLESObject> sceneLayer=new ArrayList<GLESObject>();		

	
	
	
	public int lightMoveDirection=-1;
	public int lightMoveDirectionCount=300;
	
	public SceneElements sceneElements;
	
	private GameControllers controllers;
	private GLESCamera camera;
	private TextureLoader textureLoader;
	ResourceManager resources;
	
	private CubeMap3D cubeMap;
	private PointLight3D glPointLight;

	
	public Player player;
	
	public GameScene(Context cc) {
		this.context=cc;		
	}
	
	public void init(GLESCamera camera, GameControllers gameController, TextureLoader mTextureLoader, ResourceManager mResources) {
		this.controllers=gameController;
		this.camera=camera;		
		this.textureLoader=mTextureLoader;
		this.resources=mResources;
				
		clearPrograms();						
				
		
		//создаем элементы из записанной сцены и загружаем ресурсы
		sceneElements=new SceneElements("scene1",resources);		
		
				
		
		cubeMap = new CubeMap3D(textureLoader.getTexture(0),context);
		cubeMap.scaleM(15f, 15f, 15f);
		sceneLayer.add(cubeMap);
		
		glPointLight = new PointLight3D(textureLoader.getTexture(0));
		glPointLight.translateM(0.0f,0.0f,15f);	
		glPointLight.setLuminance(0.001f);
		
		placeScene(glPointLight,sceneElements,sceneLayer);
								
		placeControllers(gameController,sceneLayer);
										
		
		//sceneLayer.add(glPointLight);
		

	}
	
	private void placeControllers(GameControllers gameController,
			ArrayList<GLESObject> sceneLayer) {				
			
			ControllerView tekElement = new ControllerView(textureLoader.getTexture(1),context,"controller1");
			
			//нельзя делать scale, потому что порядок - transtale, rotate,scale
			//tekElement.scale(.05f, 0.05f, 1f);
			tekElement.setController(gameController.getControllerByType(GameControllers.CONTROLLER_LEFT));			
			sceneLayer.add(tekElement);		
			
			tekElement = new ControllerView(textureLoader.getTexture(1),context,"controller1");			
			tekElement.setController(gameController.getControllerByType(GameControllers.CONTROLLER_RIGHT));						
			sceneLayer.add(tekElement);				
				
	}

	public void placeScene(PointLight3D glPointLight, SceneElements sceneElements, ArrayList<GLESObject> sceneLayer) {
		
		for (int pos=0;pos<sceneElements.size();pos++) {
			//for (int pos=13;pos<14;pos++) {
				Custom3D tekElement;
				if (sceneElements.getName(pos).equals("player")) {
					player = new Player(textureLoader.getTexture(1),resources,sceneElements.getName(pos));
					tekElement=player;
				} else {
					tekElement = new Custom3D(textureLoader.getTexture(1),resources,sceneElements.getName(pos));
				}
				
				tekElement.setPositionM(sceneElements.getX(pos), sceneElements.getY(pos), sceneElements.getZ(pos));
				
				//поворачиваем модели, если задан угол поворота
				if (sceneElements.getRotateX(pos)!=0) {
					tekElement.rotateM(sceneElements.getRotateX(pos), 1, 0, 0);
				}
				if (sceneElements.getRotateY(pos)!=0) {				
					tekElement.rotateM(sceneElements.getRotateY(pos), 0, 1, 0);
				}
				if (sceneElements.getRotateZ(pos)!=0) {
					tekElement.rotateM(sceneElements.getRotateZ(pos), 0, 0, 1);
				}					

				tekElement.setLight(glPointLight);
				sceneLayer.add(tekElement);			
			}		
	}

	public void onDrawFrame(float timer) {
		
		// матрицу вида полуваем один раз в цикле отрисовки, потому что она расчетная
		// каждый раз при вызове getViewMatrix будет пересчитываться
		float[] viewMatrix = camera.getViewMatrix();
		camera.moveByController(timer,controllers);
		
		for (GLESObject tekObject:sceneLayer) {
			GLES20.glUseProgram(tekObject.getShaderProgramInstance().programHandler);	
			if (tekObject.isGUI()) {
				tekObject.draw(camera.getGUIView(),camera.getGUIMatrix(),timer);
			} else {
				tekObject.draw(viewMatrix,camera.getProjectionMatrix(),timer);
			}
		}
				
		
		if (cubeMap!=null) {
			cubeMap.rotateM(0.1f, 0f, 1f, 0f);
		}
		
		if (glPointLight!=null) {	
			lightMoveDirectionCount--;
			if (lightMoveDirectionCount<0) {
				lightMoveDirectionCount=300;
				lightMoveDirection*=-1;
			}
			glPointLight.translateM(0,0,-0.01f*lightMoveDirection);
		}
		
		//Matrix.rotateM(viewMatrix, 0, 0.1f, 0f, 1f, 0f);
		
		/*angle+=0.2f;
		glRectangle1.resetMatrix();
		glRectangle1.moveBack();		
		glRectangle1.scaleByGeometri();
		glRectangle1.rotate(angle, 0.5f, 1f, 0f);*/
		
		//GLES20.glDisable(GLES20.GL_BLEND);
			
	}
	
	private void clearPrograms() {	
		// при повороте activity, нужно пересоздавать сингтоны shaderProgram
		   Font2D.reset();
		   Cube3D.reset();
		   Sprite2D.reset();
		   Rectangle2D.reset();
		   Custom3D.reset();
		   PointLight3D.reset();
		   CubeMap3D.reset();
		   ControllerView.reset();
		   Player.reset();
	   }
	   
	   

}
