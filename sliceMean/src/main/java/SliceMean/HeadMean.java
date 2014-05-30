package SliceMean;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.ImageStack;
import ij.VirtualStack;
import loci.formats.ChannelSeparator;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

//import ij.process

public class HeadMean {

	ArrayList<Future<Double>> m_resultList = null;
	ArrayList<Double> resultList = null;
	int imax = 100;
	int arraysize = 100000;
	double meanValue;
	
	double time;
	private boolean MULTITHREAD;
	ExecutorService executor = null;

	// ImageProcessorReader r = null;

	public HeadMean(int imax, int arraysize) {
		// TODO Auto-generated constructor stub
		this.imax = imax;
		this.arraysize = arraysize;
	}

	public void run() {

		m_resultList = new ArrayList<Future<Double>>();
		resultList = new ArrayList<Double>();

		double startTime = 0.001 * (new java.util.Date().getTime());
		System.out.println("Start");
		if (isMULTITHREAD()) {
			System.out.println("Multithreading");
			IJ.log("Multithreading");
			process_multi();
		} else {
			IJ.log("Single??");
			process_single();
		}
		try {
			if (isMULTITHREAD()) {
				Double tc = m_resultList.get(0).get();
				System.out.println(tc);// + tc.get(0));
				double mtm = 0;
				for (Future<Double> ff : m_resultList) {
					double m = ff.get();
					if (m > mtm) {
						mtm = m;
					}
					resultList.add(m);
				}
				meanValue = mtm;
				System.out.println("Max value :" + mtm);
				// executor.shutdown();
			} else {
				double allmax = 0f;
				for (Double f : resultList) {
					if (f > allmax) {
						allmax = f;
					}
				}
				meanValue = allmax;
				System.out.println(resultList.size() + " " + allmax);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Stop");
		double endTime = 0.001 * (new java.util.Date().getTime());
		System.out.println("Time " + (endTime - startTime));
		time = endTime - startTime;

		// }
		//
		// catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private void process_single() {

		for (int i = 0; i < imax; i++) {

			double total = 0.f;
			double[] array = new double[arraysize];
			Random r = new Random();

			for (int j = 0; j < arraysize; j++) {
				array[j] = r.nextGaussian();
			}
			double[] a2 = new double[arraysize];
			for (int j = 0; j < arraysize; j++) {
				a2[j] = array[j];
			}
			for (int j = 0; j < arraysize; j++) {
				total += array[j];
			}

			resultList.add(total / imax);
		}
	}

	public void process_multi() {

		final ForkJoinPool pool = new ForkJoinPool();
		// executor = Executors.newCachedThreadPool();
		for (int i = 0; i < imax; i++) {
			MeanArray task = new MeanArray(null, arraysize);
			m_resultList.add(pool.submit(task));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String filepath = "/Users/cjw/Desktop/test.tif";
		// String filepath = "/home/chris/flybrain-g.tif";
		boolean MULTITHREAD = true;
		// boolean MULTITHREAD = false;
		int imax = 100;
		int arraysize = 1000000;
		if (args.length == 3) {
			if (args[0].compareTo("-m") == 0) {
				MULTITHREAD = true;
				System.out.println(args[0] + " " + MULTITHREAD);
			} else {
				MULTITHREAD = false;
			}
			imax = Integer.parseInt(args[1]);
			arraysize = Integer.parseInt(args[2]);
		}
		HeadMean head = new HeadMean(imax, arraysize);
		head.imax = imax;
		head.setMULTITHREAD(MULTITHREAD);
		head.run();
	}

	public boolean isMULTITHREAD() {
		return MULTITHREAD;
	}

	public void setIMAX(int imax) {
		this.imax = imax;
	}

	public void setMULTITHREAD(boolean mULTITHREAD) {
		MULTITHREAD = mULTITHREAD;
	}

	public double getMeanValue() {
		return meanValue;
	}

	public List<Double> getResult() {
		return resultList;
	}
	
	public double getTime() {
		return time;
	}
}
