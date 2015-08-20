package draziw.gles.engine;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView.EGLConfigChooser;

public class Config3D888MSAA implements EGLConfigChooser {

	private int[] mValue;
	private int pixelFormat;
	
	/*Про антиалайзинг ( сглаживание ):
	Антиалайзинг включается на уровне драйвера.
	Использование поверхностей с EGL_SAMPLE_BUFFERS задает режим MSAA
	EGL_SAMPLES задает количество семплов на пиксель, например при 4 получаем режим MSAAx4
	В процессе рендеринга управлять MSAA-сглаживанием нельзя.
	
	EGL_COVERAGE_BUFFERS_NV и EGL_COVERAGE_SAMPLES_NV задают режим CSAA аналогичным образом.
	Некоторые чипы, Tegra например, могут работать только с CSAA антиалайзингом.
	В процессе рендеринга возможно управлять CSAA.
	
	Но я бы не советовал использовать ни тот ни другой режим - а использовать FXAA.
	Он намного "легче" в плане вычислений, просчитывается за один проход постобработки и дает лучший визуальный результат.
	FXAA возможен только для OpenGL ES 2.0 и последующих редакций. */
	

	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
		mValue = new int[1];
		pixelFormat = PixelFormat.RGBA_8888; // см configSpec: EGL_RED_SIZE,EGL_GREEN_SIZE,Blue и т.д.
		
		//пробуем установить msaa сглаживание
		int[] configSpec = { EGL10.EGL_RED_SIZE, 8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8,
				EGL10.EGL_RENDERABLE_TYPE, 4,
				EGL10.EGL_SAMPLE_BUFFERS, 1,
				EGL10.EGL_DEPTH_SIZE, 16,
				// или можно попробовать установить 4 семпла, а только потом 2
				EGL10.EGL_SAMPLES, 2, EGL10.EGL_NONE };
		if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
			throw new IllegalArgumentException("RGB888 eglChooseConfig failed");
		}
		int numConfigs = mValue[0];
		
		
		// пробуем установить CSAA
		if (numConfigs <= 0) {
            // No normal multisampling config was found. Try to create a
            // converage multisampling configuration, for the nVidia Tegra2.
            // See the EGL_NV_coverage_sample documentation.

            final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
            final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

            pixelFormat = PixelFormat.RGBA_8888;
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
                throw new IllegalArgumentException("2nd eglChooseConfig failed");
            }
            numConfigs = mValue[0];
		}

		// Если подходящих кофигураций RGB888 нет, то пробуем получить RGB888
		// или на худой конец RGB565 без сглаживания.
		if (numConfigs <= 0) {
			pixelFormat = PixelFormat.RGB_565;
			configSpec = new int[] { EGL10.EGL_RED_SIZE, 5,
					EGL10.EGL_GREEN_SIZE, 6, EGL10.EGL_BLUE_SIZE, 5,
					EGL10.EGL_RENDERABLE_TYPE, 4, EGL10.EGL_NONE };

			if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
				throw new IllegalArgumentException(
						"RGB565 eglChooseConfig failed");
			}

			numConfigs = mValue[0];

			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No configs match configSpec RGB565");
			}
		}
		EGLConfig[] configs = new EGLConfig[numConfigs];
		int[] num_conf = new int[numConfigs];
		// получаем массив конфигураций
		egl.eglChooseConfig(display, configSpec, configs, numConfigs, mValue);

		// возвращаем первый конфиг подходящий под условия хотя можем поискать в
		// массиве конфигурацию например в большим сглаживанием.
		return configs[0];
	}

	public int getPixelFormat() {
		return pixelFormat;
	}
	
}
