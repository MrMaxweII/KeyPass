package GUI;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.json.JSONException;
import BTClib3001.TOTP;
import Crypt.KeyData;
import PWGenerator.PWGenerator;



/***********************************************
 * Alle ActionListenders für die GUI 			*
 ************************************************/



public class GUI_Action 
{

	
	
	
public static void run()
{
	
	// Menü Öffnen
	GUI.btn_open.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI.txt_meldung.setText("");
			try 
			{
				KeyData.open();	
				clearFormular();
				GUI.combo_filename.setEnabled(false);
				GUI.frame.setEnabled(false);
				GUI_KeyList frame= new GUI_KeyList(GUI.frame.getX()+10, GUI.frame.getY()+30);
				frame.setVisible(true);								
				GUI.btn_edit.setSelected(true);
				GUI.btn_edit.setSelected(false);
				GUI.btn_edit.setVisible(true); 
				GUI.btn_saveChange.setVisible(false);
				GUI.btn_saveNew.setVisible(false);
				GUI.btn_clear.setVisible(false);
			} 
			catch (Exception e1) {GUI.txt_meldung.setText(e1.getMessage());}
		}
	});	
	
	
	
	// Menü Neuer Eintrag
	GUI.btn_new.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{		
			GUI.txt_meldung.setText("");
			try 
			{
				KeyData.open();
				clearFormular();
				GUI.combo_filename.setEnabled(false);
				GUI.pnl_hauptfeld.setVisible(true);
				GUI.btn_edit.setSelected(true);
				GUI.btn_edit.setVisible(false); 
				GUI.btn_PWGenerator.setVisible(true);
				GUI.btn_saveChange.setVisible(false);
				GUI.btn_saveNew.setVisible(true);
				GUI.btn_clear.setVisible(false);
			} 
			catch (Exception e1) {GUI.txt_meldung.setText(e1.getMessage());}
		}
	});	
	
	
	// Benutzername in Zwichenablage speichern
	GUI.btn_copy1.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			copyToClipboard(GUI.txt_userName.getText());
			GUI.txt_userName.setForeground(Color.blue);
			GUI.txt_passwort.setForeground(Color.black);
		}
	});		
		
		
	// Passwort in Zwischenablage speichern			
	GUI.btn_copy2.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			copyToClipboard(new String(GUI.txt_passwort.getPassword()));
			GUI.txt_passwort.setForeground(Color.blue);	
			GUI.txt_userName.setForeground(Color.black);
		}
	});
		
	
	
	GUI.btn_PWzeigen.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent e) 
		{
			if(GUI.btn_PWzeigen.isSelected())
			{
				GUI.txt_passwort.setEchoChar((char)0);
			}
			else
			{
				GUI.txt_passwort.setEchoChar((char)8226);	
			}			
		}
	});
	
	
	// Auswahl Eintrag ändern
	GUI.btn_edit.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent e) 
		{
			if(GUI.btn_edit.isSelected())
			{
				GUI.txt_name.setEditable(true);
				GUI.txt_group.setEditable(true);
				GUI.txt_userName.setEditable(true);
				GUI.txt_url.setEditable(true);
				GUI.txt_url.setForeground(Color.black);
				GUI.txt_url.setBorder(new LineBorder(new Color(171, 173, 179)));
				GUI.txt_url.setBackground(Color.white);
				GUI.txt_passwort.setEditable(true);
				GUI.txt_description.setEditable(true);
				GUI.txt_description.setBackground(Color.white);
				GUI.txt_TOTP_Secret.setVisible(true);
				GUI.txt_TOTP_len.setVisible(true);
				GUI.btn_PWGenerator.setVisible(true);
				GUI.btn_saveChange.setVisible(true);
				GUI.btn_clear.setVisible(true);
				GUI.lbl_TOTP.setVisible(true);
				GUI.lbl_TOTP_len.setVisible(true);
			}
			else
			{
				GUI.txt_name.setEditable(false);
				GUI.txt_group.setEditable(false);
				GUI.txt_userName.setEditable(false);
				GUI.txt_url.setEditable(false);
				GUI.txt_url.setForeground(Color.blue);
				GUI.txt_url.setBorder(null);
				GUI.txt_url.setBackground(GUI.color3);
				GUI.txt_passwort.setEditable(false);
				GUI.txt_description.setEditable(false);
				GUI.txt_description.setBackground(new Color(240,240,240));
				GUI.txt_TOTP_Secret.setVisible(false);
				GUI.txt_TOTP_len.setVisible(false);
				GUI.btn_PWGenerator.setVisible(false);
				GUI.btn_saveChange.setVisible(false);
				GUI.btn_clear.setVisible(false);
				GUI.lbl_TOTP.setVisible(false);
				GUI.lbl_TOTP_len.setVisible(false);
			}
		}
	});
	
	
	
	
	// Diesen Datensatz aus der Datenbank Löschen?\nAlle Daten zu diesem Eintrag werden unwiderruflich gelöscht
	GUI.btn_clear.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{				
			String name1 = GUI.txt_name.getText();
			int delite = JOptionPane.showConfirmDialog(GUI.frame,"Datensatz mit dem Namen: ["+name1+"] aus der Datenbank Löschen? \nAlle Daten zu diesem Eintrag werden sofort und unwiderruflich gelöscht!", "Löschen bestätigen",JOptionPane.WARNING_MESSAGE);
			if(delite==0)  // Wenn Löschen bestätigt wurde
			{
				int delIndex = Integer.parseInt(GUI.txt_id.getText()); // Die Nummer im Array die gelöscht werden soll.
				try 
				{
					KeyData.remove(delIndex);
					clearFormular();
					GUI.pnl_hauptfeld.setVisible(false);					
					GUI.txt_meldung.setText("Datensatz mit dem Namen: ["+name1+"] wurde gelöscht");												
				} 
				catch (Exception e1) {GUI.txt_meldung.setText(e1.getMessage());}
			}			
		}
	});
	
	
	
	// Neuen Eintrag Speichern
	GUI.btn_saveNew.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try 
			{
				checkFormular();
				KeyData.add(GUI.txt_name.getText(), GUI.txt_group.getText(), GUI.txt_userName.getText(), new String(GUI.txt_passwort.getPassword()), GUI.txt_description.getText(), getDate(), GUI.txt_url.getText(), GUI.txt_TOTP_Secret.getText(), GUI.txt_TOTP_len.getText());
				GUI.txt_meldung.setText("Neuer Eintrag wurde gespeichert.");
				GUI.pnl_hauptfeld.setVisible(false);					
				clearFormular();
			} 
			catch (Exception e1) 
			{
				JOptionPane.showMessageDialog(GUI.frame, e1.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}

		}
	});		
	
	
	
	// Änderung Speichern
	GUI.btn_saveChange.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try 
			{			
				checkFormular();
				KeyData.toChange(Integer.parseInt(GUI.txt_id.getText()), GUI.txt_name.getText(), GUI.txt_group.getText(), GUI.txt_userName.getText(), new String(GUI.txt_passwort.getPassword()), GUI.txt_description.getText(), getDate(), GUI.txt_url.getText(), GUI.txt_TOTP_Secret.getText(), GUI.txt_TOTP_len.getText());			
				GUI.txt_meldung.setText("Änderungen wurden gespeichert.");
				GUI.pnl_hauptfeld.setVisible(false);					
				clearFormular();
			} 
			catch (Exception e1) 
			{
				JOptionPane.showMessageDialog(GUI.frame, e1.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}

		}
	});	
	
	

	GUI.btn_PWGenerator.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI.frame.setEnabled(false);
			PWGenerator pwGen = new PWGenerator(GUI.frame.getX()+10, GUI.frame.getY()+30);	
			pwGen.setVisible(true);								
			pwGen.start();
		}
	});
	
	
	
	// Der Link auf die Internetseite wird aufgerufen
	GUI.txt_url.addMouseListener(new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent e) 
		{
			try
			{
				if (e.getClickCount() == 2 && !e.isConsumed()) 								// Doppelklick
				{
				    e.consume();
				    int xPosTextEnd =  GUI.txt_url.getCaret().getMagicCaretPosition().x; 	// x-Position des Text Endes
					if((e.getX() <= xPosTextEnd) && (GUI.txt_url.isEditable()==false)) 		// Mausklick muss innerhalb des Textes sein, um den Link zu öffnen.
					{
						Desktop.getDesktop().browse(URI.create(GUI.txt_url.getText()));
					}
				}		
			}		
			catch (Exception e1) {GUI.txt_meldung.setText("URL kann nicht geöffnet werden!");}					
		}
	});	
	
	
	
	// TOTP-Run Button
	GUI.btn_TOTP.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{
			try 
			{
				String totpCode = TOTP.getTOTP( GUI.txt_TOTP_Secret.getText(), Integer.parseInt(GUI.txt_TOTP_len.getText()));
				JOptionPane.showMessageDialog(GUI.frame, totpCode,"TOTP-Einmalcode",JOptionPane.PLAIN_MESSAGE);
			} 
			catch (Exception e1) 
			{
				GUI.txt_meldung.setText(e1.getMessage());
				e1.printStackTrace();	
			}
		}
		
	});
	
	
	// TOTP Secret Eingabefeld (De/Aktivert den TOTP-Start-Button)
	GUI.txt_TOTP_Secret.addKeyListener(new KeyAdapter() 
	{
		public void keyReleased(KeyEvent e) 
		{
			if(GUI.txt_TOTP_Secret.getText().equals("") || GUI.txt_TOTP_len.getText().equals("")) 	GUI.btn_TOTP.setVisible(false);
			else 
			{
				try
				{
					TOTP.checkBase32Input(GUI.txt_TOTP_Secret.getText());
					GUI.btn_TOTP.setVisible(true);
				}
				catch(IOException e1) {GUI.btn_TOTP.setVisible(false);}
			}							 				
		}
	});
	
	
	// TOTP Secret Eingabefeld (De/Aktivert den TOTP-Start-Button)
	GUI.txt_TOTP_len.addKeyListener(new KeyAdapter() 
	{
		public void keyReleased(KeyEvent e) 
		{
			if(GUI.txt_TOTP_Secret.getText().equals("") || GUI.txt_TOTP_len.getText().equals("")) 	GUI.btn_TOTP.setVisible(false);
			else 
			{
				try
				{
					TOTP.checkBase32Input(GUI.txt_TOTP_Secret.getText());
					GUI.btn_TOTP.setVisible(true);
				}
				catch(IOException e1) {GUI.btn_TOTP.setVisible(false);}
			}
		}
	});
	
	
}








