
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

 interface conStatus{
    void closed();
}
class ReaderThread extends Thread{
    static final int PERSONAL_CHAT=1;
    Socket soc;
    ReaderThread(Socket s)
    {
        soc=s;
    }
    public String[] printOnlineClientList(String s) throws NullPointerException
    {
        if(s==null)
        {
            throw new NullPointerException();
        }
        String onlineClients[]=s.split("-");
        
        System.out.println("Online Clients: "+onlineClients.length);
        for(String client: onlineClients)
        {
            System.out.println(client);
            System.out.println(":n");
        }
        return onlineClients;
    }
    @Override
    public void run()
    {
        try
        {
            BufferedReader reader=new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String s=reader.readLine();
            String onlineClients[]=printOnlineClientList(s);
            String otherPerson=getOtherPersonName(onlineClients);
            Client.messageSender(otherPerson,PERSONAL_CHAT);
            while(true)
            {
                s=reader.readLine();
                if(s==null)
                {
                    throw new RuntimeException();
                }
                System.out.println("\nMessage from server: "+s);
            }
        }
        catch(IOException | RuntimeException e)
        {
            Client ob=new Client();
            ob.closed();
        } catch (Exception ex) {
            System.out.println("Error in sending message");
        }
    }

    private String getOtherPersonName(String[] onlineList){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        String name=null;
        try
        {
            System.out.println("Whom do you wish to talk to?");
            name=reader.readLine();
        }
        catch(Exception e)
        {
            return getOtherPersonName(onlineList);
        }
        if(name==null)
        {
            return getOtherPersonName(onlineList);
        }
            
        for(String s: onlineList)
        {
            if(s.compareToIgnoreCase(name) == 0)
            {
                return name;
            }   
        }
        return getOtherPersonName(onlineList);
            
    }
} 
public class Client implements conStatus
{
    static Socket socket;
    static ReaderThread thread;
    static BufferedReader br;
    static final int portNumber=Multichat.portNumber;
    static PrintWriter pw;
    static final String TAG_MESSAGE="message-";
    static final String TAG_SENDER_NAME="sender-";
    static final String TAG_RECIEVER_NAME="reciever-";
    public static void main(String args[])
    {
        try
        {
            init();
            System.out.println("Enter name: ");
            String name=br.readLine();
            System.out.println(name+" is  connected with server...");
            sendName(name);
            
            
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
        pw.println(TAG_SENDER_NAME+clientName);    
    }
    static void messageSender(String name, int type) throws Exception
    {
        pw.println(TAG_RECIEVER_NAME+name);
        while(true)
        {            
            System.out.print("Your Message: ");
            String str=br.readLine();
            pw.println(TAG_MESSAGE+str);
        }
    }
    @Override
    public void closed()
    {
        System.out.println("\nServer turned off");
        socket=null;
        System.exit(0);
    }
}
