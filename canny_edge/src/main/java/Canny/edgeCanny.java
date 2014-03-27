package Canny;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Convolver;
import ij.process.Blitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.plugin.filter.GaussianBlur;
import java.lang.Math;


public class edgeCanny {

	double low;// = .1;
	double high;// =.2;
	double sigma;// = 3.0;
	
	float e1 = 3.0f/32.0f;
	float e2 = 10.0f/32.0f;
	
	final float[] sobely = {-e1,-e2, -e1, 0.0f,0.0f,0.0f
							,e1,e2,e1};
	final float[] sobelx = {-e1, 0.0f, e1, -e2, 0.0f
							,e2, -e1, 0.0f, e1};
	
	ImageProcessor ipOrig;
	ImageProcessor gx;
	ImageProcessor gy;
	
	public edgeCanny(ImageProcessor ip, double sigma, double high, double low) {
		this.ipOrig = ip;
		this.sigma = sigma;
		this.high = high;
		this.low = low;
		dostuff();
	}
	
	public void dostuff() {
		
		ImageProcessor image = ipOrig.convertToFloat();
		
		GaussianBlur g = new GaussianBlur();
		g.blurGaussian(image, sigma, sigma, 0.01);
		
		ImageProcessor fpx = image.duplicate();
		ImageProcessor fpy = image.duplicate();
		
		Convolver conv = new Convolver();
		
		int kx =3;
		
		boolean res = conv.convolveFloat(fpx, sobelx, kx, kx);
		res = conv.convolveFloat(fpy, sobely, kx, kx);
		
		// calculate the alpha angles before doing anything else - then the imageprocessors
		// can be changed
		float[][] pixgx = fpx.getFloatArray();
		float[][] pixgy = fpy.getFloatArray();

		int nx = fpx.getWidth();
		int ny = fpx.getHeight();
		
		float[][] alpha = new float[nx][ny]; //the direction angle
		// need to calculate the magnitute of the gradient now, to use in the loop
		
		//square the sobel operators
		fpx.sqr();
		fpy.sqr();
		
		//use the blitter class to add fpx and fpy together
		fpx.copyBits(fpy, 0, 0, Blitter.ADD);
		//sqrt fpx , this is the gradient magnitude
		fpx.sqrt();
		fpx.resetMinAndMax();
		//fpx is now ImageProcessor for the magnitude of the gradient
		
		// done with gradient magnitute
		
		// now calculate the direction of the edge
		float dr = (float)(180.0/Math.PI);
		float thisalpha;
		float p1 = 0.0f;
		float p2 =0.0f;
		float mxy;
		
		
		ImageProcessor gnx = fpx.duplicate();
		float[]  np = new float[9];
		int gnH =0;
		int gnL = 0;
		try {
			for (int i = 1; i < nx -1; i++) {
			
				for (int j =1; j < ny -1; j++) {
				
					thisalpha =dr*(float)Math.atan2((double)pixgy[i][j],(double)pixgx[i][j]);
					//calculate the edge direction, 0,90, 45, -45
					int edge = getdirection(thisalpha);
					
					mxy = fpx.getf(i,j);

					np[0] = fpx.getf(i-1,j-1);
					np[1] = fpx.getf(i,j-1);
					np[2] = fpx.getf(i+1,j-1);
					np[3] = fpx.getf(i-1,j);
					np[4] = fpx.getf(i,j);					
					np[5] = fpx.getf(i + 1,j);
					np[6] = fpx.getf(i-1,j +1);
					np[7] = fpx.getf(i,j+1);
					np[8] = fpx.getf(i+1,j+1);
					
					switch (edge) {
						case 90:{
							p1 = np[3];  //fpx.getf(i-1,j);
							p2 = np[5]; //fpx.getf(i+1,j);
							break;
						}
						case 0:{
							p1 = np[1]; //fpx.getf(i,j-1);
							p2 = np[7]; //fpx.getf(i,j+1);
							break;
						}
						case -45:{
							p1 = np[2]; // fpx.getf(i+1,j-1);
							p2 = np[6]; //fpx.getf(i-1,j+1);
							break;
						}
						case 45:{
							p1 = np[0]; //fpx.getf(i-1,j-1);
							p2 = np[8]; //fpx.getf(i+1,j+1);
							break;
						}
					}
					
				//	gnx = fpx.duplicate();
					
					if ((mxy < p1) || (mxy < p2)) {
						gnx.setf(i,j, 0.0f);
						
					}
					
					//end first pass now - do the thresholding and edge following in another
					//loop
				}
			}
			
			double maxgnx = gnx.getMax();
			double mingnx = gnx.getMin();
			
			double rangegnx = maxgnx-mingnx;
			
			double tlow = mingnx + low*rangegnx;
			double thigh = mingnx + high*rangegnx;
			
			for (int i = 1; i < nx -1; i++) {
				for (int j =1; j < ny -1; j++)
				{
					
					mxy = gnx.getf(i,j);
					
					//if (mxy < 0.0001f) continue;
					if (mxy <= tlow) {
						gnx.setf(i,j, 0.0f);
						continue;
					}
					
					np[0] = gnx.getf(i-1,j-1);
					np[1] = gnx.getf(i,j-1);
					np[2] = gnx.getf(i+1,j-1);
					np[3] = gnx.getf(i-1,j);
					np[4] = gnx.getf(i,j);					
					np[5] = gnx.getf(i + 1,j);
					np[6] = gnx.getf(i-1,j +1);
					np[7] = gnx.getf(i,j+1);
					np[8] = gnx.getf(i+1,j+1);
				
					
					// do the high threshold
					if (mxy >= thigh) {
						gnH = 1; //gnx.getf(i,j);
						continue;
					}
					else gnH = 0;
					
					//do the low threshold
					if (mxy >= tlow) {
						gnL = 1;
					}
					else {
						gnL = 0;
					}
					gnL = gnL - gnH;
					int setthis = 0;
					
					for (int k =0;k < 9; k++) {
						if (np[k] >= tlow) {
							setthis = 1;
							
							break;
						}
					}
					
					if (setthis == 0) gnx.setf(i,j, 0.0f) ;
					
				}
			}
		}
			
		catch (IndexOutOfBoundsException e) {
				
				e.printStackTrace();
			}
					
		//ImageProcessor ipalpha = fpx.duplicate();
		//ipalpha.setFloatArray(alpha);
		fpx.resetMinAndMax();
		//ImagePlus winx = new ImagePlus("Sobel sqr", fpx);
		//winx.show();

		//ImagePlus winy = new ImagePlus("Sobel Y", fpy);
		//winy.show();
		
		//ipalpha.resetMinAndMax();
		gnx.resetMinAndMax();
		ImagePlus wina = new ImagePlus("Alpha", gnx);
		
		wina.show();
		

}
	
	private int getdirection(float alpha) {
	
		float[] angles = {-22.5f,22.5f,67.5f,112.5f,157.5f, -157.5f, -112.5f, -67.5f, -22.5f};
		
		int n = angles.length;
		boolean found = false;
		int index=0;
		for (int i = 0; i < n -1; i++) {
			if ((alpha > angles[i]) && (alpha <= angles[i + 1]))  
				found = true;
			if (found) {
				index =i;
				break;
			}
		}
		
		int res = 0;
		switch (index) {
			case 0:res=90;break;
			case 1:res=45;break;//
			case 2:res=0;break;
			case 3:res=-45;break;
			case 4:res=90;break;
			case 5:res=45;break;//
			case 6:res=0;break;
			case 7:res=-45;break;
		}	
		
		return res;
		
	}
	
	
}
