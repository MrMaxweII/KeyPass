package plugIn_2FA;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.*;
import BTClib3001.Calc;
import BTClib3001.Convert;
import BTClib3001.Secp256k1;
import Crypt.CryptDialog;
import plugInAPI.Plugins;
import GUI.GUI;



/****************************************************************************************************************
* Mr. Maxwell																				15.12.2025			*
* 2FA-QR PlugIn für den KeyPass																					*
* Beschreibung: "Info PlugIn.txt"																				*
*																												*
*	Benötigte Klassen: QrCapture, QRCodeZXING																	*
*	Benötiget Bibliotheken:																						*
*	- webcam-capture-smal-0.3.12.jar	Kamera-API´s (wurde von mir angepasst: weitere libs, daraus entfernt)	*
*	- bridj-0.7.0.jar					Wird von webcam-Capture selbst benötigt									*
*	- zxing.jar							QR-Code API  															*
*	- json.jar																									*
*****************************************************************************************************************/



public class TwoFaQR implements Plugins
{
	
	final static 	String 			name 	= "TwoFaQR";					// Name des PlugIns
	final static 	String 			version = "1.0.0";						// PlugIn version
	private static 	JMenuItem 		menu1;									// Das PlugIn Menü-Item in der Menü-Zeile oben
    private static 	JRadioButton 	btn_1 		= new JRadioButton(""); 	// Schaltet das PlugIn Ein oder Aus
    private static 	JTextPane 		txt_meldung = new JTextPane();			// Fehlermeldungen etc.
    private static  JSONObject		joPlugIn;								// Das JSON-Object des gesamten PlugIns wird hier geladen um die Settings darin zu laden/speichern.
	private static 	BufferedImage 	qrCode;									// Der QR-Code als Bild
	private static 	String 			p1; 									// Punkt1 auf der ECDSA-Kurve der als QR-Code von hier zum Handy gesendet wird.
	private static 	String			k;										// Die Geheime Zufallszahl "k" für den Diffihelman Schlüsselaustausch.
    private static 	JFrame 			mainFrame;								// Hauptframe des PlugIn´s		
    private static  CryptDialog		cd;										// Das CryptDialog Opject aus dem KeyPass wird hier her geholt.
    private static 	JProgressBar 	progressBar = new JProgressBar();		// Der animierate Wartebalken
    private static	Color			color1		= new Color(255, 248, 220);	// PlugIn Hintergrundfarbe gelb	
    private static	Color			qrColor;								// Farbe des QR-Codes wird dynamisch geändert. Um die Season zu erkennen	
    private	static  int				activPwField=0;							// Das aktive PasswortFeld (gelb markiert). kann 1,2,3 oder 0 sein.
    
	

    
	@Override
	public String getPluginName() 
	{	
		return name; 
	}

	
	
/** Haupt Start-Methode. Dies ist der Einstiegspungt des PlugIn´s, ähnlich wie eine Java-Main-Methode
	Der KeyPass ruft diese Methode auf, wenn er das PlugIn lädt.	*/
	@Override
	public void start()
	{
		try {loadSettings();} 
		catch (Exception e1) 
		{
			txt_meldung.setText(e1.getMessage());
			e1.printStackTrace();
		}
		
		
			
		// Dies ist ein selbst implementierter Listener, der auf das Öffnen des CryptDialog-Feld wartet und reagiert. 
		Thread t = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				try {Thread.sleep(2000);} 									// nötige Startverzögertung, es muss auf GUI.frame gewartet werden.
				catch (InterruptedException e1) {e1.printStackTrace();}
				int hashCode = 0;
				while (GUI.frame.isVisible()) 
				{
					if (CryptDialog.getCryptDialog() != null && btn_1.isSelected()) 
					{
						cd = CryptDialog.getCryptDialog();
						if (hashCode != cd.hashCode()) 
						{
							hashCode = cd.hashCode();
							if (mainFrame == null || mainFrame.isVisible() == false) 
							{
								createP1();
								frame(cd.getX() + 486, cd.getY());
							}
						}
						if (cd.isVisible() == false) mainFrame.dispose();
						markActivPwField();
					}
					try {Thread.sleep(1000);} 
					catch (InterruptedException e1) {e1.printStackTrace();}					
				}
			}
		});
		t.start();
		
		
		// ----------------------------- Das MenüItem wird erstellt ------------------------------- //
		setMenuItem(getPluginName());							
		menu1.addActionListener(new ActionListener() 			// Wenn auf Das MenüItem geklickt wird
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				GUI.frame.setEnabled(false);
				setMenuFrame();
				txt_meldung.setText("");
			}
		});		
	}

	
	
	
	
	
	
