package bleach_Window;

import java.util.ArrayList;

import ij.io.PluginClassLoader;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;

public class BleachWindow_PlugIn implements PlugIn {

	@Override
	public void run(String arg0) {
		// TODO Auto-generated method stub
		//ij.IJ.log("Running");
		String filename = "/Volumes/projects/jru/public/Fiji_teaching.app/Jaspersen FRET sample data/SLJ7699 n 4.ome.tiff";
		BleachWindow b = new BleachWindow(filename);
		int res = b.readImage();
		ArrayList<Integer> window = b.findWindow();
		
		ImagePlus nx = b.makeCroppedStack();
		//b.showImage();
		nx.show();
	}

	public static void main(String[] argv) {

//		BleachWindow_PlugIn x = new BleachWindow_PlugIn();
		Class<?> clazz = BleachWindow_PlugIn.class;
		String url = clazz.getResource(
				"/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length()
				- clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();
		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}