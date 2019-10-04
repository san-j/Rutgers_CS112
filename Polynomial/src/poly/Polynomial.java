package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node sum = new Node(0,0,null);
		Node sumFront = new Node(0,0,sum);
		
		//ATTEMPT 1
		/*while(current1 != null) {
			Node current2 = poly2;
			while(current2 != null) {
				if (current1.term.degree == current2.term.degree) {
					sum.term.coeff = current1.term.coeff + current2.term.coeff;
					sum.term.degree = current1.term.degree;
				}
				else {
					sum.term.coeff = current1.term.coeff;
					sum.term.degree = current1.term.degree;
				}
				current2 = current2.next;
			}
			current1 = current1.next;
		}
		sumFront = sum;*/
		
		//ATTEMPT 2
		Node current1 = poly1;
		Node current2 = poly2;
		if (poly1 == null && poly2 == null) {
			sum = null;
		}
		else if (poly1 == null) {
			sum.next = poly2;
		}
		else if (poly2 == null) {
			sum.next = poly1;
		}
		else {
			while (current1 != null || current2!= null) {
				if(current1 == null) {
					sum.next = current2;
					break;
				}
				else if(current2 == null) {
					sum.next = current1;
					break;
				}
				else {
					Node temp = new Node(0,0,null);
					if(current1.term.degree < current2.term.degree) {
						temp.term = current1.term;
						sum.next = temp;
						current1 = current1.next;
					}	
					else if (current1.term.degree > current2.term.degree) {
						temp.term = current2.term;
						sum.next = temp;
						current2 = current2.next;
					}
					else {
						float newCoeff = current1.term.coeff + current2.term.coeff;
						temp.term.degree = current2.term.degree;
						temp.term.coeff = newCoeff;
						sum.next = temp;
						current1 = current1.next;
						current2 = current2.next;
					}
				}
				sum = sum.next;	
			}
		}
		
		sum = sumFront.next.next;
		Node tempHead = new Node(0,0,sum);
		sumFront.next = tempHead;
		
		/*System.out.println("Front: " + sumFront.term.degree + "," + sumFront.term.coeff);
		System.out.println("temp: " + sumFront.next.term.degree + "," + sumFront.next.term.coeff);
		System.out.println("sum: " + tempHead.next.term.degree + "," + tempHead.next.term.coeff);*/
		
		while (sum != null && sum.next != null) {
			if (sum.next.term.coeff == 0) {
				sum.next = sum.next.next;
				//tempHead.next = sum.next;
				//System.out.println("First: " + sum.term.coeff);
				//System.out.println("Second: " + sum); 
			}
			else {
				sum = sum.next;
			} 
		} 
		
		if (tempHead.next != null && tempHead.next.term.coeff == 0) {
			tempHead.next = tempHead.next.next;
		} 
		
		tempHead = sumFront.next;
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		return tempHead.next;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node product = new Node(0,0,null);
		Node productHead = new Node(0,0,product);
		Node ans = new Node(0,0,null);
		
		if (poly1 == null || poly2 == null) {
			return null;
		}
		else {
			Node current1 = poly1;
			Node current2 = poly2;
			Node current2Head = new Node(0,0,current2);
			while (current1 != null) {
				while (current2 != null) {
					float newCoeff = current1.term.coeff * current2.term.coeff;
					int newDegree = current1.term.degree + current2.term.degree;
					Node temp = new Node(newCoeff, newDegree, null);
					product.next = temp;
					current2 = current2.next;
					product = product.next;
					//System.out.println("Product: " + Polynomial.toString(product));
				}
				current2 = current2Head.next;
				current1 = current1.next;
				product = productHead.next;
				//System.out.println("Post-Product: " + Polynomial.toString(product.next));
				ans = Polynomial.add(ans, product.next);
			}
			return ans;
		}
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		float ans = 0;
		Node current = poly;
		while (current != null) {
			ans += current.term.coeff * (float) Math.pow(x,current.term.degree);
			current = current.next;
		}
		return ans;
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
