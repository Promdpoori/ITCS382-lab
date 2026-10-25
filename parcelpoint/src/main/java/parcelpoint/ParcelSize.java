package parcelpoint;

public enum ParcelSize {
    SMALL(10.0),
    MEDIUM(15.0),
    LARGE(20.0);

    private static final int FREE_DAYS = 3;

    private final double feePerDay;

    ParcelSize(double feePerDay) {
        this.feePerDay = feePerDay;
    }

    public static ParcelSize fromNumber(int number) {
        return switch (number) {
            case 1 -> SMALL;
            case 2 -> MEDIUM;
            case 3 -> LARGE;
            default -> throw new IllegalArgumentException("Size must be 1, 2 or 3");
        };
    }

    public boolean fitsIn(ParcelSize lockerSize) {
        return compareTo(lockerSize) <= 0;
    }

    public double fee(int days) {
        return days <= FREE_DAYS ? 0 : (days - FREE_DAYS) * feePerDay;
    }
}
