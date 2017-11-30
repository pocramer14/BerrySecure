import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.io.File;
import javax.imageio.ImageIO;
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
	/*static {
		Webcam.setDriver(new V4l4jDriver());
	}*/	
	static boolean intruderDetected;
	static boolean isArmed;
	static int imageCount;

	//Server I/O variables
	static ServerSocket servSock;
	static Socket sock;
	static InputStream sockIn;
	static PrintStream sockOut;

	//DoorSensor variables
	static GpioPinDigitalInput doorInput;

	//MotionSensor variables
	static Pin motionSensorPin;
	static GpioPinDigitalInput motionInput;

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
		buzzerOutput.high();
		return;
	}

	public static void deactivateBuzzer(){
		buzzerOutput.low(); 
		return;
	}

	public static void setupSensors() throws IOException{
		System.out.println("setting up sensors..");
		//setup GPIO ports for Motion Sensor, Door/Windows Sensors, and Buzzer
		
		// Setup Buzzer
		gpio = GpioFactory.getInstance();
		Pin pins[] = RaspiPin.allPins();
		Arrays.sort(pins);
		buzzerPin = pins[7];
		buzzerOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, PinState.LOW);
		buzzerOutput.setShutdownOptions(true, PinState.LOW);
		buzzerOutput.low();

		// Setup Motion Sensor
		motionSensorPin = pins[0];
		motionInput = gpio.provisionDigitalInputPin(motionSensorPin, PinPullResistance.PULL_DOWN);
		motionInput.setShutdownOptions(true);

		// Setup Door Sensor
		doorInput = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, PinPullResistance.PULL_DOWN);
		doorInput.setShutdownOptions(true);

		System.out.println("finished setting up sensors..");
		return;
	}

	public static void takePicture() throws IOException{
		Webcam webcam = Webcam.getDefault();
		if(webcam != null){
			System.out.println("Webcam: "+webcam.getName());
		}else{System.out.println("No Webcam Detected");}
		webcam.setViewSize(new Dimension(640, 480));
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();
		String filename = "/pics/screenshot"+imageCount;
		ImageIO.write(webcam.getImage(), "JPG", new File(filename));
		webcam.close();
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
			//error handling her
			System.out.println("error connecting to app: "+e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception{
		System.out.println("Setting up server/client socket..");
		
		//setup server/client I/O variables
		ServerListener servListen = new ServerListener();
		servListen.start();
		setupSocket(1444);	
		Scanner scan = new Scanner(sockIn);
		System.out.println("Main thread connected to Server Listener...");
		
		//setup sensors
		setupSensors();
		activateBuzzer();
		Thread.sleep(1000);
		isArmed = false;
		imageCount = 0;
		//Scanner scan = new Scanner(sockIn);
		System.out.println("Main function finished setting up...");
		//do{
			try{
				//main program loop, check for changes in user/sensor input and react accordingly
				while(true){
					imageCount++;
					//takePicture();
					boolean notifyApp = false;
					//System.out.println("Buzzer State: "+buzzerOutput.getState());
					if(motionInput.isHigh() && isArmed){
						//if alarm isnt on, or if alarm is already triggered, then do nothing
						//System.out.println("Intruder Detected by Motion Sensor");
						//set intruderDetected variable to true
						intruderDetected = true;
							
						//activate buzzer
						deactivateBuzzer();
						
						//notify app of intruder
						notifyApp = true;
					}

					//check for messages from DoorSensor
					/*
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
					*/
					//check for messages from MotionSensor

					if(scan.hasNext()){
						String command = scan.next();
						System.out.println("Command received from Server Listener: "+command);
						if(command.equals("1")){
							//send a picture
							System.out.println("Send picture to client");
						}
						else if(command.contains("Arm")){
							//arm alarm
							isArmed = true;
							System.out.println("Arm alarm");
							//sockIn.skip(1);
						}
						else if(command.contains("Disarm")){
							//disarm alarm
							isArmed = false;
							intruderDetected = false;
							System.out.println("Disarm Alarm");
							activateBuzzer();
							//sockIn.skip(1);
						}
					}
				}		
			}catch(Exception e){
				//put error handling here
				System.out.println("Error thrown in main: "+e.getMessage());
			}
		//}while(true);
	}
}
