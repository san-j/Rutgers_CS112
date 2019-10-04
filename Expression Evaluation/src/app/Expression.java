package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	expr = expr.replaceAll("\\s","");		//Strip whitespace
    	StringTokenizer parsed = new StringTokenizer(expr, delims,true); //Tokenize
    	//Iterate through all tokens
    	while (parsed.hasMoreTokens()) {
    		String token = parsed.nextToken();
    		try {
    			Integer.parseInt(token);
    		}
    		catch (NumberFormatException e) {
    			if (checkToken(token)) {
        			try {
        				String next = parsed.nextToken();
        				if (next.equals("[")) {
            				Array arr = new Array(token);
            				if (arrays.contains(arr)) {
            					continue;
            				}
            				else {
                    			arrays.add(arr);
            				}
            			}
            			else {
            				Variable var = new Variable(token);
            				if (vars.contains(var)) {
            					continue;
            				}
            				else {
                    			vars.add(var);
            				}
            			}
        			}
        			catch (NoSuchElementException f){
        				Variable var = new Variable(token);
        				if (vars.contains(var)) {
        					continue;
        				}
        				else {
        					vars.add(var);
        				}
        			}
        		}
    		}
    	}
    }
    
    /**Methods checks if the input String is one of the original delimiters
     * If it is not one of the delimiters, then it must be a variable or an array name
     * @param s- String input
     * @return boolean
     */
    private static boolean checkToken(String s) {
    	boolean good = false;
    	if (s.startsWith("+") || s.startsWith("-") || s.startsWith("*") || s.startsWith("/")
    		|| s.startsWith("(") || s.startsWith(")") || s.startsWith("[") || s.startsWith("]")) {
    		good = false;
    	}
    	else {
    		good = true;
    	}
    	return good;
    }
    
    private static boolean isOperator(String s) {
    	boolean isOperator = false;
    	if (s.startsWith("+") || s.startsWith("-") || s.startsWith("*") || s.startsWith("/")) {
    		isOperator = true;
    	}
    	else {
    		isOperator = false;
    	}
    	return isOperator;
    }

    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    private static float eval(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	float ret = 0;
    	expr = expr.replaceAll("\\s","");
    	StringTokenizer split = new StringTokenizer(expr,delims,true);
    	String extra = expr+" ";
    	StringTokenizer duplicate =  new StringTokenizer(extra,delims,true);
    	duplicate.nextToken();
    	Stack<Float> nums = new Stack<Float>();
    	Stack<String> operators = new Stack<String>();
    	while (split.hasMoreTokens()) {
    		String token = split.nextToken();
    		String next = duplicate.nextToken();
    		try {
    			float num = Float.parseFloat(token);
    			nums.push(num);
    		}
    		catch (NumberFormatException e) {
    			if (checkToken(token)) {
    				float val = 0;
    				for (int i = 0; i < vars.size(); i++) {
    					if (vars.get(i).name.equals(token)) {
    						val = vars.get(i).value;
    					}
    				}
    				nums.push(val);
    			}
    			else {
    				//Do some order of operations in here
    				//System.out.println("Token: " + token);
    				//System.out.println("Next: " + next);
    				if (next.equals("-"))  {
    					//System.out.println("elif");
    					//System.out.println(next);
    					float num = Float.parseFloat(duplicate.nextToken());
    					split.nextToken();
    					nums.push(num*-1);
    					split.nextToken(); //split.nextToken();
    					duplicate.nextToken(); //duplicate.nextToken();
					}
    				//else {
    				while(!operators.isEmpty() && precedence(token, operators.peek())) {
    					if (nums.size() == 1 & operators.peek().equals("-")) {
    						//System.out.println("if");
    						operators.pop();
    						nums.push(nums.pop() * -1);
    					}
    					/*else if (next.equals("-"))  {
    						System.out.println("elif");
    						System.out.println(next);
    						float num = Float.parseFloat(duplicate.nextToken());
    						split.nextToken();
    						nums.push(num*-1);
    						split.nextToken(); //split.nextToken();
    						duplicate.nextToken(); //duplicate.nextToken();
    					}*/
    					else {
    						//System.out.println("else");
    						float num1 = nums.pop();
    						float num2 = nums.pop();
    	        			float newNum = 0;
    	        			String op = operators.pop();
    	        			switch (op) {
    	        			case "+": newNum = num2 + num1; break;
    	        			case "-": newNum = num2 - num1; break;
    	        			case "*": newNum = num2 * num1; break;
    	        			case "/": newNum = num2 / num1; break;
    	        			}
    	        			nums.push(newNum);
    					}
    				}
    			//}
    				if (isOperator(token)) {
    					operators.push(token);
    				}
    			}
    		}
    	}
    	if (nums.size() == 1) {
    		if (operators.size() == 1 && operators.peek().equals("-")) {
    			ret = nums.pop() * -1;
    		}
    		else {
    			ret = nums.pop();
    		}
    	}
    	else {
    		while (!operators.isEmpty()) {
    			if (nums.size() == 1 & operators.peek().equals("-")) {
					operators.pop();
					nums.push(nums.pop() * -1);
				}
    			else {
    				float num1 = nums.pop();
        			float num2 = nums.pop();
        			float newNum = 0;
        			String op = operators.pop();
        			switch (op) {
        			case "+": newNum = num2 + num1; break;
        			case "-": newNum = num2 - num1; break;
        			case "*": newNum = num2 * num1; break;
        			case "/": newNum = num2 / num1; break;
        			}
        			nums.push(newNum);
    			}
        	}
    		if (nums.peek() == -0) {
    			ret = 0;
    		}
    		else {
    			ret = nums.pop();
    		}
    	}
    	return ret;
    }
    
    private static boolean precedence(String operatorToCompare, String topOfStack) {
    	boolean precedence = true;
    	if ((topOfStack.startsWith("+") || topOfStack.startsWith("-")) &&
    			(operatorToCompare.startsWith("*") || operatorToCompare.startsWith("/"))) {
    		precedence = false;
    	}
    	return precedence;
    }
    
    private static String getArrayVal(String arrayName, String index, ArrayList<Array> arrays) {
    	String out = "";
    	for (int i = 0; i < arrays.size(); i++) {
    		if (arrays.get(i).name.equals(arrayName)) {
    			float thing = Float.parseFloat(index);
    			//System.out.println("thing: " + thing);
    			int ind = (int) thing;
    			int num = arrays.get(i).values[ind];
    			out = String.valueOf(num);
    		}
    	}
    	//System.out.println("out: " + out);
    	return out;
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	
    	//Parentheses
    	float ans = 0;
    	expr = expr.replaceAll("\\s","");
    	int index = expr.indexOf('(');
    	int end = 0;
    	int count = 0;
    	int arrayIndex = expr.indexOf('[');
    	int arrayEnd = 0;
    	int arrayCount = 0;
    	
    	if (index != -1) {
    		for (int i = index; i < expr.length(); i++) {
    			if (expr.charAt(i) == '(') {
    				count++;
    			}
    			else if (expr.charAt(i) == ')' && count == 1) {
    				end = i;
    				break;
    			}
    			else if (expr.charAt(i) == ')') {
    				count--;
    			}
    		}
    		String subWithParenth = expr.substring(index, end+1);
    		String subExpression = expr.substring(index+1,end);
        	float innerVal = evaluate(subExpression, vars, arrays);
        	String replacement = String.valueOf(innerVal);
        	//System.out.println("replacement1: " + replacement);
        	//System.out.println("Sub: " + subWithParenth);
        	String newStr = expr.replace(subWithParenth,replacement);
        	//System.out.println("new: " + newStr);
        	ans = evaluate(newStr, vars, arrays);
    	}
    	
    	//Array subscripts
    	
    	else if (arrayIndex != -1) {
    		for (int i = arrayIndex; i < expr.length(); i++) {
    			if (expr.charAt(i) == '[') {
    				arrayCount++;
    			}
    			else if (expr.charAt(i) == ']' && arrayCount == 1) {
    				arrayEnd = i;
    				break;
    			}
    			else if (expr.charAt(i) == ']') {
    				arrayCount--;
    			}
    		}
    		String subArray = expr.substring(arrayIndex+1,arrayEnd);
        	float innerVal = evaluate(subArray, vars, arrays);
        	//System.out.println("sub array: " + subArray);
        	String replacement = String.valueOf(innerVal);
        	//System.out.println("this is the mistake: " + replacement);
        	String newSubArray = "[" + subArray + "]";
        	String newReplacement = "[" + replacement + "]";
        	String arraySubscript = expr.replace(newSubArray,newReplacement);
        	StringTokenizer splitArray = new StringTokenizer(arraySubscript, delims, true);
        	String check = splitArray.nextToken();
        	String bracket = splitArray.nextToken();
        	String arrayName = "";
        	String ind = "";
        	while (splitArray.hasMoreTokens()) {
        		if (bracket.startsWith("[")) {
        			arrayName = check;
        			ind = splitArray.nextToken();
        			break;
        		}
        		check = splitArray.nextToken();
            	bracket = splitArray.nextToken();
        	}
        	String replace = getArrayVal(arrayName,ind,arrays);
        	//System.out.println("replacement2: " + replace);
        	String toReplace = arrayName + "[" + ind + "]";
        	//System.out.println("to replace: " + toReplace);
        	//System.out.println("equation: " + arraySubscript);
        	String newStr = arraySubscript.replace(toReplace, replace);
        	//System.out.println("newa: " + newStr);
        	ans = evaluate(newStr, vars, arrays);
    	}
    	else {
    		ans = eval(expr, vars, arrays);
    	}
    	return ans;
    	//Done, 3/5 @ 4:19
    }
}
