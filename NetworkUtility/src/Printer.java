
public class Printer {
	public static void println(Class<?> tag, boolean error, String content){
		String identifier  = tag.getName() + ": ";
		String message = String.format("%-20s", identifier) + content;
		if(error)	System.out.println("[ERROR] " + message);
		else 		System.out.println("[DEBUG] " + message);
	}
}
