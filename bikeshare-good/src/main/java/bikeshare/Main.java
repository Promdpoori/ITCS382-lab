package bikeshare;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class Main {
    private final BikeShareService service;

    private Main(BikeShareService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        BikeShareService service = new BikeShareService(
                List.of("Library", "Canteen", "Dorm"), 2, new CsvRideRepository("rides.csv"));
        new Main(service).run();
    }

    private void run() {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Commands: rent <memberId> <student|staff> <station>, return <bike> <station> <minutes>, quit");
            while (in.hasNextLine()) {
                String[] words = in.nextLine().trim().split("\\s+");
                if (words[0].equals("quit")) {
                    return;
                }
                System.out.println(execute(words));
            }
        }
    }

    private String execute(String[] words) {
        try {
            return switch (words[0]) {
                case "rent" -> rent(words[1], MemberType.valueOf(words[2].toUpperCase(Locale.ROOT)), words[3]);
                case "return" -> giveBack(words[1], words[2], Integer.parseInt(words[3]));
                default -> "Unknown command";
            };
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            return "Bad input";
        }
    }

    private String rent(String memberId, MemberType type, String station) {
        if (!service.hasStation(station)) {
            return "Unknown station";
        }
        return service.rent(memberId, type, station)
                .map(bike -> "Bike " + bike + " rented to " + memberId)
                .orElse("No bike available at " + station);
    }

    private String giveBack(String bikeId, String station, int minutes) {
        if (!service.hasStation(station)) {
            return "Unknown station";
        }
        try {
            return service.giveBack(bikeId, station, minutes)
                    .map(ride -> "Receipt: bike " + ride.bikeId() + ", " + ride.minutes() + " minutes, fee " + ride.fee() + " baht")
                    .orElse("Bike " + bikeId + " is not rented");
        } catch (IOException e) {
            return "Could not save ride: " + e.getMessage();
        }
    }
}
