package bikeshare;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public final class CsvRideRepository implements RideRepository {
    private final String fileName;

    public CsvRideRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void save(Ride ride) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println(ride.bikeId() + "," + ride.memberId() + "," + ride.minutes() + "," + ride.fee());
        }
    }
}
