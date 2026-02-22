package lib3001.crypt;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



/********************************************************************************************************************************************
*	Version 1.1                            				 Autor: Mr. Maxwell                 				      	vom 06.09.2023    		*
*	LIB3001 Bibliotheks Klasse																												*
*	TOTP: Time-Based One-Time Password RFC6238																								*
*	wird meist als 2FA Authentifikator verwendet. Als Referenz kann z.B. WinAuth.exe betrachtet werden										*
*																																			*
*	Diese statische Klasse ist rein mathematisch und vollkommen unabhängig von anderen Klassen oder Bibliotheken.							* 
*	Sie ist allgemein und nicht fest für ein Programm bestimmt. Sie kann als Bibliothek-Klasse verwendet werden.							*
*																																			*
*	Es wird in der einzigen Hauptmehtode ein Einmalpasswort TOPT generiert.																	*
*	Dafür wird ein Secret-Key im im Base32 Format übergeben.																				*
*	Ein weiterer Parameter ist die Systemzeit die intern verwendet wird.																	*
*	Die Systemzeit muss daher korrekt eingestellt sein, da sonnst die Ausgabe falsch ist!													*
*	Google nach: TOTP, RFC6238																												*
*	Links:		https://www.ionos.de/digitalguide/server/sicherheit/totp/																	*
*				https://www.dcode.fr/base-32-encoding																						*
*				https://www.verifyr.com/en/otp/check#totp																					*
*																																			*
*	Bemerkung:																																*
*	Dieses TOTP Verfahren wird derzeit als ein Standard für die 2FA verwendet und von diversen Webseiten z.B. Github per Zwang verlangt!	*
*	Daher habe ich es hier implementiert, um den Standard zu folgen. 																		*
*	Ich halte diese Arte der 2FA aber für unsicher, da 																						*
*	1. durch Ausschluss des TimeServers  unangenehme User ausgeschlossen werden könnten,													*
*	2. der Secret-Key in Klartext auf dem Client und dem Webserver vorliegen muss und so abgreifbar ist.									*
*	3. der Secret-Key kann möglicherweise Brute-Forces werden.																				*
*	4. das Verfahren grundsätzlich viel zu Schwach ist um diversen Angegriffen stand zu halten.												*	
*	Achtung verwenden sie niemals den TOTP-Secret-Key als Geheimen Schlüssel für irgend etwas anderes!										*
*	Er liegt bei Verwendung in Klartext auf ihrem Client und dem Web-Server vor und ist daher im Zweifel Öffentlich!						*
********************************************************************************************************************************************/



public class TOTP 
{
	

		
/**	Einzige Hauptmethode die den TOTP Einmalcode erstellt. Achtung die Systemzeit muss korrekt Synchronisiert sein, da sie ein Parameter ist!
	@param secretBase32 Der einzugebende Geheim-Schlüssel muss korrekt im Base32 Format vorliegen. 
	Also durch 8 Teilbar sein und nur die erlaubten Zeichen enthalten: "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=". Groß/Kleinschreibung ist hier egal.
	Wenn der Key zu kurz ist, kann einfach mit dem "=" Zeichen hinten aufgefüllt werden, bis die Länge durch 8 teilbar ist!
	Bei der Berechnung werden alle "=" Zeichen eh ignoriert, so das es das Ergebnis nicht beeinflusst! Der Standard schreibt es halt vor.	
	@param len Die Ausgebelänge, meistens 6, kann von 2 bis 10 eingestellt werden.
	Durch die Verkürzung der Ausgabelänge werden nur die vorderen Ziffern abgeschnitten. Der berechnete Ausgabewert (intern Integer) wird dadurch nicht verändert.
	@return Gibt den Einmalcode als String zurück. Der Ausgabewert ist intern ein Integer und wird dann vorne gekürzt und zu String konvertiert.     */
	public static String getTOTP(String secretBase32, int len) throws Exception
	{
		byte[] key = fromBase32(secretBase32);	
		byte[] b = getHMAC_SHA128(getTimeData(), key);
		return truncation(b,len);
	}
	
	
	
