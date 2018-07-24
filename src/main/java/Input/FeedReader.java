package Input;


import Database.PitStop;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;

public class FeedReader {

    private URL webAddress;
    private JSONObject jsonObject;

    public FeedReader(String webAddress) throws IOException{
        this.webAddress = new URL(webAddress);
    }

    public synchronized void update(){
        try {
            jsonObject = new JSONObject( new JSONTokener(webAddress.openStream()));


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public JSONObject getJson(){
        return jsonObject;
    }

    public static void main(String[] args){
        try {
            FeedReader fr = new FeedReader("http://localhost:9000/api/livefeed");
            fr.update();
            System.out.println("Hello World");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
