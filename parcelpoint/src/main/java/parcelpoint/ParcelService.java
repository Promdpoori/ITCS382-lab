package parcelpoint;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParcelService {
    private List<Locker> lockers = new ArrayList<>();
    private List<Parcel> parcels = new ArrayList<>();
    private int maxParcels = 100;

    public ParcelService() {
        lockers.add(new Locker(1, 1));
        lockers.add(new Locker(2, 2));
        lockers.add(new Locker(3, 3));
    }

    public List<Locker> getLockers() {
        return lockers;
    }

    public Locker storeParcel(Parcel parcel) {
        for (Locker locker : lockers) {
            if (locker.isFree() && locker.getSize() >= parcel.getSize()) {
                locker.setParcel(parcel);
                parcel.status = "STORED";
                parcels.add(parcel);
                System.out.println("SMS to " + parcel.getStudentId() + ": parcel "
                        + parcel.getCode() + " is in locker " + locker.getNumber());
                saveToFile();
                return locker;
            }
        }
        return null;
    }

    public double pickUp(String code, int days) {
        for (Locker locker : lockers) {
            Parcel parcel = locker.getParcel();
            if (parcel != null && parcel.getCode() == code) {
                locker.setParcel(null);
                parcel.status = "PICKED_UP";
                saveToFile();
                return fee(parcel, days);
            }
        }
        return 0;
    }

    public double fee(Parcel parcel, int days) {
        if (days <= 3) {
            return 0;
        }
        if (parcel.getSize() == 1) {
            return (days - 3) * 10.0;
        } else if (parcel.getSize() == 2) {
            return (days - 3) * 15.0;
        }
        return (days - 3) * 20.0;
    }

    public void printParcels(boolean forStaff) {
        for (Parcel parcel : parcels) {
            if (forStaff) {
                System.out.println(parcel.getCode() + " " + parcel.getStudentId() + " " + parcel.status);
            } else if (parcel.status.equals("STORED")) {
                System.out.println(parcel.getCode() + " is waiting");
            }
        }
    }

    private void saveToFile() {
        try {
            FileWriter writer = new FileWriter("parcels.csv");
            for (Parcel parcel : parcels) {
                writer.write(parcel.getCode() + "," + parcel.status + "\n");
            }
        } catch (IOException e) {
        }
    }
}
