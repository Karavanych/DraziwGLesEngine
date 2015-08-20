package draziw.gles.game;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class SceneElements {
	ArrayList<Element> elements=new ArrayList<Element>();
	
	public SceneElements(String fileName,ResourceManager resources) {
		
		AssetManager assManager = resources.context.getAssets();
		InputStream is = null;
		ByteBuffer pointVBB = null;
		try {
		        is = assManager.open(fileName+".scn");
		        pointVBB = readToByteBuffer(is);
		        
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		
				
		FloatBuffer fB = pointVBB.asFloatBuffer();		
		fB.position(0);
		
		while (fB.hasRemaining()) {
			elements.add(new Element(fB.get(),fB.get(),fB.get(),fB.get(),fB.get(),fB.get(),fB.get()));			
		}	
		
		loadNames(fileName,resources);
		
		/*for (Element each:elements) {
			Log.d(,"element "+each.getName()+","+each.getIdx()+","+each.getX()+","+each.getY()+","+each.getZ()+" rotate = "+each.getRotateX()+","+each.getRotateY()+","+each.getRotateZ());
		}*/
		
	}
	
	@SuppressWarnings("unchecked")
	public void loadNames(String fileName,ResourceManager resources) {
		HashMap<String, Float> objectNames=new HashMap<String, Float>(); 
		AssetManager assManager = resources.context.getAssets();
		
	      try
	      {
	    	 InputStream is = assManager.open(fileName+".imp");
	         ObjectInputStream in = new ObjectInputStream(is);
	         objectNames = (HashMap<String, Float>) in.readObject();
	         in.close();
	         is.close();
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	         return;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("HashMap<String, Float> class not found");
	         c.printStackTrace();
	         return;
	      }
	      
	      // загружаем VNT-буферы объектов
	      resources.loadVntBuffers(objectNames);
	      
	      //если нашли индексы имен, то записываем имена в элементы
	      if (objectNames.size()>0) {
		      Iterator<Entry<String, Float>> it = objectNames.entrySet().iterator();
			  while (it.hasNext()) {
			        Map.Entry<String, Float> pair = (Map.Entry<String, Float>)it.next();
			        for (Element each:elements) {
			        	if (each.getIdx()==pair.getValue()) {
			        		each.setName(pair.getKey());
			        	}
			        }	
			        it.remove();
			    }				 
	      }
	      
	      
		
	}
	
	public ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
		int bufferSize = 0x20000;
	    byte[] buffer = new byte[bufferSize];
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
	    int read;
	    while (true) {
	      read = inStream.read(buffer);
	      if (read == -1)
	        break;
	      outStream.write(buffer, 0, read);
	    }	    	    
	    
	    ByteBuffer byteData=ByteBuffer.allocateDirect(outStream.size());
	    byteData.order(ByteOrder.nativeOrder());
	    byteData.put(outStream.toByteArray());
	   
	    byteData.position(0);	    	      
	    
	    return byteData;
	  }
	
	public int size() {
		return elements.size();
	}
	
	public float getX(int pos) {
		return elements.get(pos).getX();
	}
	
	public float getY(int pos) {
		return elements.get(pos).getY();
	}
	
	public float getZ(int pos) {
		return elements.get(pos).getZ();
	}
	
	public float getRotateX(int pos) {
		return elements.get(pos).getRotateX();
	}
	
	public float getRotateY(int pos) {
		return elements.get(pos).getRotateY();
	}
	
	public float getRotateZ(int pos) {
		return elements.get(pos).getRotateZ();
	}
	
	public String getName(int pos) {
		return elements.get(pos).getName();
	}
	
	public float getIdx(int pos) {
		return elements.get(pos).getIdx();
	}
	
	public float[] getPositionArray(int pos) {
		return elements.get(pos).getPosition();
	}
	
	
	private class Element {
		float[] position=new float[7];// idx,x,y,z, rX, rY, rZ	
		String name;			
		
		public Element(float idx,float x,float y,float z,float rX,float rY,float rZ) {			
			position[0]=idx;
			position[1]=x;
			position[2]=y;
			position[3]=z;
			position[4]=rX;
			position[5]=rY;
			position[6]=rZ;							
		}
		
		public void setName(String name) {
			this.name=name;
		}
		
		public String getName() {
			return name;
		}
		
		public float getIdx() {
			return position[0];			
		}
		public float getX() {
			return position[1];			
		}
		public float getY() {
			return position[2];			
		}
		public float getZ() {
			return position[3];			
		}	
		
		public float getRotateX() {
			return position[4];			
		}
		public float getRotateY() {
			return position[5];			
		}
		public float getRotateZ() {
			return position[6];			
		}
		
		public float[] getPosition() {
			return position;
		}
			
	}
	
}
