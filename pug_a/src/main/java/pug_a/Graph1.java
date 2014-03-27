package pug_a;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.Random;

public class Graph1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int n = 3*3;
		int[] data = new int[n];
		
		Random rand = new Random();
		
		for (int i = 0; i < n; i++) {
			data[i] = rand.nextInt(5);
			System.out.println(data[i]);
		}
		HashMap<Integer, ArrayList<int []>> map = new HashMap<Integer, ArrayList<int []>>();
		
		for (int j = 0; j < n; j++) {
			for (int i =0; i < n; i++) {
				int[] value = {i,j};
				int key = Math.abs(data[i] - data[j]);
				if (map.containsKey(key)) {
					map.get(key).add(value);
				}
				else {
					ArrayList a = new ArrayList<int[]>();
					a.add(value);
					map.put(key, a);
				}
			}
		}
		
		for (Map.Entry<Integer, ArrayList<int[]>> entry : map.entrySet()) {
			ArrayList<int[]> vp = entry.getValue();
			Integer ev = entry.getKey();
			System.out.print(ev + " " );
			for (int[] x : vp) {
				System.out.print("(" + x[0] + "," + x[1] + ") ");
			}
			System.out.println("");
		}

		
	}

}
