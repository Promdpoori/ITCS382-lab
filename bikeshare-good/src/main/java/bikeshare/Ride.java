package bikeshare;

public record Ride(String bikeId, String memberId, int minutes, int fee) {
}
