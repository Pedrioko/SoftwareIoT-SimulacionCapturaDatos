package vista;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author GABRIEL
 */
public class Principal0 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        
        SerialPort sp=SerialPort.getCommPort("COM6");
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        if(sp.openPort()){
            System.out.println("El puerto está abierto");
                while(true){
                    if(sp.bytesAvailable()>0){
                      InputStream is = sp.getInputStream(); 
                      BufferedReader br=new BufferedReader(new InputStreamReader(sp.getInputStream()));
                      System.out.println(""+br.readLine());
                      Thread.sleep(500);
                    }
                }
        }
        else{
            System.out.println("No se puede abrir");
        }
    }
    
}
