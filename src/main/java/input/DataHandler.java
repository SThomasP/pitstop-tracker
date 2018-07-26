package input;

import database.DBInterface;
import database.PitStop;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

// main class and a controller for connecting all the parts.
public class DataHandler {

    private DBInterface dbInterface;
    private FeedReader reader;
    private Thread acquisitionThread;

    public void startAcquisitionThread() {
        acquisitionThread = new DataAcquisitonThread(this, 1000);
        acquisitionThread.start();
    }

    public void stopAcquisitionThread(){
        acquisitionThread.interrupt();
    }

    // Thread for reading from the feed and then writing to the database.
    public static class DataAcquisitonThread extends Thread{
        private DataHandler controller;
        private long updateInterval;

        public DataAcquisitonThread(DataHandler controller, long updateInterval){
            this.controller = controller;
            this.updateInterval = updateInterval;

        }

        // call the update method repeatedly at a fixed interval.
        public void run(){
            try {
                while (true) {
                    controller.updateDatabase();
                    Thread.sleep(updateInterval);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }

    // pass in the locations of the database and feed.
    public DataHandler(String databaseLocation, String feedLocation){
        dbInterface = new DBInterface(databaseLocation);
        try {
            reader = new FeedReader(feedLocation);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    // refresh the feed and then update the database.
    public synchronized void updateDatabase(){
        // get the JSON object from the feed reader
        JSONObject json = reader.getJsonObject();

        // iterate through the vehicles in the JSON object
        JSONArray vehicles =json.getJSONArray("vehicles");
            for(int i = 0; i < vehicles.length(); i++ ){
                JSONObject raceVehicle = vehicles.getJSONObject(i);
                int vehicleNumber = raceVehicle.getInt("vehicle_number");

                // then iterate through the pit stops that that vehicle made.
                JSONArray pitStops = raceVehicle.getJSONArray("pit_stops");
                for (int j = 0; j < pitStops.length(); j++){
                    JSONObject pitStopObject = pitStops.getJSONObject(j);
                    // create a PitStop object based on the data
                    PitStop pitStop = new PitStop(vehicleNumber, pitStopObject.getDouble("pit_in_elapsed_time"), pitStopObject.getDouble("pit_out_elapsed_time"));

                    // check to see if the PitStop is already in the database.
                    if (!dbInterface.checkPitStopInDatabase(pitStop)) {

                        //if not, add it.
                        dbInterface.addPitStop(pitStop);

                        // System.out.println("New Pit Stop added: ("+pitStop.vehicleNumber+","+pitStop.timeIn+")");
                    }
                }
            }

    }

    // get the pit stops in the database, in a json format that can be changed into a REST API.
    public String getPitStopsJSONArray()
    {
        ArrayList<PitStop> pitStops = dbInterface.getPitStops();
        JSONArray pitStopsJSONArray = new JSONArray();
        for (PitStop pitStop :pitStops){
            JSONObject pitStopJSON = new JSONObject();
            pitStopJSON.put("vehicle_number", pitStop.vehicleNumber);
            pitStopJSON.put("time_in", pitStop.timeIn);
            pitStopJSON.put("time_out", pitStop.timeOut);
            pitStopJSON.put("comment", pitStop.comment);

            pitStopsJSONArray.put(pitStopJSON);
        }
        return pitStopsJSONArray.toString();
    }

    public synchronized void updateComment(String objectString){
        System.out.println(objectString);
        JSONObject object = new JSONObject(objectString);
        PitStop pitStop = new PitStop(object.getInt("vehicle_number"), object.getDouble("time_in"), object.getDouble("time_out"));
        pitStop.comment = object.getString("comment");
        dbInterface.updateComment(pitStop);
    }

    // main thread and method, for launching the controller and the various sub threads.
    public static void main(String[] args){
        DataHandler controller = new DataHandler(args[0], args[1]);
        controller.startAcquisitionThread();

    }

}
