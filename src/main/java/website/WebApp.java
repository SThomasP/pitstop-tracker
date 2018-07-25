package website;

import com.sun.net.httpserver.HttpServer;
import input.DataHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebApp {


    private HttpServer server;
    private DataHandler dataHandler;


    public WebApp(DataHandler dataHandler) throws IOException {
        this.dataHandler = dataHandler;
        dataHandler.startAcquisitionThread();
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api", new ApiHandler(dataHandler));
        server.createContext("/", new StaticFileHandler("src/main/resources/index.html", "text/html"));
        server.createContext("/ajax_script.js", new StaticFileHandler("src/main/resources/script.js", ""));
        server.setExecutor(null);
        server.start();
    }


    public static void main(String[] args) throws IOException {
        DataHandler dataHandler = new DataHandler("pitstops.db", "http://localhost:9000/api/livefeed");
        WebApp webApp = new WebApp(dataHandler);

    }
}
