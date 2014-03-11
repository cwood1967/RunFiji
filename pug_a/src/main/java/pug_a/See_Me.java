package pug_a;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.ImageJ;

public class See_Me  implements PlugIn {
	public void run(String arg) {
		IJ.log("hello");
		IJ.log("See");
		IJ.log("See Me");
		IJ.log("Feel Me");
		IJ.log("Touch Me");
		
		ImagePlus image = IJ.openImage("http://imagej.net/images/clown.jpg");
		image.show();
	}
}	