	/** Prüft ob der übergebene String im korrektem Base32 Format vorliegt.
	@return gibt "true" zurück wenn Ja, wenn Nein wird eine IOException ausgelöst. 
	Diese Methode kann genutzt werden um schon während der Eingabe die Gültigkeit zu prüfen.**/
	public static boolean checkBase32Input(String base32str) throws IOException
	{
		String s = base32str.toUpperCase();
		if(s.matches("[ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=]*")==false) 	throw new IOException("No Base 32 format! Only allowed characters: ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=");
		if(s.length()%8!=0) 											throw new IOException("No Base 32 format! Length must be divisible by 8!");
		return true;
	}

	
	
	
	
// ------------------------------------------------------------ Private Methoden -------------------------------------------------	
	
	// gibt den Time-Datensatz der Systemzeit zurück, der 30sec. lang gilt.
	private static byte[] getTimeData()
	{
		long t = System.currentTimeMillis()/30000;
		return Convert.long_To_8_ByteArray(t);
	}
	
	
	//	Gibt den SHA1 Hash als byteArray zurück  
	private static byte[] sha128(byte[] in) throws Exception 
	{ 
		MessageDigest crypt = MessageDigest.getInstance("SHA-1");
		crypt.update(in);
		return crypt.digest();	
	}	
	
	
	//	HMAC Funktion die SHA256 verwendet 
	private static byte[] getHMAC_SHA128(byte[] data, byte[] key) throws Exception
	{	
		byte[] k;
		if(key.length>64) k = Arrays.copyOf(sha128(key),64);
		else k = Arrays.copyOf(key, 64);	
		byte[] opad = new byte[64];
		byte[] ipad = new byte[64];
		for(int i=0; i<64;i++) opad[i]=0x5c;
		for(int i=0; i<64;i++) ipad[i]=0x36;
		byte[] b = sha128(add(xor(k,ipad), data));
		byte[] out = sha128(add(xor(k,opad), b));	
		return out;	
	}
	
	
	// Kürzt den Hash auf die angegebenen Dezimalstellen die dann ausgegeben werden.
	//	Diese Methode ist völlig sinnlos und muss nur implementiert werden um den Standard zu folgen. Normalerweise würde man den Hash einfach abschneiden, aber naja ;-)
	//	@param in  Roher Hash zB. SHA1
	//	@param len Die Ausgebelänge, meistens 6, kann hier von 2 bis 10 eingestellt werden.
	//	@return Der Einmalcode als String
	//  Code Beschreibung: Ist nötig da diese Art der Kürzung so ungewöhlich ist.
	//  Achtung im folgenden ist die Rede von 4 Bits und 4 Bytes, diese dürfen nicht verwechselt werden, da sie unterschiedliche Dinge darstellen.
	//	Aus dem rohen SHA1 der eingegeben wird, werden irgendwo in der Mitte 4 Bytes entnommen. 
	//	Die Position dieser 4 Bytes wird durch die letzen 4 Bits bestimmt und mit "Offset" benannt.
	//  Zuerst werden also die letzten 4 Bits herausgenomme um den Offset zu bestimmen. Diese 4 Bits werden direkt als Zahl interpretiert und Offset genannt.
	//	Der Offset (4Bits) bestimmt nun den startwert der zu entnehmenden 4Bytes! 
	//	Die entnommenen 4Bytes werden nun als Integer interpretiert und bilden direkt schon den Ausgabewert.
	// 	Sollte der Ausgabewert negativ sein, wird einfach das erste Bit gelöscht.
	//  Der Integerwert wird in String konvertiert und vorne mit 10 Nullen verlänngert, damit er nicht zu kurz ist, wenn der Wert zu klein ist.
	//  Der AusgabeString wird nun einfach bis auf die benötigte Ausgabelägen von vorne gekürzt und dann ausgegeben.
	private static String truncation(byte[] in, int len) throws IOException
	{
		if(len < 2 || len > 10) throw new IOException("The length of the output must be between 2 and 10!");
		int offset = in[in.length-1] & 0x0f;
		byte[] b = Arrays.copyOfRange(in, offset, offset+4);
		b[0] = (byte) (b[0] & 0x7f); // löscht das erste Bit, damit keine negativen Zahlen auftauchen.
		String str = Integer.toString(Convert.byteArray_to_int(b));
		str = "0000000000"+str;		 // Stellt Nullen voran damit die Ausgabe nicht zu kurz ist.
		return str.substring(str.length()-len, str.length());
	}
	
	
	// verbindet die beiden Arrays hintereinander
	private static byte[] add(byte[] a, byte[] b) 
	{
		byte[] out = new byte[a.length + b.length];
		System.arraycopy(a, 0, out, 0, a.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;	
	}
	
	
	// XOR von a und b. a unb b müssen gleich lang sein!
	private static byte[] xor(byte[] a, byte[] b) throws Exception
	{
		if(a.length != b.length) throw new Exception("a and b are not the same length");
		byte[] out = new byte[a.length];
		for(int i=0;i<a.length;i++)
		{
			out[i] =  (byte) (a[i] ^ b[i]);
		}
		return out;
	}
	
	

	
	
// ----------------------------------------------------------------------- Base32 Converter --------------------------------------------------------------------------------------
	
	
	// Codiert ein String der im Base23 Format vorliegen muss in ein ByteArray
	private static byte[] fromBase32(String str) throws IOException
	{
		String s = str.toUpperCase();
		if(s.matches("[ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=]*")==false) 	throw new IOException("No Base 32 format! Only allowed characters: ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=");
		if(s.length()%8!=0) 											throw new IOException("No Base 32 format! Length must be divisible by 8!");
		s = s.replaceAll("=","");	// Alle "=" Zeichen werden entfernt.
		Map<String, String> map = new HashMap<>();
		{
		    map.put("A", "00000");
		    map.put("B", "00001");
		    map.put("C", "00010");
		    map.put("D", "00011");
		    map.put("E", "00100");
		    map.put("F", "00101");
		    map.put("G", "00110");
		    map.put("H", "00111");
		    map.put("I", "01000");
		    map.put("J", "01001");
		    map.put("K", "01010");
		    map.put("L", "01011");
		    map.put("M", "01100");
		    map.put("N", "01101");
		    map.put("O", "01110");
		    map.put("P", "01111");
		    map.put("Q", "10000");
		    map.put("R", "10001");
		    map.put("S", "10010");
		    map.put("T", "10011");
		    map.put("U", "10100");
		    map.put("V", "10101");
		    map.put("W", "10110");
		    map.put("X", "10111");
		    map.put("Y", "11000");
		    map.put("Z", "11001");
		    map.put("2", "11010");
		    map.put("3", "11011");
		    map.put("4", "11100");
		    map.put("5", "11101");
		    map.put("6", "11110");
		    map.put("7", "11111");
		}	
		String dual = "";
		for(int i=0; i<s.length(); i++)
		{
			dual = dual + map.get(s.substring(i, i+1));
		}
		dual = dual.substring(0, dual.length() - dual.length()%4);	
		String hex = binToHex(dual); 
		if((hex.length()%2)==1) 
		{
			if(hex.substring(hex.length()-1).equals("0")) hex = hex.substring(0,hex.length()-1);
			else hex = hex+"0";	
		}
		return Convert.hexStringToByteArray(hex);
	}
	
	
	// Condiert eine ByteArray zu einem Base32-String
	// Methode ist im Vergleichstest mit "org.apache.commons.codec.binary.Base32" hinreichend getestet.
	@SuppressWarnings("unused")
	private static String toBase32(byte[] in)
	{
		String[] alphabet = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","2","3","4","5","6","7"};
		String str = Convert.byteArrayToHexString(in);		
		str = hexToBin(str);
		while(str.length()%5!=0) {str = str + "0";}	// Hängt "0" an, bis Länge durch 5 teilbar ist.
		String[] s = new String[str.length()/5];
		String out = "";
		int k = 0;
		for(int i=0; i<str.length();i=i+5)
		{
			s[k]=str.substring(i, i+5);
			int dec = Integer.parseInt(s[k],2);
			out = out+ alphabet[dec];		
			k++;
		}
		while(out.length()%8!=0) out = out + "=";	// Hängt "=" an, bis Länge durch 8 teilbar ist.	
		return out;
	}
	

	// Konvertiert einen HexString in ein BinärString
	private static String hexToBin(String s) 
	{
		s = s.toLowerCase();			
		Map<String, String> digiMap = new HashMap<>();
		{
		    digiMap.put("0", "0000");
		    digiMap.put("1", "0001");
		    digiMap.put("2", "0010");
		    digiMap.put("3", "0011");
		    digiMap.put("4", "0100");
		    digiMap.put("5", "0101");
		    digiMap.put("6", "0110");
		    digiMap.put("7", "0111");
		    digiMap.put("8", "1000");
		    digiMap.put("9", "1001");
		    digiMap.put("a", "1010");
		    digiMap.put("b", "1011");
		    digiMap.put("c", "1100");
		    digiMap.put("d", "1101");
		    digiMap.put("e", "1110");
		    digiMap.put("f", "1111");
		}
		char[] hex = s.toCharArray();
	    String binaryString = "";
	    for (char h : hex) 
	    {
	        binaryString = binaryString + digiMap.get(String.valueOf(h));
	    }
	    return binaryString;
	}	
	
	
	// Konvertiert eine BinärZeichenfolge in einen HexString
	private static String binToHex(String in) 
	{
		Map<String, String> map = new HashMap<>();
		{
		    map.put("0000","0");
		    map.put("0001","1");
		    map.put("0010","2");
		    map.put("0011","3");
		    map.put("0100","4");
		    map.put("0101","5");
		    map.put("0110","6");
		    map.put("0111","7");
		    map.put("1000","8");
		    map.put("1001","9");
		    map.put("1010","a");
		    map.put("1011","b");
		    map.put("1100","c");
		    map.put("1101","d");
		    map.put("1110","e");
		    map.put("1111","f");
		}
		String out = "";
		for(int i=0; i<in.length(); i=i+4)
		{
			String z = in.substring(i,i+4);
			out = out + map.get(z);
		}
	    return out;
	}	
	
	
	
	

	
// ------------------------------------------------------------------------------- Test Methoden ---------------------------------------------------------
	
	
	
	// Tested die eigene Base32 Codierung in dem Random ver und endschlüsselt wird.
//	private static void testBase32() throws IOException
//	{
//			Random rand = new Random();
//			for(int i=0; i<10000000; i++)
//			{
//				if(i%10000==0)System.out.println(i);		
//		        byte[] original = new byte[Math.abs(rand.nextInt()%9)+1];
//		        rand.nextBytes(original);		        
//		        String b32 = toBase32(original);	        
//		        byte[] b = fromBase32(b32);	        
//		     
//				if(Arrays.equals(original, b) == false)
//				{
//					System.out.println("Fehler:");
//			        System.out.println("Original  Byte: "+Convert.byteArrayToHexString(original));	
//			        System.out.println("Base32 Codiert: "+b32);	        
//			        System.out.println("Codiertes Byte: "+Convert.byteArrayToHexString(b));
//					return;
//				}
//			}
//			System.out.println("Ende kein Fehler.");
//	}	
	
	
	
// Diese Methode tested im Verglich die Base32 Methode. Benötigt: import org.apache.commons.codec.binary.Base32;
//private static void testBase32withApache() throws IOException
//{
//		Random rand = new Random();
//		for(int i=0; i<100000000; i++)
//		{		
//			// ------- ToBase32
//			if(i%100000==0)System.out.println(i);		
//	        byte[] b = new byte[Math.abs(rand.nextInt()%10)+1];
//	        rand.nextBytes(b);
//			Base32 base32 = new Base32();
//			String erg1 = base32.encodeAsString(b);
//			String erg2 = toBase32(b);
//			if(erg1.equals(erg2)==false)
//			{
//				System.out.println("Fehler toBase32");
//				System.out.println(Convert.byteArrayToHexString(b));
//				System.out.println(erg1);
//				System.out.println(erg2);
//				return;
//			}
//			// --------- fromBase32 back
//			byte[] backMy     = fromBase32(erg1);
//			byte[] backApache = base32.decode(erg1);
//			if(Arrays.equals(backMy, backApache) == false)
//			{
//				System.out.println("Fehler fromBase32");
//				System.out.println(erg1);
//				System.out.println(Convert.byteArrayToHexString(backMy));
//				System.out.println(Convert.byteArrayToHexString(backApache));
//				return;
//			}
//		}
//		System.out.println("Endle kein Fehler.");
//}
	
}