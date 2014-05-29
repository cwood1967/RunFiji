package SliceMean;

import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

import clojure.lang.IMapEntry;
import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.ImageStack;
import ij.VirtualStack;
import loci.formats.ChannelSeparator;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

public class MaxTask implements Callable<Float> {

	private ImageProcessorReader r = null;
	ImageProcessor ip = null;

	String DimOrder;
	int num;

	public MaxTask(ImageProcessor ip) {
		super();
		this.ip = ip;
	}

	@Override
	public Float call() {
		long count = 0;
		float allmax = 0.0f;
		float max = 0.f;
		
		ip = ip.convertToFloat();
		float[] pixels = (float[])ip.getPixels();
		for (int j = 0; j < pixels.length; j++) {
			if (pixels[j] > max) {
				max = pixels[j];
			}
		}
		
		return max;
	}

}
