package parcelpoint;

public class Locker {
    private int number;
    private ParcelSize size;
    private Parcel parcel;

    public Locker(int number, ParcelSize size) {
        this.number = number;
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public ParcelSize getSize() {
        return size;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public boolean isFree() {
        return parcel == null;
    }
}
