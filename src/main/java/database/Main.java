package database;

import input.FeedDataHandler;
import website.SiteDataHandler;
import website.WebApp;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
        try {
            FeedDataHandler handler = new FeedDataHandler(args[0], args[1]);
            WebApp app = new WebApp(new SiteDataHandler(args[0]), 8080);
            app.start();
            handler.startAcquisitionThread();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Please run the jar file with two arguments");
            System.out.println("The First should be the (desired) location of the database, eg: pitstops.db");
            System.out.println("The Second should be the location of the live feed eg. http://localhost:9000/api/livefeed");
        }

    }
}
