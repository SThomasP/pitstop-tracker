package database;


// data wrapper for storing information about a pit stop
public class PitStop {

    public final int vehicleNumber;
    public final double timeIn;
    public final double timeOut;
    public String comment;

    public PitStop(int vehicleNumber, double inTime, double outTime){
        this.vehicleNumber = vehicleNumber;
        this.timeIn = inTime;
        this.timeOut = outTime;
        this.comment = "";
    }

}
