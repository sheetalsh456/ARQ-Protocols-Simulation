import java.util.*;
import java.net.*;
import java.io.*;
public class g_sender
{
public static void main(String args[]) throws Exception
{
BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
System.out.print("Enter the value of m : ");
int m=Integer.parseInt(br.readLine());
int x=(int)((Math.pow(2,m))-1);
System.out.print("Enter no. of frames to be sent:");
int count=Integer.parseInt(br.readLine());
int data[]=new int[count];
int h=0;
for(int i=0;i<count;i++)
{
System.out.print("Enter data for frame no " +h+ " => ");
data[i]=Integer.parseInt(br.readLine());
h=(h+1)%x;
}
Socket client=new Socket("192.168.43.118",6262); // IP address of receiver
ObjectInputStream ois=new ObjectInputStream(client.getInputStream());
ObjectOutputStream oos=new ObjectOutputStream(client.getOutputStream());
System.out.println("Connected with server.");
boolean flag=false;
GoBackNListener listener=new GoBackNListener(ois,x);
listener=new GoBackNListener(ois,x);
listener.t.start();
int strt=0;
h=0;
oos.writeObject(x);
do
{
int c=h;
for(int i=h;i<count;i++)
{
System.out.print("|"+c+"|");
c=(c+1)%x;
}
System.out.println();
System.out.println();
h=strt;
for(int i=strt;i<x;i++)
{
System.out.println("Sending frame:"+h);
h=(h+1)%x;
System.out.println();
oos.writeObject(i);
oos.writeObject(data[i]);
Thread.sleep(100);
}
listener.t.join(3500);
if(listener.reply!=x-1) // checks for the last cumulative acknowledgement
{
System.out.println("No reply from server in 3.5 seconds. Resending data from frame no " + (listener.reply+1));
System.out.println();
strt=listener.reply+1; // starts resending data from the pervious acknowledgement received 
flag=false;
}
else
{
System.out.println("All elements sent successfully. Exiting");
flag=true;
}
}while(!flag);  // if the elements are not sent successfully 
oos.writeObject(-1);
}
}

class GoBackNListener implements Runnable
{
Thread t;
ObjectInputStream ois;
int reply,x;
GoBackNListener(ObjectInputStream o,int i)  
{
t=new Thread(this);
ois=o;
reply=-2;
x=i;
}
@Override
public void run() {
try
{
int temp=0;
while(reply!=-1)
{
reply=(Integer)ois.readObject(); // receiving acknowledgement from the receiver 
if(reply!=-1 && reply!=temp+1)
reply=temp;
if(reply!=-1)
{
temp=reply;
System.out.println("Acknowledgement of frame no " + (reply%x) + " recieved.");
System.out.println();
}
}
reply=temp;
}
catch(Exception e)
{
System.out.println("Exception => " + e);
}
}
}
