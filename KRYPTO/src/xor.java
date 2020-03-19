import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class xor {

	public xor(String[] args) {
		String[] temps = {};
		for(int i = 0 ; i < args.length; i++) {
			switch(args[i]) {
			case "-c": 
				System.out.println("Cezar");
				for(int j = 0 ; j < args.length; j++) {
					switch(args[j]) {
					case "-p": 
						temps = prepare(readLines("key.txt").length());
						if(temps == null) {
							return;
						}
						saveAll("plain.txt",temps);
						break; 
					case "-e": 
						temps = encrypt(readLines("key.txt"),readLinesArray("plain.txt"));
						if(temps == null) {
							return;
						}
						saveAll("crypto.txt",temps);
						break; 
					case "-k": 
						temps = analyze(readLinesArray("crypto.txt"));
						if(temps == null) {
							return;
						}
						saveAll("decrypt.txt",temps);
						break; 
					}
				}
				break;
			}
		}
	}

	public xor() {
	}

	@SuppressWarnings("null")
	private String[] prepare(int length) {
		ArrayList<String> prepared_text = new ArrayList<String>();
		String text;
		//Wczytujemy orig na Stringa
		text = readLines("orig.txt");
		//Dzielimy tego stringa
		for(int i = 0; i < (text.length()/length); i++) {
			prepared_text.add(text.substring((i)*length, (i+1)*length));  
		}  
		prepared_text.add(text.substring(text.length()-text.length()%length));

		System.out.println(prepared_text);

		return prepared_text.stream().toArray(String[]::new);
	}

	String[] analyze(String[] text){
		//XOR Litery i spacji ma 7 znakow, a XOR litery i litery ma <=5
		//Dlatego przy decrypt wyniki gdzie wiemy gdzie jest spacja XORUJEMY ze spacja

		String[] result = new String[text.length];
		String[] key_found = new String[text[0].length()];

		//Szukamy spacji!
		//Iteracja przez slowa
		for(int i = 0 ; i < text.length; i ++) {
			String part_text = text[i];
			//Iteracja przez litery
			for(int k = 0 ; k < part_text.length(); k ++) {
				if(Integer.toBinaryString(-32+text[i].charAt(k)).length() == 7 && key_found[k] == null) {
					//Spacja
					key_found[k] = ""+ (char) ((text[i].charAt(k)-32)^(' '));
				}
			}
			result[i] = text[i];
		}
		//Stosujemy decrypt

		for(int i = 0 ; i < text.length; i ++) {
			String part_text = text[i];
			char[] temp = result[i].toCharArray();
			//Iteracja przez litery
			for(int k = 0 ; k < part_text.length(); k ++) {
				if(key_found[k] != null) {
					temp[k] = (char) ((text[i].charAt(k)-32)^(key_found[k].charAt(0)));
				}
			}
			result[i] = new String(temp);
		}
		
		System.out.println("FOUND KEY");
		for(String key : key_found) {
			if(key == null)
				System.out.print("[] ");
			else
				System.out.print(key+" ");
		}
		
		return result;
	}
	String[] encrypt(String key, String[] text) {
		String[] result = new String[text.length];
		String temp = "";

		//Iteracja przez slowa
		for(int i = 0 ; i < text.length; i ++) {
			//Iteracja przez litery
			for(int k = 0 ; k < key.length(); k ++) {
				//100000 to spacja
				if(text[i].length() == k)
					break;

				//Print znaku klucza i tekstu w binarnym
				System.out.println(
						Integer.toBinaryString(( (int) key.toCharArray()[k]))
						+" "+
						Integer.toBinaryString(( (int) text[i].toCharArray()[k]))
						);
				//Print znaku tekstu w char oraz XOR binarnego znaku klucza i tekstu.
				System.out.println(
						text[i].toCharArray()[k] + " : " +
								Integer.toBinaryString(
										( (int) key.toCharArray()[k] ) ^ ( (int) text[i].toCharArray()[k] )
										));

				System.out.println(
						"CHAR: "+(char)(32+
								(( (int) key.toCharArray()[k]) ^ ( (int) text[i].toCharArray()[k]))
								)
						);
				temp += (char)(32+(( (int) key.toCharArray()[k]) ^ ( (int) text[i].toCharArray()[k])));
			}
			result[i] = temp;
			temp = "";
		}

		return result;
	}



	private void saveAll(String filename, String[] result) {
		OutputStream os;
		new File(filename).delete();
		try {
			os = new FileOutputStream(new File(filename), true);
			PrintStream stream = new PrintStream(os, true, "UTF-8");
			for(String a : result) {
				if(a != null)
					stream.println(a);
			}
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private String readLines(String filename) {
		BufferedReader Buff;
		try {
			String text = "";
			String temp;
			Buff = new BufferedReader(new FileReader(filename));
			while((temp=Buff.readLine())!=null && temp.length()!=0) {
				//for(int i = 0 ; i < line_number; i++) {
				text += temp;
			}
			Buff.close();
			return text;
		} catch (FileNotFoundException e) {
			System.out.println("NIE ZNALEZIONO PLIKU "+filename);
			System.exit(1);
		} catch (IOException e) {
		} catch (Exception e) {
			System.out.println("Pusta linia");
			System.exit(1);
		}
		return null;
	}

	@SuppressWarnings("resource")
	private String[] readLinesArray(String filename) {
		BufferedReader Buff;
		try {
			ArrayList<String> text = new ArrayList<String>();
			String temp;
			Buff = new BufferedReader(new FileReader(filename));
			while((temp=Buff.readLine())!=null && temp.length()!=0) {
				//for(int i = 0 ; i < line_number; i++) {
				text.add(temp);
			}
			Buff.close();
			return text.stream().toArray(String[]::new);
		} catch (FileNotFoundException e) {
			System.out.println("NIE ZNALEZIONO PLIKU "+filename);
			System.exit(1);
		} catch (IOException e) {
		} catch (Exception e) {
			System.out.println("Pusta linia");
			System.exit(1);
		}
		return null;
	}


	public static void main(String[] args) {
		xor krypto = new xor();
		krypto.saveAll("plain.txt", krypto.prepare(5));
		String[] a = krypto.encrypt("osiem", krypto.readLinesArray("plain.txt"));
		krypto.saveAll("crypto.txt", a);
		krypto.saveAll("decrypt.txt",krypto.analyze(a));

	}

}
