package PWGenerator;
import GUI.GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import javax.swing.event.ChangeEvent;



/***************************************************************************************************************************
*		Mr.Maxwell                              Version 1.0											15.05.2022				*
*		Der PW-Generator generiert Passwörter aus einem Zufallsgenerator + der Mausbewegung beim Zeichnen eines Bildes.		*
*		Es werden Methoden zur Verfügung gestellt, Passwörter in verschiedensten Formaten und Längen auszugeben.			*	
*																															*
*		Dieser Passwort-Generator ist ein unabhängig und selbstständig lauffähiges JAVA Programm.							*
*		Kann durch die MAIN-Methode gestartet werden, oder als Erweiterung in Übergeordnete Programme integriert werden.	*
*		Diese Klasse ist eine Erweiterung von JFrame und kann grundsätzlich auch so angewendet werden.						*
*		Es muss über den Konstruktor ein Object erzeugt werden. Dies Zeigt dann die GUI vom PW-Generator an.				*
*		In dieser Version gibt es noch ein Problem mit der automatischen Steuerung des Start/Stop Buttons.					*
*		Die Steuerung von würde in der Form so, schwere Bugs verursachen, und ist daher hier deaktiviert.					*
****************************************************************************************************************************/




public class PWGenerator extends JFrame 
{

	// Diese MAIN-Methode ist nur für Tests dieser Klasse hier implementiert.
//		public static void main(String[] args)
//		{	
//			PWGenerator pwGen = new PWGenerator(100,100);
//			pwGen.setVisible(true);		
//			pwGen.start();
//		}
	

	public  JToggleButton 		btn_new 	= new JToggleButton("Stop");										// Startet und stoppt das Malen mit der Maus
	public  JLabel 				lbl_pwLen 	= new JLabel("Paswort Länge:");									// Label "Passwort Länge"
	public  JFormattedTextField txt_pwLen 	= new JFormattedTextField();									// Länge das Passwortes wird hier festgelegt
	public  JTextField 			txt_white 	= new JTextField("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyzÄÖÜäüö");	// Nur diese Zeichen erlauben
	public  JTextField 			txt_black 	= new JTextField("");											// Zeichen verbieten
	public  JLabel 				lbl_white 	= new JLabel("Zeichensatz:");									// Nur diese Zeichen erlauben Button
	public  JCheckBox 			btn_black 	= new JCheckBox("Daraus Verboten:");							// Zeichen verbieten Button
	public  JTextField 			txt_pw		= new JTextField();												// Das fertige generierte Passwort (Output)
	public  JButton 			btn_ok 		= new JButton("Passwort Übernehmen");							// "Fertig" Button der das Passwort der übergeordneten Anwendung übergibt.
	public  JPanel 				pnl_unten 	= new JPanel();													// Unteres Panel (zum Ausblenden hier)




// ------------------------------------------------- Konstruktor der GUI -------------------------------------------------------------------------
	public PWGenerator(int x, int y)
	{
		RandomJLabel 	rl 				= new RandomJLabel();
		JPanel 			contentPane 	= new JPanel();
		JPanel 			pnl_oben 		= new JPanel();	
		JPanel 			pnl_mitte 		= new JPanel();
		JLabel 			description 	= new JLabel("Male mit der Maus ein bisschen!          ");
	
		setTitle("Passwort Generator");
		setBounds(x, y, 579, 430);
		setContentPane(contentPane);
		
		// Formatter für die Passwortlänge
		NumberFormatter n = new NumberFormatter();
		n.setMinimum(6);
		n.setMaximum(64);		
		txt_pwLen = new JFormattedTextField(n);
		txt_pwLen.setText("8");
		
		
		rl			.setBounds(0, 0, 563, 364);
		lbl_pwLen	.setBounds(15, 190, 105, 14);
		txt_pwLen	.setBounds(130, 190, 25, 20);	
		lbl_white	.setBounds(32, 220, 104, 23);
		btn_black	.setBounds(10, 250, 119, 23);
		txt_white	.setBounds(130, 220, 461, 20);
		txt_black	.setBounds(130, 250, 461, 20);	
		txt_black	.setForeground(Color.LIGHT_GRAY);
		
		Font font1 = new Font("Arial", Font.PLAIN, 11);  		// Kleine Schriftart für Labels und Buttons
		Font font2 = new Font("Bahnschrift", Font.PLAIN, 11);  	// Schriftart für die Erlaubten und verbotenen Zeichen.	
		
		lbl_pwLen	.setFont(font1);	
		btn_new		.setFont(font1);
		btn_black	.setFont(font1);
		lbl_white	.setFont(font1);
		btn_ok		.setFont(font1);
		txt_black	.setFont(font2);
		txt_white	.setFont(font2);
		description	.setFont(new Font("Arial", Font.PLAIN, 12));
		txt_pw		.setFont(new Font("Bahnschrift", Font.PLAIN, 16));
		txt_pw		.setBorder(new EmptyBorder(10, 10, 10, 10));

		contentPane	.setLayout(new BorderLayout(0, 0));	
		pnl_mitte	.setLayout(null);
		pnl_unten	.setLayout(new BoxLayout(pnl_unten, BoxLayout.Y_AXIS));
		btn_new		.setMargin(new Insets(0, 10, 0, 10));

		txt_pw		.setEditable(false);
		
		btn_ok		.setAlignmentX(Component.CENTER_ALIGNMENT);
		txt_pw		.setHorizontalAlignment(SwingConstants.CENTER);
				
		Component strut1 = Box.createVerticalStrut(20);
		Component strut2 = Box.createVerticalStrut(20);
		strut2.setPreferredSize(new Dimension(0, 8));
		strut1.setPreferredSize(new Dimension(0, 8));
		
		contentPane	.add(pnl_oben, BorderLayout.NORTH);
		pnl_oben	.add(description);
		pnl_oben	.add(btn_new);	
		contentPane	.add(pnl_mitte, BorderLayout.CENTER);
		pnl_mitte	.add(lbl_pwLen);	
		pnl_mitte	.add(btn_black);	
		pnl_mitte	.add(lbl_white);	
		pnl_mitte	.add(txt_pwLen);	
		pnl_mitte	.add(txt_black);
		pnl_mitte	.add(txt_white);
		pnl_mitte	.add(rl);		
		contentPane	.add(pnl_unten, BorderLayout.SOUTH);
		pnl_unten	.add(txt_pw);	
		pnl_unten	.add(strut1);
		pnl_unten	.add(btn_ok);	
		pnl_unten	.add(strut2);				
		
		visible(false);
		rl.setMouseListener();
		
		
	
		
// ------------------------------------ Actions ---------------------------------
		

		
		
		btn_new.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{			
				if(btn_new.isSelected())
				{				
					btn_new.setText("Stop");
					visible(false);
					rl.startRandom();					
// Dieser Teil soll den Button steuern. Verursacht so aber Bugs und muss überarbeitet werden.					
//					btn_new.setEnabled(false);
//					for(int i=0;i<10;i++)
//					{
//						if(rl.getXPosSize()>=150)
//						{
//							btn_new.setEnabled(true);
//							btn_new.requestFocus();
//						}
//						if(rl.getXPosSize()>=500)
//						{
//							btn_new_clickOff();
//						}
//						try {Thread.sleep(1000);} catch (InterruptedException e1) {e1.printStackTrace();}
//					}
//					btn_new_clickOff();
					
				}
				else
				{

					try
					{
						
						btn_new.setText("Start");
						visible(true);
						rl.stopRandom();
						byte[] rand = rl.getKey();			
						String z = txt_white.getText();  	// Zeichensatz
						String v = txt_black.getText();  	// Verbotene Zeichen
						if(btn_black.isSelected()) 					
						{								
								z = z.replaceAll("["+v+" ]", "");
						}						
						String out = toBase(rand, z);	
						out = out.substring(0, Integer.parseInt(txt_pwLen.getText()));
						txt_pw.setText(out);
					}
					catch(Exception e1) {e1.printStackTrace();}
					
				}
			}
		});
		
		

	
			
		
		btn_black.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e) 
			{
				if(btn_black.isSelected())	txt_black.setForeground(Color.RED);
				else						txt_black.setForeground(Color.LIGHT_GRAY);
			}
		});
		
		
		
		btn_ok.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				GUI.txt_passwort.setText(txt_pw.getText());
				GUI.frame.setEnabled(true);
				dispose();
			}
		});
		
		
		
		// Close Button wird abgefangen und hier selbst verarbeitet.
		// Ist notwendig um die Haupt-GUI wieder frei zu geben.
		// Falls diese Klasse ohne GUI von der Konsole gestartet werden soll, muss diese Methode, oder die erste Zeile entfernt weren!
		addWindowListener(new java.awt.event.WindowAdapter() 
		{
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
		    {
		    	GUI.frame.setEnabled(true);
		    }
		});
	
	}
	
	
	
