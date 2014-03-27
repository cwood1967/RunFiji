package pug_a;
import ij.IJ;
import ij.plugin.PlugIn;
import ij.ImagePlus;
import ij.process.ShortProcessor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class test_spim implements PlugIn {

	private static final String inputfile = 
			"/Volumes/projects/smc/public/YUN/Garbage/out1.raw";
	private static final int width = 1004;
	private static final int height = 1002;
	private static final int aqbits = 16;

	public static void readspim() 
			throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		int planebytes = width*height*(aqbits/8);
		System.out.println(planebytes);
		File rawfile = new File(inputfile);
		System.out.println("File length: " + rawfile.length());
		
		DataInputStream incoming = null;
		incoming = new DataInputStream(new FileInputStream(rawfile));
		
		byte[] plane = new byte[planebytes]; 
		int nread = incoming.read(plane);
		incoming.close();
		byte[] converted = new byte[planebytes];
		System.out.println(plane.length + " " + nread);
		
		short[] pixels = new short[width*height];
		int pnum =0;
		for (int i=0; i < nread; i+= 2) {
			//byte x = plane[i];
			//byte y = plane[i+1];
		    converted[i] = plane[i+1];
		    converted[i+1] = plane[i];
			short pval = (short)(((converted[i]&0xff) << 8) | ((converted[i + 1]&0xff)));
			pixels[pnum] = pval;
			pnum++;
		}
		
		ShortProcessor processor = new ShortProcessor(width, height, pixels, null);
		ImagePlus imp = new ImagePlus("test spim image", processor);
		imp.show();
		//BufferedInputStream in = new Buff
	
	}
	
	public void run(String args) 
	{
		try {
		readspim();
		}
		catch(IOException e) {
			IJ.log(e.getMessage());
		}
	}
}	
