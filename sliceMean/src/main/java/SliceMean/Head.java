package SliceMean;

import java.util.List;
import java.util.ArrayList;
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

public class Head {

	String filepath;
	ImagePlus imp = null;
	ArrayList<Float> resultList = null;
	ArrayList<Future<Float>> m_resultList = null;

	int imax = 100;
	boolean MULTITHREAD;

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

	public void run() {
		final ImageProcessorReader r = new ImageProcessorReader(
				new ChannelSeparator(LociPrefs.makeImageReader()));
		
		resultList = new ArrayList<Float>();
		m_resultList = new ArrayList<Future<Float>>();
		try {

			r.setId(filepath);
			nx = r.getSizeX();
			ny = r.getSizeY();
			nz = r.getSizeZ();
			nt = r.getSizeT();
			nc = r.getSizeC();
			DimOrder = r.getDimensionOrder();
			nSlices = nc * nt * nz;
			double startTime = 0.001 * (new java.util.Date().getTime());

			if (MULTITHREAD) {
				System.out.println("Multithreading");
				process_multi(r);
			} else {
				process_single(r);
			}

			double endTime = 0.001 * (new java.util.Date().getTime());
			
			float allmax = 0;
			for (Future<Float> m : m_resultList) {
                try {
				if (m.get() > allmax) {
					allmax = m.get();
				}
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
//				System.out.println(m);
			}
			r.close();
			System.out.println(m_resultList.size() + " " + allmax + " "
					+ (endTime - startTime));
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void process_single(ImageProcessorReader r) {

		for (int i = 0; i < nSlices; i++) {
			if (i > imax) {
				break;
			}
			ImageProcessor ip;
			try {
				ip = r.openProcessors(i)[0];
			} catch (Exception e) {
				e.printStackTrace();
				return;
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
			resultList.add(max);
		}
	}

	public void process_multi(ImageProcessorReader r) {

		final ForkJoinPool pool = new ForkJoinPool();

		for (int i = 0; i < nSlices; i++) {
			if (i > imax) {
				break;
			}
			MaxTask task = new MaxTask(r, i);
			m_resultList.add(pool.submit(task));
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		String filepath = "/Volumes/projects/smc/public/LIA/08222013/out.tif";
		String filepath = "/home/chris/flybrain-g.tif";
		boolean MULTITHREAD = true;
//		boolean MULTITHREAD = false;
		int imax = 10;
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
		head.MULTITHREAD = MULTITHREAD;
		head.run();
	}

}
