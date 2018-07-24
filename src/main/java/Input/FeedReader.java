package Input;

import Database.DBInterface;
import Database.PitStop;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;

public class FeedReader {

    private URL webAddress;
    private JSONObject jsonObject;

    public FeedReader(String webAddress) throws IOException{
        this.webAddress = new URL(webAddress);
        jsonObject = new JSONObject("{}");
    }

    public synchronized JSONObject getJsonObject(){
        try {
            jsonObject = new JSONObject( new JSONTokener(webAddress.openStream()));


        }
        catch (IOException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void main(String[] args){

    }
}
