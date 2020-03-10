import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Arrays;

public class Kr {

	public Kr(String[] args) {
		String temp = "";
		String[] temps = {};
		for(int i = 0 ; i < args.length; i++) {
			switch(args[i]) {
			case "-c": 
				System.out.println("Cezar");
				for(int j = 0 ; j < args.length; j++) {
					switch(args[j]) {
					case "-e": 
						temp = crypt(readLine("plain.txt",1),Integer.parseInt(readLine("key.txt",1)));
						if(temp == null) {
							return;
						}
						save("crypto.txt",temp);
						break; 
					case "-d": 
						temp = decrypt(readLine("crypto.txt",1),Integer.parseInt(readLine("key.txt",1)));
						if(temp == null) {
							return;
						}
						save("decrypt.txt",temp);
						break;
					case "-j": 
						temps = analyze(readLine("extra.txt",1),readLine("crypto.txt",1));
						if(temps == null) {
							return;
						}
						save("key_found.txt",temps[0]);
						save("decrypt.txt",temps[1]);
						break;
					case "-k": 
						temps = analyzeAll(readLine("crypto.txt",1));
						if(temps == null) {
							return;
						}
						saveAll("decrypt.txt",temps);
						break;
					}
				}
				break;
			case "-a": 
				System.out.println("Afiniczny");
				for(int j = 0 ; j < args.length; j++) {
					switch(args[j]) {
					case "-e": 
						System.out.println("Szyfrowanie");
						temp = a_crypt(readLine("plain.txt",1),Integer.parseInt(readLine("key.txt",1)),Integer.parseInt(readLine("key.txt",2)));
						if(temp == null) {
							return;
						}
						save("crypto.txt",temp);
						break;
					case "-d": 
						System.out.println("Odszyfrowanie");
						temp = a_decrypt(readLine("crypto.txt",1),Integer.parseInt(readLine("key.txt",1)),Integer.parseInt(readLine("key.txt",2)));
						if(temp == null) {
							return;
						}
						save("decrypt.txt",temp);
						break;
					case "-j": 
						temps = a_analyze(readLine("extra.txt",1),readLine("crypto.txt",1));
						if(temps == null) {
							return;
						}
						save("decrypt.txt",temps[2]);
						saveAll("key_found.txt", Arrays.copyOfRange(temps, 0, 2));
						break;
					case "-k": 
						temps = a_analyzeAll(readLine("crypto.txt",1));
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
	private String[] a_analyzeAll(String text) {
		Integer[] primes = {1,3,5,7,9,11,15,17,19,21,23,25};
		int index = 0;
		String[] result = new String[312];
		for(int i = 0 ; i < 26; i++) {
			for(Integer prime : primes)
				result[index++] = a_decrypt(text,prime,i);
		}
		return result;
	}
	private String[] a_analyze(String extra, String text) {
		Integer[] primes = {1,3,5,7,9,11,15,17,19,21,23,25};
		Integer key_a = null, key_b = null;
		int index = 0;
		String result = "";
		
		for(int i = 0 ; i < 26; i++) {
			for(Integer prime : primes) {
				String decrypted = a_decrypt(text,prime,i);
				if(decrypted.contains(extra)) {
					key_b = i;
					key_a = prime; 
					result = decrypted;
					break;
				}
				index++;
			}
		}
		if(key_a != null) {
			return new String[]{key_a+"",key_b+"",result};
		} else {
			System.out.println("ERROR: Nie da sie znalesc klucza!");
			return null;
		}
	} 
	private String a_decrypt(String text, int a, int b) {
		char[] result = new char[text.length()];
		for(int i = 0 ; i < text.length(); i++) {
			//Polskie znaki przepisuje
			if((int)text.charAt(i) > 260) {
				result[i] = text.charAt(i);
			}
			else if(Character.isLowerCase(text.charAt(i))) {
				System.out.println(text.charAt(i)- 'a'-b);
				result[i] = (char) (text.charAt(i) - 'a' - b);
				System.out.println((int)result[i] +"Lmao");
				while(result[i] % a != 0 || (int)result[i] < 0) {
					System.out.print("+26");
					result[i] = (char) (int)(result[i]+26);
				}
				result[i] = (char) ('a' + (result[i]/a)%26);
			} 
			else if(Character.isUpperCase(text.charAt(i))) {
				result[i] = (char) (text.charAt(i) - 'A' - b);
				while(result[i] % a != 0) {
					result[i] = (char) (result[i]+26);
				}
				result[i] = (char) ('A' + (result[i]/a)%26);
			}
			else
				result[i] = text.charAt(i);
		}
		return new String(result);
	}
	String a_crypt(String text, int a, int b) {
		System.out.println("A: "+a+" B: "+b);
	    int gcd =  BigInteger.valueOf(a).gcd(BigInteger.valueOf(26)).intValue();

		if(gcd != 1) {
			System.out.println("ERROR: Niepoprawny format klucza");
			return null;
		}
		char[] result = new char[text.length()];
		for(int i = 0 ; i < text.length(); i++) {
			//Polski znak
			if((int)text.charAt(i)>260) {
				result[i] = text.charAt(i);
			}
			else if(Character.isLowerCase(text.charAt(i))) {
				result[i] = (char)('a'+((b+a*((int)text.charAt(i)-'a')) % 26));
			}
			else if(Character.isUpperCase(text.charAt(i)))
				result[i] = (char)('A'+((b+a*((int)text.charAt(i)-'A')) % 26));
			else
				result[i] = text.charAt(i);
			System.out.println(result[i]+" "+text.charAt(i));
		}
		return new String(result);
	}
	
	String crypt(String text, int key) {
		if(key > 25 || key < 1) {
			System.out.println("ERROR: Klucz nie jest z przedzialu (1-25)");
			return null;
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
		return new String(result);

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
	

	private String[] analyze(String extra, String text) {
		Integer key = null;
		String result = "";
		
		for(int i = 1 ; i < 26; i++) {
			String decrypted = decrypt(text,i);
			if(decrypted.contains(extra)) {
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
				if(a != null)
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
		new Kr(args);
	}
}
