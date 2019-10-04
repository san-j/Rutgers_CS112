package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	//v1- Finished 4/9 5:45
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String,Occurrence> docMap = new HashMap<String,Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			String key = getKeyword(sc.next());
			if (key != null) {
				if (docMap.containsKey(key)) {
					Occurrence occ = docMap.get(key);
					occ.frequency++;
					docMap.put(key, occ);
				}
				else {
					docMap.put(key,new Occurrence(docFile,1));
				}
			}
		}
		
		sc.close();
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return docMap;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		//Collection<Occurrence> occs = kws.values();
		for (String key : kws.keySet()) {
			if (keywordsIndex.containsKey(key)) {
				ArrayList<Occurrence> occs = keywordsIndex.get(key);
				occs.add(kws.get(key));
				System.out.print("key: " + key);
				//System.out.print("\tbefore: " + occs);
				insertLastOccurrence(occs);
				System.out.println("\tafter : " + occs);
				keywordsIndex.put(key, occs);
			}
			else {
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(kws.get(key));
				//System.out.print("key: " + key);
				//System.out.print("\tbefore: " + occs);
				insertLastOccurrence(occs);
				//System.out.println("\tafter : " + occs);
				keywordsIndex.put(key, occs);
			}
		}
		
		/*kws.forEach(
			(key,value) -> keywordsIndex.merge(key,value, (v1,v2) -> v1.add(v2) )
			
		);*/
		//insertLastOccurrence
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	
	private boolean punctuation (char c) {
		if (c == '.' || c == ',' || c == '?' || c == ':' || c == ';' || c == '!') {
			return true;
		}
		return false;
	}

	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		while(word.length() > 0 && punctuation(word.charAt(word.length()-1))) {
			word = word.substring(0, word.length()-1);
		}
		
		if (word.length() == 0) {
			return null;
		}
		
		for (int i = 0; i < word.length(); i++) {
			if (!Character.isLetter(word.charAt(i))) {
				return null;
			}
		}
		
		if (noiseWords.contains(word.toLowerCase())) {
			return null;
		}
		
		/*File noise = new  File("noisewords.txt");
		Scanner sc = null;
		try {
			sc = new Scanner(noise);
		} catch (FileNotFoundException e) {
			//This should never happen so who cares
			e.printStackTrace();
		}
		while (sc.hasNextLine()) {
			if (word.equalsIgnoreCase(sc.nextLine())) {
				return null;
			}
		} 
		sc.close();*/
		
		return word.toLowerCase();
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		if (occs.size() == 0) {
			return null;
		}
		
		if (occs.size() == 1) {
			return null;
		}
		
		Occurrence toInsert = occs.get(occs.size()-1);
		int left = 0;
		int right = occs.size()-2;
		int mid = 0;
		boolean greater = false;
		while (left <= right) {
			mid = (left + right)/2;
			indices.add(mid);
			Occurrence toCompare = occs.get(mid);
			if (toInsert.frequency == toCompare.frequency) {
				break;
			}
			else if (toInsert.frequency < toCompare.frequency) {
				left = mid+1;
				greater = false;
			}
			else {
				right = mid-1;
				greater = true;
			}
		}
		if (greater) {
			occs.add(mid, toInsert);
			occs.remove(occs.size()-1);
		}
		else {
			occs.add(mid+1, toInsert);
			occs.remove(occs.size()-1);
		}
		//System.out.println("Indices: " + indices);
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return indices;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		//System.out.println("\nIndex:");
		//System.out.println(keywordsIndex + "\n");
		
		ArrayList<String> results  = new ArrayList<String>();
		ArrayList<Occurrence> kw1Occs = new ArrayList<Occurrence>();
		ArrayList<Occurrence> kw2Occs = new ArrayList<Occurrence>();
		
		if (keywordsIndex.containsKey(kw1)) {
			if (keywordsIndex.get(kw1).size() >= 5) {
				for (int i = 0; i < 5; i++) {
					kw1Occs.add(keywordsIndex.get(kw1).get(i));
				}	
			}
			else {
				for (int i = 0; i < keywordsIndex.get(kw1).size(); i++) {
					kw1Occs.add(keywordsIndex.get(kw1).get(i));
				}
			}
		}
		
		if (keywordsIndex.containsKey(kw2)) {
			if (keywordsIndex.get(kw2).size() >= 5) {
				for (int i = 0; i < 5; i++) {
					kw2Occs.add(keywordsIndex.get(kw2).get(i));
				}	
			}
			else {
				for (int i = 0; i < keywordsIndex.get(kw2).size(); i++) {
					kw2Occs.add(keywordsIndex.get(kw2).get(i));
				}
			}
		}
		
		//debugging
		System.out.println("\nOccurrences: ");
		System.out.println(kw1 + ": " + kw1Occs);
		System.out.println(kw2 + ": " + kw2Occs);
		
		while (kw1Occs.size() != 0 && kw2Occs.size()!= 0 && results.size() < 5) {
			if (kw1Occs.get(0).frequency > kw2Occs.get(0).frequency) {
				if (!results.contains(kw1Occs.get(0).document)) {
					results.add(kw1Occs.get(0).document);
				}
				kw1Occs.remove(0);
			}
			else if (kw1Occs.get(0).frequency < kw2Occs.get(0).frequency) {
				if (!results.contains(kw2Occs.get(0).document)) {
					results.add(kw2Occs.get(0).document);
				}
				kw2Occs.remove(0);
			}
			else {
				if (!kw1Occs.get(0).document.equals(kw2Occs.get(0).document)) {
					if (!results.contains(kw1Occs.get(0).document)) {
						results.add(kw1Occs.get(0).document);
					}
					if (!results.contains(kw2Occs.get(0).document)) {
						results.add(kw2Occs.get(0).document);
					}
				}
				else {
					if (!results.contains(kw1Occs.get(0).document)) {
						results.add(kw1Occs.get(0).document);
					}
				}
				kw1Occs.remove(0);
				kw2Occs.remove(0);
			}
		}
		
		if (kw1Occs.size() == 0 && kw2Occs.size() != 0) {
			while (kw2Occs.size() != 0) {
				if (!results.contains(kw2Occs.get(0).document)) {
					results.add(kw2Occs.get(0).document);
				}
				kw2Occs.remove(0);
			}
		}
		else if (kw2Occs.size() == 0 && kw1Occs.size() != 0) {
			while (kw1Occs.size() != 0) {
				if (!results.contains(kw1Occs.get(0).document)) {
					results.add(kw1Occs.get(0).document);
				}
				kw1Occs.remove(0);
			}
		}
		
		if (results.size() <= 5) {
			return results;
		}
		else {
			ArrayList<String> finalResults  = new ArrayList<String>();
			for (int i = 0; i < 5; i++) {
				finalResults.add(results.get(i));
			}
			return finalResults;
		}
		
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		
	
	}
}
