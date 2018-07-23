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
                statement.execute("create table pitstops (id integer primary key, vehichleNumber integer, timeIn real not null, timeOut real not null,comment text);");
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }


    // add a pit stop to the database, getting it's ID and adding that to the class.
    public void addPitStop(PitStop pitStop) {
        // check the id
        int pitStopId = pitStop.getId();
        Connection connection = this.connect();
        if (connection != null) {
            // if the id isn't -1 then the pit stop should already be in the database.
            if (pitStopId == -1) {
                // prepare the sql statement
                String insertSQL = "insert into pitstops (vehichleNumber, timeIn, timeOut) values (?,?,?);";
                try {
                    PreparedStatement statement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, pitStop.getVehicleNumber());
                    statement.setDouble(2, pitStop.getInTime());
                    statement.setDouble(3, pitStop.getOutTime());
                    statement.executeUpdate();

                    ResultSet keys = statement.getGeneratedKeys();
                    if (keys.next()){
                        pitStop.setId(keys.getInt(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Update the pitstop data, updating all data as changes might be made to the non comment fields in future.
    public void updatePitStop(PitStop pitStop){
        int pitStopId = pitStop.getId();
        Connection connection = this.connect();
        if (connection != null) {
            if (pitStopId > -1) {
                String updateSQL = "update pitstops set vehichleNumber = ?, timeIn = ?, timeOut = ?, comment = ? where id = ?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(updateSQL);
                    statement.setInt(1, pitStop.getVehicleNumber());
                    statement.setDouble(2, pitStop.getInTime());
                    statement.setDouble(3, pitStop.getOutTime());
                    statement.setString(4, pitStop.getComment());
                    statement.setInt(5, pitStopId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get a single pit stop, based on it's ID.
    public PitStop getPitStop(int pitStopId){
        Connection connection = this.connect();

        if (connection != null) {
            String getSQL = "SELECT * from pitstops where id = ?;";
            try {
                PreparedStatement statement = connection.prepareStatement(getSQL);
                statement.setInt(1, pitStopId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()){
                    PitStop ps = new PitStop(resultSet.getInt("vehichleNumber"),
                            resultSet.getDouble("timeIn"),
                            resultSet.getDouble("timeOut"));
                    ps.setId(pitStopId);
                    ps.setComment(resultSet.getString("comment"));
                    return ps;
                }

            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }


    //get all pit stops from the database.
    public ArrayList<PitStop> getPitStops(){
        return null;
    }
    // Testing the database read write code.
    public static void main(String[] args){
        DBInterface dbInterface = new DBInterface("/home/nightwing/Documents/IdeaProjects/PitStopTracker/pitstops.db");
        PitStop ps1 = new PitStop(33, 134.33, 0);
        dbInterface.addPitStop(ps1);
        System.out.println(ps1.getId());

        PitStop ps2 = new PitStop(27, 145.23, 0);
        dbInterface.addPitStop(ps2);
        System.out.println(ps2.getId());

        ps2.setComment("Cheating");
        dbInterface.updatePitStop(ps2);

        ps2 = null;

        ps2 = dbInterface.getPitStop(2);
        System.out.println(ps2.getComment());
    }

}
