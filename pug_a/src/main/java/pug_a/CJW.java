package pug_a;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class CJW implements PlugIn {
	
	public void run(String arg) {
		ImagePlus image = IJ.openImage("/Users/cjw/swampkoa.jpg");
		image.show();
	}

}
