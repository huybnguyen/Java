import javax.swing.*;
import java.util.*;
import java.io.IOException;

public class Dictionary{
	public static void main(String[] args) throws IOException{
		ReadWithScanner parser = new ReadWithScanner("/Users/HuyNguyen/Desktop/english/dict.txt");
		parser.processLineByLine();
		while(true){

			/*System.out.print("Please enter a word to look up: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String user_input = br.readLine();*/
			String user_input = JOptionPane.showInputDialog("Please enter a word to look up:");
			if(user_input.equals("exit")){
				break;
			}
			if(parser.dictionary.containsKey(user_input.toUpperCase())) {
        		String current = parser.dictionary.get(user_input.toUpperCase());
             	//System.out.println(current);
             	JOptionPane.showMessageDialog( null, current, "Definition", JOptionPane.INFORMATION_MESSAGE);
       	}
        	else{
             	//System.out.println("The word you entered does not exist!");
             	JOptionPane.showMessageDialog( null, "The word you entered does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
        	}
		}
		//System.out.println("Bye Bye!");
	}
}