import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
   private ArrayList<ConnectionHandler> connectionHandlers;
    private ServerSocket serverSocket;
    private boolean done;
    private ExecutorService pool;
    public Server(){
        connectionHandlers = new ArrayList<>();
        done = false;
    }
    @Override
    public void run() {
        try {
            pool =  Executors.newCachedThreadPool();
            serverSocket = new ServerSocket(9999);
            while(!done) {
                Socket client = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connectionHandlers.add(handler);
                pool.execute(handler);
            }
        }catch (IOException e){

        }
    }

    public void broadcast(String message){
        for(ConnectionHandler ch:connectionHandlers){
            if(ch!=null){
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown(){
        try {
            done = true;
            pool.shutdown();
            if (!(serverSocket.isClosed())) {

                serverSocket.close();
            }
            for (ConnectionHandler ch:connectionHandlers){
                ch.shutdown();}
        }
     catch (IOException e) {

    }
}


    public class ConnectionHandler implements Runnable{
        private Socket client;
        private BufferedReader in;
        private  String name;
        private PrintWriter out;
        public ConnectionHandler(Socket socket){
            this.client = socket;
        }
        @Override
        public void run() {
           try{
               out = new PrintWriter(client.getOutputStream(),true);
               in = new BufferedReader(new InputStreamReader(client.getInputStream()));
               out.println("Enter you name");
               name = in.readLine();
               System.out.println(name + " connected");
               broadcast(name + " Joined the room");
               String message;
               while((message=in.readLine())!=null){
                   if(message.startsWith("/quit")){
                       System.out.println(name + ": left the room");
                     broadcast(name + ": left the room");
                     shutdown();
                   }
                   else{
                       broadcast(name + ": " + message);
                   }
               }
           }catch (IOException e){
                  shutdown();
           }
        }
        public void sendMessage(String message){
            out.println(message);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            }catch (IOException e){

            }
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
