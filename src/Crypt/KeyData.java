package Crypt;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import GUI.GUI;



/***************************************************************************************************************************************
*	Autor: Mr. Maxwell						Version 1.4						04.09.2023													*
*	Letzte Änderung:	TOTP Datensatz hinzugafügt																						*
*	Öffnet, speichert und ändert die ***.key Schlüssel-Datenbank für den KeyPass														*	
*																																		*
*	Diese Klasse übernimmt die Implementierung der verschlüsselten Datenbank auf oberer Ebene.											*
*	Von hier aus werden die Crypt Klasse angesteuert.																					*
*	Diese Klasse fungiert als Schnittstelle zwischen der GUI und der verschlüsselten Datenbank.											*
*																																		*
*	Aufbau der ***.key: entschlüsselt im JSON-Format																					*
* {																																		*
*  "dateiID"  : "b7a4744f151d0d4c3968c967b3d1abe8ce7eb9bc4647b4aaab0bcb256ee93811",														*
*  "progName": "KeyPass",																												*
*  "version" : "V1.0.0",																												*
*  "pwHash"  : "92b165232fbd011da355eca0b033db22b934ba9af0145a437a832d27310b89f9",														*
*  "list": 																																*
*  [{																																	*
*      "ApplicationName"	: "Blumen-Forum",																							*
*      "UserName"     		: "MaxMusstermann",																							*
*      "Password"    		: "123456789ABC",																							*
*      "Description"		: "Zusätzliche Informationen Optional",																		*
*      "Date"       		: "2020-02-26",																								*
*      "url"				: "https://github.com",																						*
*      "totpKey"			: "23456===",																								*
*      "totpLen"			: "6"																										*
*    }]																																	*
* }																																		*
* 																																		*
*																																		*
* - "pwHash" 			ist ein 32Byte SHA256² Hex-String des Passwortes mit dem diese Datei verschlüsselt wurde						*
* - "list" 				Stellt die Key-Datenbank dar. JSON-Array																		*
* - "ApplicationName"	Der Inhalt darf nur einmal vorkommen und wird als Schlüsselwert für die entsprechende Applikation verwendet		*
****************************************************************************************************************************************/



public class KeyData 
{
	

	
	final static String 		DateiID  = "b7a4744f151d0d4c3968c967b3d1abe8ce7eb9bc4647b4aaab0bcb256ee93811";  // Zum eindeutigen erkennen der richtigen Datei
	private static JSONObject 	databaseLoaded;																	// Die geladene und entschlüsselte KeyPass-JSON-Datenbank. Diese darf nach dem laden niemals verändert werden, sie entspricht immer der Datenbank wie sie von der HDD geladen wurde.
	private static JSONObject 	databaseToSave;																	// Hier her wird die Datenbank zu speichern geklont! Danach werden die Änderungen an dieser Version vorgenommen um sie dann auf HDD zu speichern.
																												// Der Grund für diese zwei Datenbanken ist ein fataler Fehler der entstehen würde, wenn beim Speichern auf die HDD, abgebrochen wird. Dann wäre die im RAM befindliche Datenbank 
																												// nicht die Selbe, wie auf der HDD. Die nächste Speicher Aktion würde dann die vorher veränderten Daten dennoch mit abspeichern. 

	

	
	
	
/**	Gibt eine Kopie der aktuell geladenen Database als JSONObject zurück. **/
public static JSONObject getDatabase() throws JSONException
{
	return clone(databaseLoaded);
}



/**	- Die KeyPass.key wird gelesen, oder ggf. neu erstellt
	- ParanoidDecrypt wird gestartet und öffnet den Passwort-Dialog
	- KeyPass.key wird entschlüsselt und als JSONObject in "databaseLoaded" gespeichert   **/
public static void open() throws Exception
{	
	File file = new File((String)GUI.combo_filename.getSelectedItem()+".key");
	if(file.exists()==false)
	{
		databaseLoaded = new JSONObject();
		databaseLoaded.put("pwHash", "");
		databaseLoaded.put("list", new JSONArray());		
		save(databaseLoaded);
	}
	BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file));
	byte[] in = new byte[(int) file.length()];
	bi.read(in);
	bi.close();
	
	GUI.frame.setEnabled(false);
	String str = CryptDialog.paranoidDecrypt(GUI.frame.getX()+20, GUI.frame.getY()+20, in);	
	GUI.frame.setEnabled(true);

	
	
	if(str==null) 		throw new Exception("Error in KeyData.open(). null Password.");
	if(str.equals("")) 	throw new Exception("Error in KeyData.open(). empty Password.");
	databaseLoaded =  new JSONObject(str);	
	String dateiID = databaseLoaded.getString("dateiID");
	if(dateiID.equals(DateiID)==false) throw new Exception("DateiID falsch!\nDatei wurde entschlüsselt aber möglicherweise handelt es sich um eine falsche Datei.");
	//System.out.println(databaseLoaded.toString(1)); // <------------------------------------------------------------------ Hier Ausgebeprüfung Akt./Deaktivieren!
}




/**	Hängt einen neuen Datenbank Eintrag hinten an.
	Dazu wird der Crypt-Dialog geöffnet und wenn alles korrekt ist, wird die Datenbank neu verschlüsselt und auf HDD abgespeichert. **/
