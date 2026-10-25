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
                Parcel parcel = new Parcel(words[1], words[2], ParcelSize.fromNumber(Integer.parseInt(words[3])));
                Locker locker = service.storeParcel(parcel);
                if (locker != null) {
                    System.out.println("SMS to " + parcel.getStudentId() + ": parcel "
                            + parcel.getCode() + " is in locker " + locker.getNumber());
                }
                System.out.println(locker == null ? "No free locker" : "Locker " + locker.getNumber());
            } else if (words[0].equals("pickup")) {
                System.out.println("Fee: " + service.pickUp(words[1], Integer.parseInt(words[2])));
            } else if (words[0].equals("list")) {
                printWaitingParcels(service);
            } else if (words[0].equals("staff")) {
                printAllParcels(service);
                System.out.println("Free lockers: " + service.countFreeLockers());
            } else if (words[0].equals("quit")) {
                break;
            }
        }
    }

    private static void printWaitingParcels(ParcelService service) {
        for (Parcel parcel : service.getParcels()) {
            if (parcel.getStatus() == ParcelStatus.STORED) {
                System.out.println(parcel.getCode() + " is waiting");
            }
        }
    }

    private static void printAllParcels(ParcelService service) {
        for (Parcel parcel : service.getParcels()) {
            System.out.println(parcel.getCode() + " " + parcel.getStudentId() + " " + parcel.getStatus());
        }
    }
}
