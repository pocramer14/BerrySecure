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
	ServerSocket servSock;
	Socket sock;
	Scanner sockScan;
	PrintStream sockOut;
	
	public void armAlarm(){
		return;
	}

	public void disarmAlarm(){
		return;
	}

	public static void setupSensors(){
		return;
	}

	public static void takePicture(){
		return;
	}
	
	public static void sendPicToApp(){
		return;
	}

	public static void setupSocket(int port){
		servSock = new ServerSocket(port);
		sock = new servSock.accept();
		sockScan = new Scanner(sock.getInputStream());
		sockOut = new PrintStream(sock.getOutputStream());
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
