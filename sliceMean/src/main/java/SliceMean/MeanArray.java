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

public class MeanArray extends RecursiveTask<Double> {

	int num;
	double[] array = null;
	int size;

	public MeanArray(double[] array, int size) {
		super();
		this.array = array;
		this.size = size;
	}

	@Override
	public Double compute() {
		long count = 0;
		double total = 0;
		double mean;
		java.util.Random r = new java.util.Random();

		if (array == null) {
			array = new double[size];
			for (int i = 0; i < size; i++) {
				array[i] = r.nextGaussian();
			}
			
			MeanArray task = new MeanArray(array,0);
			task.fork();
			total = task.join();
		}

		else {
			double[] a2 = new double[size];
			for (int i = 0; i < size; i++) {
				a2[i] = array[i];
			}
			for (int j = 0; j < array.length; j++) {
				total += array[j];
			}
		}
		return total / array.length;
	}

}
