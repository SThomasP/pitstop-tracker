package website;

import database.DBInterface;
import database.PitStop;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SiteDataHandler {

    private DBInterface dbInterface;

    public SiteDataHandler(String dbLocation){
        dbInterface = new DBInterface(dbLocation, false);

    }

    // get the pit stops in the database, in a json format that can be changed into a REST API.
    public String getPitStopsJSONArray() {
        ArrayList<PitStop> pitStops = dbInterface.getPitStops();
        JSONArray pitStopsJSONArray = new JSONArray();
        for (PitStop pitStop : pitStops) {
            JSONObject pitStopJSON = new JSONObject();
            pitStopJSON.put("vehicle_number", pitStop.vehicleNumber);
            pitStopJSON.put("time_in", pitStop.timeIn);
            pitStopJSON.put("time_out", pitStop.timeOut);
            pitStopJSON.put("comment", pitStop.comment);

            pitStopsJSONArray.put(pitStopJSON);
        }
        return pitStopsJSONArray.toString();
    }

    // add a comment to the database, from a string json object
    public synchronized void updateComment(String objectString) {
        // convert the string to an object.
        JSONObject object = new JSONObject(objectString);
        // convert the object to a pit stop object
        PitStop pitStop = new PitStop(object.getInt("vehicle_number"), object.getDouble("time_in"), object.getDouble("time_out"));
        pitStop.comment = object.getString("comment");
        // add it to the database.
        dbInterface.updateComment(pitStop);
    }

}
