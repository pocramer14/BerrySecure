import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class DoorSensor extends Thread{

	public void run(){
		try{
			//setup socket communication
			Socket sock = new Socket("127.0.0.1", 1339);
			PrintStream out = new PrintStream(sock.getOutputStream());		
			
			//setup GPIO
			final GpioController gpio = GpioFactory.getInstance();
			Pin pins[] = RaspiPin.allPins();
			Arrays.sort(pins);
			Pin pin = pins[1];
			GpioPinDigitalInput inputPin = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
			inputPin.setShutdownOptions(true);

			// Override Gpio Event listener to write to socket upon event
			inputPin.addListener(new GpioPinListenerDigital(){
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event){
					if(event.getState() == PinState.HIGH)
						out.print("motion sensor triggered");
				}
			});

			//loop while listening for Gpio Events
			while(true){
				Thread.sleep(500);
			}
		}catch(Exception e){
			//error handling here
		}
	}
}
