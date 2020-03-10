import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Kr {

	public Kr(String[] args) {
		String temp = "";
		String[] temps = {};
		for(int i = 0 ; i < args.length; i++) {
			switch(args[i]) {
			case "-c": 
				for(int j = 0 ; j < args.length; j++) {
					switch(args[j]) {
					case "-e": 
						crypt(readLine("plain.txt",1),Integer.parseInt(readLine("key.txt",1)));
						save("crypt.txt",temp);
						break;
					case "-d": 
						temp = decrypt(readLine("crypto.txt",1),Integer.parseInt(readLine("key.txt",1)));
						save("decrypt.txt",temp);
						break;
					case "-j": 
						temps = analyze(readLine("extra.txt",1),readLine("crypto.txt",1));
						save("key_found.txt",temps[0]);
						save("decrypt.txt",temps[1]);
						break;
					case "-k": 
						temps = analyzeAll(readLine("crypto.txt",1));
						saveAll("decrypt.txt",temps);
						break;
					}
				}
				break;
			case "-a": 
				for(int j = 0 ; j < args.length; j++) {
					switch(args[j]) {
					case "-e": 
						a_crypt(readLine("plain.txt",1),Integer.parseInt(readLine("key.txt",1)),Integer.parseInt(readLine("key.txt",2)));
						save("crypt.txt",temp);
						break;
					case "-d": 
						temp = a_decrypt(readLine("crypto.txt",1),Integer.parseInt(readLine("key.txt",1)),Integer.parseInt(readLine("key.txt",2)));
						save("decrypt.txt",temp);
						break;
					case "-j": 
						temps = a_analyze(readLine("extra.txt",1),readLine("crypto.txt",1));
						save("key_found.txt",temps[0]);
						save("decrypt.txt",temps[1]);
						break;
					case "-k": 
						temps = a_analyzeAll(readLine("crypto.txt",1));
						saveAll("decrypt.txt",temps);
						break;
					}
				}
				break;
			}
		}

	}
	private String[] a_analyzeAll(String string) {
		return null;
	}
	private String[] a_analyze(String string, String string2) {
		return null;
	}
	private String a_decrypt(String string, int i, int j) {
		return null;
	}
	private void a_crypt(String string, int i, int j) {
		
	}
	void crypt(String text, int key) {
		if(key > 25 || key < 1) {
			System.out.println("ERROR: Klucz nie jest z przedzialu (1-25)");
		}
		char[] result = new char[text.length()];
		for(int i = 0 ; i < text.length(); i++) {
			//Polski znak
			if((int)text.charAt(i)>260) {
				result[i] = text.charAt(i);
			}
			else if(Character.isLowerCase(text.charAt(i))) {
				result[i] = (char)('a'+((key+(int)text.charAt(i)-'a') % 26));
			}
			else if(Character.isUpperCase(text.charAt(i)))
				result[i] = (char)('A'+((key+(int)text.charAt(i)-'A') % 26));
			else
				result[i] = text.charAt(i);
			System.out.println(result[i]+" "+text.charAt(i));
		}

	}
	String decrypt(String text, int key) {
		char[] result = new char[text.length()];
		for(int i = 0 ; i < text.length(); i++) {
			//Polskie znaki przepisuje
			if((int)text.charAt(i) > 260) {
				result[i] = text.charAt(i);
			}
			else if(Character.isLowerCase(text.charAt(i))) {
				result[i] = (char)('a'+((26-key+(int)text.charAt(i)-'a') % 26));
			}
			else if(Character.isUpperCase(text.charAt(i)))
				result[i] = (char)('A'+((26-key+(int)text.charAt(i)-'A') % 26));
			else
				result[i] = text.charAt(i);
		}
		return new String(result);
	}
	
	private String[] analyzeAll(String text) {
		String[] result = new String[25];
		for(int i = 1 ; i < 26; i++) {
			result[i-1] = decrypt(text,i);
		}
		return result;
	}
	

	private String[] analyze(String temp, String text) {
		Integer key = null;
		String result = "";
		
		for(int i = 1 ; i < 26; i++) {
			String decrypted = decrypt(text,i);
			if(temp.contentEquals(decrypted.substring(0,temp.length()))) {
				key = i;
				result = decrypted;
				break;
			}
		}
		if(key != null) {
			return new String[]{key+"",result};
		} else {
			System.out.println("ERROR: Nie da sie znalesc klucza!");
			return null;
		}
	}
	
	private void save(String filename, String result) {
		OutputStream os;
		new File(filename).delete();
		try {
			os = new FileOutputStream(new File(filename), true);
			PrintStream stream = new PrintStream(os, true, "UTF-8");
			stream.println(result);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void saveAll(String filename, String[] result) {
		OutputStream os;
		new File(filename).delete();
		try {
			os = new FileOutputStream(new File(filename), true);
			PrintStream stream = new PrintStream(os, true, "UTF-8");
			for(String a : result) {
				stream.println(a);
			}
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private String readLine(String filename, int line_number) {
		BufferedReader Buff;
		try {
			String text = "";
			Buff = new BufferedReader(new FileReader(filename));
			for(int i = 0 ; i < line_number; i++) {
				text = Buff.readLine();
				if(text == null)
					throw new Exception();
			}
			Buff.close();
	        return text;
		} catch (FileNotFoundException e) {
			System.out.println("NIE ZNALEZIONO PLIKU "+filename);
			System.exit(1);
		} catch (IOException e) {
		} catch (Exception e) {
			System.out.println("PLIK "+filename+" JEST PUSTY");
			System.exit(1);
		}
		return null;
	}

	public static void main(String[] args) {
		Kr kr = new Kr(args);
	}
}
