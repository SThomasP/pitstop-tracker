package website;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
// add in some static methods to make writing the webserver code easier.
public abstract class WebAppHandler implements HttpHandler {

    // respond with a standard response (status code 200), everything good.
    public static void respond(String response, HttpExchange exchange) throws IOException{
        respond(response, 200, exchange);
    }

    // respond with another status code (100s, 300s), also everything else runs on this
    public static void respond(String response, int httpCode, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(httpCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // simple response for error status codes (400s, 500s)
    public static void respondError(int httpCode, HttpExchange exchange) throws IOException{
        String response = "<html><body><h1>Error "+ httpCode +"</h1></body></html>";
        respond(response, httpCode, exchange);
    }

    // set a header, most commonly used at the moment to set the MIME type
    public static void setHeader(String name, String content, HttpExchange exchange){
        Headers headers = exchange.getResponseHeaders();
        headers.add(name, content);

    }


}
