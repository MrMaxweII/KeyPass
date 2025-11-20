package GUI;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;



/******************************************************************************************************************
 * Dies ist eine Hilfsklasse die DefaultTreeCellRenderer erweitert.
 * Es geht hier um den JTree der alle Accounds auflistet.
 * Ist erfoderlich um Icons in jeden Blattknoten des JTree anzuzeigen.
 * Die Icons werden hier geladen und dem JTree für jeden Blatt-Knoten hinzugefügt.
 * Die Icons werden im Ordner "icons" gesucht und müssen den "ApplicationName.png" der Anwendung tragen!
 * Die Icons sollten unbedingt im Format: 16x16pixel sein!
 * Wird das Icon nicht gefunden wird automatisch, das "temp\\keySmal.png" Icon verwendet.
 ******************************************************************************************************************/



public class MyTreeCellRenderer extends DefaultTreeCellRenderer 
{
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 
	{
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (leaf) 
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			String nodeName = node.toString();								// Entspricht dem "ApplicationName" der jeweiligen Anwendung aus der JSON Datei, bzw. dem Text der im JTree angezeigt wird.
			File iconFile = new File("icons\\"  + nodeName + ".png");		// Verzeichnis des jeweiligen Icons das ausgewählt wird.
			
			if(iconFile.exists())
			{
				setIcon(new ImageIcon(iconFile.getAbsolutePath()));
			}
			else
			{
				setIcon(new ImageIcon("temp\\keySmal.png"));
			}
		}
		return c;
	}
}
