package parcelpoint;

public class Locker {
    private int number;
    private int size;          // 1 = small, 2 = medium, 3 = large
    private Parcel parcel;

    public Locker(int number, int size) {
        this.number = number;
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
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
