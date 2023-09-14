package plugInAPI;
import javax.swing.JMenuItem;


/***************************************************************************************************************************
 *  Version 1.0											Mr. Maxwell									17.01.2023				*
 *  Diese Interface Legt Bedingungen aller PlugIns für den KeyPass fest.													*
 *  KeyPass-PlugIns werden als signierte JSON-Dateien mit der Dateiendung: .plugin Implementiert.							*
 ***************************************************************************************************************************/


/***********************************************************************************************************************
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






public interface Plugins 
{
	
	
	
	/** Start-Methode die den PlugIn Code ausführt.
	 	Plugin´s die Code ausführen wollen, Müssen diese Methode überschreiben.*/
	public default void start() 
	{
		setMenuItem(getPluginName());
	}
	
	
	/** Setzt das Menü-Item des PlugIn´s oben im Menü
	 	Wenn die Methode nicht vom PlugIn implementiert wird, wird dort nur der Name des PlugIn´s gesetzt.  */
	public static void setMenuItem(String name)
	{
		JMenuItem menuIcon1 = new JMenuItem(name);
		GUI.GUI.btn_plugIns.add(menuIcon1);	
	}
	
	
	/**	@return Gibt den Namen des PlugIn´s als String zurück 
	 	Muss jedes PlugIn implementieren!*/
	public String getPluginName();

}						