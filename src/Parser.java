import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	LinkedList<Rule> rules = new LinkedList<Rule>();

	public void createCHR(String outputFilepath) throws IOException {
		
		HashSet<String> chrDefinitions = new HashSet<String>();
		ArrayList<String> matchingCHR = new ArrayList<String>(); 
		HashSet<String> idAssignCHR = new HashSet<String>(); 
		ArrayList<String> firingCHR = new ArrayList<String>(); 
		
		System.out.println("..Converting to CHR..");

		for(Rule rule: rules) {
			System.out.println("Rule" + rule.name + "..");
			idAssignCHR.addAll(rule.toChrIdAssign());
			matchingCHR.add(rule.toChrMatch());
			firingCHR.add(rule.toChrFire());
			
			chrDefinitions.addAll(Rule.getListWhileAppend(rule.headK, "/0"));
			chrDefinitions.addAll(Rule.getListWhileAppend(rule.headK, "/1"));
			
			if(rule.headR != null) {
				chrDefinitions.addAll(Rule.getListWhileAppend(rule.headR, "/0"));
				chrDefinitions.addAll(Rule.getListWhileAppend(rule.headR, "/1"));
			}
			
			chrDefinitions.addAll(Rule.getListWhileAppend(rule.body, "/0"));
			chrDefinitions.addAll(Rule.getListWhileAppend(rule.body, "/1"));
		}
		
		
		System.out.println("..Writing CHR..");
		
		File file = new File(outputFilepath);
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(file));
		
		bw.write(":- use_module(library(chr)).\r\n" + 
				 ":- chr_constraint ");
		
		for(String definition : chrDefinitions) {
			bw.write(definition + ", ");
		}
		bw.newLine();
		
		bw.write("	start/0, conflictdone/0, fire/0,\r\n" + 
				"	history/1,\r\n" + 
				"	match/3,\r\n" + 
				"	id/1,\r\n" + 
				"	getTrueOrFalse/0.\n\n");		
		
		for(String chr : idAssignCHR) {
			bw.write(chr);
			bw.newLine();
		}
		bw.newLine();

		
		for(String chr : matchingCHR) {
			bw.write(chr);
			bw.newLine();
		}
		bw.newLine();

		
		bw.write("start <=> conflictdone.\r\n" + 
				"\r\n" + 
				"history(L) \\ match(R,IDs,_) <=> sort(IDs, IDsSorted), member((R,IDsSorted),L) | true.\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"conflictdone, match(_,_,O1) \\ match(_,_,O2) <=> O1<O2 | true.\r\n" + 
				"conflictdone, match(_,_,O1) \\ match(_,_,O2) <=> O1=O2, getTrueOrFalse | true.\r\n" + 
				"getTrueOrFalse <=> random(X), X > 0.5.\r\n" +
				"\r\n" + 
				"\r\n" + 
				"conflictdone <=> fire.\n\n");
		
		for(String chr : firingCHR) {
			bw.write(chr);
			bw.newLine();
		}
		bw.newLine();

		
		bw.write("fire<=>true.");
		
		bw.close();
	}
	
	public void parse (String inputFilepath) throws NumberFormatException, IOException {
		System.out.println("..Parsing..");

		
		// TODO consider predicates
		final Pattern rulePattern = Pattern.compile("^([A-Za-z,()]+) ([<|=]=>) ([\\S]+ \\| )*([A-Za-z,()]+) priority (\\d)$");
		final Pattern multiHeadedPattern = Pattern.compile("^([A-Za-z,()]+) \\\\ ([A-Za-z,()]+)$");
				
		File file = new File(inputFilepath);

		BufferedReader br;
		br = new BufferedReader(new FileReader(file));
		int numOfRule = 1;
		String name;
		String [] headK;
		String [] headR;
		String function;
		String condition;
		String [] body;
		int priority;
		
		Matcher m; String line;
		String headSide; String bodySide;
		while ((line = br.readLine()) != null) {
			
			m = rulePattern.matcher(line);
		    if (!m.find()) {
		    	System.out.println("Syntax Error!");
				break;
		    }
		    
	        headSide = m.group(1);
	        function = m.group(2);
	        condition = m.group(3);
	        bodySide = m.group(4);
	        priority = Integer.parseInt(m.group(5));
	        
//			System.out.println("headSide = " + headSide);
//			System.out.println("bodySide = " + bodySide);
			
			m = multiHeadedPattern.matcher(headSide);
			if(m.find()) {
				// It should be simpagation
				headK = m.group(1).split(",");
				headR = m.group(2).split(",");
			} else {
				// It should be simplification or propagation
				headK = headSide.split(",");
				headR = null;
			}
			
			body = bodySide.split(",");
//			print(headK); print(headR); print(body);
//			System.out.println("Function = " + function);
//			System.out.println("Condition = " + condition);

			name = "r" + numOfRule++;
			rules.add(new Rule(name, headK, headR, function, condition, body, priority));
		}
		
		br.close();
	}
	
	public static void print(String [] aList) {
		if(aList == null) {
			return;
		}
			
		for(int i = 0; i< aList.length; i++) {
			System.out.print(aList[i] + ", ");
		}
		System.out.println();
	}
	
	public static void print(String string) {
		if(string == null) {
			return;
		}
		System.out.println(string);
	}
}