// --------------------------------------------------------------------- Methoden ---------------------------------------------------	
	
	// Muss nach dem Kostruktor von Außen einmal aufgerufen werden, damit der Generator startet.
	public void start()
	{
		btn_new_clickOn();
	}
	
	
	// Clickt den new Button auf ON
	public void btn_new_clickOn()
	{
		if(btn_new.isSelected()==false) btn_new.doClick();
	}
	
	// Clickt den new Button auf OFF
	public void btn_new_clickOff()
	{
		if(btn_new.isSelected()==true) btn_new.doClick();
	}

	
	
	// Blendet alle relevanten Felder beim Malen Aus oder dann ein.
	private void visible(boolean t)
	{
		lbl_pwLen.setVisible(t);
		txt_pwLen.setVisible(t);	
		btn_black.setVisible(t);
		lbl_white.setVisible(t);	
		txt_black.setVisible(t);	
		txt_white.setVisible(t);	
		pnl_unten.setVisible(t);
	}
	

	
	/** Byte Array wird in eine andere Basis char Array konvertiert. */
	private String toBase(byte[] k, String alphabet)   
	{ 
	     char[] ALPHABET = alphabet.toCharArray();
	     BigInteger z = new BigInteger(1,k);
	     BigInteger z1;
	     BigInteger rest = new BigInteger("0");
	     BigInteger base = new BigInteger(String.valueOf(alphabet.length()));
	     int laenge=0;
	     z1=z;           
	     for(double i=1; i>0;) 
	     {
	        z1 = z1.divide(base);
	        i  = z1.doubleValue();
	        laenge++;
	     }           
	     char[] key = new char[laenge];             
	     for(int i=laenge; i>0; i--) 
	     {
	          rest = z.mod(base);
	          key[i-1] = ALPHABET[rest.intValue()];
	          z =  z.divide(base);
	     }
	     return String.valueOf(key);
	} 
}