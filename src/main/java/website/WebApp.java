package website;

import com.sun.net.httpserver.HttpServer;
import input.FeedDataHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebApp {


    private HttpServer server;
    private SiteDataHandler siteDataHandler;
    private int port;

    // start the server and the handler's acquisition thread.
    public void start(){
        server.start();
        System.out.println("Server Running on Port "+ port);
    }

    // stop the server, not currently used.
    public void stop(){
        server.stop(0);
    }

    // set the up the server, with it's various handlers
    public WebApp(SiteDataHandler siteDataHandler, int port) throws IOException {
        this.siteDataHandler = siteDataHandler;
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", new ApiHandler(siteDataHandler));
        server.createContext("/add/comment", new CommentHandler(siteDataHandler));
        server.createContext("/", new StaticFileHandler("/index.html", "text/html"));
        server.createContext("/ajax_script.js", new StaticFileHandler("/min_script.js", "application/javascript"));
        server.setExecutor(null);
    }

    public static void main(String[] args){
        try {
            WebApp app = new WebApp(new SiteDataHandler("pitstops.db"), 8080);
            app.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
