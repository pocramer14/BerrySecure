/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package send;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

/**
 *
 * @author timtran
 */
public class Send {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        Socket socket = new Socket("10.231.176.210", 9090);
        OutputStream outputStream = socket.getOutputStream();

        BufferedImage image = ImageIO.read(new File("/Users/timtran/Desktop/willsmith.jpg"));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);

        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        outputStream.write(size);
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.flush();
        System.out.println("Flushed: " + System.currentTimeMillis());
        
        Thread.sleep(120000);
        System.out.println("Closing: " + System.currentTimeMillis());
        socket.close();
        
        
    }
    
}
