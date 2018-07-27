package website;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;

// Serve a static file to a web server, used for the index page and the javaScript file.
public class StaticFileHandler extends WebAppHandler  {

    private String file;
    private String mimeType;

    public StaticFileHandler(String filePath, String mimeType){
        file = filePath;
        this.mimeType = mimeType;
    }


    // read the file from a stream and then send it through the internet.
    public void handle(HttpExchange exchange) throws IOException {
        setHeader("Content-Type", mimeType, exchange);
        System.out.println("Serving "+ file);
        InputStream inputStream = getClass().getResourceAsStream(file);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int len = inputStream.read(data);
        while (len != -1){
            byteBuffer.write(data, 0, len);
            len = inputStream.read(data);
        }
        inputStream.close();
        data = byteBuffer.toByteArray();
        byteBuffer.close();
        exchange.sendResponseHeaders(200, data.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(data);
        // run the contents of the inputStream through a buffer and then send it to the OutputStream
        outputStream.close();


    }
}
