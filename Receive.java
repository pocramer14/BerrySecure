/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package receive;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import sun.misc.BASE64Encoder;

/**
 *
 * @author timtran
 */
public class Receive {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       
        
        //Encode image
        BufferedImage img = ImageIO.read(new File("/Users/timtran/Desktop/capturetest.png"));
        BufferedImage newImg;
        String imgstr;
        imgstr = encodeToString(img, "png");
        //System.out.println(imgstr);
        
        //create socket connection        
        ServerSocket serverSocket = new ServerSocket(9090);
        Socket socket = serverSocket.accept();
        
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(imgstr);
        System.out.println("length of string: " + imgstr.length());
        serverSocket.close();
    }
        //function to encode
        public static String encodeToString(BufferedImage image, String type) throws IOException {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();        

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return imageString; 
        }
}
        
        
      
        
        
    
