package website;

import com.sun.net.httpserver.HttpServer;
import input.DataHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebApp {


    private HttpServer server;
    private DataHandler dataHandler;
    private int port;

    // start the server and the handler's acquisition thread.
    public void start(){
        dataHandler.startAcquisitionThread();
        server.start();
        System.out.println("Server Running on Port "+ port);
    }

    // stop the server, not currently used.
    public void stop(){
        dataHandler.stopAcquisitionThread();
        server.stop(0);
    }

    // set the up the server, with it's various handlers
    public WebApp(DataHandler dataHandler, int port) throws IOException {
        this.dataHandler = dataHandler;
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", new ApiHandler(dataHandler));
        server.createContext("/add/comment", new CommentHandler(dataHandler));
        server.createContext("/", new StaticFileHandler("/index.html", "text/html"));
        server.createContext("/ajax_script.js", new StaticFileHandler("/script.js", "application/javascript"));
        server.setExecutor(null);
    }

    public static void main(String[] args){
        try {
            DataHandler handler;
            try {
                handler = new DataHandler(args[0], args[1]);
            }
            catch (ArrayIndexOutOfBoundsException e){
                handler = new DataHandler("pitstops.db", "http://localhost:9000/api/livefeed");
            }
            WebApp app = new WebApp(handler, 8080);
            app.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
