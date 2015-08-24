package draziw.gles.engine;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.util.Log;

public class Config3D888MSAA implements EGLConfigChooser {

	private int[] mValue;
	private int pixelFormat;
	
	/*��� ������������ ( ����������� ):
	������������ ���������� �� ������ ��������.
	������������� ������������ � EGL_SAMPLE_BUFFERS ������ ����� MSAA
	EGL_SAMPLES ������ ���������� ������� �� �������, �������� ��� 4 �������� ����� MSAAx4
	� �������� ���������� ��������� MSAA-������������ ������.
	
	EGL_COVERAGE_BUFFERS_NV � EGL_COVERAGE_SAMPLES_NV ������ ����� CSAA ����������� �������.
	��������� ����, Tegra ��������, ����� �������� ������ � CSAA ��������������.
	� �������� ���������� �������� ��������� CSAA.
	
	�� � �� �� ��������� ������������ �� ��� �� ������ ����� - � ������������ FXAA.
	�� ������� "�����" � ����� ����������, �������������� �� ���� ������ ������������� � ���� ������ ���������� ���������.
	FXAA �������� ������ ��� OpenGL ES 2.0 � ����������� ��������. */
	

	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
		mValue = new int[1];
		pixelFormat = PixelFormat.RGBA_8888; // �� configSpec: EGL_RED_SIZE,EGL_GREEN_SIZE,Blue � �.�.
		
		// ����������� ��������� ���� ������, ������ �� � ��� �������� �������.
		int[] configSpec = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                //EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                EGL10.EGL_SAMPLES, 2,
                EGL10.EGL_NONE
        };
        if (!egl.eglChooseConfig(display, configSpec, null, 0,mValue)) {
        	// � ������ ���� ����� ������� throw
            //throw new IllegalArgumentException("RGB888 eglChooseConfig failed");
        	logConfigType("RGB888 + msaa failed");
        } else {
        	logConfigType("RGB88 + msaa enabled");
        }
		int numConfigs = mValue[0];
		
		
		// ������� ���������� CSAA
		if (numConfigs <= 0) {
            // No normal multisampling config was found. Try to create a
            // converage multisampling configuration, for the nVidia Tegra2.
            // See the EGL_NV_coverage_sample documentation.

            final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
            final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;
            
            configSpec = new int[]{
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                    
                    EGL_COVERAGE_BUFFERS_NV, 1 /* true */,
                    EGL_COVERAGE_SAMPLES_NV, 2,  // always 5 in practice on tegra 2
                    EGL10.EGL_NONE
            };

            if (!egl.eglChooseConfig(display, configSpec, null, 0,
                    mValue)) {
            	// � ������ ���� ����� ������� throw
                //throw new IllegalArgumentException("2nd eglChooseConfig failed");
            	logConfigType("RGB888 + csaa failed");
            } else {
            	logConfigType("RGB88 + csaa enabled");
            }
            numConfigs = mValue[0];
		}
		
		// PixelFormat.RGBA_8888 ��� �����������
		if (numConfigs <= 0) {
			// PixelFormat.RGBA_8888 ��� �����������
			configSpec = new int[]{
			                EGL10.EGL_RED_SIZE, 8,
			                EGL10.EGL_GREEN_SIZE, 8,
			                EGL10.EGL_BLUE_SIZE, 8,
			                //EGL10.EGL_ALPHA_SIZE, 8,
			                EGL10.EGL_RENDERABLE_TYPE, 4,
			                EGL10.EGL_DEPTH_SIZE, 16,		                
			                EGL10.EGL_NONE
			        };
					
			 if (!egl.eglChooseConfig(display, configSpec, null, 0,mValue)) {
			        	// � ������ ���� ����� ������� throw
			            //throw new IllegalArgumentException("RGB888 eglChooseConfig failed");
				 logConfigType("RGB888 NO_Multisampling failed");
			 } else {
				 logConfigType("RGB88 + NO_MS enabled");
			 }
			 numConfigs = mValue[0];
		}

		// ���� ���������� ����������� RGB888 ���, �� ������� �������� RGB888
		// ��� �� ����� ����� RGB565 ��� �����������.
		if (numConfigs <= 0) {
			pixelFormat = PixelFormat.RGB_565;
			configSpec = new int[] { EGL10.EGL_RED_SIZE, 5,
					EGL10.EGL_GREEN_SIZE, 6, EGL10.EGL_BLUE_SIZE, 5,
					EGL10.EGL_DEPTH_SIZE,4,
					EGL10.EGL_RENDERABLE_TYPE, 4, EGL10.EGL_NONE };

			if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
				logConfigType("RGB565 eglChooseConfig failed");					
			} else {
				logConfigType("RGB565 + NO_MS enabled");
			}

			numConfigs = mValue[0];

			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No configs match configSpec RGB565");
			}
		}
		EGLConfig[] configs = new EGLConfig[numConfigs];
		int[] num_conf = new int[numConfigs];
		// �������� ������ ������������
		egl.eglChooseConfig(display, configSpec, configs, numConfigs, mValue);

		// ���������� ������ ������ ���������� ��� ������� ���� ����� �������� �
		// ������� ������������ �������� � ������� ������������.
		return configs[0];
	}

	public int getPixelFormat() {
		return pixelFormat;
	}
	
	public void logConfigType(String message) {
		Log.e("MyLogs", message);		
	}
	
}
