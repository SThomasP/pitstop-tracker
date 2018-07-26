package website;

import com.sun.net.httpserver.HttpExchange;
import input.DataHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommentHandler extends WebAppHandler {
    private DataHandler dataHandler;

    public CommentHandler(DataHandler dataHandler){
        this.dataHandler = dataHandler;
    }

    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")){
            BufferedReader br = new BufferedReader( new InputStreamReader(exchange.getRequestBody()));
            String pitStopJSONString = "";
            String partial;
            partial = br.readLine();
            while(partial != null){
                pitStopJSONString += partial;
                partial = br.readLine();
            }
            System.out.println(pitStopJSONString);
            dataHandler.updateComment(pitStopJSONString);
            respond("True", exchange);
        }
        else{
            respondError(405, exchange);
        }
    }
}
