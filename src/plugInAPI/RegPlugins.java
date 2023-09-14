package plugInAPI;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import BTClib3001.Calc;
import BTClib3001.Convert;
import BTClib3001.Secp256k1;
import plugIn_2FA.TwoFaQR;



/********************************************************************************************************************************************
*	Diese Klasse registriert, verifiziert und lädt Plugins.																					*
*	Jedes Plugin muss im Ordner plugins abgelegt werden. xxx.plugin Datein																	*
*	Diese Klasse durchsucht den Ordner und lädt gefundene Plugins und startet sie.															*
*	Jedes PlugIn muss eine Main-Klasse mit einer Start-Methode implementieren. Diese Main-Klasse muss den Namen des Plug-Ins tragen!		*
*********************************************************************************************************************************************/


/******************************************************************************************************************************************************************
	Test.plugin Datei Beispiel:
	
	{
 	"date": "16.01.2023",
 	"classData": [
 	"cafebabe00000037006807000201000f74776f46412f5032666131243124310700040100106a6176612f6c616e672f4f626a6563740700060100126a6176612f",
 	"cafebabe00000037006807000201000f74776f46412f5032666131243124310700040100106a6176612f6c616e672f4f626a6563740700060100126a6176612f",
 	"cafebabe00000037006807000201000f74776f46412f5032666131243124310700040100106a6176612f6c616e672f4f626a6563740700060100126a6176612f",
 	],
 	"coreApp": "KeyPass",
 	"autor": "Mr. Maxwell",
 	"dateiID": "coo1c0d0d1961ae35414616de8634204f4cf8fcd990abdb7007d66e1bd602c22",
 	"pluginVersion": "0.0",
 	"r": "dc0bfb89ae8d70b52d06e51cb25713797246fd92562ce99c48d7a2ddf26f2559",
 	"s": "80c1e2a5527c31b3dbc95ac234b87ca0f3b646bded9b85995daad2865251f3bb",
 	"pluginName": "TestPlugIn1",
 	"mainClassName": "P2fa1",
 	"packageName": "twoFA",
 	"coreVersion": "1.2.0",
 	"pubKey": "03291251a267e24ed362241cdaba7c953a52b295c81f92e7fd10df665a6b000441"
	}
	
	- "date"			= Datum der Signierung des PlugIns, wird selbstständig eingetragen.
	- "classData" 		= Ein JSON-Array mit allen Java-Klassen des PlugIns als Hex-String
	- "coreApp"			= Name der Anwendung für dieses PlugIn, ist immer "KeyPass"
	- "autor"			= Name/Firma/Alis des Autors des PlugIns, ist frei wählbar und darf leer sein.
	- "dateiID"			= Ist für alle KeyPass-PlugIns immer gleich: "coo1c0d0d1961ae35414616de8634204f4cf8fcd990abdb7007d66e1bd602c22" 
	- "pluginVersion"	= Die Version des erstellen PlugIn´s. Ist frei wählbar.	
	- "r" 				= Signatur r-Teil
	- "s" 				= Signatur s-Teil
	- "pluginName"		" Der Name des PlugIns, ist frei wählbar und entspricht dem späteren Dateinamen der plugInName.json Datei. 
	- "mainClassName"	= Der PlugIn-Main-Class Name ist der Name der PlugIn Klasse die die Start-Methode enthält. Der Name wird ohne Dateiendung ".class" eingegeben. Muss korrekt sein, sonnst kann das PlugIn nicht starten.
 	- "packageName"		= Der Package Name ist der Packet-Name in der die Main-Klasse des PlugIn´s ist. Die Erste Zeile in der Java-Klasse ("package xxx;") ist hier gemeint.
	- "coreVersion" 	= Letzte KeyPass-Version unter der das Plugin getestet wurde.
	- "pubKey"			= Public-Key der Signierung (Komprimierte Form)
	Der Signatur-Hash ist SHA256 von "classData". Von jeder Klasse wird zuerst ein SHA256 Hash gebildet. Alle Hashes werden dann aneinander gehängt und wieder gehasht.
******************************************************************************************************************************************************************/




