import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigital;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.*;
import java.util.Arrays;
import java.util.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;

public class BerrySafe{
	
	/**** class variables ****/
	
	static boolean intruderDetected;
	static boolean isArmed;

	//Server I/O variables
	static ServerSocket servSock;
	static Socket sock;
	static InputStream sockIn;
	static PrintStream sockOut;

	//DoorSensor variables
	static DoorSensor doorSens;
	static ServerSocket doorServSock;
	static Socket doorSock;
	static InputStream doorIn;

	//MotionSensor variables
	static MotionSensor motionSens;
	static ServerSocket motionServSock;
	static Socket motionSock;
	static InputStream motionIn;

	//Buzzer variables
	
	static GpioController gpio;
	static Pin buzzerPin;	
	static GpioPinDigitalOutput buzzerOutput;

	public void armAlarm(){
		//when alarm is active, triggering of the Door/Window Sensors or Motion Sensor should result in Buzzer going off, a photo being taken, and the client being alerted
		isArmed = true;
		return;
	}

	public void disarmAlarm(){
		isArmed = false;
		return;
	}

	public static void activateBuzzer(){
		//buzzerOutput = gpio.provisionDigitalOutputPin(buzzerPin, PinState.HIGH); 
		return;
	}

	public static void deactivateBuzzer(){
		//buzzerOutput = gpio.provisionDigitalOutputPin(buzzerPin, PinState.LOW); 
		return;
	}

	public static void setupSensors() throws IOException{
		//setup GPIO ports for Motion Sensor, Door/Windows Sensors, and Buzzer
		
		// Setup Motion Sensor thread + socket communication
		motionSens = new MotionSensor();
		motionSens.start();
		motionServSock = new ServerSocket(1338);
		motionSock = motionServSock.accept();
		motionIn = motionSock.getInputStream();

		// Setup Door Sensor thread + socket communication
		doorSens = new DoorSensor();
		doorSens.start();
		doorServSock = new ServerSocket(1339);
		doorSock = doorServSock.accept();
		doorIn = doorSock.getInputStream();	
	
		// Setup Buzzer
		
		gpio = GpioFactory.getInstance();
		Pin pins[] = RaspiPin.allPins();
		Arrays.sort(pins);
		buzzerPin = pins[1];
		buzzerOutput.setShutdownOptions(true, PinState.LOW);

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
			sockIn = sock.getInputStream();
			sockOut = new PrintStream(sock.getOutputStream());
		}
		catch(IOException e){
			//error handling here
		}
	}

	public static void main(String[] args) throws IOException{
		//setup server/client I/O variables
		setupSocket(1337);
		
		//setup sensors
		setupSensors();

		do{
			try{
				//main program loop, check for changes in user/sensor input and react accordingly
				while(true){
					boolean notifyApp = false;
					//check for messages from app
					if(motionIn.available() > 0){
						if(!isArmed || intruderDetected){
							//if alarm isnt on, or if alarm is already triggered, then do nothing
						}else{
							//set intruderDetected variable to true
							intruderDetected = true;
							
							//activate buzzer
							activateBuzzer();
						
							//notify app of intruder
							notifyApp = true;

							//clear motion sensor input stream
							motionIn.skip(1000);
						}
					}

					//check for messages from DoorSensor
					if(doorIn.available() > 0){
						if(!isArmed || intruderDetected){
							//if alarm isnt on, or if alarm is already triggered, then do nothing
						}else{
							//set intruderDetectedvariable to true
							intruderDetected = true;
		
							//activate buzzer
							activateBuzzer();

							//notify app of intruder
							notifyApp = true;

							//clear door sensor input stream
							doorIn.skip(1000);
						}
					}

					//check for messages from MotionSensor
					if(sockIn.available() > 0){
						Scanner scan = new Scanner(sockIn);
						String command = scan.next();
						if(command.equals("1")){
							//send a picture
						}
						else if(command.equals("2")){
							//arm alarm
							isArmed = true;
							sockIn.skip(1);
						}
						else if(command.equals("3")){
							//disarm alarm
							isArmed = false;
							intruderDetected = false;
							deactivateBuzzer();
							sockIn.skip(1);
						}
					}
					Thread.sleep(500);
				}		
			}catch(Exception e){
				//put error handling here
			}
		}while(true);
	}
}
