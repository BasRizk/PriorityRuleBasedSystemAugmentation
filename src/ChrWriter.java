import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class ChrWriter {

	LinkedList<Rule> rules;
	String outputFilepath;

	private HashSet<String> chrDefinitions = new HashSet<String>();
	private ArrayList<String> matchingCHR = new ArrayList<String>(); 
	private HashSet<String> idAssignCHR = new HashSet<String>(); 
	private ArrayList<String> firingCHR = new ArrayList<String>(); 
	
	public ChrWriter(LinkedList<Rule> rules, String outputFilepath) {
		this.rules = rules;
		this.outputFilepath = outputFilepath;
	}
	
	public void write() throws IOException  {
		convertToCHR();
		writeChrFile();
	}
	
	private void convertToCHR() {
		
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
		
	}
	
	private void writeChrFile() throws IOException {
		
		System.out.println("..Writing CHR File..");
		
		File file = new File(outputFilepath);
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(file));
		
		bw.write("% SYSTEM RULES:\n");
		for(Rule rule : rules) {
			bw.write(rule.getFullStatementChrComment());
			bw.newLine();
		}
		bw.write("% \n");
		
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
}
