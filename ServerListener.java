import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.io.InputStream;

public class ServerListener extends Thread{

	public void run(){
		try{
			Socket sock2 = new Socket("127.0.0.1", 1444);
			System.out.println("Started Server Listener Thread");
			PrintStream out = new PrintStream(sock2.getOutputStream());
			//setup server socket communication
			ServerSocket servSock = new ServerSocket(1337);
			Socket sock = servSock.accept();
			System.out.println("ServerListener connected to app..");
			InputStream sockIn = sock.getInputStream();
			while(true){
				Scanner scan = new Scanner(sockIn);
				PrintStream sockOut = new PrintStream(sock2.getOutputStream());
				if(scan.hasNext()){
					String command = scan.next();
					System.out.println("Received message from app on ServerListener: "+command);
					//forward message received from app to main thread
					out.println(command);
					servSock.close();
					sock.close();
					servSock = new ServerSocket(1337);
					sock = servSock.accept();
					sockIn = sock.getInputStream();
					sockOut = new PrintStream(sock.getOutputStream());
					System.out.println("ServerListener connected to app..");
				}
			}	
		}catch(Exception e){
			//error handling here
		}
	}
}
