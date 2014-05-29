package slice_mean_plugin;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class single_max_finder implements PlugIn {

	public single_max_finder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(String arg) {
		
		ImagePlus imp = IJ.getImage();
		ArrayList<Float> resultList = new ArrayList<Float>();
		double starttime = 0.001*System.currentTimeMillis();
		ImageStack stack = imp.getStack();
		int nSlices = stack.getSize();
		int imax = 100000;
		
		// TODO Auto-generated method stub
		for (int i = 0; i < nSlices; i++) {
			if (i > imax) {
				break;
			}
			ImageProcessor ip;

			ip = stack.getProcessor(i + 1);
			ip = ip.convertToFloat();
			float[] pixels = (float[]) ip.getPixels();
			float max = 0.f;
			for (int j = 0; j < pixels.length; j++) {
				if (pixels[j] > max) {
					max = pixels[j];
				}
			}

			pixels = null;
			resultList.add(max);
		}
		
		float allmax = 0f;
		for (int i = 0; i < resultList.size(); i++) {
			if (resultList.get(i) > allmax) {
				allmax = resultList.get(i);
			}
		}
		
		IJ.log("" +  allmax + " " + resultList.size());
		double endtime = 0.001*System.currentTimeMillis();
		IJ.log("" +  (endtime - starttime));
	}

}
