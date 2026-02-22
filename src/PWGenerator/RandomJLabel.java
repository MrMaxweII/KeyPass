package PWGenerator;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JLabel;
import lib3001.crypt.Calc;




/***************************************************************************************************************************
*		Mr.Maxwell                              Version 1.0											15.05.2022				*	
*		Generiert die Zufalls-Zahl und implementiert diese Zeichnung auf dem Label  mit der Maus.							*
*		Diese Klasse ist eine Erweiterung von JLabel und wird grundsätzlich auch so angewendet.								*
*		Also wird als erstes ein RandomJLabel Objekt erzeugt usw.															*
****************************************************************************************************************************/




public class RandomJLabel extends JLabel
{
	

	private  ArrayList<Integer> posX 	= new ArrayList<Integer>();		// In diese Listen werden die Maus-Positionen X geschrieben, die abgefahren wurden.  
	private  ArrayList<Integer> posY 	= new ArrayList<Integer>();		// In diese Listen werden die Maus-Positionen Y geschrieben, die abgefahren wurden. 
	private boolean randActive 			= false;						// Legt fest ob die Random-Generierung aktive ist oder nicht.
	private boolean mouseListenerActive	= false;						// Wenn der Mouse-Listener gestartet ist. Damit der Listener nur einmal gestartet werden kann!
	private String randKey1 = "";										// Der Random-Key 1 der sich durch das Zeichnen ergibt.




/** Setzt den MousListener, der die Maus-Bewegung aufzeichnet
*	Sollte nur einmal gestartet werden!							  */
public void setMouseListener()
{
	if(mouseListenerActive==false)
	{			
		this.addMouseMotionListener(new MouseMotionAdapter() 
		{
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				if(randActive)
				{
					posX.add(e.getX());
					posY.add(e.getY());
					repaint();
				}				
			}
		});	
		mouseListenerActive = true;	
	}
}



/** Startet den Zufallsgenerator mit der Maus-Bewegung und das Zeichnen.  */
public void startRandom()
{
	randKey1 = "";
	randActive=true;
}



/** Stoppt den Zufallsgenerator mit der Maus-Bewegung und dem Zeichnen.  
	Berechnet den Radom_Key 1;*/
public void stopRandom()
{	
	for(int i=0; i<posX.size() ;i++)
	{
		randKey1 = randKey1 + posX.get(i) + posY.get(i);
	}
	
	
	randActive=false;
	posX.clear();
	posY.clear();
	repaint();
}



/** Gibt den Zufalls Key zurück der durch die zufällige Mausbewegung und secureRandom berechnet wurde.  
	Der Key entsteht aus einer 128Byte SecurityRandom Zahl + aller Koordinaten der Mausbewegung,  
	Dieses Ergebnis wird dann mit SHA256 gehascht.   
 * @throws InterruptedException **/
public byte[] getKey() throws IllegalArgumentException, InterruptedException
{	
	if(randKey1.equals("")) throw new IllegalArgumentException("Error in \"RandomJLabel\": RandomKey1 is NULL");	
	byte[] randSecur = new byte[128];
	SecureRandom s = new SecureRandom();
	s.nextBytes(randSecur);
	BigInteger bi = new BigInteger(randKey1);
	byte[] randPaint = bi.toByteArray();		
	//System.out.println("Secur Random:  "+Convert.byteArrayToHexString(randSecur));
	//System.out.println("Paint Number:  "+Convert.byteArrayToHexString(randPaint));
	byte[] random = Arrays.copyOfRange(randSecur, 0, randSecur.length + randPaint.length); 
	System.arraycopy(randPaint, 0, random, randSecur.length, randPaint.length); 
	//System.out.println("Secur + Paint: "+Convert.byteArrayToHexString(random));
	return Calc.getHashSHA256(random);
}



/**	Zeichnet die Punkte aus den Koordinaten-Listen auf das RandomJLabel. **/
protected void paintComponent(Graphics g) 
{			
	g.drawLine(3, 565, posX.size()/3+3, 565);
	g.drawLine(3, 566, posX.size()/3+3, 566);
	for(int i=0; i<posX.size();i++)
	{
		g.drawOval(posX.get(i), posY.get(i), 3, 3);
	}		
	try {Thread.sleep(4);}
	catch (InterruptedException e) {e.printStackTrace();}	
}



/**	Gibt die die Summe der Maus-punkte in X-Position zurück die schon gemalt wurde.
	Dies wird zum Messen genutzt, wie viel mit der Maus schon gemalt wurde.
	Bei getXPosSize()==150 kann z.B. der Stop Button wieder aktiviert werden.
	Bei getXPosSIze()==500 kann z.B. das Malen gestoppt werden, weil die Anzahl der Punkte nun ausreichend ist.
	Diese Auswertung muss aber in der Übergeordneten Klasse geschehen, weil dort sich der Start/Stop Button befindet. **/
public int getXPosSize()
{
	return posX.size();
}
}