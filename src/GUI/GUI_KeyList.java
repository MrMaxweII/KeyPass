package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Crypt.KeyData;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.EmptyBorder;




/***********************************************************************************
*	Version 1.6 					Autor: Mr. Maxwell				17.12.2025		*
*	Letzte Änderung: JList durch JTree ersetzt und diverse Anpassungen dazu.		*
*	Dadurch können Einträge nun gruppiert werden.									*
*	Diese Klasse ist Teil der GUI des KeyPass										*
*	Erzeugt das List-Fenster, in dem KeyPass Einträge ausgewählt werden können.		*
***********************************************************************************/



class GUI_KeyList extends JFrame
{
	
	
	GUI_KeyList(int x, int y) throws Exception 
	{
		setTitle("Anwendung Auswählen");
		setIconImage(MyIcons.keysmal.getImage());
		setBounds(x, y, 380, 430);
			
		JPanel 					contentPane = new JPanel();
		JPanel 					pnl_oben 	= new JPanel();
		JScrollPane 			scrollPane 	= new JScrollPane();
		JLabel 					lbl_1 		= new JLabel("Anzahl Account");	
		JLabel 					lbl_2 		= new JLabel("Letzte Änderung");
		DefaultMutableTreeNode 	root 		= new DefaultMutableTreeNode("root");	

		contentPane	.setLayout(new BorderLayout(0, 0));
		pnl_oben	.setLayout(new BoxLayout(pnl_oben, BoxLayout.Y_AXIS));
		pnl_oben	.setBorder(new LineBorder(GUI.color1, 3));
		pnl_oben	.setBackground(GUI.color1);
		scrollPane	.setBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray));

		lbl_1		.setFont(new Font("Arial", Font.PLAIN,11));
		lbl_2		.setFont(new Font("Arial", Font.PLAIN,11));
		lbl_1		.setForeground(GUI.color2);
		lbl_2		.setForeground(GUI.color2);

		setContentPane(contentPane);
		pnl_oben	.add(lbl_1);
		pnl_oben	.add(lbl_2);
		contentPane	.add(pnl_oben, BorderLayout.NORTH);
		contentPane	.add(scrollPane, BorderLayout.CENTER);
		
		

		
