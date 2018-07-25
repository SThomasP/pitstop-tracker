package website;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;

// Serve a static file to a web server, used for the index page and the javaScript file.
public class StaticFileHandler extends WebAppHandler  {

    private File file;
    private String mimeType;

    public StaticFileHandler(String filePath, String mimeType){
        file = new File(filePath);
        System.out.println(file.exists());
        this.mimeType = mimeType;
    }


    // read the file from a stream and then send it through the internet.
    public void handle(HttpExchange exchange) throws IOException {
        setHeader("Content-Type", mimeType, exchange);
        FileInputStream inputStream = new FileInputStream(file);
        exchange.sendResponseHeaders(200, file.length());
        OutputStream outputStream = exchange.getResponseBody();

        // run the contents of the inputStream through a buffer and then send it to an OutputStream
        byte[] buffer = new byte[1024];
        int len = inputStream.read(buffer);
        while (len != -1){
            outputStream.write(buffer, 0, len);
            len = inputStream.read(buffer);
        }
        inputStream.close();
        outputStream.close();


    }
}
