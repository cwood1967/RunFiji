package Canny;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class edge_Canny_Plugin implements PlugInFilter {

	ImagePlus im;
	
	double sigma = 3.0;
	double low = .1;
	double high =.5;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
		
	}
	
	public void run(ImageProcessor ip) {
	
		if (!showDialog()) return;
		ImageProcessor result = ip.duplicate();
		
		edgeCanny e = new edgeCanny(result, sigma, high, low);
	}
	
	private boolean showDialog() {
		
		GenericDialog dlg = new GenericDialog(
				"Canny Edge Detector", IJ.getInstance());
		
			
		dlg.addNumericField("Gaussian Width", sigma, 3);
		
		dlg.addNumericField("High Threshold", high, 3);
		dlg.addNumericField("Low Threshold", low, 3);
		
		dlg.showDialog();
		
		if (dlg.wasCanceled())
			return false;
		
		if (dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid Input");
			return false;
		}
		
		sigma = (double)dlg.getNextNumber();
		high = (double)dlg.getNextNumber();
		low = (double)dlg.getNextNumber();
		
		return true;
	}
}