// ---------------------------------------------------------- Hier wird der Tree implementiert -------------------------------------------------------------	
		
		
		JSONArray ja = KeyData.getDatabase().getJSONArray("list");
		String[] strList = new String[ja.length()];		
		lbl_1.setText("Anzahl Account: "+ja.length());
		lbl_2.setText("Letzte Änderung: "+getLastChange(ja));
		this.setSize(380, ja.length()*8+200);

		Map<String, DefaultMutableTreeNode> map = new HashMap<>();
		
		for (int i=0; i < ja.length(); i++) 
		{
			JSONObject jo = ja.getJSONObject(i);
			strList[i] = jo.getString("ApplicationName");
			String strGruppe = jo.optString("Gruppe", "");	
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(jo.getString("ApplicationName"));
			if (strGruppe.length()>0) 
			{
				DefaultMutableTreeNode folder = map.get(strGruppe);
				if(folder == null) 
				{
					folder = new DefaultMutableTreeNode(strGruppe);
					map.put(strGruppe, folder);
					root.add(folder);
				}
				folder.add(node);
			}
			else { root.add(node); }
		}	
		sortChildren(root, true);										// Sortierung des Gesamten Trees
	
		JTree 	tree = new JTree(root);
		tree.setBorder(new EmptyBorder(5, 0, 0, 0));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		// tree.setRowHeight(17);
		tree.setFont(new Font("Consolas", Font.PLAIN, 11));
		tree.setBackground(GUI.color1);
		scrollPane.setViewportView(tree);
		DefaultTreeCellRenderer renderer = new MyTreeCellRenderer();	// Eigene Class in der die Icons hinzugefügt werden.
		renderer.setBackgroundNonSelectionColor(GUI.color1);  
		tree.setCellRenderer(renderer);

	

	
		
		// Beim Enter drücken wird die Registerkarte mit dem selectiertem Element geöffnet
		tree.addKeyListener(new KeyAdapter() 							
		{
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==10) 
				{
					TreeNode node = (TreeNode) tree.getLastSelectedPathComponent();			
					if(node != null && node.isLeaf())
					{
						whriteGUI(node.toString());
						dispose();
					}
				}
			}
		});
		
		
		
		// Beim Auswählen mit der Maus, wird die Registerkarte mit dem selectiertem Element geöffnet
		tree.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
		    {
		        if (e.getClickCount() == 2) 
		        {
		        	TreeNode node = (TreeNode) tree.getLastSelectedPathComponent();			
					if(node != null && node.isLeaf())
					{
						whriteGUI(node.toString());
						dispose();
					}	
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
			GUI.lbl_id.setIcon(new ImageIcon("icons/"+jo.getString("ApplicationName")+".png"));
			GUI.txt_name.setText(jo.getString("ApplicationName"));
			try{GUI.txt_group.setText(jo.getString("Gruppe"));}				// Da "Gruppe" erst ab Version 1.3.3 im Datensatz hinzugefügt wurde, ist diese try-Abfrage hier nötig.
			catch(JSONException e4)											// Wenn "Gruppe" nicht im JSON enthalten ist, wird ein Leerstring in das Feld geschrieben.		
			{
				GUI.txt_group.setText("");
				System.out.println("Gruppe nicht in .key Datei enthalten.\nDateiversion: "+KeyData.getDatabase().getString("version"));
			}
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
	
	
	
	
	
	
// ---------------------------------------------------------------- Hilfsmethoden -----------------------------------------------------------------	
	
	
	
	// Sortiert Elemente eines DefaultMutableTreeNode
	// Wenn "foldersFirst == true" dann werden Ornder oben zu erst aufgefürht. Wenn "false" dann werden die Ordner mit Sortiert
	private void sortChildren(DefaultMutableTreeNode parent, boolean foldersFirst) 
	{
		int childCount = parent.getChildCount();
		if (childCount == 0) return;	
		ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
		for (int i = 0; i < childCount; i++) 
		{
			children.add((DefaultMutableTreeNode) parent.getChildAt(i));
		}
		parent.removeAllChildren();
		if(foldersFirst) // Sortiert so dass Ordner Oben sind
		{
			children.sort((a, b) -> 
			{
				boolean aIsFolder = a.getChildCount() > 0;
				boolean bIsFolder = b.getChildCount() > 0;
				if (aIsFolder && !bIsFolder) return -1; 
				if (!aIsFolder && bIsFolder) return 1;  
				return a.getUserObject().toString().compareToIgnoreCase(b.getUserObject().toString());
			});
		}
		else // Sortiert Ordner mit
		{		
			children.sort(Comparator.comparing(n -> n.getUserObject().toString(), String.CASE_INSENSITIVE_ORDER));	
		}
		for (DefaultMutableTreeNode child : children) 
		{
			parent.add(child);
			sortChildren(child, foldersFirst);
		}
	}	
	
	
	
	// gibt einen String der Applikation der letzten Änderung in der Datenbank zurück.
	private String getLastChange(JSONArray ja) throws JSONException, ParseException
	{
		if (ja == null || ja.length() == 0) return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date d1 = dateFormat.parse("01.01.1900");
		Date d2 = dateFormat.parse("01.01.1900");
		String name = "";
		for (int i=0; i < ja.length(); i++) 
		{
			JSONObject jo = ja.getJSONObject(i);
			d1 = dateFormat.parse(jo.getString("Date"));
			if( d1.compareTo(d2) >= 0)
			{
				d2 = d1;
				name = jo.getString("ApplicationName");
			}
		}	
		return name+"   "+dateFormat.format(d2);
	}	
}