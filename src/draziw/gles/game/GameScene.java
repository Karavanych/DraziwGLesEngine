package draziw.gles.game;

import java.util.ArrayList;
import java.util.LinkedList;


import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;



import draziw.gles.engine.TextureLoader;
import draziw.gles.objects.ControllerView;
import draziw.gles.objects.Cube3D;
import draziw.gles.objects.Custom3D;
import draziw.gles.objects.CubeMap3D;
import draziw.gles.objects.Font2D;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.Plane3D;
import draziw.gles.objects.Player;
import draziw.gles.objects.PointLight3D;
import draziw.gles.objects.Rectangle2D;
import draziw.gles.objects.Sprite2D;
import draziw.simple.physics.AnimationActor;

public abstract class GameScene {
	
	Context context;
		
	public ArrayList<GLESObject> sceneLayer=new ArrayList<GLESObject>();	
	public LinkedList<AnimationActor> actors=new LinkedList<AnimationActor>(); // используется для простых анимаций	
	
	public SceneElements sceneElements;
	
	public GameControllers controllers;
	public GLESCamera camera;
	public TextureLoader textureLoader;
	public ResourceManager resources;
		
	public Player player;
		
	
	protected boolean isReady=false;
	
	protected GameScene() {
		
	}
	
	public void init(Context context,GLESCamera camera, GameControllers gameController, TextureLoader mTextureLoader, ResourceManager mResources) {
		this.context=context;
		this.controllers=gameController;
		this.camera=camera;		
		this.textureLoader=mTextureLoader;
		this.resources=mResources;
				
		clearPrograms();
		
		
       
        isReady=true;
	}
	
	public void placeControllers() {
		placeControllers(controllers,sceneLayer);		
	}
	
	/*public void loadSceneFromFile(String sceneName,PointLight3D glPointLight,Player mPlayer) {
		if (mPlayer!=null) this.player=
		sceneElements=new SceneElements("scene1",resources);
		placeScene(glPointLight,sceneElements,sceneLayer);
	}*/
	
	public void loadSceneFromFile(String sceneName,PointLight3D glPointLight) {
		sceneElements=new SceneElements("scene1",resources);
		placeScene(glPointLight,sceneElements,sceneLayer);
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
					if (player==null) { // если он уже есть не надо создавать
						player = new Player(textureLoader.getTexture(1),resources,sceneElements.getName(pos));						
					} 
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

	public abstract void onDrawFrame(float timer);
	
	public void actorsRun(float timer) {
		for (AnimationActor each:actors) {
			each.run(timer);
        }
	}
	
	public float[] defaultDraw(float timer) {
		float[] viewMatrix = camera.getViewMatrix();
		
		int currentProgramHandler = 0;
		for (GLESObject tekObject:sceneLayer) {
			if (currentProgramHandler!=tekObject.getShaderProgramInstance().programHandler) {
				currentProgramHandler=tekObject.getShaderProgramInstance().programHandler;
				GLES20.glUseProgram(currentProgramHandler);	
			}
			if (tekObject.isGUI()) {
				tekObject.draw(camera.getGUIView(),camera.getGUIMatrix(),timer);
			} else {
				tekObject.draw(viewMatrix,camera.getProjectionMatrix(),timer);
			}
		}
		
		return viewMatrix;
	}
	/*{
		
		// матрицу вида полуваем один раз в цикле отрисовки, потому что она расчетная
		// каждый раз при вызове getViewMatrix будет пересчитываться
		
				
		gameLogicsFrame(timer);
		
		
		float[] viewMatrix = camera.getViewMatrix();
		
		
		
		//player.moveByController(timer, controllers);
		
		//camera.setPositionByGLESObject(player);
		glPointLight.setPositionM(camera.position[0],camera.position[1],camera.position[2]);
		
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
		}*/
		
		//Matrix.rotateM(viewMatrix, 0, 0.1f, 0f, 1f, 0f);
		
		/*angle+=0.2f;
		glRectangle1.resetMatrix();
		glRectangle1.moveBack();		
		glRectangle1.scaleByGeometri();
		glRectangle1.rotate(angle, 0.5f, 1f, 0f);
		
		//GLES20.glDisable(GLES20.GL_BLEND);
			
	}*/
	
	/*public void gameLogicsFrame(float timer) {
		// сначала перемещаем камеру, если надо, потом получаем матрицу вида
				//camera.moveByController(timer,controllers);
				
			   	 final float delta = Math.min(1f/30f, timer);
			
			     if (!collision) {
			    	 player.translateM(0f, -delta, 0f);			         						         
			     }
			     playerCollisionObject.setWorldTransform(player.getGdxMatrix());
			     collision = checkCollision();
			    //player.actualizeObjectMatrix();
				player.moveByController(timer, controllers);
				
				camera.setByPlayerTranslateRotation(player);
		
	}*/
	
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
		   Plane3D.reset();
	   }
	
	/*boolean checkCollision() {
    	CollisionObjectWrapper co0 = new CollisionObjectWrapper(playerCollisionObject);
        CollisionObjectWrapper co1 = new CollisionObjectWrapper(groundCollisionObject);

        btCollisionAlgorithmConstructionInfo ci = new btCollisionAlgorithmConstructionInfo();
        ci.setDispatcher1(dispatcher);
        btCollisionAlgorithm algorithm = new btSphereBoxCollisionAlgorithm(null, ci, co0.wrapper, co1.wrapper, false); 

        btDispatcherInfo info = new btDispatcherInfo();
        btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);

        algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

        boolean r = result.getPersistentManifold().getNumContacts() > 0;

        result.dispose();
        info.dispose();
        algorithm.dispose();
        ci.dispose();
        co1.dispose();
        co0.dispose();

        return r;
    }
	
	public void dispose () {
		//TODO нужно как-то реализовать очищение ресурсов, при умирании приложения
		// либо при паузе очищать все, а при resume восстанавливать
		groundCollisionObject.dispose();
	    groundShape.dispose();
	
	    playerCollisionObject.dispose();
	    playerShape.dispose();
	
	    dispatcher.dispose();
	    collisionConfig.dispose();
	}*/

	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	
	   
	   

}
