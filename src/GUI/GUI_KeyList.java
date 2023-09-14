package GUI;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Crypt.KeyData;




/***********************************************************************************
*	Autor: Mr. Maxwell						Version 1.3			04.09.2023			*
*	Diese Klasse ist Teil der GUI des KeyPass										*
*	Erzeugt das List-Fenster, in dem KeyPass Einträge ausgewählt werden können.		*
***********************************************************************************/



class GUI_KeyList extends JFrame
{
	
	
	
	GUI_KeyList(int x, int y) throws JSONException 
	{
		setTitle("Anwendung Auswählen");
		setIconImage(Toolkit.getDefaultToolkit().getImage("temp\\key.png"));
		setBounds(x, y, 380, 430);
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
			
		JSONArray ja = KeyData.getDatabase().getJSONArray("list");
		String[] strList = new String[ja.length()];	
		for(int i=0;i<ja.length();i++)
		{
			JSONObject jo = ja.getJSONObject(i);
			strList[i] = jo.getString("ApplicationName");
		}
		
		
		Arrays.sort(strList);
		
		
		JList list = new JList(strList);
		list.setFont(new Font("Consolas", Font.PLAIN, 11));
		scrollPane.setViewportView(list);
		
		list.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==10) 
				{
					whriteGUI((String) list.getSelectedValue());
					dispose();
				}
			}
		});
		
		list.addMouseListener(new MouseAdapter() 
		{
		    public void mouseClicked(MouseEvent e) 
		    {
		        JList list = (JList)e.getSource();
		        if (e.getClickCount() == 2) 
		        {
					whriteGUI((String) list.getSelectedValue());	
					dispose();  	
		        }
		    }
		});
		
		// Close Button wird abgefangen und hier selbst verarbeitet.
		addWindowListener(new java.awt.event.WindowAdapter() 
		{
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
		    {
		    	GUI.frame.setEnabled(true);
		    }
		});	
	}
// -------------------------------------------------- Ende GUI ------------------------------------------------------------
	
	
	
	// schreibt die Key-Daten in die GUI   
	private void whriteGUI(String selectedString)
	{
		if(selectedString==null) {GUI.txt_meldung.setText("Diese Datenbank ist leer.\nZuerst muss ein Neuer Eintrag hinzugefügt werden!"); GUI.frame.setEnabled(true); return;}	
		GUI.frame.setEnabled(true);
		GUI.pnl_hauptfeld.setVisible(true);				
		try
		{
			JSONArray ja = KeyData.getDatabase().getJSONArray("list");				
			int selectedIndex = -1;
			for(int i=0;i<ja.length();i++)
			{
				JSONObject jo = ja.getJSONObject(i);	
				if(selectedString.equals(jo.getString("ApplicationName"))) {selectedIndex =i; break;}
			}
			JSONObject jo = ja.getJSONObject(selectedIndex);	
			GUI.txt_id.setText(String.valueOf(selectedIndex));
			GUI.txt_name.setText(jo.getString("ApplicationName"));
			GUI.txt_date.setText(jo.getString("Date"));
			GUI.txt_userName.setText(jo.getString("UserName"));
			GUI.txt_passwort.setText(jo.getString("Password"));
			GUI.txt_description.setText(jo.getString("Description"));
			try{GUI.txt_url.setText(jo.getString("url"));}					// Da "url" erst ab Version 1.1.0 im Datensatz hinzugefügt wurde, ist diese try-Abfrage hier nötig.
			catch(JSONException e2)											// Wenn "url" nicht im JSON enthalten ist, wird ein Leerstring in das Feld geschrieben.		
			{
				GUI.txt_url.setText("");
				System.out.println("url nicht in .key Datei enthalten.\nDateiversion: "+KeyData.getDatabase().getString("version"));
			}	
			try{GUI.txt_TOTP_Secret.setText(jo.getString("totpKey"));}
			catch(JSONException e3)
			{
				GUI.txt_TOTP_Secret .setText("");
				System.out.println("totpKey nicht in .key Datei enthalten.\nDateiversion: "+KeyData.getDatabase().getString("version"));
			}
			try{GUI.txt_TOTP_len.setText(jo.getString("totpLen"));}
			catch(JSONException e3)
			{
				GUI.txt_TOTP_len.setText("");
				System.out.println("totpLen nicht in .key Datei enthalten.\nDateiversion: "+KeyData.getDatabase().getString("version"));
			}
			if(GUI.txt_TOTP_Secret.getText().equals("")) 	GUI.btn_TOTP.setVisible(false);
			else							 				GUI.btn_TOTP.setVisible(true);
		}
		catch(Exception e1) {GUI.txt_meldung.setText(e1.getMessage());};
	}	
}