import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	LinkedList<Rule> rules = new LinkedList<Rule>();

	
	private final Pattern rulePattern = Pattern.compile("^([A-Za-z,\\\\ ]+) ([<|=]=>) (([\\S]+) \\| )*([A-Za-z,()]+) priority (\\d)$");
	private final Pattern multiHeadedPattern = Pattern.compile("^([A-Za-z,()]+) \\\\ ([A-Za-z,()]+)$");		
	private static final String END_OF_FILE = "EOP";
	
	public void parse (String inputFilepath) throws NumberFormatException, IOException {
		System.out.println("..Parsing..");

		File file = new File(inputFilepath);

		BufferedReader br;
		br = new BufferedReader(new FileReader(file));
		int numOfRule = 1;
		String fullStatement;
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
		    	if(!line.contains(END_OF_FILE)) {
		    		System.out.println("WARNING :: Syntax Error!");
		    	}
				break;
		    }
		    fullStatement = m.group(0);
		    headSide = m.group(1);
	        function = m.group(2);
	        condition = m.group(4);
	        bodySide = m.group(5);
	        priority = Integer.parseInt(m.group(6));
	        
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

			name = "r" + numOfRule++;
			rules.add(new Rule(fullStatement, name, headK, headR,
							   function, condition, body, priority));
		}
		
		br.close();
	}
	
	private static void print(String [] aList) {
		if(aList == null) {
			return;
		}
			
		for(int i = 0; i< aList.length; i++) {
			System.out.print(aList[i] + ", ");
		}
		System.out.println();
	}
	
	private static void print(String string) {
		if(string == null) {
			return;
		}
		System.out.println(string);
	}
}
