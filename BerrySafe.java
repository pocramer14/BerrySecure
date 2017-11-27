import java.util.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;

public class BerrySafe{
	
	/**** class variables ****/
	
	boolean isArmed;

	//Server I/O variables
	static ServerSocket servSock;
	static Socket sock;
	static Scanner sockScan;
	static PrintStream sockOut;
	
	public void armAlarm(){
		//when alarm is active, triggering of the Door/Window Sensors or Motion Sensor should result in Buzzer going off, a photo being taken, and the client being alerted
		return;
	}

	public void disarmAlarm(){
		return;
	}

	public static void setupSensors(){
		//setup GPIO ports for Motion Sensor, Door/Windows Sensors, and Buzzer
		return;
	}

	public static void takePicture(){
		return;
	}
	
	public static void sendPicToApp(){
		return;
	}

	public static void setupSocket(int port){
		try{
			servSock = new ServerSocket(port);
			sock = servSock.accept();
			sockScan = new Scanner(sock.getInputStream());
			sockOut = new PrintStream(sock.getOutputStream());
		}
		catch(IOException e){
			//error handling here
		}
	}

	public static void main(String[] args) throws IOException{
		//setup server/client I/O variables
		setupSocket(1337);

		String command = "";
		do{
			try{
				//main program loop, check for changing in user/sensor input and react accordingly
			}catch(Exception e){
				//put error handling here
			}
		}while(command.equals("QUIT")==false);
	}
}
