import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Rule{

	String name;
	List<String> headK;
	List<String> headR;
	String function;
	String condition;
	List<String> body;
	int priority;

	public Rule(String name, String[] headK, String[] headR, String function,
				 String condition, String[] body, int priority) {
		this.name = name;
		this.headK = Arrays.asList(headK);
		if(headR != null && headR.length > 0 ) {
			this.headR = Arrays.asList(headR);
		} else {
			this.headR = null;
		}
		this.function = function;
		this.condition = condition;
		this.body = Arrays.asList(body);
		this.priority = priority;
	}
	
	public List<String> toChrIdAssign() {
		List<String> lines = new ArrayList<String>();
		
		HashSet<String> allTerms = new HashSet<String>();
		allTerms.addAll(headK);
		if(headR != null) {
			allTerms.addAll(headR);
		}
		
		allTerms.addAll(body);
		
		for(String term : allTerms) {
			lines.add("id(I), " + term + " <=> " + term + "(I), I1 is I+1, id(I1)."); 
		}
		
		return lines;
	}
	
	public String toChrMatch() {

		int numOfPredicates = headK.size();
		String line = name + " @ start, ";
		line += lineWithAppendIncrementally(headK, "(ID", ")", 1);
		if(headR != null) {
			line += lineWithAppendIncrementally(headK, "(ID", ")", headK.size());
			numOfPredicates += headR.size();
		}
		
//		Map<String, String> valuesMap = new HashMap<String, String>();
//		valuesMap.put("name", "quick brown fox");
//		valuesMap.put("head_predicates_with_ids", "lazy dog");
//		StringSubstitutor sub = new StringSubstitutor(valuesMap);
//		String matchingChrTemplate = "${name} @  start, ${head_predicates_with_ids}"
//									+ " ==> match(${name},[${ids_sequence}],${priority}).";
//		String resolvedString = sub.replace(matchingChrTemplate);
	 
		
		line += " ";
		line += "==> ";
		if(condition != null) {
			line += condition + "| ";
		}
		
		String usedIDsList = generateLineIncrementally("ID", "", 1, numOfPredicates);
		line += "match(" + name + ", [" + usedIDsList + "]," + priority + ").";
		
		return line;
	}
	
	public String toChrFire() {
		
//		String firingChrTemplate = "${name} @  ${head_predicates_with_ids} \\ history(L), fire, match(${name},IDs,_)"
//				+ " <=> ${member_check_id_ids} | "
//				+ "print('fired $(name)'), nl, ${body_predicates_with_ids}, history([(r1,[${ids_sequence}])|L]), start.";

		int numOfPredicates = headK.size();
		String line = name + " @ ";
		line += lineWithAppendIncrementally(headK, "(ID", ")", 1);
		switch(function) {
			case "==>":
				line += " \\\\ " + "history(L), fire, match(" + name + ",IDs,_)";
				break;
			default:
				if(headR != null && headR.size() > 0) {
					line += " \\\\ " + lineWithAppendIncrementally(headR, "(ID", ")", numOfPredicates + 1) +
							"history(L),fire,match(" + name + ",IDs,_)";
					numOfPredicates += headR.size();
				} else {
					line += ", history(L), fire, match(" + name + ", IDs, _)";
				}
				break;
		}
		
		line += " <=> ";
		String memberAssuranceStat = generateLineIncrementally("member(ID", ", IDs)", 1, numOfPredicates);
		line += memberAssuranceStat;
		line += " | print(\"fired " + name + "\"), nl, ";
		line += lineComma(body) + ", ";
		String usedIDsList = generateLineIncrementally("ID", "", 1, numOfPredicates);
		line += "history([(" + name + ", " + "[" + usedIDsList + "])|L]), start.";
		
		return line;
	}
	
	//	HELPERS
	public static String generateLineIncrementally(String substring1, String substring2, int beginID, int endID) {
		String output = "";
		
		for(int i = beginID; i<= endID; i++) {
			output += substring1 + i + substring2 + ",";
		}
		
		return output.substring(0, output.length() - 1);
		
	}
	
	public static String lineComma(List<String> someList) {
		return lineWithAppend(someList, "");
	}
	
	public static String lineWithAppend(List<String> someList, String appendance) {
		String output = "";
		for(int i = 0; i < someList.size(); i++) {
			output += someList.get(i) + appendance + ",";
		}
		
		return output.substring(0, output.length() - 1);
	}
	
	public static String lineWithAppendIncrementally(List<String> someList,
					String appendance1, String appendance2, int incrementOffset) {
		String output = "";
		String currentAppendance;
		for(int i = 0; i < someList.size(); i++) {
			currentAppendance = appendance1 + (i+incrementOffset) + appendance2;
			output += someList.get(i) + currentAppendance + ",";
		}
		
		return output.substring(0, output.length() - 1);
	}
	
	public static List<String> getListWhileAppend(List<String> someList, String appendance) {
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < someList.size(); i++) {
			output.add(someList.get(i) + appendance);
		}
		
		return output;
	}
}