package bikeshare;

import java.io.IOException;

public interface RideRepository {
    void save(Ride ride) throws IOException;
}
