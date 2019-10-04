package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	//v7- 4/01 9:59
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	
	private String getInside(String s) {
		s = s.substring(1,s.length()-1);
		return s;
	}
	
	private boolean isTag(String s) {
		if (s.indexOf('<') == -1) {
			return false;
		}
		return true;
	}
	
	public void build() {
		/** COMPLETE THIS METHOD **/
		Stack<TagNode> tags = new Stack<TagNode>();
		String rootTag = getInside(sc.nextLine());
		String bodyTag = getInside(sc.nextLine());
		TagNode bodyNode = new TagNode(bodyTag,null,null);
		root = new TagNode(rootTag,bodyNode,null);
		tags.push(root);
		tags.push(bodyNode);
		while (sc.hasNextLine()) {	
			String line = sc.nextLine();
			if (isTag(line)) {
				//System.out.println("tag: " + line);
				if (line.indexOf('/') != -1) {
					tags.pop();
				}
				else {
					TagNode next = new TagNode(getInside(line),null,null);
					if (tags.peek().firstChild == null) {
						tags.peek().firstChild = next;
					}
					else {
						TagNode ptr = tags.peek().firstChild;
						while (ptr.sibling != null) {
							ptr = ptr.sibling;
						}
						ptr.sibling = next;
					}
					tags.push(next);
				}
			}
			else {
				//System.out.println("line: " + line);
				TagNode next = new TagNode(line,null,null);
				if (tags.peek().firstChild == null) {
					tags.peek().firstChild = next;
				}
				else {
					TagNode ptr = tags.peek().firstChild;
					while (ptr.sibling != null) {
						ptr = ptr.sibling;
					}
					ptr.sibling = next;
				}
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		TagNode current = root;
		Stack<TagNode> tags = new Stack<TagNode>();
		find(oldTag,current,tags);
		if (tags.isEmpty()) {
			System.out.println("not found"); 
		}
		else {
			while(!tags.isEmpty()) {
				TagNode toReplace = tags.pop();
				toReplace.tag = toReplace.tag.replace(oldTag, newTag);
			}
		}
		/*if (current == null) {
			return;
		}
		else {
			//System.out.println(current.tag);
			replace(oldTag,newTag,current);
		}*/
	}
	
	/*private void replace(String oldTag, String newTag, TagNode currentPtr) {
		if (currentPtr == null) {
			return;
		}
		if (currentPtr.tag.equals(oldTag)) {
			//System.out.println("to replace: " + currentPtr.tag);
			currentPtr.tag = currentPtr.tag.replace(oldTag, newTag);
			//System.out.println("replaced: " + currentPtr.tag);
		}
		TagNode sibling = currentPtr.sibling;
		TagNode child = currentPtr.firstChild;
		replace(oldTag,newTag,sibling);
		replace(oldTag,newTag,child);
	}*/
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		Stack<TagNode> tags = new Stack<TagNode>();
		//TagNode tablePtr = locate("table", ptr.firstChild.firstChild);
		find("table", root, tags);
		
		if (tags.isEmpty()) {
			System.out.println("not found"); 
		}
		else {
			TagNode table = tags.pop();
			//System.out.println(table.tag);
			TagNode tr = table.firstChild;
			int count = 1;
			while (count!=row && tr != null) {
				tr = tr.sibling;
				count++;
			}
			if (count!= row || tr == null) {
				System.out.println("row doesn't exist");
			}
			else {
				TagNode td = tr.firstChild;
				while (td != null) {
					td.firstChild = new TagNode("b", td.firstChild, null);
					td = td.sibling;
				}
			}
		}
	}
		
	
	private void find(String s, TagNode current, Stack<TagNode> tags) {
		if (current == null) {
			return;
		}
		if (current.tag.equals(s)) {
			//ans = current;
			tags.push(current);
		}
		find(s, current.firstChild,tags);
		find(s, current.sibling,tags);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if (tag == null) {
			return;
		}
		while (hasTag(tag,root)) {
			remove(tag,root,root.firstChild);
		}
	}
	
	private void remove(String tag, TagNode previous, TagNode current) {
		if (current == null) {
			return;
		}
		
		if (current.tag.equals(tag) && !isList(tag)) {
			if (previous.sibling == current) {
				TagNode child = current.firstChild;
				TagNode sibling = current.sibling;
				previous.sibling = child;
				while (child.sibling != null) {
					child = child.sibling;
				}
				child.sibling = sibling;
			}
			else if (previous.firstChild == current) { 
				TagNode sibling = current.sibling;
				previous.firstChild = current.firstChild;
				TagNode thing = previous.firstChild;
				while(thing.sibling != null) {
					thing = thing.sibling;
				}
				thing.sibling = sibling;
			}
			//here
		}
		else if (current.tag.equals(tag) && isList(tag)) {
			 TagNode ptr = current.firstChild;
			 while (ptr != null) {
				 if (ptr.tag.equals("li")) {
					 ptr.tag = "p";
				 }
				 ptr = ptr.sibling;
			 }
			 if (previous.sibling == current) {
				TagNode child = current.firstChild;
				TagNode sibling = current.sibling;
				previous.sibling = child;
				while (child.sibling != null) {
					child = child.sibling;
				}
				child.sibling = sibling;
			 }
			 else if (previous.firstChild == current) { 
				TagNode sibling = current.sibling;
				previous.firstChild = current.firstChild;
				TagNode thing = previous.firstChild;
				while(thing.sibling != null) {
					thing = thing.sibling;
				}
				thing.sibling = sibling;
			 }
			 //here
		}
		else {
			remove(tag,current,current.firstChild);
			remove(tag,current,current.sibling);
		}
		
	}
	
	private boolean hasTag(String tag, TagNode current) {
		if (current == null)
			return false;
		if (current.tag.equals(tag))
			return true;
		
		boolean child = hasTag(tag,current.firstChild);
		boolean sibling = hasTag(tag,current.sibling);
		return (child || sibling);
	}
	
	private boolean isList(String tag) {
		if (tag.equals("ol") || tag.equals("ul")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		if (word == null || !(tag.equals("em") || tag.equals("b"))) {
			return;
		}	
		add(word,tag,root);
	}
	
	private boolean punctuation(String s) {
		if (s.equals(".") || s.equals(",") || s.equals("?") || s.equals("!") || s.equals(":")
				|| s.equals(";")) {
			return true;
		}
		return false;
	}
	
	private void add(String word, String tag, TagNode current) {
		if (current == null) {
			return;
		}
		
		int index = current.tag.toLowerCase().indexOf(word.toLowerCase());
		int len = word.length();
		if (index != -1) {
			String prev = current.tag.substring(0,index);
			String sub = current.tag.substring(index, index+len);		
			String post = current.tag.substring(index+len);
			
			if (prev.length() == 0 && post.length() == 0) {
				current.tag = tag;
				current.firstChild = new TagNode(sub, current.firstChild,null);
			}
			else if (prev.length() == 0 && post.length() == 1 && punctuation(post)) {
				current.tag = tag;
				current.firstChild = new TagNode(sub+post,current.firstChild,null);
			}
			else {
				TagNode sib = current.sibling;
				if (post.length() > 1 && post.charAt(1) == ' ' &&punctuation(Character.toString(post.charAt(0)))) {
					sub += post.charAt(0);
					post = post.substring(1);
				}
				else if (post.length() >= 1 && post.charAt(0) != ' ' && !punctuation(Character.toString(post.charAt(0)))) {
					/*if (post.toLowerCase().contains(word.toLowerCase())) {
						prev = prev+sub;
						int newInd = current.tag.indexOf(word, prev.length());
						prev = current.tag.substring(0,newInd);
						sub = current.tag.substring(newInd, newInd + len);
						post = current.tag.substring(newInd+len);
					}
					else {
						post = sub+post;
						sub = "";
					}*/
					post = sub+post;
					sub = "";
				}
				else if (post.length() == 1 && punctuation(post)) {
					sub += post;
					post = "";
				}
				else if (post.length() > 1 && punctuation(Character.toString(post.charAt(0))) && post.charAt(1) != ' ') {
					/*if (post.toLowerCase().contains(word.toLowerCase())) {
						prev = prev+sub;
						int newInd = current.tag.indexOf(word, prev.length());
						prev = current.tag.substring(0,newInd);
						sub = current.tag.substring(newInd, newInd + len);
						post = current.tag.substring(newInd+len);
					}
					else {
						post = sub+post;
						sub = "";
					}*/
					post = sub+post;
					sub = "";
				}
				
				if (post.toLowerCase().contains(word.toLowerCase())) {
					
				}
				
				if (sub.length() > 0) {
					if (prev.length() > 0 && prev.charAt(prev.length()-1) == ' ') {
						current.tag = prev;
						current.sibling = new TagNode(tag, new TagNode(sub,null,null), null);
						if (post.length() > 0) {
							current.sibling.sibling = new TagNode(post, null, sib);
						}
						else {
							current.sibling.sibling = sib;
							
						}
						add(word,tag,current.sibling.sibling);
					}
					else if (prev.length() > 0) {
						/*if (post.toLowerCase().contains(word.toLowerCase())) {
							
						}
						else {
							add(word,tag,current.sibling);
						}*/
						add(word,tag,current.sibling);
					}
					else {
						current.tag = tag;
						current.firstChild = new TagNode(sub,null,null);
						if (post.length() > 0) {
							current.sibling = new TagNode(post, null, sib);
						}
						else {
							current.sibling = sib;
						}
						add(word,tag,current.sibling);
					}
				}
				else {
					add(word,tag,current.sibling);
				}
			}
				
		}
		else {
			add(word,tag,current.firstChild);
			add(word,tag,current.sibling);
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
