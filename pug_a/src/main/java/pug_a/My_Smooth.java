package pug_a;
import java.util.Arrays;

import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.FloatProcessor;
import ij.process.ByteProcessor;
import ij.process.ShortProcessor;
import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

public class My_Smooth implements PlugInFilter {
// for git
// for git from eclipse on laptop
	// these are from the PluginFilter source
	
//	/** Set this flag if the filter handles 8-bit grayscale images. */
//	public int DOES_8G = 1;
//	/** Set this flag if the filter handles 8-bit indexed color images. */
//	public int DOES_8C = 2;
//	/** Set this flag if the filter handles 16-bit images. */
//	public int DOES_16 = 4;
//	/** Set this flag if the filter handles float images. */
//	public int DOES_32 = 8;
//	/** Set this flag if the filter handles RGB images. */
//	public int DOES_RGB = 16;
//	/** Set this flag if the filter handles all types of images. */
//	public int DOES_ALL = DOES_8G+DOES_8C+DOES_16+DOES_32+DOES_RGB;
//	
	ImagePlus imp = null;
	int width;
	int height;
	int bitdepth;
	FloatProcessor fip = null;
	float[] pixels = null;
	float[] newpixels = null;
	ImageProcessor ip = null;
	
	@Override
	  
	public int setup(String arg, ImagePlus imp) {
		System.out.println("dfdf");
		width = imp.getWidth();
		height = imp.getHeight();
		bitdepth = imp.getBitDepth();
		this.imp = imp;
		
		return DOES_ALL;
	}
	@Override
	public void run(ImageProcessor ip) {
		
		this.ip = ip;
		fip = (FloatProcessor)ip.convertToFloat();
		pixels = (float[])fip.getPixels();
		newpixels = Arrays.copyOf(pixels, pixels.length);
//		newpixels = new float[width*height];	
		doSmooth();
		IJ.log("hey");
		
		FloatProcessor nip = new FloatProcessor(width, height, newpixels);
		ImagePlus newimp = new ImagePlus();
		newimp.setTitle("My Smooth");
		newimp.setProcessor(nip);
		
		newimp.show();
	}
	
	private void doSmooth() {
//		int npixels = width*height;
		long t1 = System.nanoTime();
		
		int ip;
		float[] hood = null;
		for (int iy = 0; iy< height; iy++) {
			for (int ix =0; ix < width; ix++) {
				ip = iy*width + ix;
				if (isEdge(ix,iy) > 0) {
//					System.out.println(ip + " - " + ix + " , " + iy);
				} else {
					hood = getHood(ix, iy);
					float avg = hood_average(hood);
					newpixels[ip] = avg;
				}
			}
		}
	}
	
	private int isEdge(int x, int y) {
		
		int res = 0;
	
		if (x == 0) {
			res = 4;
		}
		else if (x == width -1) {
			res = 2;
		}
		else if ( y == 0) {
			res = 1;
		}
		else if (y == height -1) {
			res = 3;
		}
		return res;
	}
	
	private int pixelIndex(int x, int y) {
		return y*width + x; 
	}
	
	private float[] getHood(int x, int y) {
		float[] hood = new float[9];
		//int ip = y*width + x;
		hood[0] = pixels[pixelIndex(x-1, y-1)];
		hood[1] = pixels[pixelIndex(x, y-1)];
		hood[2] = pixels[pixelIndex(x+1, y-1)];
		hood[3] = pixels[pixelIndex(x-1, y)];
		hood[4] = pixels[pixelIndex(x, y)];
		hood[5] = pixels[pixelIndex(x+1, y)];
		hood[6] = pixels[pixelIndex(x-1, y+1)];
		hood[7] = pixels[pixelIndex(x, y+1)];
		hood[8] = pixels[pixelIndex(x+1, y+1)];
		
		return hood;
	}
	
