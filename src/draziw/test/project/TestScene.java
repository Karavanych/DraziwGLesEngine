package draziw.test.project;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithmConstructionInfo;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.collision.btSphereBoxCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

import draziw.gles.engine.ShaderManager;
import draziw.gles.engine.TextureLoader;
import draziw.gles.game.GLESCamera;
import draziw.gles.game.GameControllers;
import draziw.gles.game.GameScene;
import draziw.gles.game.ResourceManager;
import draziw.gles.materials.MaterialCubeMap;
import draziw.gles.materials.MaterialPoint;
import draziw.gles.objects.CubeMap3D;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.PointLight3D;



public class TestScene extends GameScene {
	
	public int lightMoveDirection=-1;
	public int lightMoveDirectionCount=300;		
	
	private CubeMap3D cubeMap;
	private PointLight3D glPointLight;

	

	private btSphereShape playerShape;

	private btBoxShape groundShape;

	private btCollisionObject groundCollisionObject;

	private btCollisionObject playerCollisionObject;

	private btDefaultCollisionConfiguration collisionConfig;

	private btCollisionDispatcher dispatcher;

	private boolean collision=false;	
	
	@Override
	public void init(Context context,GLESCamera camera, GameControllers gameController, TextureLoader mTextureLoader, ResourceManager mResources,ShaderManager mShaders) {
		
		super.init(context, camera, gameController,mTextureLoader, mResources, mShaders);
		
		
			
		
		Bullet.init();
		
		//создаем элементы из записанной сцены и загружаем ресурсы
		MaterialCubeMap materialCM=new MaterialCubeMap(shaders);
		MaterialPoint materialPL=new MaterialPoint(shaders);
		
		cubeMap = new CubeMap3D(textureLoader.getTexture(0),materialCM,context);
		cubeMap.scaleM(15f, 15f, 15f);
		sceneLayer.add(cubeMap);
		
		glPointLight = new PointLight3D(textureLoader.getTexture(0),materialPL);
		glPointLight.translateM(0.0f,0.0f,15f);	
		glPointLight.setLuminance(0.001f);
		
		
		super.loadSceneFromFile("scene1",glPointLight);
		
		//placeScene(glPointLight,sceneElements,sceneLayer);
								
		//placeControllers(gameController,sceneLayer);
											
		//sceneLayer.add(glPointLight);
				
		
		playerShape = new btSphereShape(2f);
        groundShape = new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f));
        
        groundCollisionObject = new btCollisionObject();
        groundCollisionObject.setCollisionShape(groundShape);
        groundCollisionObject.setWorldTransform(new Matrix4());

        playerCollisionObject = new btCollisionObject();
        playerCollisionObject.setCollisionShape(playerShape);
        //ballObject.setWorldTransform(ball.transform);
        
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        
        //isReady=true;	
        
        
        // должен вызываться в конце, чтобы отрисовывался сверху
        super.placeControllers();

	}
	
	/*private void placeControllers(GameControllers gameController,
			ArrayList<GLESObject> sceneLayer) {				
			
			ControllerView tekElement = new ControllerView(textureLoader.getTexture(1),context,"controller1");
			
			//нельзя делать scale, потому что порядок - transtale, rotate,scale
			//tekElement.scale(.05f, 0.05f, 1f);
			tekElement.setController(gameController.getControllerByType(GameControllers.CONTROLLER_LEFT));			
			sceneLayer.add(tekElement);		
			
			tekElement = new ControllerView(textureLoader.getTexture(1),context,"controller1");			
			tekElement.setController(gameController.getControllerByType(GameControllers.CONTROLLER_RIGHT));						
			sceneLayer.add(tekElement);				
				
	}*/

	/*public void placeScene(PointLight3D glPointLight, SceneElements sceneElements, ArrayList<GLESObject> sceneLayer) {
		
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
	}*/

	@Override
	public void onDrawFrame(float timer) {
		
		// матрицу вида полуваем один раз в цикле отрисовки, потому что она расчетная
		// каждый раз при вызове getViewMatrix будет пересчитываться		
				
		gameLogicsFrame(timer);
		
		glPointLight.setPositionM(camera.position[0],camera.position[1],camera.position[2]);
		
		float[] viewMatrix = super.defaultDraw(timer);	
				
		
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
	
	public void gameLogicsFrame(float timer) {
		// сначала перемещаем камеру, если надо, потом получаем матрицу вида
				//camera.moveByController(timer,controllers);
				
			   	 final double delta = Math.min(1f/30f, timer);
			
			     if (!collision) {
			    	 player.translateM(0f, (float) -delta, 0f);			         						         
			     }
			     playerCollisionObject.setWorldTransform(player.getGdxMatrix());
			     collision = checkCollision();
			    //player.actualizeObjectMatrix();
				player.moveByController((float) timer, controllers);
				
				camera.setByPlayerTranslateRotation(player);
		
	}		
	
	boolean checkCollision() {
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
	}

}
