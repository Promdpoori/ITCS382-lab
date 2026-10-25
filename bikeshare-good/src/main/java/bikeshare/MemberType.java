package bikeshare;

public enum MemberType {
    STUDENT(2),
    STAFF(3);

    private final int bahtPerMinute;

    MemberType(int bahtPerMinute) {
        this.bahtPerMinute = bahtPerMinute;
    }

    public int feeFor(int minutes) {
        return bahtPerMinute * minutes;
    }
}
