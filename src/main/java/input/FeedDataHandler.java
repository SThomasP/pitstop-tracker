package input;

import database.DBInterface;
import database.PitStop;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

// main class and a controller for connecting all the parts.
public class FeedDataHandler {

    private DBInterface dbInterface;
    private FeedReader reader;
    private Thread acquisitionThread;

    public static class DataAcquisitionThread extends Thread {
        private FeedDataHandler controller;
        private long updateInterval;

        public DataAcquisitionThread(FeedDataHandler controller, long updateInterval) {
            this.controller = controller;
            this.updateInterval = updateInterval;

        }

        // call the update method repeatedly at a fixed interval.
        public void run() {
            try {
                while (true) {
                    controller.updateDatabase();
                    Thread.sleep(updateInterval);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    // start the acquisition thread, which polls data from the feed
    public void startAcquisitionThread() {
        acquisitionThread = new DataAcquisitionThread(this, 1000);
        acquisitionThread.start();
    }

    // stop the acquisition thread, meaning that the application will stop polling the feed
    public void stopAcquisitionThread() {
        acquisitionThread.interrupt();
    }

    // Thread for reading from the feed and then writing to the database.


    // pass in the locations of the database and feed.
    public FeedDataHandler(String databaseLocation, String feedLocation) {
        dbInterface = new DBInterface(databaseLocation, true);
        try {
            reader = new FeedReader(feedLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // refresh the feed and then update the database.
    public synchronized void updateDatabase() {
        // get the JSON object from the feed reader
        JSONObject json = reader.getJsonObject();
        try {
            // iterate through the vehicles in the JSON object
            JSONArray vehicles = json.getJSONArray("vehicles");
            for (int i = 0; i < vehicles.length(); i++) {
                JSONObject raceVehicle = vehicles.getJSONObject(i);
                int vehicleNumber = raceVehicle.getInt("vehicle_number");

                // then iterate through the pit stops that that vehicle made.
                JSONArray pitStops = raceVehicle.getJSONArray("pit_stops");
                for (int j = 0; j < pitStops.length(); j++) {
                    JSONObject pitStopObject = pitStops.getJSONObject(j);
                    // create a PitStop object based on the data
                    PitStop pitStop = new PitStop(vehicleNumber, pitStopObject.getDouble("pit_in_elapsed_time"), pitStopObject.getDouble("pit_out_elapsed_time"));

                    // check to see if the PitStop is already in the database.
                    if (!dbInterface.checkPitStopInDatabase(pitStop)) {

                        //if not, add it.
                        dbInterface.addPitStop(pitStop);

                        System.out.println("New Pit Stop added: (" + pitStop.vehicleNumber + "," + pitStop.timeIn + ")");
                    }
                }
            }
        } catch (JSONException e){
            System.err.println("Error reading data from JSON :" + e.getMessage());
        }

    }


    // main thread and method, for launching the controller and the various sub threads.
    public static void main(String[] args) {
        FeedDataHandler dataHandler = new FeedDataHandler(args[0], args[1]);
        dataHandler.startAcquisitionThread();

    }

}
