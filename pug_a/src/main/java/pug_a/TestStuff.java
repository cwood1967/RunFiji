package pug_a;

import ij.IJ;
import ij.plugin.Macro_Runner;
import ij.plugin.filter.MaximumFinder;


public class TestStuff {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(12 & 4);
		int x = 0 == 4 ? 45 : 78;
		System.out.println(x);
		
		Macro_Runner mr = new Macro_Runner();
		String mfile = "/Users/cjw/script.ijm";
		
		mr.run(mfile);
	}

}
