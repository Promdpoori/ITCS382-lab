package bikeshare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BikeShareService {
    private final List<String> stations;
    private final List<Bike> bikes = new ArrayList<>();
    private final RideRepository rides;

    public BikeShareService(List<String> stations, int bikesPerStation, RideRepository rides) {
        this.stations = List.copyOf(stations);
        this.rides = rides;
        for (String station : this.stations) {
            for (int i = 1; i <= bikesPerStation; i++) {
                bikes.add(new Bike(station.charAt(0) + String.valueOf(i), station));
            }
        }
    }

    public boolean hasStation(String station) {
        return stations.contains(station);
    }

    public Optional<String> rent(String memberId, MemberType type, String station) {
        for (Bike bike : bikes) {
            if (bike.isAvailableAt(station)) {
                bike.rentTo(memberId, type);
                return Optional.of(bike.getId());
            }
        }
        return Optional.empty();
    }

    public Optional<Ride> giveBack(String bikeId, String station, int minutes) throws IOException {
        for (Bike bike : bikes) {
            if (bike.getId().equals(bikeId) && bike.isRented()) {
                Rental rental = bike.returnTo(station);
                Ride ride = new Ride(bikeId, rental.memberId(), minutes, rental.memberType().feeFor(minutes));
                rides.save(ride);
                return Optional.of(ride);
            }
        }
        return Optional.empty();
    }
}
