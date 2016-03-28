package draziw.gles.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


import android.content.Context;
import android.opengl.GLES20;



import draziw.gles.animation.AnimationActor;
import draziw.gles.animation.AnimationActorListener;
import draziw.gles.animation.AnimationActorListener.ActionsAfter;
import draziw.gles.controllers.GameControllers;
import draziw.gles.engine.ShaderManager;
import draziw.gles.engine.TextureLoader;
import draziw.gles.lights.GLESLight;
import draziw.gles.materials.Material;
import draziw.gles.materials.MaterialPixelLight;
import draziw.gles.materials.MaterialSimpleTexture;
import draziw.gles.objects.ControllerView;
import draziw.gles.objects.Custom3D;
import draziw.gles.objects.GLESObject;
import draziw.gles.objects.Player;

public abstract class GameScene implements ActionsAfter {
	
	public SceneManager sceneManager;
	
	public Context context;	
		
	public ArrayList<GLESObject> sceneLayer=new ArrayList<GLESObject>();	
	public LinkedList<AnimationActor> actors=new LinkedList<AnimationActor>(); // используется для простых анимаций
	
	public AnimationActorListener actorsListener;
	
	public SceneElements sceneElements;
	
	public GameControllers controllers;
	public GLESCamera camera;
	public TextureLoader textureLoader;
	public ResourceManager resources;
	public ShaderManager shaders;
		
	public Player player;
		
	
	protected boolean isReady=false;


	public abstract void onDrawFrame(float timer);
	
	protected GameScene(SceneManager sceneManager) {
		this.sceneManager=sceneManager;
	}
	
	public void init(Context context,GLESCamera camera, GameControllers gameController, TextureLoader mTextureLoader, ResourceManager mResources, ShaderManager mShaders) {
		
		this.context=context;
		this.controllers=gameController;
		this.camera=camera;		
		this.textureLoader=mTextureLoader;
		this.resources=mResources;
		this.shaders=mShaders;
						
		shaders.clear();		
		
       
        isReady=true;
        
        actorsListener=new AnimationActorListener(this);
	}
	
	public void placeControllers() {
		placeControllers(controllers,sceneLayer);		
	}
	
	/*public void loadSceneFromFile(String sceneName,PointLight3D glPointLight,Player mPlayer) {
		if (mPlayer!=null) this.player=
		sceneElements=new SceneElements("scene1",resources);
		placeScene(glPointLight,sceneElements,sceneLayer);
	}*/
	
	public void loadSceneFromFile(String sceneName,GLESLight glMainLight) {
		sceneElements=new SceneElements("scene1",resources);
		placeScene(glMainLight,sceneElements,sceneLayer);
	}
	
	private void placeControllers(GameControllers gameController,
			ArrayList<GLESObject> sceneLayer) {	
		
			Material materialSimpleTex=new MaterialSimpleTexture(shaders);	
			
			ControllerView tekElement = new ControllerView(textureLoader.getTexture(2),materialSimpleTex,context,"controller1");
			
			//нельзя делать scale, потому что порядок - transtale, rotate,scale
			//tekElement.scale(.05f, 0.05f, 1f);
			tekElement.setController(gameController.getControllerByType(GameControllers.CONTROLLER_LEFT));			
			sceneLayer.add(tekElement);		
			
			tekElement = new ControllerView(textureLoader.getTexture(2),materialSimpleTex,context,"controller1");			
			tekElement.setController(gameController.getControllerByType(GameControllers.CONTROLLER_RIGHT));						
			sceneLayer.add(tekElement);				
				
	}

	public void placeScene(GLESLight glPointLight, SceneElements sceneElements, ArrayList<GLESObject> sceneLayer) {
		
		for (int pos=0;pos<sceneElements.size();pos++) {
			//for (int pos=13;pos<14;pos++) {
				Custom3D tekElement;
				
				MaterialPixelLight materialPixelLight=new MaterialPixelLight(shaders);
				materialPixelLight.setLight(glPointLight);
				
				if (sceneElements.getName(pos).equals("player")) {
					if (player==null) { // если он уже есть не надо создавать
						player = new Player(textureLoader.getTexture(1),materialPixelLight,resources,sceneElements.getName(pos));						
					} 
					tekElement=player;
				} else {
					tekElement = new Custom3D(textureLoader.getTexture(1),materialPixelLight,resources,sceneElements.getName(pos));
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
				
				sceneLayer.add(tekElement);			
			}		
	}

	
	public void actorsRun(float timer) {
		ListIterator<AnimationActor> iterator = actors.listIterator();  
		AnimationActor each;
		while (iterator.hasNext()) {		
			each=iterator.next();
			if (each.isRun) each.run(timer);
			else if (each.destroy) iterator.remove();			
        }
		each=null;
	}
	
	public float[] defaultDraw(float timer) {
		float[] viewMatrix = camera.getViewMatrix();
		
		Material currentMaterial=null;
		for (GLESObject tekObject:sceneLayer) {
			if (currentMaterial!=tekObject.material) {
				currentMaterial=tekObject.material;
				GLES20.glUseProgram(currentMaterial.shaderProgramHandler);
				
				// пока будем передавать матрицу проекции
				currentMaterial.applyMaterialParams(viewMatrix, camera.getProjectionMatrix(),timer);
				
				/*int error = GLES20.glGetError();
				if (error != GLES20.GL_NO_ERROR)
			        {         	
						Log.e("MyLogs", " material: " + currentMaterial.getClass().getName());  
			            Log.e("MyLogs", " on draw Error: " + GLU.gluErrorString(error));            
			        }*/
			}
			if (tekObject.isGUI()) {
				tekObject.draw(camera.getGUIView(),camera.getGUIMatrix(),timer);
			} else {
				tekObject.draw(viewMatrix,camera.getProjectionMatrix(),timer);
			}
		}
		
		return viewMatrix;
	}
	
	
	public void sceneLayerAdd(GLESObject... glesObjects) {
		for (GLESObject each:glesObjects) {
			sceneLayer.add(each);
		}	
	}
	
	public void sceneLayerRemove(GLESObject... glesObjects) {
		for (GLESObject each:glesObjects) {
			sceneLayer.remove(each);
		}	
	}
	
	private void clearPrograms() {	
		// при повороте activity, нужно пересоздавать сингтоны shaderProgram
		   shaders.clear();
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
		return isReady;
	}
	
	public void destroy() {
		
        Iterator<AnimationActor> ait = actors.iterator();
        while (ait.hasNext()) {
	        	ait.next().destroy();
	        	ait.remove();               
            }
		
		player=null;
		
		Iterator<GLESObject> it = sceneLayer.iterator();
        while (it.hasNext()) {
        		it.next().destroy();
                it.remove();               
            }        		             
	}
	
	
	

	
	   
	   

}
