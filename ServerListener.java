import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.io.InputStream;
import sun.misc.BASE64Encoder;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import java.io.ObjectOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
			//Scanner scan = new Scanner(sockIn);
			PrintStream sockOut = new PrintStream(sock2.getOutputStream());
			while(true){
				//Scanner scan = new Scanner(sockIn);
				//PrintStream sockOut = new PrintStream(sock2.getOutputStream());
				if(sockIn.available() > 0){
					Scanner scan = new Scanner(sockIn);
					String command = scan.next();
					System.out.println("Received message from app on ServerListener: "+command);
					//forward message received from app to main thread
					out.println(command);
					if(command.contains("Image")){
						System.out.println("ServerListener received image request");
						ServerSocket imageServSock = new ServerSocket(1338);
						Socket imageSock = imageServSock.accept();
						int index = BerrySafe.imageCount-1;
						System.out.println("Attempting to send file: pic"+index);
						BufferedImage img = ImageIO.read(new File("/home/pi/462/BerrySafe/BerrySecure/pics/pic"+index+".png"));
						BufferedImage newImg;
						//BufferedImage img = ImageIO.read(new File("/home/pi/462/BerrySafe/BerrySecure/pics/test.png"));
						String imgStr;
						imgStr = encodeToString(img, "png");
						ObjectOutputStream oos = null;
						oos = new ObjectOutputStream(imageSock.getOutputStream());
						oos.writeObject(imgStr);
						oos.close();
						imageServSock.close();
						imageSock.close();
					}
					servSock.close();
					sock.close();
					servSock = new ServerSocket(1337);
					sock = servSock.accept();
					sockIn = sock.getInputStream();
					sockOut = new PrintStream(sock2.getOutputStream());
					System.out.println("ServerListener connected to app..");
				}
			}	
		}catch(Exception e){
			//error handling here
			System.out.println("Error in ServerListener thread: "+e.getMessage());
		}
	}
	
	public static String encodeToString(BufferedImage image, String type) throws IOException{
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			BASE64Encoder encoder = new BASE64Encoder();
			imageString = encoder.encode(imageBytes);
			bos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return imageString;
	}
}
