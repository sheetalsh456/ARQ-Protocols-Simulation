/*  this is reciever - Server
    send the packet of one character with its sequence number

*/
import java.io.*;
import java.net.*;
public class StopAndWaitReceiver{
    
    String packet,ack,data="";
    int i=0,seq=0;
    ObjectOutputStream output;
    ObjectInputStream input;
    ServerSocket reciever;
    Socket connection=null;
    StopAndWaitReceiver(){}
    public void run(){
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            reciever = new ServerSocket(2004,10); // port number of receiver
            System.out.println("waiting for connection...");
            connection=reciever.accept();
            seq=0;
            System.out.println("Connection established!!!");
            
                output=new ObjectOutputStream(connection.getOutputStream()); // object to write to socket
                output.flush();
                input=new ObjectInputStream(connection.getInputStream()); // object to read from socket
                output.writeObject("connection established!");  // get printed at sender's side
            do{
                try{
                    packet=(String)input.readObject(); // packet read from the sender
                    // here, each packet read from the sender consists of one character of the string along with its sequence number of 0 or 1.
                    if(Integer.valueOf(packet.substring(0,1))==seq){  // checking for correct sequence number
                        data+=packet.substring(1);
                        seq=(seq==0)?1:0; // changing of sequence number if acknowledgement is received. 
                        System.out.println("\n\ndata received"+packet);
                    }else{
                        System.out.println("\n\ndata received"+packet +" <-duplicate data");
                    }
                    /*
                        when i==3 then i destroy the ack
                    */
                    if(i<3){  // after regular intervals, we demonstrate the loss of ACK
                        output.writeObject(String.valueOf(seq));i++; // write the required sequence number into the socket
                    }else{
                        output.writeObject(String.valueOf((seq+1)%2));i=0; // write the toggled sequence number into the socket
                    }
                }catch(Exception e){}
            }while(!packet.equals("end")); // after entire message has been sent from sender's side, this packet is sent to mark the end of the string
            System.out.println("Full Data recived="+data); // the total data, which is a collection of all the packets received in sequential order
            output.writeObject("Quit from connection");
        }catch(Exception e){}
        finally{
            try{  // closing the sockets
                input.close();
                output.close();
                reciever.close();
            }catch(Exception e){}
        }
    }
    public static void main(String args[]){
        StopAndWaitReceiver s=new StopAndWaitReceiver();
        while(true){
            s.run();
        }
    }
}

