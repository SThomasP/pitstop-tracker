package website;

import com.sun.net.httpserver.HttpExchange;
import input.FeedDataHandler;

import java.io.IOException;

// Serve the contents of the database as a JSON array to the client.
public class ApiHandler extends WebAppHandler {

    private SiteDataHandler siteDataHandler;

    public ApiHandler(SiteDataHandler siteDataHandler){
        this.siteDataHandler = siteDataHandler;
    }

    public void handle(HttpExchange exchange){
        try{
            // make sure that it's a get request
            if(exchange.getRequestMethod().equals("GET")){
                setHeader("Content-Type", "application/json", exchange);

                // get the string representation of the json array and then send it as a response.
                String response = siteDataHandler.getPitStopsJSONArray();
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
