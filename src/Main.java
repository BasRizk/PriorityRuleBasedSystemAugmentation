import java.io.IOException;

public class Main {

	public static void main(String[]args) {
		String rulesFilepath = "CHR_RULES.rules";
		String chrOutputFilepath = "RB_System.pl";
		Parser system = new Parser();
		try {
			system.parse(rulesFilepath);
			System.out.println(rulesFilepath);
			system.createCHR(chrOutputFilepath);
			System.out.println(chrOutputFilepath);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
