package website;

import com.sun.net.httpserver.HttpExchange;
import input.FeedDataHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


// Handler for dealing with comments, pass them to the data handler, which in turn passes it to the database interface.
public class CommentHandler extends WebAppHandler {
    private SiteDataHandler siteDataHandler;

    public CommentHandler(SiteDataHandler siteDataHandler){
        this.siteDataHandler = siteDataHandler;
    }

    public void handle(HttpExchange exchange) throws IOException {

        // check that this is a post request.
        if (exchange.getRequestMethod().equals("POST")){

            // read the body to a buffer
            BufferedReader br = new BufferedReader( new InputStreamReader(exchange.getRequestBody()));
            String pitStopJSONString = "";
            String partial;
            partial = br.readLine();
            while(partial != null){
                pitStopJSONString += partial;
                partial = br.readLine();
            }
            System.out.println(pitStopJSONString);
            //and then pass it to the data handler
            siteDataHandler.updateComment(pitStopJSONString);

            //and then respond with a short message of acceptance.
            respond("True", exchange);
        }
        else{
            respondError(405, exchange);
        }
    }
}
