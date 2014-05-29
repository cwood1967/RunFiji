package slice_mean_plugin;

import java.util.Random;

import ij.plugin.PlugIn;

public class over_line implements PlugIn {

	public over_line() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		System.out.println(Integer.MAX_VALUE);
		byte[] x = new byte[Integer.MAX_VALUE / 2];
		Random r = new Random();
		r.nextBytes(x);
		float total = 0f;
		for (int k = 0; k < 2000; k++) {
			for (int i = 0; i < x.length; i++) {
				total += x[i];
			}
		}
		System.out.println(total / x.length);
	}

	public static void main(String args[]) {
		over_line k = new over_line();
		k.run("");
	}
}
