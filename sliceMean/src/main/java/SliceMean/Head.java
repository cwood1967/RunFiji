package SliceMean;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.concurrent.ForkJoinPool;
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

public class Head {

	String filepath;
	ImagePlus imp = null;
	ArrayList<Float> resultList = null;
	ArrayList<Future<Float>> m_resultList = null;
	ImageStack stack = null;
	int imax = 100;
	float maxValue;
	
	private boolean MULTITHREAD;
	ExecutorService executor = null;
	// ImageProcessorReader r = null;
	int nx;
	int ny;
	int nz;
	int nt;
	int nc;
	int nSlices;
	String DimOrder;

	public Head(String path) {
		// TODO Auto-generated constructor stub
		filepath = path;
	}

	public Head(ImagePlus imp) {
		nx = imp.getWidth();
		ny = imp.getHeight();
		nc = imp.getNChannels();
		nz = imp.getNSlices();
		nt = imp.getNFrames();
		nSlices = nz*nt*nc;
		
		stack = imp.getStack();
		this.stack = stack;
		
	}

	public void run() {

		if (stack == null) {
			readImage();
		}

		resultList = new ArrayList<Float>();
		m_resultList = new ArrayList<Future<Float>>();

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
				Float tc = m_resultList.get(0).get();
				System.out.println(tc);// + tc.get(0));
				float mtm = 0;
				for (Future<Float> ff : m_resultList) {
					float m = ff.get();
					if (m > mtm) {
						mtm = m;
					}
					resultList.add(m);
				}
				maxValue = mtm;
				System.out.println("Max value :" + mtm);
				executor.shutdown();
			} else {
				float allmax = 0f;
				for (Float f : resultList) {
					if (f > allmax) {
						allmax = f;
					}
				}
				maxValue = allmax;
				System.out.println(resultList.size() + " " + allmax);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Stop");
		double endTime = 0.001 * (new java.util.Date().getTime());
		System.out.println("Time " + (endTime - startTime));
		
		// }
		//
		// catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private void readImage() {
		final ImageProcessorReader r = new ImageProcessorReader(
				new ChannelSeparator(LociPrefs.makeImageReader()));

		try {

			r.setId(filepath);
			nx = r.getSizeX();
			ny = r.getSizeY();
			nz = r.getSizeZ();
			nt = r.getSizeT();
			nc = r.getSizeC();
			DimOrder = r.getDimensionOrder();
			nSlices = nc * nt * nz;

			stack = new ImageStack(nx, ny);

			for (int i = 0; i < nSlices; i++) {
				ImageProcessor ip = null;
				try {
					ip = r.openProcessors(i)[0];
					stack.addSlice("" + (i + 1), ip);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (i > imax) {
					break;
				}
				r.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void process_single() {

		for (int i = 0; i < nSlices; i++) {
			if (i > imax) {
				break;
			}
			ImageProcessor ip;

			ip = stack.getProcessor(i + 1);
			//ip = ip.convertToFloat();
			short[] pixels = (short[]) ip.getPixels();
			float[] fpix = new float[pixels.length];
			
			float max = 0.f;
			for (int k = 0; k < pixels.length; k++) {
				fpix[k] = pixels[k]&0xffff;
			}
			
			for (int j = 0; j < fpix.length; j++) {
				if (fpix[j] > max) {
					max = fpix[j];
				}
			}
			
			pixels = null;
			resultList.add(max);
		}
	}

	public void process_multi() {

		// final ForkJoinPool pool = new ForkJoinPool();
		executor = Executors.newCachedThreadPool();
		for (int i = 0; i < nSlices; i++) {
			if (i > imax) {
				break;
			}
			ImageProcessor ip = stack.getProcessor(i + 1);
			// MaxTask task = new MaxTask(ip);
			// m_resultList.add(pool.submit(task));
			Callable maxer = new MaxTask(ip);
			Future<Float> future = executor.submit(maxer);
			m_resultList.add(future);

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String filepath = "/Users/cjw/Desktop/test.tif";
		// String filepath = "/home/chris/flybrain-g.tif";
		boolean MULTITHREAD = true;
		// boolean MULTITHREAD = false;
		int imax = 100;
		if (args.length == 3) {
			if (args[0].compareTo("-m") == 0) {
				MULTITHREAD = true;
				System.out.println(args[0] + " " + MULTITHREAD);
			} else {
				MULTITHREAD = false;
			}
			filepath = args[1];
			System.out.println(filepath);
			imax = Integer.parseInt(args[2]);
		}
		Head head = new Head(filepath);
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

	public float getMaxValue() {
		return maxValue;
	}
	public List<Float> getResult() {
		return resultList;
	}
}