// Haupt-PlugIn Frame wird erstellt. Dieses Fenster öffnet sich immer, wenn das Passwort-Eingabefeld erscheint.	
	/** @wbp.parser.entryPoint */
	private static void frame(int x, int y)
		{		
						mainFrame 	= new JFrame();							// Hauptframe 		
			JPanel 		panel_0 	= new JPanel();							// Haupt Pannel in der Mitte
			JTextPane 	txt_info 	= new JTextPane();						// Info und Beschreibungs Text oben
			JLabel 		lbl_qrCode 	= new JLabel(new ImageIcon(qrCode));	// Der QR-Code 	
			JButton 	btn_scan 	= new JButton();						// Der Scan Button
			JTextField 	txt_outHex 	= new JTextField();						// Ausgabe des QR-Codes als Hexa-Zeichen 		
			JTextField 	txt_inHex 	= new JTextField();

			Toolkit.getDefaultToolkit().setDynamicLayout(false);			// Layout Darstellung muss geändert werden, damit die dynamische Größenänderung des QrCodes problemlos läuft.
			mainFrame.setTitle(name);
			mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("temp/key.png"));
			mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
			mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
			mainFrame.getContentPane().add(panel_0, BorderLayout.CENTER);
			mainFrame.getContentPane().add(txt_meldung, BorderLayout.SOUTH);		
			mainFrame.setBounds(x,y,0,0);
			mainFrame.setMinimumSize(new Dimension(200,300));
			mainFrame.setVisible(true);
			panel_0		.setLayout(new BoxLayout(panel_0, BoxLayout.Y_AXIS));
			lbl_qrCode	.setAlignmentX(Component.CENTER_ALIGNMENT);
			btn_scan	.setAlignmentX(Component.CENTER_ALIGNMENT);
			txt_outHex	.setAlignmentX(Component.CENTER_ALIGNMENT);
			txt_outHex	.setHorizontalAlignment(SwingConstants.CENTER);
			lbl_qrCode	.setHorizontalAlignment(SwingConstants.CENTER);
			btn_scan	.setHorizontalTextPosition(SwingConstants.CENTER);
			progressBar	.setPreferredSize(new Dimension(146, 5));
			txt_inHex	.setFont(new Font("Dialog", Font.PLAIN, 11));
			txt_outHex	.setFont(new Font("Arial", Font.PLAIN, 10));			
			txt_info	.setFont(new Font("Arial", Font.PLAIN, 11));
			txt_inHex	.setForeground(new Color(0, 0, 0));
			txt_meldung	.setForeground(new Color(204, 0, 51));
			txt_outHex	.setForeground(SystemColor.textInactiveText);
			progressBar	.setForeground(new Color(128, 128, 128));
			panel_0		.setBackground(Color.WHITE);
			txt_info	.setBackground(color1);
			txt_outHex	.setBackground(new Color(255, 255, 255));
			txt_meldung	.setBackground(color1);
			progressBar	.setBackground(new Color(255, 255, 255));
			txt_outHex	.setBorder(new EmptyBorder(0, 4, 10, 4));
			txt_info	.setBorder(new EmptyBorder(4, 4, 4, 0));
			txt_meldung	.setBorder(new EmptyBorder(2, 5, 4, 5));
			txt_inHex	.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Bei Scan-Problemen, trage hier die Zahlen vom Handy ein ", TitledBorder.CENTER, TitledBorder.TOP, new Font("Dialog", Font.PLAIN, 9), new Color(165, 42, 42)));
			progressBar	.setBorderPainted(false);
			txt_outHex	.setEditable(false);		
			txt_info	.setEditable(false);
			txt_meldung	.setEditable(false);
			txt_inHex	.setVisible(false);
			txt_inHex	.setColumns(10);
			txt_info	.setText("Scanne mit der KeyPass-App auf deinem Handy diesen QR-Code.\r\nDie App zeigt dann einen neuen QR-Code an.\r\nWenn die Handy-App den neuen QR-Code anzeigt, \r\ndrücke den Butten unten und Scanne mit der PC-Kamera den QR-Code vom Handy.\r\nFalls Scannen nicht geht, kannst du die Zahlen unter dem QR-Code manuell eingeben.\r\nKlicke auf diese Zahlen um das Eingabefeld zu öffnen.");
			btn_scan	.setText("QR-Code vom Handy Scannen");
			txt_outHex	.setText(p1);							
			txt_meldung.setText("");
			panel_0.add(txt_info);
			panel_0.add(lbl_qrCode);
			panel_0.add(txt_outHex);
			panel_0.add(btn_scan);
			panel_0.add(progressBar);
			panel_0.add(txt_inHex);
			mainFrame.pack();
		
			
			// Wenn die Größe des Hauptfensters geändet wird, wird auch die Größe des QrCodes angepasst.
			// Die Größenänderung des QR-Codes verursacht aber Performance Probleme,
			// Um diese Probleme zu lösen wird folgender Code oben hinzugefügt: Toolkit.getDefaultToolkit().setDynamicLayout(false);
			mainFrame.addComponentListener(new ComponentAdapter() 
			{
				@Override
				public void componentResized(ComponentEvent e) 
				{
					int h = panel_0.getHeight();
					qrCode 	= QRCodeZXING.writeQRCode(p1, Color.WHITE, qrColor,h-175, h-175);	
					lbl_qrCode.setIcon(new ImageIcon(qrCode));	
				}
			});
		
		
			// Wenn in die Zahlen geklickt wird, öffnet sich das zusätzliche Eingabefenster
			txt_outHex.addMouseListener(new MouseAdapter() 
			{
				@Override
				public void mousePressed(MouseEvent e) 
				{
					txt_inHex.setVisible(true);			
					lbl_qrCode.setIcon(new ImageIcon(qrCode));	
					mainFrame.pack();
				}
			});
				
			
			// Beim Enter-Drücken in das zusätzliche Eingabefenster
			txt_inHex.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{			
					Thread t = new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							try 
							{
								String key = createKey(txt_inHex.getText());
								pushKeyToKeyPass(key);
							} 
							catch (Exception e1) 
							{
								progressBar.setIndeterminate(false);
								txt_meldung.setText(e1.getMessage());
								mainFrame.pack();
								e1.printStackTrace();
							}
						}
					});
					t.start();
				}
			});
		
		
			// Wenn die Scan-Taste gedrückt wird
			btn_scan.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{	
					Thread t = new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{														
							try
							{
								QrCapture qr = new QrCapture(mainFrame.getX()+50, mainFrame.getY()+80);							
								String p2 = qr.getResult();
								qr.close();								
								if(p2.equals("")) throw new IOException("Benutzer Abbruch");								
								String key = createKey(p2);	
								pushKeyToKeyPass(key);
							}
							catch(Exception ex) 
							{
								progressBar.setIndeterminate(false);
								txt_inHex.setVisible(true);				
								lbl_qrCode.setIcon(new ImageIcon(qrCode));	
								mainFrame.setEnabled(true);
								txt_meldung.setText(ex.getMessage());
								mainFrame.pack();
								ex.printStackTrace();
							};							
						}
					});
					t.start();
				}
			});		
		}
	
	

	// Das PlugIn Menü mit der Beschreibung und den Settings in der Menü-Leiste oben
	private static void setMenuFrame()
	{
		 JFrame 	frame 		= new JFrame();
		 JTextPane 	txt_info 	= new JTextPane();
	     JPanel 	panel 		= new JPanel();
	     JLabel 	lbl_btn1 	= new JLabel("PlugIn Ein/Auschalten");
		 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		 frame.setLocationRelativeTo(GUI.btn_plugIns);
		 frame.setBounds(frame.getX()-48, frame.getY()+13, 500, 440);
		 frame.setVisible(true); 
	     frame.setTitle(name+"   Version: "+version);     
		 frame.setIconImage(Toolkit.getDefaultToolkit().getImage("temp\\key.png"));
	     panel.setLayout(null);
	     txt_info.setBackground(new Color(255, 248, 220));
	     txt_info.setBorder(new EmptyBorder(3, 5, 3, 2));
	     txt_info.setText("Dieses Plug-In erweitert den KeyPass um eine 2FA-QR Funktion.\r\nDie Zwei-Faktor-Authentifizierung wird per QR-Code durchgeführt.\r\n\r\nVoraussetzung:\r\n- Auf dem Handy muss sich die 2FA-QR Code App befinden und konfiguriert sein\r\n- Ein Kamera muss angeschlossen sein\r\n\r\nFunktion:\r\n- Bei der Eingabe des Master-Passwortes wird nun ein QR-Code angezeigt\r\n- Dieser QR-Code muss auf dem Handy mit der 2FA-App gescannt werden\r\n- Die App zeigt nach der richtigen Handhabung einen neuen QR-Code an\r\n- Dieser QR-Code muss nun mit der Kamera am PC gescannt werden.\r\n- Der MasterKey wird automatisch in das Passwort-Feld eingetragen.");
	     txt_info.setEditable(false);
	     txt_meldung.setBorder(new EmptyBorder(2, 5, 4, 2));
	     txt_meldung.setBackground(SystemColor.control);
	     txt_meldung.setEditable(false);
	     txt_meldung.setForeground(Color.red);
	     btn_1.setBounds(10, 35, 298, 23);
	     lbl_btn1.setBounds(31, 11, 228, 23);    
	     frame.getContentPane().add(txt_info, BorderLayout.NORTH);  
	     frame.getContentPane().add(txt_meldung, BorderLayout.SOUTH);
	     frame.getContentPane().add(panel, BorderLayout.CENTER);
	     panel.add(btn_1);
	     panel.add(lbl_btn1);  
	     
	     if(btn_1.isSelected()) { btn_1.setText("PlugIn ist Eingeschaltet"); btn_1.setForeground(new Color(0,128,0));}
	     else	 				{ btn_1.setText("PlugIn ist Ausgeschaltet"); btn_1.setForeground(Color.red);}	
	     
	     btn_1.addActionListener(new ActionListener() 
	     {
		     	public void actionPerformed(ActionEvent e) 
		     	{	     		
		    		if(btn_1.isSelected()) 	{ btn_1.setText("PlugIn ist Eingeschaltet"); btn_1.setForeground(new Color(0,128,0)); 	txt_meldung.setText("PlugIn wurde Eingeschaltet");}
		    		else	 				{ btn_1.setText("PlugIn ist Ausgeschaltet"); btn_1.setForeground(Color.red);   			txt_meldung.setText("PlugIn wurde Ausgeschaltet");}	
		    		try 
		    		{
		    			joPlugIn.put("activ", btn_1.isSelected());
		    			saveSettings();
					} 
		    		catch (Exception e1) 
		    		{
						txt_meldung.setText(e1.getMessage());
		    			e1.printStackTrace();
					}
		     	}
		 });
	     
	     // Wenn das Menü geschlossen wird
	     frame.addWindowListener(new WindowAdapter() 
	     {
	         @Override
	         public void windowClosed(WindowEvent windowEvent) 
	         {
	             GUI.frame.setEnabled(true);
	         }
	     }); 
	}
	
	
	
