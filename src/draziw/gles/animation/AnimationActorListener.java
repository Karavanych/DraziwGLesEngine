package draziw.gles.animation;

import java.util.ArrayList;
import java.util.ListIterator;


import draziw.gles.objects.GLESObject;


public class AnimationActorListener {
	
	ActionsAfter listener;
	
	public interface ActionsAfter {
		public void onActionsAfter(String tag,GLESObject obj);
	}
	
	public AnimationActorListener(ActionsAfter listener) {
		this.listener=listener;
	}
	
	ArrayList <ActionsAfterActor> afterActions=new ArrayList<ActionsAfterActor>();
	
	public void onAnimationEnd(String tag, GLESObject obj) {
		afterActions.add(new ActionsAfterActor(tag,obj));		
	}
	
	
	public void actionsRun() {
		if (afterActions.size()>0) {		
			ListIterator<ActionsAfterActor> iterator = afterActions.listIterator();  
			ActionsAfterActor each;
			while (iterator.hasNext()) {		
				each=iterator.next();
				listener.onActionsAfter(each.tag, each.obj);
				iterator.remove();			
	        }
			each=null;		
		}
	}
	
	class ActionsAfterActor {
		public String tag;
		public GLESObject obj;
		
		public ActionsAfterActor(String tag,GLESObject obj) {
			this.tag=tag;
			this.obj=obj;
		}

	}

}
