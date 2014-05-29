package slice_mean_plugin;


import java.util.List;

import ij.IJ;
import ij.plugin.PlugIn;
import ij.ImagePlus;
import ij.ImageStack;
import SliceMean.Head;

public class Max_Finder_Plugin implements PlugIn {

	@Override
	public void run(String args) {
		
		ImagePlus imp = IJ.getImage();
		ImageStack stack = imp.getImageStack();
		IJ.log("" + stack.getSize());
		Head head  = new Head(imp);
		head.setMULTITHREAD(false);
		head.setIMAX(100000);
		double starttime = .001*System.currentTimeMillis();
		head.run();
		double endtime = .001*System.currentTimeMillis();
		List<Float> rlist = head.getResult();
		float rmax = head.getMaxValue();
		IJ.log("" + rmax);
		IJ.log("" + (endtime - starttime));
		
		Head head2 = new Head(imp);
		head2.setIMAX(100000);
		head2.setMULTITHREAD(true);
		double starttime2 = .001*System.currentTimeMillis();
		head2.run();
		double endtime2 = .001*System.currentTimeMillis();
		List<Float> rlist2 = head2.getResult();
		float rmax2 = head2.getMaxValue();
		IJ.log("" + rmax2);
		IJ.log("" + (endtime2 - starttime2));
		IJ.log("" + rlist.size());
		
	}
	public Max_Finder_Plugin() {
		// TODO Auto-generated constructor stub
		int x = 1;
	}

}
