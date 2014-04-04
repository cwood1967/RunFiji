package bleach_Window;

import java.util.ArrayList;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.ImagePlus;
import ij.ImageStack;
import ij.IJ;
import loci.formats.in.OMETiffReader;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

public class BleachWindow {

	int num;
	int width;
	int height;
	
	ArrayList<WindowStats> statsList = new ArrayList<WindowStats>();
	String filename;
	ImagePlus imp = null;
	ImagePlus croppedImp = null;
	ArrayList<Integer> window = null;
	ImagePlus template = null; //assume template is always the same
	int templateSlice = 1; // the slice of the first template image, zero based 
	
	public BleachWindow(String filename) {
		this.filename = filename;
	}

	public BleachWindow(String filename, int template_slice) {
		this.filename = filename;
		this.templateSlice = template_slice;
	}
	
	public void setTemplateSlice(int num) {
		templateSlice = num;
	}
	public int readImage() {
		ImageProcessorReader reader = new ImageProcessorReader(
				new ChannelSeparator(LociPrefs.makeImageReader()));

		int result = 0;
		try {
			reader.setId(filename);
			num = reader.getImageCount();
			width = reader.getSizeX();
			height = reader.getSizeY();
			
			ImageStack stack = new ImageStack(width, height);
			int nstart;
			if (templateSlice % 2 == 0) {
				nstart = 1;
			} else {
				nstart = 0;
			}
			for (int i=nstart; i<num; i+=2) {
		        IJ.showStatus("Reading image plane #" + (i + 1) + "/" + num);
		        FloatProcessor ip = (FloatProcessor)reader.openProcessors(i)[0].convertToFloat();
		        
		        stack.addSlice("" + (i + 1), ip);
		        int channel = reader.getZCTCoords(i)[1];
		        
		      }
			
			imp = new ImagePlus("Some Image", stack);
			FloatProcessor tip =  (FloatProcessor)reader.openProcessors(templateSlice)[0].convertToFloat();
			template = new ImagePlus("template", tip);
			reader.close();
		}

		catch (FormatException e) {
			e.printStackTrace();
			result = -1;
		} catch (Exception e) {
			e.printStackTrace();
			result = -2;
		}
		
		return result;
	}
	
	public ImagePlus makeCroppedStack() {
		int index = 0;
		int diff = window.get(index + 1) - window.get(index); 
		int wx = 0;
		int wy = 0;
		while (diff == 1) {
			index++;
			diff = window.get(index + 1) - window.get(index); 
		}
		wx = index + 1;
		wy = window.size()/wx;
		
		int numslices = imp.getStack().getSize();
		ImageStack croppedStack = new ImageStack(wx, wy);
		for (int i = 0; i < numslices; i++) {
			croppedStack.addSlice(makeCroppedImage(wx, wy, i + 1));
		}
		
		croppedImp = new ImagePlus("Cropped Stack", croppedStack);
		return croppedImp;
	}
	
	public ImageProcessor makeCroppedImage(int wx,int wy, int slice) {
		
		FloatProcessor nip = new FloatProcessor(wx, wy);
		
		float[] pixels = new float[window.size()];
		float[] imagePixels = (float[])imp.getStack().getPixels(slice);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = imagePixels[window.get(i)];
		}
		nip.setPixels(pixels);
		WindowStats stats = new WindowStats(nip);
        int[] minmax = stats.getMaxPosition();
        statsList.add(stats);
        System.out.print(slice + " ");
        stats.Print();
        System.out.println(minmax[0] + " " + minmax[1]);
		return nip;

	}
	
	public ArrayList<Integer> findWindow() {
		ArrayList<Integer> plist = new ArrayList<Integer>();
		float[] pixels = (float[])template.getProcessor().getPixels();
		for (int i =0; i < pixels.length; i++ ) {
			if (pixels[i] < 0.1) {
				plist.add(i);
				System.out.print(i + ", ");
			}
		}
		System.out.println("");
		System.out.println(plist.size());
		window = plist;
		return plist;
	}
	
	
	public void showImage() {
		imp.show();
	}
}
