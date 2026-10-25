package parcelpoint;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ParcelService service = new ParcelService();
        Scanner in = new Scanner(System.in);
        System.out.println("Commands: store <code> <studentId> <size 1-3>, pickup <code> <days>, list, staff, quit");
        while (in.hasNextLine()) {
            String[] words = in.nextLine().trim().split(" ");
            if (words[0].equals("store")) {
                Parcel parcel = new Parcel(words[1], words[2], Integer.parseInt(words[3]));
                Locker locker = service.storeParcel(parcel);
                System.out.println(locker == null ? "No free locker" : "Locker " + locker.getNumber());
            } else if (words[0].equals("pickup")) {
                System.out.println("Fee: " + service.pickUp(words[1], Integer.parseInt(words[2])));
            } else if (words[0].equals("list")) {
                service.printParcels(false);
            } else if (words[0].equals("staff")) {
                service.printParcels(true);
                System.out.println("Free lockers: " + service.getLockers().stream().filter(Locker::isFree).count());
            } else if (words[0].equals("quit")) {
                break;
            }
        }
    }
}
