package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.border.LineBorder;
import plugInAPI.RegPlugins;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JRadioButton;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Toolkit;
import javax.swing.JMenu;





/***************************************************************************************************************
*	Der KeyPass verwaltet deine Passwörter in einer verschlüsselten Datenbank									*
*	Die Verschlüsselung wird mit eigenen Crypto-Classen "ParanoidCrypt" realisiert. (Siehe ParanoidCrypt.png)	*
*	Dabei werden zwei anerkannte Verschlüsselungen, AES und Twofish hintereinander angewendet.					*
*	Ebenso ist ein BruteForce-Schutz durch eine Laufzeitverlängerung mit Scrypt realisiert.						*
*	PlugIn API ab Version 1.2.0 hinzugefügt.																	*
*	Achtung! 																									*
*	Die String Codierung muss bei allen String.getBytes("UTF-8") Methoden auf "UTF-8" stehen!					*
*	Sonnst kommt es zu Codierungsfehlern außerhalb von eclipse bei z.B: Umlauten "ä ö ü ..."					*
****************************************************************************************************************/







public class GUI extends JFrame 
{

	public static final String			progName		= "KeyPass";
	public static final String			version 		= "V1.3.1";
	public static final String			autor 			= "Mr. Maxwell";
	public static final String			pubKeySig		= "03291251a267e24ed362241cdaba7c953a52b295c81f92e7fd10df665a6b000441";	 // Der Public-Key für die Signaturprüfung von PlugIns (final)
	public static 		GUI 			frame;															// Frame dieser GUI
	public static 		JPanel 			pnl_hauptfeld 	= new JPanel();									// Haupt-Panel in der Mitte, in dem alle Daten und weiter Panels sind.
	public static		JPanel 			pnl_h_oben 		= new JPanel();									// Obere Teil des Hauptfeldes
	public static 		JPanel 			pnl_h_mitte 	= new JPanel();									// Mittlere Teil des Hauptfeldes
	public static		JPanel 			pnl_h_unten 	= new JPanel();									// Untere Teil des Hauptfeldes
	public static 		JComboBox 		combo_filename 	= new JComboBox();								// File-Name der Key-Datenbank
	public static		JLabel 			txt_id 			= new JLabel("-1");								// Nummer (ID) des Datenbankeintrages, Index im JSONArry
	public static		JLabel 			txt_date 		= new JLabel("Unbekanntes Datum");				// Datum der letzten Änderung
	public static		JLabel 			lbl_TOTP 		= new JLabel("TOTP Geheimschlüssel");			// TOTP Label
	public static		JLabel 			lbl_TOTP_len 	= new JLabel("TOTP Ausgabezeichen, regulär: 6");// TOTP Label Ausgabelänge
	public static 		JTextField 		txt_name 		= new JTextField();								// Name der Anwendung 	(Eingabefeld)
	public static 		JTextField 		txt_userName 	= new JTextField();								// UserName 			(Eingabefeld)
	public static 		JTextField		txt_url 		= new JTextField();								// Url		 			(Eingabefeld)
	public static		JTextField		txt_TOTP_Secret	= new JTextField();								// TOTP Geheimschlüssel	(Eingabefeld)
	public static		JTextField 		txt_TOTP_len	= new JTextField();								// TOTP Länge Ausgabe 	(Eingabefeld)
	public static 		JPasswordField 	txt_passwort 	= new JPasswordField();							// Passwort				(Eingabefeld)
	public static 		JTextArea 		txt_description = new JTextArea();								// Beschreibung         (Eingabefeld)
	public static 		JTextArea 		txt_meldung 	= new JTextArea();								// Unteres Meldungsfenster
	public static		JMenuItem 		btn_open 		= new JMenuItem(" Öffnen");						// Menü [Öffnen]
	public static		JMenuItem 		btn_new 		= new JMenuItem(" Neuer Eintrag");				// Menü [Neuer Eintrag]
	public static		JMenu 			btn_plugIns 	= new JMenu("  Plug In´s  ");					// Menü [PlugIns]
	public static		JButton 		btn_copy1 		= new JButton("Copy");							// [User Namen] in Zwischenablage speichern
	public static		JButton 		btn_copy2 		= new JButton("Copy");							// [Passwort] in Zwischenablage speichern
	public static		JButton 		btn_PWGenerator = new JButton("Passwort Generator");			// Passwortgenerator Öffnen	
	public static		JButton 		btn_saveChange 	= new JButton("Änderung Speichern");			// Geänderten Eintrag speichern
	public static 		JButton 		btn_saveNew 	= new JButton("Neuen Eintrag Speichern");		// Neuen Eintrag speichern
	public static		JButton 		btn_clear 		= new JButton("Löschen");						// Bestehenden Eintrag löschen
	public static		JButton 		btn_TOTP 		= new JButton("TOTP Einmal-Passwort ausgeben");	// TOPT Einmalpasswort
	public static		JRadioButton 	btn_edit 		= new JRadioButton("Ändern");					// Eintrag ändern 
	public static		JRadioButton 	btn_PWzeigen 	= new JRadioButton("Passwort Zeigen");			// Passwort anzeigen
	public static		Color 			color1 			= new Color(255, 235, 228); 					// Farbe hell rot  des Hintergrundes
	public static 		Color 			color2 			= new Color(100, 100, 100);						// Farbe hell grau aller Labels für die Textfelder	
	public static 		Color 			color3			= new Color(240, 240, 240);						// Formularfarbe gesperrt (grau)

	

		
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run()
			{
				try 
				{
					setup();
					frame = new GUI();
					frame.setVisible(true);
				} 
				catch (Exception e) 
				{		
					txt_meldung.setText(e.getMessage());
					e.printStackTrace();
				}	
			}
		});		
		RegPlugins.aktivate();	// Plugins werden hier geladen, außerhalb des GUI-Thread!
	}

	
	
	

	// Diese Methode starte als erstes befor die GUI startet. Alle Aufgaben bei Programstart 
	// - erstellt benötigte Resourcen, temp Ordner mit icons, plugIn Ordner etc.
	// Wenn diese Resourcen nicht vorhanden sind, werden sie neu erstllt.	
	// Im temp-Ordner werden die Icons gespeichert und zur laufzeit geladen. Der temp Ordner wird bei Programm-Ende wieder gelöscht.
	private static void setup() throws IOException 
	{
		File temp 	= new File("temp");
		File plugInsFolder 	= new File("plugins");
		temp.mkdir();
		plugInsFolder.mkdir();
		Icons.saveAsPNG(Icons.keyPNG, "temp\\key.png");		
	}
	
	
	
	public GUI() throws Exception 
	{		
		JPanel 		contentPane 	= new JPanel();
		JPanel 		panel_oben 		= new JPanel();
		JMenuBar 	menuBar 		= new JMenuBar();	
		JMenu 		btn_info 		= new JMenu(" Info ? ");
		JTextArea 	txt_info 		= new JTextArea();
		JLabel 		lbl_key 		= new JLabel(".key");
		JLabel 		lbl_database 	= new JLabel("Datenbank Name:  ");	
		JLabel 		lbl_id 			= new JLabel("No.:");	
		JLabel 		lbl_name 		= new JLabel("Name der Anwendung");
		JLabel 		lbl_date 		= new JLabel("Datum der letzten Änderung");
		JLabel 		lbl_userName 	= new JLabel("User Name");
		JLabel 		lbl_passwort 	= new JLabel("Passwort");
		JLabel 		lbl_url 		= new JLabel("URL");
		JLabel 		lbl_description = new JLabel("Beschreibung");
		JScrollPane sp_description 	= new JScrollPane();
	
		setTitle(progName+"     "+version);	
		setIconImage(Toolkit.getDefaultToolkit().getImage("temp\\key.png"));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(605, 485));
		setJMenuBar(menuBar);
		setBounds(100, 100, 609, 530);
		setContentPane(contentPane);	
		
		lbl_id			.setBounds(10, 2, 19, 14);
		lbl_name		.setBounds(10, 31, 305, 14);
		lbl_date		.setBounds(433, 31, 184, 14);
		lbl_userName	.setBounds(10, 77, 294, 20);
		lbl_passwort	.setBounds(314, 77, 260, 20);
		lbl_url			.setBounds(10, 156, 81, 22);
		lbl_TOTP		.setBounds(314, 180, 260, 14);
		lbl_TOTP_len	.setBounds(314, 214, 233, 14);
		sp_description	.setBounds(10, 210, 564, 92);
		txt_id			.setBounds(39, 2, 111, 14);
		txt_name		.setBounds(10, 46, 294, 20);
		txt_userName	.setBounds(10, 94, 294, 20);
		txt_passwort	.setBounds(314, 94, 260, 20);
		txt_url			.setBounds(46, 158, 528, 18);
		txt_TOTP_Secret	.setBounds(314, 194, 260, 18);
		txt_TOTP_len	.setBounds(555, 214, 19, 16);
		txt_date		.setBounds(433, 49, 141, 17);
		btn_copy1		.setBounds(10, 114, 39, 18);
		btn_copy2		.setBounds(314, 114, 39, 18);
		btn_PWGenerator	.setBounds(433, 135, 141, 18);
		btn_PWzeigen	.setBounds(433, 116, 141, 14);		
		btn_edit		.setBounds(6, 7, 81, 23);
		btn_clear		.setBounds(93, 7, 81, 22);
		btn_saveChange	.setBounds(392, 7, 181, 22);
		btn_saveNew		.setBounds(317, 7, 256, 22);	
		btn_TOTP		.setBounds(10, 195, 184, 20);

		pnl_h_oben		.setBackground(color3);
		lbl_id			.setForeground(color2);
		lbl_name		.setForeground(color2);
		lbl_date		.setForeground(color2);
		lbl_userName	.setForeground(color2);
		lbl_url			.setForeground(color2);
		lbl_passwort	.setForeground(color2);
		lbl_description	.setForeground(color2);
		lbl_TOTP		.setForeground(color2);
		lbl_TOTP_len	.setForeground(color2);
		txt_id			.setForeground(color2);
		txt_meldung		.setForeground(Color.RED);	
		txt_meldung		.setBackground(color1);
		contentPane		.setBackground(color1);
		panel_oben		.setBackground(color1);

		Font font1		= new Font("Arial", Font.PLAIN,11);			// Schriftart der Labels im Hauptfeld
		Font font2		= new Font("Bahnschrift", Font.PLAIN, 13);	// Schriftart für Textfelder
		
		combo_filename	.setFont(new Font("Arial", Font.PLAIN, 11));
		lbl_id			.setFont(font1);
		lbl_name		.setFont(font1);
		lbl_date		.setFont(font1);
		lbl_userName	.setFont(font1);
		lbl_passwort	.setFont(font1);
		lbl_url			.setFont(font1);
		lbl_description	.setFont(font1);
		lbl_TOTP		.setFont(font1);
		lbl_TOTP_len	.setFont(font1);
		txt_name		.setFont(font2);
		txt_userName	.setFont(font2);
		txt_passwort	.setFont(font2);
		txt_description	.setFont(font2);
		txt_url			.setFont(font1);
		txt_TOTP_Secret	.setFont(font2);
		txt_TOTP_len	.setFont(font2);
		btn_open		.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btn_new			.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btn_plugIns		.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btn_info		.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txt_info		.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btn_copy1		.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btn_copy2		.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btn_PWzeigen	.setFont(new Font("Tahoma", Font.PLAIN, 10));
		txt_meldung		.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btn_PWGenerator	.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btn_TOTP		.setFont(new Font("Tahoma", Font.PLAIN, 10));


		contentPane	 	.setBorder(new EmptyBorder(5, 5, 5, 5));
		btn_open	 	.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btn_new		 	.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btn_plugIns		.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btn_info		.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnl_hauptfeld	.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnl_h_mitte		.setBorder(new LineBorder(new Color(240, 240, 240), 7));
		sp_description	.setBorder(new LineBorder(SystemColor.inactiveCaption));
		txt_meldung		.setBorder(new LineBorder(color1, 7));

		contentPane	 	.setLayout(new BorderLayout(0, 0));
		panel_oben		.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnl_hauptfeld	.setLayout(new BorderLayout(0, 0));
		pnl_h_oben		.setLayout(null);
		pnl_h_mitte		.setLayout(new BorderLayout(0, 0));
		pnl_h_unten		.setLayout(null);
			
		btn_copy1		.setMargin(new Insets(0, 0, 0, 0));
		btn_copy2		.setMargin(new Insets(0, 0, 0, 0));
		btn_PWGenerator	.setMargin(new Insets(0, 0, 0, 0));
		btn_PWzeigen	.setMargin(new Insets(0, 0, 0, 0));
		btn_clear		.setMargin(new Insets(0, 0, 0, 0));
		btn_saveChange	.setMargin(new Insets(0, 0, 0, 0));
		btn_saveNew		.setMargin(new Insets(0, 0, 0, 0));
		btn_TOTP		.setMargin(new Insets(0, 0, 0, 0));

		panel_oben		.setPreferredSize(new Dimension(10, 40));
		pnl_h_oben		.setPreferredSize(new Dimension(10, 230));
		pnl_h_unten		.setPreferredSize(new Dimension(10, 35));
		combo_filename	.setPreferredSize(new Dimension(250, 17));
		
		btn_open		.setHorizontalAlignment(SwingConstants.LEFT);
		btn_new			.setHorizontalAlignment(SwingConstants.LEFT);
		btn_plugIns		.setHorizontalAlignment(SwingConstants.LEFT);
		btn_PWzeigen	.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_database	.setHorizontalAlignment(SwingConstants.RIGHT);	
		lbl_TOTP_len	.setHorizontalAlignment(SwingConstants.RIGHT);
		
		Component horizontalStrut = Box.createHorizontalStrut(140);

		txt_name		.setColumns(10);	
		txt_userName	.setColumns(10);
		txt_passwort	.setColumns(10);
		txt_url			.setColumns(10);
		txt_TOTP_Secret	.setColumns(10);
		txt_TOTP_len	.setColumns(10);
		
		txt_description	.setEditable(false);
		combo_filename	.setEditable(true);
		txt_meldung		.setEditable(false);
		txt_info		.setEditable(false);
		sp_description	.setViewportView(txt_description);	
		pnl_hauptfeld	.setVisible(false);
		combo_filename	.setModel(new DefaultComboBoxModel(getKeyFilesName()));	
		txt_info		.setText(progName+"                 Java KeyPass\r\nVersion:                "+version+"\r\nAuthor:                 "+autor+"\r\nWeb:                     https://github.com/MrMaxweII\r\nE-Mail:                  Maxwell-KSP@gmx.de\r\nPlugIn PubKey:     "+pubKeySig+"\r\nplease donate!\nBTC address:        12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB");
	
		menuBar			.add(btn_open);		
		menuBar			.add(btn_new);
		menuBar			.add(btn_plugIns);
		menuBar			.add(btn_info);
		btn_info		.add(txt_info);
		menuBar			.add(horizontalStrut);
		contentPane		.add(panel_oben, BorderLayout.NORTH);		
		pnl_h_oben		.add(lbl_name);		
		pnl_h_oben		.add(lbl_date);	
		pnl_h_oben		.add(txt_name);
		pnl_h_oben		.add(lbl_userName);	
		pnl_h_oben		.add(lbl_passwort);
		pnl_h_oben		.add(lbl_url);	
		pnl_h_oben		.add(txt_userName);	
		pnl_h_oben		.add(txt_passwort);	
		pnl_h_oben		.add(txt_url);
		pnl_h_oben		.add(btn_copy1);
		pnl_h_oben		.add(btn_copy2);	
		pnl_h_oben		.add(btn_PWGenerator);
		pnl_h_oben		.add(txt_date);	
		pnl_h_oben		.add(btn_PWzeigen);
		pnl_h_oben		.add(lbl_id);
		pnl_h_oben		.add(txt_id);
		pnl_h_oben		.add(btn_TOTP);
		pnl_h_oben		.add(txt_TOTP_Secret);
		pnl_h_oben		.add(lbl_TOTP);
		pnl_h_oben		.add(txt_TOTP_len);
		pnl_h_oben		.add(lbl_TOTP_len);
		contentPane		.add(pnl_hauptfeld, BorderLayout.CENTER);
		pnl_hauptfeld	.add(pnl_h_oben, BorderLayout.NORTH);				
		pnl_hauptfeld	.add(pnl_h_mitte, BorderLayout.CENTER);
		pnl_hauptfeld	.add(pnl_h_unten, BorderLayout.SOUTH);
		pnl_h_mitte		.add(sp_description, BorderLayout.CENTER);
		pnl_h_mitte		.add(lbl_description, BorderLayout.NORTH);
		pnl_h_unten		.add(btn_edit);
		pnl_h_unten		.add(btn_clear);
		pnl_h_unten		.add(btn_saveChange);
		pnl_h_unten		.add(btn_saveNew);
		contentPane		.add(txt_meldung, BorderLayout.SOUTH);		
		panel_oben		.add(lbl_database);
		panel_oben		.add(combo_filename);	
		panel_oben		.add(lbl_key);				
		
		GUI_Action.run();	
		
	
		
		
	// Close Button wird abgefangen, Ende des Programmes.
	// Alle Aufgaben die am Ende des Progr. durchgeführt werden müssen
	addWindowListener(new java.awt.event.WindowAdapter() 
	{
	    @Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
	    {
	    	GUI_Action.deleteClipboard();										// Zwischenablage wird gelöscht
	    	File keyPng = new File("temp\\key.png"); keyPng.delete();			// Temp Daten werden gelöscht
	    	File temp 	= new File("temp"); 		 temp.deleteOnExit();		// Temp Ordner wird gelöscht	    	
	    	System.exit(getDefaultCloseOperation());
	    }
	});			
}	
	
	
	
	
	
// ---------------------------------------- Hilfsmethoden --------------------------------------------------------//
	
	

	// Gibt alle Dateinamen die mit ".key" enden im aktuellen Verzeichnis zurück.
	private static String[] getKeyFilesName()
	{
		File f = new File(".");
		String[] str = f.list();			
		ArrayList<String> out = new ArrayList<String>();	
		for (int i=0;i<str.length;i++) 
		{		
			if(str[i].indexOf(".")>-1)
			{
				String end = str[i].substring(str[i].indexOf("."));
				if(end.equals(".key")) out.add(str[i].substring(0, str[i].indexOf(".")));
			}	
		}	
		return (String[]) out.toArray(new String[out.size()]);
	}	
}