	private float hood_average(float[] hood) {
		float hsum = 0;
		for (int i = 0; i < hood.length; i++) {
			hsum +=hood[i];
		}
		
		return hsum/(float)hood.length;
	}
	
	private void doSmooth2() {
		int npixels = width*height;
		long t1 = System.nanoTime();
		FloatProcessor fip = (FloatProcessor)ip.convertToFloat();
		float[] pixels = (float[])fip.getPixels();
		float[] newpixels = new float[npixels];
		
		float pv;
		float[] upperleft = cornerhood(pixels, 0);		
		newpixels[0] = hood_average(upperleft);
		
		float[] upperright = cornerhood(pixels, width-1);
		newpixels[width-1] = hood_average(upperright);
		
		float[] lowerleft = cornerhood(pixels, width*height- width);
		newpixels[width*height - width] = hood_average(lowerleft);
		
		float[] lowerright = cornerhood(pixels, width*height-1);
		newpixels[width*height-1] = hood_average(lowerright);
		
	
		for (int i = 0; i < newpixels.length; i++) {
			if (newpixels[i] > 0) {
				System.out.println(i + " " + pixels[i] + " " + newpixels[i]);
			}
		}
		
		long t2 = System.nanoTime();
		System.out.println("A " + newpixels[0]);
		System.out.println("B " + (t2-t1)*1e-6);
		
		FloatProcessor sip = new FloatProcessor(width-10, height-10, newpixels);
		ImagePlus smoothed = new ImagePlus("smoothed", sip);
		smoothed.show();
	}
	
//	private float[] getHood(float[] pixels, int t) {
//		
//	}
	
	private float[] cornerhood(float[] pixels, int pixelIndex) {
		float[] hood = new float[9];
		int np = pixelIndex;
		
		//do the corner by n=0
		if (np == 0) {
			hood[0] = pixels[0];
			hood[1] = pixels[0];
			hood[2] = pixels[1];
			hood[3] = pixels[0];
			hood[4] = pixels[0];
			hood[5] = pixels[1];
			hood[6] = pixels[width];
			hood[7] = pixels[width];
			hood[8] = pixels[width+1];
		}
		
		else if (np == (width - 1)) {
			//do the corner by n=width-1
			hood[0] = pixels[width-2];
			hood[1] = pixels[width-1];
			hood[2] = pixels[width-1];
			hood[3] = pixels[width-2];
			hood[4] = pixels[width-1];
			hood[5] = pixels[width-1];
			hood[6] = pixels[2*width-2];
			hood[7] = pixels[2*width-1];
			hood[8] = pixels[2*width-1];
			// end
		}
		
		else if (np == (width*height -width)) {
			//do the corner by n=width*height - width
			hood[0] = pixels[np-width];
			hood[1] = pixels[np-width];
			hood[2] = pixels[np - width + 1];
			hood[3] = pixels[np];
			hood[4] = pixels[np];
			hood[5] = pixels[np + 1];
			hood[6] = pixels[np];
			hood[7] = pixels[np];
			hood[8] = pixels[np + 1];
		}
		
		else if (np == (width*height - 1)) {
			//do the corner by n=width*height - width
			hood[0] = pixels[np - width -1];
			hood[1] = pixels[np-width];
			hood[2] = pixels[np - width + 1];
			hood[3] = pixels[np -1];
			hood[4] = pixels[np];
			hood[5] = pixels[np];
			hood[6] = pixels[np - 1];
			hood[7] = pixels[np];
			hood[8] = pixels[np];
			// end
		}
			
		return hood;
	}
	
	
	public static void main(String args[]) {
		
		Opener opener = new Opener();
		ImagePlus imp = opener.openImage("http://imagej.nih.gov/ij/images/blobs.gif");
		imp.show();
		
		My_Smooth my = new My_Smooth();
		int s = my.setup("nice try", imp);
		my.run(imp.getProcessor());
		System.out.println("Done");
	}

}
