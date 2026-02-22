package plugInAPI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.json.JSONArray;
import org.json.JSONObject;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;
import lib3001.ecdsa.Secp256k1;




/****************************************************************************************************************************************************************************************
* Version 1.2																Mr. Maxwell															03.03.2023								*
* Signiert und codiert plugin.class Dateien für den KeyPass																																*
* Dies ist ein eigenständiges Java-Programm das hier nur aus der Main-Methode gestartet wird.																							*
* Dieses Programm kann daher auch einzeln Kompiliert und als .jar unabhängig ausgeführt werden.																							*
* Der KeyPass ruft diese Klasse nicht oder nur temporär auf!																															*
* Plugins für den KeyPass werden signiert. Dies soll verhindern das Schadcode durch ein PlugIn geladen wird.																			*
* Mit diesem Programm kann nur der Besitzer des Private-Keys ein Plugin signieren.																										*
* Nur richtig signierte Plugins, werden später vom KeyPass auch ausgeführt.																												*
* Der Public-Key befindet sich im Code des KeyGen, (GUI: pubKeySig) und wird mit dem Pub-Key in der Signierung verglichen.																*
* Der Private-Key wird natürlich nicht gespeichert, und muss von Signierer selbst eingetragen werden.																					* 
* Das Signierte Plugin wird anschließend in eine JSON-Datei mit der Dateiendung .plugin integriert die dann das fertige Plugin darstellen.												*
* Ausführbare Plugins für den KeyPass sind daher JSON-Dateien in der Form: pluginName.plugin																								*
* 																																														*
* OpenSource:																																											*
* Sollte der KeyGen veröffentliche werden so müsste der jeweilige Besitzer einen eigenen PublicKey 																						*
* im Code hinterlegen und neu Kompilieren.	(GUI: pubKeySig)																															*
* 																																														*
* PlugIn packen und Signieren:																																							*
* Dieses Programm ladt alle Java-Klassen des PlugIns, signiert sie und packt sie anschließend in eine JSON-Datei mit der Dateiendung .plugin.											*
* Das Programm hat eine GUI in die alle benötigten Daten eingetragen werden.																											*
* Alle zum PlugIn gehörenden Klasse müssen sich im verlangten Verzeichnis befinden. Dieses Verzeichnis wird beim Starten des Programms automatisch erstellt und							*
* das verlangte Verzeichnis wird im Infotext nach dem Starten angezeigt.	(z.B. "PlugInClasses")																						*
* Es können nur Java-Klassen (xxx.class) verwendet werden! (Innere Klassen nicht vergessen)																								*
* Die Anzahl der Java-Klassen ist unbegrenzt, es müssen aber alle Java-Klassen im verlangten Verzeichnis liegen, Unterordner können verwendet werden									*
* Die Reihenfolge der Klassen ist zu beachten. z.B. müssen erst alle Interface-Klassen geladen werden.																					*
* Die Reihenfolge des Ladens wird in Alphabetischer Reihenfolge des Klassennamens bestimmt. Um Klassen nach vorne zu bringen einfach den Dateinamen der Klasse ändern. 					*
* Der Innere Klassenname der Java-Klasse muss nicht geändert werden und muss auch nicht mit dem Dateinahmen der Klasse übereinstimmen. 													*
* Jedes Plug in muss eine Main-Klasse mit einer Start-Methode implementieren. Diese Main-Klasse muss den Namen des Plug-Ins tragen!														*
* Der Eintrag: PlugIn-Name ist frei wählbar und entspricht dem späteren Dateinamen der plugInName.plugin Datei.																			*
* Der Eintrag packageName muss dem Packet Name der PlugIn-Main-Klasse entsprechen.																										*
* Der Eintrag mainClassName ist der Name der PlugIn Klasse die die Start-Methode enthält. Der Name wird ohne Dateiendung ".class" eingegeben. 											*
* Wenn alle Eingaben richtig/plausibel sind, wird nach dem start, eine xxx.plugin Datei im selben Verzeichnis erstellt die das signierte PlugIn darstellt.		 						*
 ****************************************************************************************************************************************************************************************/					



