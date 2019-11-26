import java.io.IOException;

public class Main {

	public static void main(String[]args) {
		String rulesFilepath = "CHR_RULES.rules";
		String chrOutputFilepath = "RB_System.pl";
		Parser parser = new Parser();
		ChrWriter writer;
		try {
			parser.parse(rulesFilepath);
			System.out.println(rulesFilepath);
			writer = new ChrWriter(parser.rules, chrOutputFilepath);
			writer.write();
			System.out.println(chrOutputFilepath);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
