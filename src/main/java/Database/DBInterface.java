package Database;

import java.sql.*;
import java.util.ArrayList;

public class DBInterface {

    private String dbPath;

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

    public DBInterface(String filePath){
        dbPath = "jdbc:sqlite:" + filePath;

        Connection connection = this.connect();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.execute("drop table if exists pitstops;");
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
        // check the id
        Connection connection = this.connect();
        if (connection != null) {
                // prepare the sql statement
                String insertSQL = "insert into pitstops (vehicleNumber, timeIn, timeOut) values (?,?,?);";
                try {
                    PreparedStatement statement = connection.prepareStatement(insertSQL);
                    statement.setInt(1, pitStop.vehicleNumber);
                    statement.setDouble(2, pitStop.inTime);
                    statement.setDouble(3, pitStop.outTime);
                    statement.executeUpdate();

                    connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

    }

    public void updateComment(PitStop pitStop){
        String comment = pitStop.comment;
        Connection connection = this.connect();
        if (connection != null){
            String updateSQL = "update pitstops set comment = ? where vehicleNumber = ? and timeIn = ?;";
            try {
                PreparedStatement statement = connection.prepareStatement(updateSQL);
                statement.setString(1, comment);
                statement.setInt(2, pitStop.vehicleNumber);
                statement.setDouble(3, pitStop.inTime);
                statement.executeUpdate();

                connection.close();

            } catch (SQLException e){
                e.printStackTrace();
            }

        }

    }

    public boolean checkPitStopInDatabase(PitStop pitStop){
        Connection connection = this.connect();
        String checkSQL = "SELECT COUNT(*) FROM pitstops where vehicleNumber = ? and timeIn = ? and timeOut = ?";
        if (connection != null){
            try {
                PreparedStatement statement = connection.prepareStatement(checkSQL);

                statement.setInt(1, pitStop.vehicleNumber);
                statement.setDouble(2, pitStop.inTime);
                statement.setDouble(3, pitStop.outTime);

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

    //get all pit stops from the database.
    public ArrayList<PitStop> getPitStops(){
        return null;
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
