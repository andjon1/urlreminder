import java.net.*;
import java.lang.*;
import java.io.*;
import java.awt.Toolkit;
/*
*
* URLReminder:
* 	Bevakar en websida tills dess innehåll ändras.
*	Startas med (på guide, där det finns en proxy, tag annars bort de båda -D...):
*	java -Dhttp.proxyHost=10.1.16.16 -Dhttp.proxyPort=8080 URLReminder http-adress tidsintervall-i-ms 
*
*/

class URLReminder implements Runnable {
	
	private URLConnection urlConn;
	private String oldStr;
	private String newStr;
	private boolean getOut = false;
	private Thread currThread;
	private BufferedReader br;
	private String tmpStr;
	private URL currUrl;
	private int timeout;

	public URLReminder(URL theUrl, int intTimeout){
		timeout=intTimeout;
		currUrl=theUrl;
		currThread = new Thread (this, "Wait for changes");
		currThread.start();
	}

	public static void main(String[] args) {
		URL theURL= null;
		try{	
			theURL= new URL(args[0]);
		} catch (MalformedURLException e){
			e.printStackTrace();
		}
		
		URLReminder currReminder = new URLReminder(theURL,Integer.parseInt(args[1]));
	}
	
	public void run(){
		try{
			br=new BufferedReader(new InputStreamReader(currUrl.openStream()));
			while ((tmpStr = br.readLine()) != null)
				oldStr+=tmpStr;
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println(oldStr);
		while (!getOut) {
			try{
				currThread.sleep(timeout);
		                } catch (InterruptedException e){
                    			e.printStackTrace();   
                		}
			System.out.println("Tick...");
			try{
				br=new BufferedReader(new InputStreamReader(currUrl.openStream()));
				while ((tmpStr = br.readLine()) != null)
					newStr+=tmpStr;
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}

			if (oldStr.compareTo(newStr)==0){
				oldStr=newStr;
				newStr=null;
			}else{
				System.out.println(newStr);
				Toolkit.getDefaultToolkit().beep();
				getOut=true;
			}
		}

	}
}
