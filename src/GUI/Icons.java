package GUI;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;



/************************************************************************************************************************************************
 * Verwendete Icons sind hier harde-Codiert
 * Dadurch müssen sie nicht dem Installationsverzeichnis per Hand hinzugefügt werden.
 * Die Icons werden dann mit der Methode saveAsPNG() wärend der Laufzeit im Ordner temp erstellt.
 * Achtung die JTree Icons der Anwendungen, sind nicht Teil dieser Klasse und werden in der Klasse "MyTreeCellRenderer.java" implementiert
 ************************************************************************************************************************************************/



public class Icons 
{
	
	// Das key.png Icon im Base64Format.
	final public static String keyPNG     = "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";
	final public static String keyPNGsmal = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAEfSURBVDhPzZK9TgJBFIXP/iBC1MhD0FvQWlja8wAYX8BXoDHhFdQQWirpKGxsqGmM8RksdGOikGWX8dyZq2GWn5AYEr/k5tydvXvm3tnBXwlULaaHY0orm6MZh6jncxxEIfazHG9RhBGLx0ELbVftKBrcUS4ZCU2eaZIaw6LAGp9IDenR5EJzn6yLJ5oYxr0u/cK1vr571yVLsYOcEtrc4JY7c3/LIeOcIZ2k7KAsi4JnwA4+OXNVH9cxpUFFc7fbAq+qm3hRtXgGYYCOppsYqFq8EYSvG5ztxTjlKO00A0qxK5rluC5FeGD7j67SsWTwg5z4x8TlRxUk/LDmnnyKZ7ASXqihpktsZcBx5Beu5B93wFNviPJGJpQryXcA8A0XaE9rBW16wgAAAABJRU5ErkJggg==";
	
	

	// Nur zur Erstellung
//	public static void main(String[] args) throws Exception
//	{
//		String str = getPNGinBase64("iconTemp/key.png");
//		System.out.println(str);
//		
//		test2();
//	}
	

	
	
	
	
	
//---------------------------------------------------------- Hier wird versucht das favicon von Webseiten rutnerzuladen um es später für jeden Accound im JTree anzeigen zu können ------------------------	
	
	
	
		// Lädt das Favicon und speichert es als .ico
		// funktioniert.
//	    public static void test1() throws Exception 
//	    {
//	        String url = "https://stackoverflow.com/favicon.ico";
//	        try (InputStream in = new URL(url).openStream();
//	             FileOutputStream out = new FileOutputStream("favicon.ico")) 
//	        {
//	            byte[] buffer = new byte[4096];
//	            int n;
//	            while ((n = in.read(buffer)) != -1) {
//	                out.write(buffer, 0, n);
//	            }
//	            System.out.println("Favicon gespeichert als favicon.ico");
//	        }
//	    }
	
	
	
	
	
	
	
	
//	 public static void test2() throws Exception 
//	 {
//		 String faviconUrl = "https://google.com/favicon.ico";
//
//	        try {
//	            // Favicon herunterladen und versuchen als Bild zu laden
//	            BufferedImage original = ImageIO.read(new URL(faviconUrl));
//	            if (original == null) {
//	                System.out.println("Favicon konnte nicht als Bild geladen werden. Das Format wird von Java wahrscheinlich nicht unterstützt (z.B. klassisches ICO).");
//	                return;
//	            }
//
//	            // Auf 16x16 skalieren
//	            Image tmp = original.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
//	            BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
//	            Graphics2D g2d = resized.createGraphics();
//	            g2d.drawImage(tmp, 0, 0, null);
//	            g2d.dispose();
//
//	            // Als PNG speichern
//	            File outputfile = new File("favicon.png");
//	            ImageIO.write(resized, "png", outputfile);
//	            System.out.println("Favicon erfolgreich als favicon.png gespeichert!");
//	        } catch (IOException e) {
//	            System.out.println("Fehler beim Herunterladen oder Speichern: " + e.getMessage());
//	            e.printStackTrace();
//	        }
//	    }
	
// --------------------------------------------------------------- Test Ende ------------------------------------------------------------------------------	
	
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	
	/**	Speichert ein .png Icon als PNG Bild auf die Festplatte
	@param base64String Die Quelle muss ein Base64-String sein!
	@param fileName Der Dateiname der Datei.     **/
	public static void saveAsPNG(String base64String, String fileName) throws IOException
	{
			byte[] b = Base64.getDecoder().decode(base64String);	
			BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(fileName));
			bo.write(b);
			bo.close();		
	}
	
	
	
	
	
// ----------------------------------------------------- Hilfsmethoden zur Erzeugung --------------------------------------------------------
	
	
	// Lädt ein icon.png aus der Datei und gibt die Daten als byte-Array zurück.
	// Ist zum vorherigen Einlesen eines gewünschten Icons gedacht.
	protected static byte[] loadIconFromDatei(String fileName) throws IOException
	{
		BufferedInputStream bf = new BufferedInputStream(new FileInputStream(fileName));
		byte[] b = bf.readAllBytes();
		bf.close();
		return b;
	}
	
	
	// Öffnet eine PNG-Datei und gibt sie als Base64-String zurück
	protected static String getPNGinBase64(String NamePNGDatei) throws IOException
	{
			BufferedInputStream bi = new BufferedInputStream(new FileInputStream(NamePNGDatei));
			byte[] b = bi.readAllBytes();    
			bi.close();
			return new String(Base64.getEncoder().encode(b));	   	
	}
	
	
	// Speichert den Datensatzt auf der Festpaltte
	protected static void saveData(String data, String fileName) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		bw.write(data);
		bw.close();
	}
}