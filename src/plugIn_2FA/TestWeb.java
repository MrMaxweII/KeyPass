package plugIn_2FA;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class TestWeb 
{
	
	public static void main(String[] args) throws Exception
	{
		
	//	System.out.println(readOneMessage(3001,50000));
		
		clientSendOneMessage("hallo Test");
		
		
	}
	
	
	private static void clientSendOneMessage(String message) throws Exception
	{
		Socket soc = new Socket("192.168.178.70",3001);
		DataOutputStream out = new DataOutputStream(soc.getOutputStream());

		//out.writeByte(1);
		out.writeUTF("message");
		//out.flush(); // Send off the data
		
		out.close();
		soc.close();
	}
	
	
	
	
	
	
/** Öffnet einen Server-Port und wartet (Blockiert) solange bis ein Client etwas auf diesen Port sendet.
	Diese Nachricht wird dann zurückgegeben und der Socket wird geschlossen.
	@param port Der Port wird übergeben
	@param timeOut nach dieser Zeit wird eine SocketTimeOutException geworfen
	@return Die Empfangene Nachricht wird als String zurückgegeben	 **/
	public static String readOneMessage(int port, int timeOut) throws IOException
	{	
		ServerSocket ssoc = new ServerSocket(port);  
		ssoc.setSoTimeout(timeOut);
    	Socket soc = ssoc.accept();  // Blockiert bis sich ein Client meldet.
    	InputStream is = soc.getInputStream();
    	   
    	BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));  	
    	String str = "";
    	while(in.ready())
        str += in.readLine()+"\n";
    	
    	is.close();
	    soc.close();
    	ssoc.close();
    	return str;
	}
	
	
	
	

}
