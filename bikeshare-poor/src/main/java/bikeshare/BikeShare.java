package bikeshare;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BikeShare {
    public static final int STUDENT = 1;
    public static final int STAFF = 2;

    public static List<String> stations = new ArrayList<>();
    public static List<String> bikes = new ArrayList<>();
    public static List<String> bikeStation = new ArrayList<>();
    public static List<String> bikeRenter = new ArrayList<>();
    public static List<Integer> bikeRenterType = new ArrayList<>();

    public static void main(String[] args) {
        stations.add("Library");
        stations.add("Canteen");
        stations.add("Dorm");
        for (String s : stations) {
            for (int i = 1; i <= 2; i++) {
                bikes.add(s.charAt(0) + "" + i);
                bikeStation.add(s);
                bikeRenter.add(null);
                bikeRenterType.add(0);
            }
        }
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Commands: rent <memberId> <student|staff> <station>, return <bike> <station> <minutes>, quit");
            while (in.hasNextLine()) {
                String[] w = in.nextLine().trim().split("\\s+");
                if (w[0].equals("quit")) {
                    break;
                }
                try {
                    if (w[0].equals("rent")) {
                        int type = w[2].equals("staff") ? STAFF : STUDENT;
                        System.out.println(handle(true, w[1], type, w[3], 0));
                    } else if (w[0].equals("return")) {
                        System.out.println(handle(false, w[1], 0, w[2], Integer.parseInt(w[3])));
                    } else {
                        System.out.println("Unknown command");
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("Bad input");
                }
            }
        }
    }

    // rent == true: id is the member ID; rent == false: id is the bike
    public static String handle(boolean rent, String id, int type, String station, int minutes) {
        if (!stations.contains(station)) {
            return "Unknown station";
        }
        for (int i = 0; i < bikes.size(); i++) {
            if (rent && station.equals(bikeStation.get(i)) && bikeRenter.get(i) == null) {
                bikeRenter.set(i, id);
                bikeRenterType.set(i, type);
                bikeStation.set(i, null);
                return "Bike " + bikes.get(i) + " rented to " + id;
            }
            if (!rent && bikes.get(i).equals(id) && bikeRenter.get(i) != null) {
                int fee = bikeRenterType.get(i) == STAFF ? minutes * 3 : minutes * 2;
                String member = bikeRenter.get(i);
                bikeRenter.set(i, null);
                bikeStation.set(i, station);
                try (PrintWriter out = new PrintWriter(new FileWriter("rides.csv", true))) {
                    out.println(bikes.get(i) + "," + member + "," + minutes + "," + fee);
                } catch (IOException e) {
                    return "Could not save ride: " + e.getMessage();
                }
                return "Receipt: bike " + bikes.get(i) + ", " + minutes + " minutes, fee " + fee + " baht";
            }
        }
        return rent ? "No bike available at " + station : "Bike " + id + " is not rented";
    }
}
