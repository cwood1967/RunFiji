package bleach_Window;

import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.FloatStatistics;

public class WindowStats extends FloatStatistics {
	
	ImageProcessor ip = null;
	public WindowStats(ImageProcessor ip) {
		super(ip, 23, null);
		this.ip = ip;
		
	}
	
	public int[] getMaxPosition() {
		float[] pixels = (float[])ip.getPixels();
		int imax = -1;
//		while (imax == -1) {
		for (int i = 0; i < pixels.length;i++) {
			if (pixels[i] == max) {
				imax = i;
				break;
			}
		}
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		int xmax = imax % w;
		int ymax = imax / w;

		return new int[] {xmax, ymax};
	}
	public void Print() {
		System.out.println(mean + " " + max + " " + min);
	}
} 
