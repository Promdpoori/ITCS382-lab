package parcelpoint;

public class Parcel {
    private String code;
    private String studentId;
    private ParcelSize size;
    private ParcelStatus status;

    public Parcel(String code, String studentId, ParcelSize size) {
        this.code = code;
        this.studentId = studentId;
        this.size = size;
        this.status = ParcelStatus.NEW;
    }

    public String getCode() {
        return code;
    }

    public String getStudentId() {
        return studentId;
    }

    public ParcelSize getSize() {
        return size;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void markStored() {
        status = ParcelStatus.STORED;
    }

    public void markPickedUp() {
        status = ParcelStatus.PICKED_UP;
    }
}
