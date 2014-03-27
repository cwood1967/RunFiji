package pug_a;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.ImageJ;

/**
 * 
 * @author cjw
 *
 */
public class CopyOfSee_Me  implements PlugIn {

	@Override
	public void run(String arg) {
		IJ.log("hello, this is a copy");
		IJ.log("hello, this is a copy");
		
		ImagePlus image = IJ.openImage("http://imagej.net/images/clown.jpg");
		image.show();
		IJ.log("hey clown! " +  someMethod(12, 3));

	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int someMethod(int x, int y) {
		return x + y;
	}
}	

