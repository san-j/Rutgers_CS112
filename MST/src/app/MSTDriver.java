package app;

import java.io.IOException;
import structures.*;
import java.util.ArrayList;
import java.util.Iterator;

public class MSTDriver {
	public static void main (String[] args) throws IOException {
		//System.out.println("h");
		Graph graph = new Graph("graph1.txt");
		PartialTreeList L = PartialTreeList.initialize(graph);
		
		Iterator<PartialTree> iter = L.iterator();
		
		while (iter.hasNext()) {
			//System.out.println("hh");
			PartialTree pt = iter.next();
			System.out.println(pt.toString());
		}
		
		ArrayList<Arc> MST = new ArrayList<Arc>();
		
		MST = PartialTreeList.execute(L);
		
		System.out.println("\nMST ARCS: ");
		for (int i = 0; i < MST.size(); i++) {
			System.out.println(MST.get(i));
		}
		
	}
}