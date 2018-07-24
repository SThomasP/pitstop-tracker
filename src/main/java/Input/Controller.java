package Input;

import Database.DBInterface;
import Database.PitStop;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

// main class and a controller for connecting all the parts.
public class Controller {

    private DBInterface dbInterface;
    private FeedReader reader;

    // Thread for reading from the feed and then writing to the database.
    public static class FeedReaderThread extends Thread{
        private Controller controller;
        private long updateInterval;

        public FeedReaderThread(Controller controller, long updateInterval){
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
    public Controller(String databaseLocation, String feedLocation){
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

                        // System.out.println("New Pit Stop added: ("+pitStop.vehicleNumber+","+pitStop.inTime+")");
                    }
                }
            }

    }


    public JSONArray getPitStops(){
        return null;
    }

    // main thread and method, for launching the controller and the various sub threads.
    public static void main(String[] args){
        Controller controller = new Controller(args[0], args[1]);
        FeedReaderThread feedReaderThread = new FeedReaderThread(controller, 500);
        feedReaderThread.start();

    }

}