//----------------------------------------- Hilfs-Methoden -------------------------------------------------------------//
	

	// Löscht die Zwischenablage 
	public static void deleteClipboard()
	{
		System.out.println("Löschvorgang Zwischenablage");
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard(); 
		clip.setContents(new StringSelection(""), null);	
	}
	
//----------------------------------------- Private Methoden -------------------------------------------------------------//

	
	// Kopiert den übergebenen String in die Zwischenablage
	private static void copyToClipboard(String s)
	{
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard(); 
		StringSelection str = new StringSelection(s);
		clip.setContents(str, null);
	}
		
	
	// Gibt das aktuelle Datum in der gewünschen Form zurück.
	private static String getDate()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");  
	    return formatter.format(new Date());  
	}
	
	
	// Hier wird das eingabeFormular auf Fehler geprüft
	// Alle Eingabefelder werden nacheinander auf Eingabefehler überprüft.
	// Wenn alles richtig ist, wird true zurück gegeben, bei einem Fehler wird IOExeption geworfen.
	private static boolean checkFormular() throws JSONException, IOException
	{
		if(GUI.txt_name		.getText().equals(""))												throw new IOException("Name der Anwendung darf nicht leer sein!");
		if(GUI.txt_userName	.getText().equals(""))												throw new IOException("User Name darf nicht leer sein!");
		if(GUI.txt_passwort	.getPassword().length<4)											throw new IOException("Passwort muss min. 4 Zeichen haben!");		
		String totp = GUI.txt_TOTP_Secret.getText().toUpperCase();
		if(totp.matches("[ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=]*")==false) 						throw new IOException("TOTP Geheimschlüssel, falsches Format!\nFormat muss Base32 sein!\nErlaubte Zeichen:  ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=");
		if(totp.length()%8!=0) 																	throw new IOException("TOTP Geheimschlüssel, falsche Länge!\nDie Länge muss genau durch 8 teilbar sein!\nSie können die Länge mit dem \"=\" Zeichen hinten auffüllen.");
		if(GUI.txt_TOTP_len.getText().matches("(2)|(3)|(4)|(5)|(6)|(7)|(8)|(9)|(10)*")==false)	throw new IOException("TOTP Ausgabezeichen, die Anzahl kann nur zwischen 2 und 10 gewählt werden!\n Üblicherweise werden 6 Ausgabezeichen verwendet.");
		if(totp.equals("")==false && GUI.txt_TOTP_len.getText().equals(""))						throw new IOException("Wenn ein TOTP Geheimschlüssel angegegeben wird, darf das Feld \"TOTP Ausgabezeichen\" nicht leer sein.\n Üblicherweise werden 6 Ausgabezeichen verwendet.");
		return true;
	}
	
	
	// Eingabeformular wird geleert.
	private static void clearFormular()
	{
		GUI.lbl_id.setIcon(null);
		GUI.txt_id.setText("-1");
		GUI.txt_name.setText("");
		GUI.txt_group.setText("");
		GUI.txt_date.setText("");
		GUI.txt_userName.setText("");
		GUI.txt_passwort.setText("");
		GUI.txt_url.setText("");
		GUI.txt_description.setText("");
		GUI.txt_passwort.setForeground(Color.black);
		GUI.txt_userName.setForeground(Color.black);
		GUI.txt_TOTP_Secret.setText("");
		GUI.txt_TOTP_len.setText("6");
		GUI.btn_PWzeigen.setSelected(false);
		GUI.btn_edit.setSelected(false);
		GUI.btn_TOTP.setVisible(false);
		GUI.pnl_hauptfeld.setVisible(false);
		
	}
}