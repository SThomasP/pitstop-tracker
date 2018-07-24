package Input;

import Database.DBInterface;
import Database.PitStop;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Controller {

    public static class FeedReaderThread extends Thread{
        private Controller controller;
        private long updateInterval;

        public FeedReaderThread(Controller controller, long updateInterval){
            this.controller = controller;
            this.updateInterval = updateInterval;

        }

        public void run(){
            try {
                while (true) {
                    synchronized (controller) {
                        controller.updateDatabase();
                    }
                    Thread.sleep(updateInterval);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }

    private DBInterface dbInterface;
    private FeedReader reader;


    public Controller(String databaseLocation, String feedLocation){
        dbInterface = new DBInterface(databaseLocation);
        try {
            reader = new FeedReader(feedLocation);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public synchronized void updateDatabase(){
        JSONObject json = reader.getJsonObject();
        JSONArray vehicles =json.getJSONArray("vehicles");
            for(int i = 0; i < vehicles.length(); i++ ){
                JSONObject raceVehicle = vehicles.getJSONObject(i);
                int vehicleNumber = raceVehicle.getInt("vehicle_number");
                JSONArray pitStops = raceVehicle.getJSONArray("pit_stops");
                for (int j = 0; j < pitStops.length(); j++){
                    JSONObject pitStopObject = pitStops.getJSONObject(j);
                    PitStop pitStop = new PitStop(vehicleNumber, pitStopObject.getDouble("pit_in_elapsed_time"), pitStopObject.getDouble("pit_out_elapsed_time"));
                    if (!dbInterface.checkPitStopInDatabase(pitStop)) {
                        dbInterface.addPitStop(pitStop);
                        System.out.println("New Pit Stop added: ("+pitStop.vehicleNumber+","+pitStop.inTime+")");
                    }
                }
            }

    }


    public JSONArray getPitStops(){
        return null;
    }


    public static void main(String[] args){
        Controller controller = new Controller("pitstops.db", "http://localhost:9000/api/livefeed");
        FeedReaderThread feedReaderThread = new FeedReaderThread(controller, 500);
        feedReaderThread.start();

    }

}
