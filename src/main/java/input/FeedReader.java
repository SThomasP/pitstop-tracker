package input;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

// class for obtaining the initial JSON file and converting it to something usable.
public class FeedReader {

    private URL webAddress;
    private JSONObject jsonObject;


    public FeedReader(String webAddress) throws IOException{
        this.webAddress = new URL(webAddress);
        jsonObject = new JSONObject("{}");
    }

    // read the json from the feed, wrap in an object and then return it.
    public synchronized JSONObject getJsonObject(){
        try {
            InputStream inputStream = webAddress.openStream();
            jsonObject = new JSONObject( new JSONTokener(inputStream));
            inputStream.close();

        }
        catch (IOException e){
            System.out.println("Failed to get feed"+ e.getMessage());
        }
        return jsonObject;
    }
}
