package Database;

public class PitStop {

    private final int vehicleNumber;
    private int id = -1;
    private final double inTime;
    private final double outTime;
    private String comment;

    public PitStop(int vehicleNumber, double inTime, double outTime){
        this.vehicleNumber = vehicleNumber;
        this.inTime = inTime;
        this.outTime = outTime;
    }


    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public double getInTime() {
        return inTime;
    }

    public double getOutTime() {
        return outTime;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

}
