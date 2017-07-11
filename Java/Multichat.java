/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
interface clientStatus
{
    void clientDisconnected(String clientID);
}
class Shared
{
    static ServerSocket server;
}

class MyAllSockets
{
    static ArrayList<Socket> arr=new ArrayList<>();
    static ArrayList<PrintWriter> parr=new ArrayList<>();
    static ArrayList<String> name=new ArrayList<>();
    static Map<String, Integer> Kmap=new HashMap<>();
}

class ReadThread extends Thread implements clientStatus
{
    int index;
    String name;
    boolean connected;
    public ReadThread(int index) 
    {
    	System.out.println(index+" Reader Thread Init");
        this.index=index;
        name=null;
        connected=true;
        Client.ReadThread=this;
    }

    ReadThread() {
    }
    
    public void run()
    {
        try 
        {
            System.out.println("Reader thread working");
            int flag=0;
            BufferedReader brs=new BufferedReader(new InputStreamReader(MyAllSockets.arr.get(index).getInputStream()));
            while(connected)
            {
                if(flag==0)
                {
                    flag=1;
                    String s=getClientName(brs);
                    MyAllSockets.Kmap.put(s, index);
                    this.name=s;
                    MyAllSockets.name.add(this.name);
                    echoOnlineClients();
                }
                else
                {
                    normalReader(brs);
                }
            }
            System.out.println("Stopped "+this.name);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(ReadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    String getClientName(BufferedReader brs) throws Exception
    {
        String name=brs.readLine();
        System.out.println(name+" has connected.");
        return name;
    }
    void echoOnlineClients() throws Exception
    {
        
        System.out.println("Size: "+MyAllSockets.Kmap.size());
        Set<String> ar=MyAllSockets.Kmap.keySet();
        PrintWriter pw=MyAllSockets.parr.get(index);
        System.out.println("index "+index);
        pw.println( "Hello zxhjnkj ");
        StringBuffer buf=new StringBuffer();
        for (Object clientName : ar)
        {
            System.out.println("Online: "+clientName.toString());
            buf.append(clientName.toString()+"$");
        }
        String sent=buf.toString();
        System.out.println("Sending: "+sent);
        pw.println(sent);
    }
    void normalReader(BufferedReader reader) throws Exception
    {
        String s=reader.readLine();
        if(s==null)
        {
            throw new RuntimeException();
        }
        System.out.println(name+": "+s);
    }

    @Override
    public void clientDisconnected() {
        System.out.println("Stocbcjfdsncknpped");
        connected=false;
        MyAllSockets.Kmap.remove(this.name);
        MyAllSockets.arr.remove(this.index);
        MyAllSockets.parr.remove(this.index);
        System.out.print(clientID + " Stopped");
    }
}

class MyClientConnection extends Thread
{
    PrintWriter pw;
    Socket socket;
    public void run()
    {
        while(true)
        {
            try 
            {
                socket=Shared.server.accept();
                init();
                new ReadThread(MyAllSockets.parr.size()-1).start();
            } 
            catch (Exception ex) 
            {
                System.exit(0);
                break;
            }
        }
    }
    void init() throws Exception
    {
        pw=new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Adding new client");
        MyAllSockets.parr.add(pw);
        MyAllSockets.arr.add(socket);
    }
}

public class Multichat 
{
    public static final int portNumber=3032;
    static BufferedReader br;
    static void init() throws Exception
    {
        br=new BufferedReader(new InputStreamReader(System.in));
        Shared.server=new ServerSocket(3032);
        MyClientConnection mcc=new MyClientConnection();
        mcc.start();
    }

    public static void main(String[] args) 
    {
        try
        {
    
            System.out.println("Server is going to run...");
            init();   
            System.out.println("Server is running at IP: "+InetAddress.getLocalHost());
    
            while(true)
            {
                broadcast();
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught: "+ex);
        }
    }
    static void broadcast() throws Exception
    {
        System.out.println("Enter text to broadcast: ");
        String str=br.readLine();
        for(int i=0;i<MyAllSockets.parr.size();i++)
        {
            System.out.println("Sending "+str+" to "+MyAllSockets.name.get(i));
            MyAllSockets.parr.get(i).println(str);
        }
    }
}
