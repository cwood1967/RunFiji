package slice_mean_plugin;

import java.awt.Checkbox;
import java.util.Random;
import java.util.Vector;

import org.python.antlr.ast.boolopType;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog;
import SliceMean.HeadMean;
public class over_line implements PlugIn {

	public over_line() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		
		GenericDialog gd = new GenericDialog("Input");
		gd.addNumericField("imax", 100, 3);
		gd.addNumericField("size", 100000, 7);
		gd.addCheckbox("multi", true);
		gd.centerDialog(true);
		gd.showDialog();
		int imax = (int)gd.getNextNumber();
		int arraysize = (int)gd.getNextNumber();
		Checkbox cb = (Checkbox)gd.getCheckboxes().get(0);
		boolean multi = cb.getState();
		HeadMean h = new HeadMean(imax, arraysize);
		h.setMULTITHREAD(multi);
		h.run();
		IJ.log("" + h.getTime() + " :: " + imax + " " + arraysize + " " + multi);
	}
	public static void main(String args[]) {
		
		Class<?> clazz = over_line.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);
		System.out.println(url + " " + pluginsDir);
		// start ImageJ
		new ImageJ();
		
		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
