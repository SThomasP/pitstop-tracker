package website;

import com.sun.net.httpserver.HttpExchange;
import input.DataHandler;

import java.io.IOException;

// Serve the contents of the database as a JSON array to the client.
public class ApiHandler extends WebAppHandler {

    private DataHandler dataHandler;

    public ApiHandler(DataHandler dataHandler){
        this.dataHandler = dataHandler;
    }

    public void handle(HttpExchange exchange){
        try{
            // make sure that it's a get request
            if(exchange.getRequestMethod().equals("GET")){
                setHeader("Content-Type", "application/json", exchange);

                // get the string representation of the json array and then send it as a response.
                String response = dataHandler.getPitStopsJSONArray();
                respond(response, exchange);
            }
            else{
                respondError(405,exchange);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