public class RegPlugins 
{


	
	public static void aktivate() 
	{	
	
	
	// Dieser Abschnuitt ist nur zum Erstellen und Testen von PlugIns dar. 
	// Zum Testen der PlugIns = true,  muss sonnst immer false sein!
	// Wenn die KeyPass.jar Datei Kompiliert wird, MUSS dieser Abschnitt false sein!!!
	if(false)
	{	
		TwoFaQR t =new TwoFaQR();
		t.start();	
	}
	else
	{								
			File f = new File("plugins");
			File[] fileArray = f.listFiles();		
			if(fileArray.length<=0) 	// Wenn keine PlugIns vorhanden sind...
			{
				JTextArea txt = new JTextArea();
				txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
				txt.setEditable(false);
				txt.setText("Keine Plugins installiert.\nPlugIns sind ###.plugin Dateien und müssen sich im Ordner plugins befinden.");
				GUI.GUI.btn_plugIns.add(txt);
			}
			else{GUI.GUI.txt_meldung.setText("Plugins:\n");}
			for(int i=0;i<fileArray.length;i++)
			{
				String name = null;
				try
				{
					name = fileArray[i].getName();
					GUI.GUI.txt_meldung.setText(GUI.GUI.txt_meldung.getText()+name+"\n");				
					BufferedReader br = new BufferedReader(new FileReader("plugins\\"+name));
					String str = "";
					while(br.ready())	str += br.readLine()+"\n";			
					br.close(); 	
					String[] classData = verifyPlugIn(GUI.GUI.pubKeySig, str);												
					for(int j=0; j<classData.length;j++)
					{
						MyClassLoader.loadClass(Convert.hexStringToByteArray(classData[j]));
					}
					
					JSONObject jo = new JSONObject(str);
					MyClassLoader.runStartMethod(jo.getString("packageName"), jo.getString("mainClassName"), "start");
					
				}	      
				catch(Throwable et) 
				{
					JOptionPane.showMessageDialog(GUI.GUI.frame, "Beim laden des Plugins: "+name+" ist ein Fehler aufgetreten.\n"+et.getMessage(),"Plugin Error", JOptionPane.ERROR_MESSAGE);		        	
					et.printStackTrace();
					System.exit(0);
				}
			}	
		}		
	}
	
	
	
// ----------------------------------------------------------------- Private Methoden ------------------------------------------------------------------/ /	
	
	
	// Verifiziert ein PlugIn mit ECDSA-SECP256k1
	// - plugInData: Das PlugIn wird als Daten-String so übergeben wie es von der Festpatte geladen wird.
	// - pub:  Der PublicKey der SignaturPrüfung (compressed) muss hier übergeben werden und wird mit dem Pub-Key in der Signatur verglichen! Beide MÜSSEN Gleich sein !!!
	// Wenn die Verifizierung erfolgreich war, wird das PlugIn als HexString-Array zurückgegeben, wenn nicht wird ein Fehler (Excpetion) erzeugt.
	private static String[] verifyPlugIn(String pub, String plugInData) throws Exception 
	{
		final String errorText1 = ("Signature Error:\n"
				+ "Der Public-Key in der Signatur des PlugIns stimmt nicht mit dem im KeyPass hinterlegtem Public-Key überein!\n"
				+ "Das PlugIn wird nicht geladen, wenn die Signatur falsch ist!\n"
				+ "Das PlugIn wurde wahrscheinlich mit einem falschem Private-Key signiert.\n"
				+ "Wenden sie sich an den Herausgeber diese PlugIns, oder des KeyPass\n");
		
		final String errorText2 = "Datei Error:\n"
				+ "Eine falsche Datei liegt im Ordner plugins.\n"
				+ "Der Ordner plugins darf nur Plugins (###.plugin Dateien) und keine anderen Dateien oder Ordner enthalten!\n"
				+ "Entfernen sie alle fehlerhaften Dateien aus dem Ordner plugins!";

		final String errorText3 ="Signature Error:\n"
				+ "Prüfung der Signatur schlug fehl!\n"
				+ "Das PlugIn wird nicht geladen, wenn die Signatur falsch ist!\n"
				+ "Der KeyPass lädt aus Sicherheitsgründen nur richtig signierte PlugIn´s.\n"
				+ "Wenden sie sich an den Herausgeber diese PlugIns, oder des KeyPass.\n"
				+ "Benötigen sie die Berechtigung zum Signieren von PlugIns?\n"
				+ "Wenden sie sich an den Autor des KeyPass.\n"
				+ "Autor KeyPass: "+ GUI.GUI.autor +"\n"
				+ "Autor PlugIn: ";
			
		JSONObject jo;
		try {jo = new JSONObject(plugInData);} 
		catch (JSONException e) {throw new JSONException(errorText2);}		
		if(jo.getString("pubKey").equals(pub)==false) throw new Exception(errorText1);  // Wenn der Public-Key nicht mit dem Hinterlegtem Public-Key im KeyPass übereinstimmt wird hier schon abgebrochen! 		
		JSONArray ja = jo.getJSONArray("classData");		
		String[] out = new String[ja.length()];
		String h = "";
		for(int i=0; i<ja.length();i++)
		{
			out[i] = ja.getString(i);
			h = h +  Calc.getHashSHA256_from_HexString(out[i]);
		}
		byte[] hash = Convert.hexStringToByteArray(Calc.getHashSHA256_from_HexString(h));	
		BigInteger[] sig = new BigInteger[2];
		sig[0] = new BigInteger(jo.getString("r"),16);
		sig[1] = new BigInteger(jo.getString("s"),16);
		BigInteger[] pubKey = Secp256k1.deComp(pub);		
		Secp256k1 secp = new Secp256k1();
		if(secp.verify(hash, sig, pubKey)==false) throw new Exception(errorText3 + jo.getString("autor")); // Wenn die Signatur falsch ist	
		else {return out;}	
	}
}