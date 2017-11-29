import java.util.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;

public class MotionSensor extends Thread{
	
	public void run(){
		try{
			//setup socket communication
			Socket sock = new Socket("127.0.0.1", 1338);
			PrintStream out = new PrintStream(sock.getOutputStream());			
			
			//bluetooth stuff goes here
			//send messages to main class with 'out.print("message")'
		}catch(Exception e){
			//error handling here
		}
	}
}
