package ft_p;

import ij.IJ;
import ij.plugin.PlugIn;
import ij.ImageJ;

public class my_plug  implements PlugIn {
	public void run(String arg) {
		IJ.log("hello");
		IJ.log("hello");
	}
	
	public static void main(String[] args) {
		
		new ImageJ();
		Class clazz = my_plug.class;
		IJ.runPlugIn(clazz.getName(), "");
	}
	
	
}
