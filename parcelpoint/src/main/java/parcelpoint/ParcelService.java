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
        lockers.add(new Locker(1, ParcelSize.SMALL));
        lockers.add(new Locker(2, ParcelSize.MEDIUM));
        lockers.add(new Locker(3, ParcelSize.LARGE));
    }

    public long countFreeLockers() {
        return lockers.stream().filter(Locker::isFree).count();
    }

    public List<Parcel> getParcels() {
        return Collections.unmodifiableList(parcels);
    }

    public Locker storeParcel(Parcel parcel) {
        for (Locker locker : lockers) {
            if (locker.isFree() && parcel.getSize().fitsIn(locker.getSize())) {
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
                return parcel.getSize().fee(days);
            }
        }
        return 0;
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
