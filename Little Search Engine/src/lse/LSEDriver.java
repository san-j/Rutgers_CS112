package lse;

import java.io.FileNotFoundException;

public class LSEDriver {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		LittleSearchEngine lse = new LittleSearchEngine();
		lse.makeIndex("docs.txt", "noisewords.txt");
		System.out.println("\n" + "result: " + lse.top5search("small", "jeez"));
		//System.out.println(lse.getKeyword("Through"));
	}
}