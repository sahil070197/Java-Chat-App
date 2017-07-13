
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

 interface conStatus{
    void closed();
}
class ReaderThread extends Thread{
    Socket soc;
    ReaderThread(Socket s)
    {
        soc=s;
    }
    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                BufferedReader reader=new BufferedReader(new InputStreamReader(soc.getInputStream()));
                String s=reader.readLine();
                if(s==null)
                {
                    throw new RuntimeException();
                }
                System.out.println("\nMessage from server: "+s);
            }
            catch(Exception e)
            {
                Client ob=new Client();
                ob.closed();
                break;
            }
        }
    }
} 
public class Client implements conStatus
{
    static Socket socket;
    static ReaderThread thread;
    static BufferedReader br;
    static final int portNumber=Multichat.portNumber;
    static PrintWriter pw;
    public static void main(String args[])
    {
        try
        {
            init();
            System.out.println("Enter name: ");
            String name=br.readLine();
            System.out.println(name+" is  connected with server...");
            sendName(name);
            
            messageSender();
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught: "+ex);
        }
    }
    static void init() throws Exception
    {
            socket=new Socket(InetAddress.getLocalHost(), portNumber);
            thread=new ReaderThread(socket);
            thread.start();
            br=new BufferedReader(new InputStreamReader(System.in));
            pw=new PrintWriter(socket.getOutputStream(),true);
    }
    static void sendName(String clientName)
    {   
        pw.println(clientName);    
    }
    static void messageSender() throws Exception
    {
        while(true)
        {            
            System.out.print("Your Message: ");
            String str=br.readLine();
            pw.println(str);
        }
    }
    public void closed()
    {
        System.out.println("\nServer turned off");
        socket=null;
        System.exit(0);
    }
}
