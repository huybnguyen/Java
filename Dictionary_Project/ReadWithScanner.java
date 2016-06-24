import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;

public class ReadWithScanner {
	private final Path file_path;
	public TreeMap<String,String> dictionary = new TreeMap<String,String>();
	private ArrayList<String> temp = new ArrayList<String>();
	private String word = "";
	private int count = 0;
	private ArrayList<ArrayList<String>> defn = new ArrayList<ArrayList<String>>();
	public ReadWithScanner(String Filename){
		file_path = Paths.get(Filename);
	}

	public final void processLineByLine() throws IOException {
		try(Scanner scanner = new Scanner(file_path)){
			while(scanner.hasNextLine()){
				processLine(scanner.nextLine());
			}
		}
	}

	public static boolean testAllUpperCase(String str){
		for(int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			if(c >= 97 && c <= 122) {
				return false;
			}
		}
		return true;
	}

	public boolean letter(String name) {
    	return name.matches("[a-zA-Z]+");
	}

	protected void processLine(String line){
		for(String phrase: line.split("Defn: ")){
			if(letter(phrase)){
				if(testAllUpperCase(phrase) == true){
					word = phrase;
					//if(count > 0){
						//log(temp + "\n\n\n");
					String listString = "";

					for (String s : temp)
					{
    					listString += s;
					}
					dictionary.put(word,listString);
					temp.clear();
				}
				else{
					temp.add(phrase);
				}
			}
			else{
				temp.add(phrase);
			}
		}
		//log(temp + "\n\n");
		
	}

	private static void log(Object object){
		System.out.println(String.valueOf(object));
	}
}