// ------------------------------------------------------------- Private Hilfs-Methoden --------------------------------------------------------------------------------------------	
	
	
	// Erstellt das MenüIcon in der oberen Menüleiste für das PlugIn
	private static void setMenuItem(String name)
	{
		menu1 = new JMenuItem(name);
		GUI.btn_plugIns.add(menu1);	
	}
	
	
	// Lädt die Settings aus der PlugIn datei (JSON-Format) 
	// Sind die Settings noch nicht enthalten, werden sie erstellt und die default Settings eingestellt.
	private static void loadSettings() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("plugins/"+name+".plugin"));
		String str = "";
		while(br.ready())	str += br.readLine()+"\n";			
		br.close(); 		
		joPlugIn = new JSONObject(str);		
		try
		{
			btn_1.setSelected(joPlugIn.getBoolean("activ"));
		}
		catch (Exception e)
		{
			joPlugIn.put("activ", true);
			btn_1.setSelected(true);
			saveSettings();
		}	
	}
	
	
	// Speichert die Settings in die PlugIn datei (JSON-Format) 
	private static void saveSettings() throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter("plugins/"+name+".plugin"));
		bw.write(joPlugIn.toString(1));
		bw.close();
	}
		
	
	// Generiert den Punkt P1 und inizialisiert die Variablen k, p1 und erzeugt den QR-Code.
	private static void createP1()
	{
		Random random = ThreadLocalRandom.current();
		byte[] r = new byte[32]; 
		random.nextBytes(r);
		k = Calc.getHashSHA256_from_HexString(Convert.byteArrayToHexString(r));
		p1 		= Calc.getPublicKey(k, true);				
		qrColor = createQrColor(p1);		
		qrCode 	= QRCodeZXING.writeQRCode(p1, Color.WHITE, qrColor ,250, 250);	
	}
	
	
	// erstellt die QR-Farbe aus dem Punkt P1.
	// Die QR-Code Farbe ist zur Wiedererkennung der Season wichtig.
	// Maximum Color = (165,165,165). So kann der QR-Code gerade noch erkannt werden.
	private static Color createQrColor(String p1)
	{
		final int max = 165;
		int r = Integer.valueOf(p1.substring(2,4),16);
		int g = Integer.valueOf(p1.substring(4,6),16);
		int b = Integer.valueOf(p1.substring(6,8),16);	
		r = (r*max)/255;
		g = (g*max)/255;
		b = (b*max)/255;		
		return new Color(r,g,b);
	}
	
	
	
	// Generiert den MasterKey aus dem Punkt P2 und tragt ihn in Das Eingabefeld des CryptDialogs ein.
	// Hier wird auch geprüft, ob P2 aus dem aktuellem P1 erzeugt wurde.
	// Dafür wurden hinten an P2 8Byte des P1´s angehänt. Diese 8 Byte werden nun hier mit dem originalem P1 verglichen.
	private static String createKey(String p2) throws Exception
	{
		if(p2.length()!=74) throw new Exception("Der gescannte QR-Code entspricht nicht dem richtigem Format!\nString-Länge ist: "+p2.length()+"\nDie richtige Länge muss aber: 74 Zeichen betragen!");
		if((p2.subSequence(0,2).equals("02") || p2.subSequence(0,2).equals("03"))==false) throw new Exception("Der gescannte QR-Code entspricht nicht dem richtigem Format!\nEr muss mit 02 oder 03 beginnen!");	
		String p1CheckSum = p2.substring(66,74);
		p2 = p2.substring(0,66);
		if(p1.substring(0,8).equals(p1CheckSum)==false) throw new Exception("Der gescannte QR-Code ist veraltet, er entpricht nicht der aktuellen Saison!\nHinweis zu diesem Fehler:\n"
				+ "Jedes mal wenn am KeyPass am PC der Passwort-Dialog geöffnet wird, wird ein neuer QR-Code  mit einer neuem Farbe erstellt.\n"
				+ "Wird dieser QR-Code mit dem Handy gescannt, so wird am Handy ein weiterer QR-Code mit der selben Farbe angezeigt, der dann wieder am PC gescannt werden muss.\n"
				+ "Hierbei müssen die Farben immer übereinstimmen!\n"
				+ "Wenn die Farben der QR-Codes nicht übereinstimmen, kommt es zu diesem Fehler, weil versucht wird, einen QR-Code vom Handy auf dem PC zu scannen der veraltet ist.\n"
				+ "In dem Fall muss der QR-Code am PC mit dem Handy neu gescannt werden! Danach wird auch am Handy wieder ein neuer QR-Code mit der richtigen Farbe angezeigt.");	
		progressBar	.setIndeterminate(true);
		BigInteger[] p =  Secp256k1.deComp(p2);
		BigInteger[] erg = Secp256k1.div(p, new BigInteger(k,16));
		progressBar	.setIndeterminate(false);		
		String out=  erg[0].toString(16);	
		if(out.equals("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798")) throw new Exception("Der erzeugte Schlüssel ist ungültig, da er dem EC-Generatorpunkt entspricht.\nEs muss der QR-Code vom Handy gescannt werden!");
		return out;
	}
	
	
	
	// Entscheidet welches Password-Feld aktiviert wird markiert es gelb und setzt die Variable "activPwField" darauf.
	// Das aktive PW-Feld ist das Feld, in das diese PlugIn das Passwort schreibt. (Es gibt 3)
	private static void markActivPwField()
	{
		if(mainFrame == null || mainFrame.isVisible() == false)
		{
			cd.get_txt_pw1().setBackground(Color.white);
			cd.get_txt_pw2().setBackground(Color.white); 
			cd.get_txt_pw3().setBackground(Color.white); 
			activPwField=0;
			return;
		}
		if(cd.get_txt_pw1().isVisible() && cd.get_txt_pw1().getPassword().length==0)	// Wenn das erste Eingabefeld sichtbar und leer ist
		{
			cd.get_txt_pw1().setBackground(color1);
			cd.get_txt_pw2().setBackground(Color.white); 
			cd.get_txt_pw3().setBackground(Color.white); 
			activPwField=1;
			return;
		}
			
		if(cd.get_txt_pw2().isVisible() && cd.get_txt_pw2().getPassword().length==0)	// Wenn das zweite Eingabefeld sichtbar und leer ist
		{
			cd.get_txt_pw1().setBackground(Color.white);
			cd.get_txt_pw2().setBackground(color1); 
			cd.get_txt_pw3().setBackground(Color.white);
			activPwField=2;
			return;
		}		
		
		if(cd.get_txt_pw3().isVisible() && cd.get_txt_pw3().getPassword().length==0)	// Wenn das zweite Eingabefeld sichtbar und leer ist
		{
			cd.get_txt_pw1().setBackground(Color.white);
			cd.get_txt_pw2().setBackground(Color.white); 
			cd.get_txt_pw3().setBackground(color1); 
			activPwField=3;
			return;
		}
		cd.get_txt_pw1().setBackground(Color.white);
		cd.get_txt_pw2().setBackground(Color.white); 
		cd.get_txt_pw3().setBackground(Color.white); 
		activPwField=0;
	}
	
	
	
	// Trägt den MasterKey in die Eingabefelder des KeyPass ein
	// Mehrere Fälle werden unterschiedlich behandelt
	private static void pushKeyToKeyPass(String key) throws Exception
	{	
		switch(activPwField)
		{
			case 1 : cd.get_txt_pw1().setText(key); break;
			case 2 : cd.get_txt_pw2().setText(key); break;
			case 3 : cd.get_txt_pw3().setText(key); break;
			case 0 : throw new Exception("Alle Felder sind belegt.\nMindestens ein Eingabe-Feld muss leer sein, um dort den Schlüssen einzutragen!\nBelegte Eingabe-Felder werden nicht überschrieben.");
		}	
		if(cd.getProfil().equals("decrypt")) cd.get_btn_ok().doClick();		
	}

	
}