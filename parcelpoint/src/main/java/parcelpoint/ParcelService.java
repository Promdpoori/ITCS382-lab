package parcelpoint;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParcelService {
    private static final Logger LOGGER = Logger.getLogger(ParcelService.class.getName());

    private List<Locker> lockers = new ArrayList<>();
    private List<Parcel> parcels = new ArrayList<>();

    public ParcelService() {
        lockers.add(new Locker(1, 1));
        lockers.add(new Locker(2, 2));
        lockers.add(new Locker(3, 3));
    }

    public long countFreeLockers() {
        return lockers.stream().filter(Locker::isFree).count();
    }

    public List<Parcel> getParcels() {
        return Collections.unmodifiableList(parcels);
    }

    public Locker storeParcel(Parcel parcel) {
        for (Locker locker : lockers) {
            if (locker.isFree() && locker.getSize() >= parcel.getSize()) {
                locker.setParcel(parcel);
                parcel.markStored();
                parcels.add(parcel);
                saveToFile();
                return locker;
            }
        }
        return null;
    }

    public double pickUp(String code, int days) {
        for (Locker locker : lockers) {
            Parcel parcel = locker.getParcel();
            if (parcel != null && parcel.getCode().equals(code)) {
                locker.setParcel(null);
                parcel.markPickedUp();
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

    private void saveToFile() {
        try (FileWriter writer = new FileWriter("parcels.csv")) {
            for (Parcel parcel : parcels) {
                writer.write(parcel.getCode() + "," + parcel.getStatus() + "\n");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not save parcels.csv", e);
        }
    }
}
