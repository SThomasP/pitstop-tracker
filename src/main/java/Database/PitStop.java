package Database;


// data wrapper for storing information about a pit stop
public class PitStop {

    public final int vehicleNumber;
    public final double inTime;
    public final double outTime;
    public String comment;

    public PitStop(int vehicleNumber, double inTime, double outTime){
        this.vehicleNumber = vehicleNumber;
        this.inTime = inTime;
        this.outTime = outTime;
    }

}
