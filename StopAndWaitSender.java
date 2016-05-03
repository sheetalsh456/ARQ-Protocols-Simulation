/*  this is sender - client
    send the packet of one character with its sequence number

*/
import java.io.*;
import java.net.*;
public class StopAndWaitSender{
    
    String packet,ack,str, msg;
    int n,i=0,seq=0;
    Socket sender;
    ObjectOutputStream output;
    ObjectInputStream input;

    StopAndWaitSender(){}
    public void run(){
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Waiting for Connection....");
            sender = new Socket("192.168.43.240",2004); // IP address of receiver
            seq=0;
            output=new ObjectOutputStream(sender.getOutputStream()); // object to write into socket
            output.flush();
            input=new ObjectInputStream(sender.getInputStream()); // object to read from socket
            str=(String)input.readObject();
            System.out.println("received- "+str);
            System.out.println("Enter a string to send over the network");
            packet=br.readLine();  // get input from user
            n=packet.length();
            
            do{
                try{
                    if(i<n){
                        msg=String.valueOf(seq); 
                        // converts sequence number (0 or 1) to string and concatenates that sequence number with each character of the string
                        msg=msg.concat(packet.substring(i,i+1));
                    }
                    else if(i==n){ // if end of string, then end will be written into the socket
                        msg="end";output.writeObject(msg);break;
                    }
                    output.writeObject(msg); 
                    /*
                        changing sequence number since data sent
                    */
                    seq=(seq==0)?1:0;
                    output.flush();
                    System.out.println("data sent - "+msg);
                    System.out.println("waiting for ack.....\n\n");                    
                    ack=(String)input.readObject(); // receives acknowledgement from receiver, for the next sequence number 
                    if(ack.equals(String.valueOf(seq))){  // if ACK number is equal to next packet sequence number, then send it 
                        i++;
                        System.out.println("data received "+" packet recieved\n\n");
                    }
                    else{      // whenever ack lost or wrong ack we change the sequence number
                                
                        System.out.println("Time out!! resending data....\n\n"); // else, will resend the data 
                        seq=(seq==0)?1:0;
                    }
                }catch(Exception e){}
            }while(i<n+1);
            System.out.println("All data sent. exiting.");
        }catch(Exception e){}
        finally{
            try{ // closing all sockets
                input.close();
                output.close();
                sender.close();
            }catch(Exception e){}
        }
    }
    public static void main(String args[]){
        StopAndWaitSender s=new StopAndWaitSender();
            s.run();
    }
}
