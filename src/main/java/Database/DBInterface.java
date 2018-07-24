package Database;

import java.sql.*;
import java.util.ArrayList;

public class DBInterface {

    private String dbPath;

    // create a connection to the database.
    private Connection connect(){
        Connection databaseConnection = null;
        try {
            databaseConnection = DriverManager.getConnection(dbPath);

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return databaseConnection;
    }

    // add the prefixes to the filepath, and then connect to the database and reset the tables.
    public DBInterface(String filePath){
        dbPath = "jdbc:sqlite:" + filePath;

        Connection connection = this.connect();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                // drop the old table
                statement.execute("drop table if exists pitstops;");
                // and then create a new one
                statement.execute("create table pitstops (vehicleNumber integer, timeIn real, timeOut real not null,comment text, PRIMARY KEY (vehicleNumber, timeIn));");
                connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }


    // add a pit stop to the database, getting it's ID and adding that to the class.
    public void addPitStop(PitStop pitStop) {
        Connection connection = this.connect();
        if (connection != null) {
                // prepare the sql statement
                String insertSQL = "insert into pitstops (vehicleNumber, timeIn, timeOut, comment) values (?,?,?,?);";
                try {
                    PreparedStatement statement = connection.prepareStatement(insertSQL);
                    statement.setInt(1, pitStop.vehicleNumber);
                    statement.setDouble(2, pitStop.timeIn);
                    statement.setDouble(3, pitStop.timeOut);
                    statement.setString(4, pitStop.comment);
                    statement.executeUpdate();

                    connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

    }

    // update the comment on a pit stop.
    public void updateComment(PitStop pitStop){
        String comment = pitStop.comment;
        Connection connection = this.connect();
        if (connection != null){
            String updateSQL = "update pitstops set comment = ? where vehicleNumber = ? and timeIn = ?;";
            try {
                PreparedStatement statement = connection.prepareStatement(updateSQL);
                statement.setString(1, comment);
                statement.setInt(2, pitStop.vehicleNumber);
                statement.setDouble(3, pitStop.timeIn);
                statement.executeUpdate();

                connection.close();

            } catch (SQLException e){
                e.printStackTrace();
            }

        }

    }

    // check to see the pit stop is in the base, returning true if it is.
    public boolean checkPitStopInDatabase(PitStop pitStop){
        Connection connection = this.connect();
        String checkSQL = "SELECT COUNT(*) FROM pitstops where vehicleNumber = ? and timeIn = ? and timeOut = ?";
        if (connection != null){
            try {
                PreparedStatement statement = connection.prepareStatement(checkSQL);

                statement.setInt(1, pitStop.vehicleNumber);
                statement.setDouble(2, pitStop.timeIn);
                statement.setDouble(3, pitStop.timeOut);

                ResultSet results = statement.executeQuery();
                boolean inDatabase = results.getInt(1) == 1;
                connection.close();
                return inDatabase;

            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //get all pit stops from the database, saving them to the ArrayList
    public ArrayList<PitStop> getPitStops(){
        ArrayList<PitStop> pitStops = new ArrayList<PitStop>();
        Connection connection = this.connect();
        String getAllSQL = "SELECT * FROM pitstops";
        if (connection != null){
            try{
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(getAllSQL);
                while (results.next()){
                    PitStop pitStop = new PitStop(results.getInt("vehicleNumber"), results.getDouble("timeIn"), results.getDouble("timeOut"));
                    pitStop.comment = results.getString("comment");
                    pitStops.add(pitStop);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return pitStops;
    }


    // Testing the database read write code.
    public static void main(String[] args){
        DBInterface dbInterface = new DBInterface("pitstops.db");
        PitStop ps1 = new PitStop(33, 134.33, 0);
        dbInterface.addPitStop(ps1);

        System.out.println(dbInterface.checkPitStopInDatabase(ps1));

        PitStop ps2 = new PitStop(27, 145.23, 0);
        dbInterface.addPitStop(ps2);

        ps2.comment = "Cheating... ";
        dbInterface.updateComment(ps2);
        }


}
