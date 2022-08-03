import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{

private Socket client;
private BufferedReader in;
private PrintWriter out;

private boolean done;

    @Override
    public void run() {
        done = false;
        try{
             client = new Socket("localhost",9999);
             out = new PrintWriter(client.getOutputStream(),true);
             in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             InputHandler inputHandler = new InputHandler();
             Thread t = new Thread(inputHandler);
             t.start();
             String inMesaage;
             while((inMesaage=in.readLine())!=null){
                 System.out.println(inMesaage);
             }
        }catch(IOException e){
shutdown();
        }
    }

    public void shutdown(){
        done = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }catch (IOException e){

        }
    }
    public class InputHandler implements Runnable{


        @Override
        public void run() {
            try{
              BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
              while(!done){
               String message = inReader.readLine();
               if(message.equals("/quit")){
                   out.println(message);
                   inReader.close();
                   shutdown();
               }
               else{
                 out.println(message);
               }
              }
            }catch(IOException e){
shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client cl = new Client();
        cl.run();
    }
}