public static void add(String applicationName, String userName, String passwort, String description, String date, String url, String totpKey, String totpLen) throws Exception
{
	if(isDuplicateAddress(applicationName,-1))  	throw new IOException("Name der Anwendung existiert bereits!");
	databaseToSave = clone(databaseLoaded);
	JSONArray ja = databaseToSave.getJSONArray("list");
	JSONObject jo = new JSONObject();
	jo.put("ApplicationName",	applicationName);
	jo.put("UserName", 			userName);
	jo.put("Password", 			new String(passwort));
	jo.put("Description",		description);
	jo.put("Date", 				date);
	jo.put("url", 				url);
	jo.put("totpKey", 			totpKey);
	jo.put("totpLen", 			totpLen);
	ja.put(jo);
	save(databaseToSave);	
}



/**	Ändert den Datenbankeintrag mit der Nummer "id".
	Dazu wird der Crypt-Dialog geöffnet und wenn alles korrekt ist, wird die Datenbank neu verschlüsselt und auf HDD abgespeichert. **/
public static void toChange(int id, String applicationName, String userName, String passwort, String description, String date, String url, String totpKey, String totpLen) throws Exception
{
	if(isDuplicateAddress(applicationName,id))  	throw new IOException("Name der Anwendung existiert bereits!");
	databaseToSave = clone(databaseLoaded);
	JSONArray ja = databaseToSave.getJSONArray("list");
	ja.getJSONObject(id).put("ApplicationName",	applicationName);
	ja.getJSONObject(id).put("UserName", 		userName);
	ja.getJSONObject(id).put("Password",		passwort);
	ja.getJSONObject(id).put("Description",		description);
	ja.getJSONObject(id).put("Date", 			date);
	ja.getJSONObject(id).put("url", 			url);
	ja.getJSONObject(id).put("totpKey", 		totpKey);
	ja.getJSONObject(id).put("totpLen", 		totpLen);
	save(databaseToSave);	
}



/**	Löscht den Eintrag mit der "id" aus der Datenbank.
	@param id Es handelt sich um den Index im JSONArray.  
	Der Crypt-Dialog wird geöffnet und wenn alles ok ist, wird die geänderte Datenbank neu verschlüsselt und auf HDD gespeichert. **/
public static void remove(int id) throws Exception
{
	databaseToSave = clone(databaseLoaded);
	JSONArray ja = databaseToSave.getJSONArray("list");
	ja.remove(id);
	save(databaseToSave);		
}



/**	@param str Prüft ob dieser Datenbank Eintrag schon vorhanden ist
	@param skip Diese Nummer ist der Index im JSONArray der für die Prüfung nicht berücksichtigt wird.
	Wird benötigt um einen Datenbankeintrag zu ändern ohne das der "Duplikat-Fehler" durch den zu überschreibenden Eintrag bei der Prüfung auslöst.
	Sollen alle Einträge berücksichtigt werden, wird hier -1 übergeben.
	@return gibt true zurück wenn der Eintrag schon vorhanden ist UND nicht übersprungen werden soll.  **/
public static boolean isDuplicateAddress(String str, int skip) throws JSONException
{			
	JSONArray ja = databaseLoaded.getJSONArray("list");
	if(ja.length()==0) return false;	
	for(int i=0; i<ja.length();i++)
	{
		JSONObject jo = (JSONObject) ja.get(i);
		String name = jo.getString("ApplicationName");
		if(name.equals(str) && i!=skip) return true;
	}
	return false;	
}







// -------------------------------------------------------------- Private Methoden ----------------------------------------------------------------




//	Verschlüsselt und Speichert die übergebene Key-Database (JSONObject) auf die Festplatte
//- Kopiert die KeyData.key	
//- ParanoidEcrypt wird gestartet und öffnet den Passwort-Dialog
//- Das übergebene JSON-Object wird mit ParanoidEncrypt verschlüsselt und als KeyDate.key auf HDD gespeichert.
private static void save(JSONObject jo) throws Exception
{	
	kopieBackup();
	jo.put("dateiID", DateiID);
	jo.put("progName", GUI.progName);
	jo.put("version",  GUI.version);
	byte[] ch;	
	File file = new File((String)GUI.combo_filename.getSelectedItem()+".key");
	GUI.frame.setEnabled(false);
	if(file.exists()) 		ch = CryptDialog.paranoidEncrypt(GUI.frame.getX()+20, GUI.frame.getY()+20, jo);
	else 					ch = CryptDialog.paranoidEncrypt(GUI.frame.getX()+20, GUI.frame.getY()+20, jo);	
	GUI.frame.setEnabled(true);
	if(ch == null) throw new Exception("Error Encrypt, NULL!");	
	BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(file));
	bo.write(ch);
	bo.close();	
}



//Kopiert die vorhandene (alte) KeyData-Datei nach Backup_KeyData.key
private static void kopieBackup() throws IOException
{
	File src = new File((String)GUI.combo_filename.getSelectedItem()+".key");
	File des = new File("_old_"+(String)GUI.combo_filename.getSelectedItem()+".key");
	if(src.exists()) Files.copy(src.toPath(), des.toPath(), StandardCopyOption.REPLACE_EXISTING);
}



// Klont ein JSONObject tief. 
// Bei üblichen Zuweisungen (=) werden JSONObject nicht kopiert sondern nur Referenzen erstellt.
// Diese Methode kopiert es tief, also alle enthaltenen Elemente werden hart kopiert (geklont).
private static JSONObject clone(JSONObject jo) throws JSONException
{
	return new JSONObject(jo.toString());
}
}