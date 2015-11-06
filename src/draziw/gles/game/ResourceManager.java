package draziw.gles.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import draziw.gles.engine.Texture;
import draziw.gles.engine.TextureLoader;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

public class ResourceManager {
		
	
	public static int VNT_STRIDE=56;// 14*4
	public static int NORMAL_OFFSET=12;
	public static int TEXTURE_OFFSET=NORMAL_OFFSET+12;
	public static int TANGENT_OFFSET=TEXTURE_OFFSET+8;
	public static int BITANGENT_OFFSET=TANGENT_OFFSET+12;
	
	//private ArrayList<Texture> textures;
	private HashMap<String, int[]> bufferIndex=new HashMap<String, int[]>();
	public Context context;	

	public ResourceManager(Context context) {
		this.context=context;								
	}	

	
	public void loadVntBuffers(HashMap<String, Float> objectNames) { // vertex, normal, texture
		int bufferCount = objectNames.size();
				
		final int[] buffers = new int[bufferCount];
		GLES20.glGenBuffers(bufferCount, buffers, 0);
		
		
		if (objectNames.size()>0) {
			int pos=0;
			Iterator<Entry<String, Float>> it = objectNames.entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry<String, Float> pair = (Map.Entry<String, Float>)it.next();
		        loadBufferToVBO(pair.getKey(),buffers[pos]);
		        pos++;
			    }				 
	      }
		
	}
	
	public void loadSingleModelData(String modelName,boolean override) {
		if (bufferIndex.get(modelName)==null | override) {
			final int[] buffers = new int[1];
			GLES20.glGenBuffers(1, buffers, 0);
			loadBufferToVBO(modelName,buffers[0]);
		}
	}

	private void loadBufferToVBO(String modelName, int vntBufferHolder) {
				
		FloatBuffer vntBuffer; // vertex+ normals + texture Buffer, stride 8*4 (8*float)
								
		AssetManager assManager = context.getAssets();
		InputStream is = null;
		ByteBuffer pointVBB = null;
		try {
		        is = assManager.open(modelName+".vrt");
		        pointVBB = readToByteBuffer(is);
		        
		    } catch (IOException e) {		        
		        e.printStackTrace();
		    }
		
				
		vntBuffer=pointVBB.asFloatBuffer();		
		vntBuffer.position(0);
		
		
		int indexCount = (int) (vntBuffer.remaining()*4/VNT_STRIDE);
				
		
		// Bind to the buffer. Future commands will affect this buffer specifically.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vntBufferHolder);
		 
		// Transfer data from client memory to the buffer.
		// We can release the client memory after this call.
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vntBuffer.capacity() * 4,
				vntBuffer, GLES20.GL_STATIC_DRAW);
		
		// IMPORTANT: Unbind from the buffer when we're done with it.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		// clear buffer from appmemory		
		vntBuffer.limit(0);
		vntBuffer = null;				
		
		bufferIndex.put(modelName,new int[]{vntBufferHolder,indexCount});	
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
	
	
	public int getBufferHolder(String modelName) {
		return bufferIndex.get(modelName)[0];		
	}
	
	public int getVertexCount(String modelName) {
		return bufferIndex.get(modelName)[1];
	}

	public void confirmBuffers() {
		
	/*	for (int each:texturesId) {
			needReload=(needReload || !GLES20.glIsBuffer(each));
		}
		*/
				
		
		Iterator<Entry<String, int[]>> it = bufferIndex.entrySet().iterator();
		
	    while (it.hasNext()) {
	        Entry<String, int[]> pair = it.next();
	        int bufferIdx = pair.getValue()[0];
	        
	        // проверим валидность буфера
	        if (!GLES20.glIsBuffer(bufferIdx)) {
	        	//  достаточно просто перезагрузить буферы, нет необходимости их удал€ть
	        	// а посколько у нас дл€ хранени€ используетс€ HashMap,
	        	// то она просто переложит элемент по имени
	        	loadBufferToVBO(pair.getKey(),bufferIdx);	 
	        	
	        } else {
	        	Log.d("MyLogs", "buffer "+pair.getKey()+" is OK!");
	        }
	    }
	    
	    /*if (indicesToRemove.size()>0) {
	    
		    int[] indicesToRemoveArray=new int[indicesToRemove.size()];
		    
		    for (int i=0;i<indicesToRemoveArray.length;i++) {
		    	indicesToRemoveArray[i]=indicesToRemove.get(i);
		    }	    
		    
			GLES20.glDeleteBuffers(indicesToRemoveArray.length,indicesToRemoveArray,0);	
			
			indicesToRemoveArray=null;
		
			// reload buffers
			
			int buffers[] = new int[1];
			for (int i=0;i<indicesToRemove.size();i++) {								
				GLES20.glGenBuffers(1, buffers, 0);
				loadBufferToVBO(namesToRemove.get(i),buffers[0]);				
			}			
			
	    }*/
		
		
	}
		

}
