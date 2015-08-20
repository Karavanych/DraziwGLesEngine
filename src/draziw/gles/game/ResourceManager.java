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

public class ResourceManager {
		
	
	public static int VNT_STRIDE=32;// 8*4
	public static int NORMAL_OFFSET=12;
	public static int TEXTURE_OFFSET=24;
	
	//private ArrayList<Texture> textures;
	private HashMap<String, int[]> bufferIndex=new HashMap<String, int[]>();
	public Context context;	

	public ResourceManager(Context context) {
		this.context=context;
								
	}	

	
	public void loadVntBuffers(HashMap<String, Float> objectNames) { // vertex, normal, texture
		int bufferCount = objectNames.size();
		
		// для тестов сгенерируем буферы здесь
		final int buffers[] = new int[bufferCount];
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

	public void clearBuffers() {		
		int[] indicesToRemove=new int[bufferIndex.size()];
		
		Iterator<Entry<String, int[]>> it = bufferIndex.entrySet().iterator();
		int count=0;
	    while (it.hasNext()) {
	        Entry<String, int[]> pair = it.next();	       	        
	        indicesToRemove[count]=pair.getValue()[0];
	        count++;
	       
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		GLES20.glDeleteBuffers(indicesToRemove.length,indicesToRemove,0);		
	}
		

}
