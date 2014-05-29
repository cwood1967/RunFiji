package SliceMean;

import java.util.concurrent.RecursiveTask;
import java.util.List;
import java.util.LinkedList;

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

public class MaxTask extends RecursiveTask<Float> {

	private ImageProcessorReader r = null;
	private ImageProcessor ip = null;
	
	int slice;
	int nx;
	int ny;
	int nz;
	int nt;
	int nc;
	int nSlices;
	
	String DimOrder;
	
	public MaxTask(ImageProcessorReader r, int slice) {
		super();
		this.r = r;
		this.slice = slice;
		nx = r.getSizeX();
		ny = r.getSizeY();
		nz = r.getSizeZ();
		nt = r.getSizeT();
		nc = r.getSizeC();
		nSlices = nt*nz*nc;
		DimOrder = r.getDimensionOrder();
	}
	@Override
	protected Float compute() {
	
		try {
			ip = r.openProcessors(slice)[0];
		} catch (Exception e) {
			e.printStackTrace();
			return -1.1f;
		}
		ip = ip.convertToFloat();
		float[] pixels = (float[]) ip.getPixels();
		float max = 0.f;
		for (int j = 0; j < pixels.length; j++) {
			if (pixels[j] > max) {
				max = pixels[j];
			}
		}

		pixels = null;
		return max;
	}
	
	
	
	
}
