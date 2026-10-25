# AI review of bikeshare-good (Prompt A)

- AI tool and model: Claude Code (desktop app, Code tab), model Claude Opus 5.5 (`claude-opus-5-5`)
- Date: 2026-10-25
- How the AI read the files: it opened the folder and read the Java files directly from disk
- Prompt: Prompt A from prompts.md, with `<folder>` replaced by `bikeshare-good`

---

1. Files read: bikeshare-good/src/main/java/bikeshare/Bike.java, BikeShareService.java, CsvRideRepository.java, Main.java, MemberType.java, Rental.java, Ride.java, RideRepository.java

2. Findings

| No. | Check | Code location (file:line) | Finding | Severity | Suggested fix |
| --- | --- | --- | --- | --- | --- |
| 1 | 1 | Main.java:41 | Catching `IllegalArgumentException` also catches `NumberFormatException` and the error from `MemberType.valueOf`, which is intended, but it would also hide any other `IllegalArgumentException` thrown by the service. | Note | Catch `NumberFormatException` and check the member type before calling `valueOf`. |
| 2 | 1 | Bike.java:30-35 | `returnTo` returns `null` when the bike is not rented, and the caller must remember to call `isRented()` first. | Note | Return an `Optional<Rental>`, or throw `IllegalStateException`. |
| 3 | 1 | Main.java:38 | A negative number of minutes is accepted and gives a negative fee. | Note | Reject minutes below zero. |
| 4 | 2 | BikeShareService.java:37-47 | `giveBack` has sequential cohesion: it returns the bike, builds the ride from the rental, and saves the ride, where each step uses the output of the one before. This is stronger than procedural, so it is reported only as a note. | Note | No change needed. |
| 5 | 2 | BikeShareService.java:41 | Reach-through: `rental.memberType().feeFor(minutes)` goes through Rental to call MemberType. The chain is short and Rental is a plain data record. | Note | Add `Rental.feeFor(minutes)` if the chain grows. |
| 6 | 2 | Bike.java:26 and BikeShareService.java:41 | Connascence of position: `new Rental(memberId, type)` and `new Ride(bikeId, memberId, minutes, fee)` depend on the order of the record components, and Ride has two String and two int components. | Note | Accept this for records, or use named factory methods. |
| 7 | 2 | BikeShareService.java:18 and Main.java:17 | Connascence of convention: a bike ID is the first letter of its station plus a number, and the station names must be spelled the same in Main and in the user's input. | Note | Accept this, or let the service list its station names for the menu. |
| 8 | 2 | Main.java:17 | Main creates the concrete `CsvRideRepository`, which is the only place that knows the storage class. This is intended dependency injection, not a problem. | Note | No change needed. |

Methods whose cohesion is procedural or weaker: not found.
Interactions tighter than data coupling: not found. `BikeShareService.giveBack` passes a whole `Ride` to `RideRepository.save`, which is stamp coupling, but the repository uses every field of the Ride, so it is the data that it needs.
Fields that are not private: not found.
Operations that return an internal collection: not found. The station list is copied with `List.copyOf` and the bike list is never returned.

3. Number of findings: Check 1, 3 findings. Check 2, 5 findings, all notes.