/******************************************************************************************************************************************************************
	test.plugin - JSON Datei Beispiel:
	
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






public class PluginSignierer extends JFrame 
{
	
	final static	String				classFolderName = "plugInClasses";															// Der Ordnername der erstellt wird und in den die Java-Klassen kopiert werden die zum PlugIn gepackt werden.
	final static 	String 				coreApp 		= "KeyPass";   																// Name der Kern-Anwendung
	final static 	String 				dateiID 		= "coo1c0d0d1961ae35414616de8634204f4cf8fcd990abdb7007d66e1bd602c22";   	// DateiID, für alle KeyPass-Plugins immer gleich!
	private static 	JTextField 			plugIn_Name 	= new JTextField();															// EingabeFeld Plugin Name
	private static 	JTextField 			txt_autor 		= new JTextField();															// EingabeFeld Autor
	private static 	JTextField 			txt_version 	= new JTextField();															// EingabeFeld Plugin Version	
	private static 	JTextField			txt_packName	= new JTextField();															// EingabeFeld des Package-Name der PlugIn-Main-Classe
	private static 	JTextField			txt_mClassName	= new JTextField();															// EingabeFild des Class-Name der PlugIn-Main-Classe	
	private static 	JPasswordField 		txt_pw 			= new JPasswordField();														// EingabeFeld Passwort
	private static 	JTextField 			txt_versionKP	= new JTextField();															// EingabeFeld KeyPass Version
	private static 	JTextArea  			txt_meldung 	= new JTextArea();															// Das untere Meldungs- und Fehler Fenster
	private static 	JPanel 	  			contentPane 	= new JPanel();	
	private static	JScrollPane			scrollPane 		= new JScrollPane();	
	private static  ArrayList<String> 	classArray 		= new ArrayList<String>();													// Die String-ArrayList der Java-Classen



	public static void main(String[] args) throws Exception 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					File f = new File(classFolderName);
					f.mkdir();	
					PluginSignierer frame = new PluginSignierer();
					frame.setVisible(true);
				} 
				catch (Exception e) {e.printStackTrace();}
			}
		});
	}


	
	public PluginSignierer() 	// GUI
	{
		setTitle("KeyPass PluginSignierer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 590, 700);
		contentPane.setBackground(new Color(230, 230, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		JPanel panel_0 	= new JPanel();
		JPanel panel_1 	= new JPanel();
		JPanel panel_2 	= new JPanel();
		JPanel panel_3 	= new JPanel();
		JPanel panel_4 	= new JPanel();
		JPanel panel_5 	= new JPanel();		
		JPanel panel_6 	= new JPanel();
		JPanel panel_7 	= new JPanel();		
		JLabel lbl_1 	= new JLabel("Plugin Name");
		JLabel lbl_2 	= new JLabel("Autor");
		JLabel lbl_3 	= new JLabel("KeyPass Version");
		JLabel lbl_4 	= new JLabel("PlugIn Version");
		JLabel lbl_5 	= new JLabel("Private-Key   Hexa-Dezimal!");
		JLabel lbl_6 	= new JLabel("PlugIn Package Name");
		JLabel lbl_7 	= new JLabel("PlugIn Main-Class Name");
		JTextArea  txt_info 	= new JTextArea();	
		txt_info.setBorder(new EmptyBorder(1, 2, 1, 2));
		JButton    btn_load  = new JButton("Klassen Laden");
		JButton    btn_start = new JButton("Signieren");		
		scrollPane	.setPreferredSize(new Dimension(2, 190));
		txt_meldung.setBorder(new EmptyBorder(2, 3, 3, 2));
		scrollPane	.setViewportView(txt_meldung);
		txt_meldung	.setEditable(false);
		txt_info	.setEditable(false);
		txt_meldung	.setForeground(new Color(210, 105, 30));
		txt_meldung	.setBackground(UIManager.getColor("Button.background"));
		plugIn_Name	.setColumns(50);
		txt_autor	.setColumns(50);
		txt_versionKP.setColumns(50);
		txt_version .setColumns(50);
		txt_pw		.setColumns(50);
		txt_packName.setColumns(50);
		txt_mClassName.setColumns(50);
		txt_pw.setEchoChar((char)8226);
		panel_0.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_2.setLayout(new BorderLayout(0, 0));
		panel_3.setLayout(new BorderLayout(0, 0));
		panel_4.setLayout(new BorderLayout(0, 0));
		panel_5.setLayout(new BorderLayout(0, 0));	
		panel_6.setLayout(new BorderLayout(0, 0));	
		panel_7.setLayout(new BorderLayout(0, 0));	
		txt_pw.setBorder(new LineBorder(new Color(255, 0, 0)));
		txt_pw.setBackground(new Color(255, 255, 255));
		txt_pw.setForeground(new Color(0, 0, 0));	
		txt_info.setFont(new Font("Arial", Font.PLAIN, 12));
		txt_info.setText("Packt und Signiert Java PlugIn´s für den KeyPass\r\nAlle Java-Klassen des PlugIns müssen sich im im Ordner: "+classFolderName+"\n\rvon diesem PlugInSignierer befinden!\r\nZeige mit der Maus über ein Texfeld um genauere Beschreibung zu sehen!");
		txt_info.setBackground(new Color(230, 230, 250));
		txt_info.setPreferredSize(new Dimension(5, 80));
		contentPane.add(scrollPane, BorderLayout.SOUTH);	
		contentPane.add(panel_0, BorderLayout.CENTER);
		panel_1.add(lbl_1, BorderLayout.NORTH);
		panel_1.add(plugIn_Name, BorderLayout.CENTER);
		panel_2.add(lbl_2, BorderLayout.NORTH);
		panel_2.add(txt_autor, BorderLayout.CENTER);		
		panel_0.add(btn_load);			
		panel_0.add(Box.createHorizontalStrut(400));		
		panel_0.add(panel_1);
		panel_0.add(panel_2);
		panel_0.add(panel_3);
		panel_3.add(lbl_3, BorderLayout.NORTH);
		panel_3.add(txt_versionKP, BorderLayout.CENTER);
		panel_0.add(panel_4);
		panel_4.add(lbl_4, BorderLayout.NORTH);
		panel_4.add(txt_version, BorderLayout.CENTER);	
		panel_6.add(lbl_6, BorderLayout.NORTH);
		panel_6.add(txt_packName, BorderLayout.CENTER);
		panel_0.add(panel_6);
		panel_7.add(lbl_7, BorderLayout.NORTH);
		panel_7.add(txt_mClassName, BorderLayout.CENTER);
		panel_0.add(panel_7);
		panel_0.add(panel_5);
		panel_5.add(lbl_5, BorderLayout.NORTH);
		panel_5.add(txt_pw, BorderLayout.CENTER);	
		panel_0.add(Box.createHorizontalStrut(460));		
		panel_0.add(btn_start);
		contentPane.add(txt_info, BorderLayout.NORTH);
		
		
		btn_load.setToolTipText("Lade zuerst die Plug-In Klassen aus dem Ordner: "+classFolderName+". Dieser Ordner wurde im Hauptverzeichnis automatisch erstellt. Deine PlugIn-Klassen musst du dort vorher hinein kopieren.");
		lbl_1.setToolTipText("Gebe hier den Plug-In Namen deines PlugIn´s ein. Dies entspricht dem späteren Dateinamen des gepackten PlugIn.plugin. Der Name ist frei wählbar.");
		lbl_2.setToolTipText("Gebe hier deinen bzw. den Namen des Autors des PlugIns ein. Der Name ist frei wählbar und dieses Feld darf leer sein.");
		lbl_3.setToolTipText("Hier muss die neuste Version des KeyPass stehen unter der dieses PlugIn getestet wurde.");
		lbl_4.setToolTipText("Hier muss die Version von diesem PlugIn eingetragen werden.");
		lbl_6.setToolTipText("Der Package Name der PlugIn-Main-Class muss hier zwingend richtig eingetragen werden! Das PlugIn kann später nicht starten, wenn der Package-Name falsch ist.");
		lbl_7.setToolTipText("Der PlugIn-Main-Class Name ist der Name der PlugIn Klasse die die Start-Methode enthält. Der Name wird ohne Dateiendung \".class\" eingegeben. Muss korrekt sein, sonnst kann das PlugIn nicht starten.");
		lbl_5.setToolTipText("Der Private-Schlüssel zur ECDSA-Signierung wird hier eingetragen (64 Hexa-Zeichen). Sollte der Schlüssel falsch sein, wird die Verifizierung des PlugIn im KeyPass fehlschlagen und das PlugIn nicht starten.");
		btn_start.setToolTipText("Wenn alle PlugIn-Klassen geladen wurden und die Felder richtig ausgefüllt sind, kannst du das PlugIn Signieren und erstellen. Es wird dann im Hauptverzeichnis als PlugInName.plugin Datei abgelegt.");

		
		
		
		// Wenn der Button "Klasssen Laden" gedrückt wird
		btn_load.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent ev) 
			{
				File f = new File(classFolderName);
				txt_meldung.setText("");
				try 
				{
					if(f.list().length==0) throw new Exception("Der Ordner "+classFolderName+" ist leer.\nEs muss sich mindestens eine Java-Klasse im Ordner: "+classFolderName+" befinden!");
					classArray.clear();
					packClasses(f);
				} 
				catch (Exception e) 
				{
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText(e.toString());
					e.printStackTrace();
				}					
			}
		});	
		
		
		
		
	
		// Wenn der Button gedrückt wird
		btn_start.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent ev) 
			{
				txt_meldung.setText("");
				if(plugIn_Name.getText().length()==0)
				{
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText("Du musst einen Namen für dein PlugIn eingeben!");
					return;
				}
				if(txt_version.getText().length()==0)
				{
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText("Bitte gib eine Version für dein PlugIn an!");
					return;
				}
				if(txt_pw.getText().length()==0)
				{
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText("Du musst einen Private-Key eingeben!");
					return;
				}
				if(txt_packName.getText().length()==0)
				{
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText("Du musst den Package-Name der Plug-In Class eingeben, die die Start-Methode enthalät.");
					return;
				}
				if(txt_mClassName.getText().length()==0)
				{
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText("Du musst den Class Name der Plug-In Class eingeben, die die Start-Methode enthalät.");
					return;
				}			
				try 
				{
					sig();
					txt_meldung.setText("Signierung Erfolgt.\n"+plugIn_Name.getText()+".plugin wurde erstellt\nEine Prüfung ob mit dem richtigem Private Schlüssel signiert wurde, erfolgt hier nicht!");
					txt_meldung.setForeground(Color.black);
					contentPane.setBackground(Color.green);
				} 
				catch(IllegalArgumentException e)
				{
					contentPane.setBackground(Color.red);
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText("Private Key hat falsches Format!\nNur Hexa-Dezimale Zeichen erlaubt!\n"+e.toString());
					e.printStackTrace();
				}
				catch (Exception e1) 
				{
					contentPane.setBackground(Color.red);
					txt_meldung.setForeground(Color.red);
					txt_meldung.setText(e1.toString());
					e1.printStackTrace();
				} 
			}
		});	
	}
	
	

	
	// führt die Signatur aus
	private static void sig() throws Exception 
	{	
		if(classArray==null) 		throw new Exception("Es wurde keine JAVA-Klassen geladen!");
		if(classArray.size()==0) 	throw new Exception("Es wurde keine JAVA-Klassen geladen!");	
		String hash = "";
		for(int i=0; i<classArray.size();i++)
		{
			hash = hash +  Calc.getHashSHA256_from_HexString(classArray.get(i));
		}
		byte[] h = Convert.hexStringToByteArray(Calc.getHashSHA256_from_HexString(hash));	
		byte[] k  = new BigInteger(256, new Random()).toByteArray();		
		Secp256k1 secp = new Secp256k1();
		BigInteger[] sig = secp.sig(h, Convert.hexStringToByteArray(txt_pw.getText()), k);
		String r = sig[0].toString(16);
		String s = sig[1].toString(16);
		String pubKey = Calc.getPublicKey(txt_pw.getText(), true);
		save(r,s,pubKey);
		
	}
	
	
	
	// speichert die Daten als JSON-Datei im hauptVerzeichnis.
	// r         = Signaturteil: r
	// s         = Signaturteil: s
	// pubKey    = PublicKey der Signatur.
	// classData = ist die plugIn.class selbst als HexString Codiert.
	private static void save(String r, String s, String pubKey) throws Exception
	{
		JSONObject jo = new JSONObject();
		jo.put("dateiID", 		dateiID);
		jo.put("pluginName", 	plugIn_Name.getText());
		jo.put("autor", 		txt_autor.getText());
		jo.put("date", 			getDate());
		jo.put("coreApp",		coreApp);
		jo.put("coreVersion",	txt_versionKP.getText());
		jo.put("pluginVersion", txt_version.getText());
		jo.put("packageName",	txt_packName.getText());
		jo.put("mainClassName",	txt_mClassName.getText());
		jo.put("r", r);
		jo.put("s", s);
		jo.put("pubKey", 		pubKey);
		JSONArray ja = new JSONArray(classArray);
		jo.put("classData",		ja);		
		BufferedWriter bw = new BufferedWriter(new FileWriter(plugIn_Name.getText()+".plugin"));
		bw.write(jo.toString(1));
		bw.close();		
	}
	
	
	
	// ------------------------------------------------------ Hilfsmethoden -----------------------------------------------
	
	// Hier werden alle Class-Datein die sich im Verzeichnis befinden geladen und als und in die globale Variable "classArray" gespeichert.
	// Unterordner werden einbezogen.
	// Dateien die keine Java-Klassen sind, werden ignoriert.
	private static void packClasses(File f) throws Exception
	{	
		for (File file : f.listFiles()) 
		{
			if (file.isDirectory()==false) 
			{
				InputStream stream = new FileInputStream(file);
				byte[] b = stream.readAllBytes();
				stream.close();	
				if(isClass(b))
				{
					txt_meldung.setForeground(Color.black);
					txt_meldung.setText(txt_meldung.getText()+file.toString()+"\n");  					
					classArray.add(Convert.byteArrayToHexString(b));				
				}
				else 
				{
					txt_meldung.setForeground(Color.black);
					txt_meldung.setText(txt_meldung.getText()+"Diese Datei ist keine Class: "+file.toString()+"\n"); 
				}
			} 
			else {packClasses(file);}
		}		
	}
	
	
	// Gibt das aktuelle Datum in der gewünschen Form zurück.
	private static String getDate()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");  
	    return formatter.format(new Date());  
	}
	
		
	// Gibt true zurück wenn es sich um eine Java-Klasse handelt (Magic = cafebabe)
	private static boolean isClass(byte[] b)
	{
		b = Arrays.copyOf(b, 4);
		byte[] b1 = Convert.hexStringToByteArray("cafebabe");
		return Arrays.equals(b, b1);
	}
}