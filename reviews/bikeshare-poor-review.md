# AI review of bikeshare-poor (Prompt A)

- AI tool and model: Claude Code (desktop app, Code tab), model Claude Opus 5.5 (`claude-opus-5-5`)
- Date: 2026-10-25
- How the AI read the files: it opened the folder and read the Java file directly from disk
- Prompt: Prompt A from prompts.md, with `<folder>` replaced by `bikeshare-poor`

---

1. Files read: bikeshare-poor/src/main/java/bikeshare/BikeShare.java

2. Findings

| No. | Check | Code location (file:line) | Finding | Severity | Suggested fix |
| --- | --- | --- | --- | --- | --- |
| 1 | 1 | BikeShare.java:56-81 | `handle` is long and hard to read: one loop holds two unrelated branches, each with its own condition, state changes and return text. | Should fix | Split it into `rent` and `giveBack` methods. |
| 2 | 1 | BikeShare.java:20-53 | `main` sets up the stations and bikes, runs the menu loop, parses every command and prints every result. | Note | Move the setup and each command into their own methods. |
| 3 | 1 | BikeShare.java:41 | Any member type other than "staff", including a typo, is silently treated as a student, so a mistyped type is charged the student rate. | Should fix | Reject an unknown member type. |
| 4 | 1 | BikeShare.java:14-18 | Five parallel lists hold the parts of one bike record and must always be changed together at the same index. | Should fix | Replace them with one list of Bike objects. |
| 5 | 1 | BikeShare.java:25 and 68 | The magic numbers 2 (bikes per station), 2 and 3 (baht per minute) are written inline. | Note | Name them, or keep the rate with the member type. |
| 6 | 2 | BikeShare.java:56 | `handle` has logical cohesion: the flag `rent` selects between two different operations, renting and returning, that share one method. | Must fix | Split it into two methods, one for each operation. |
| 7 | 2 | BikeShare.java:42, 44 to BikeShare.java:56 | Control coupling: `main` passes the boolean `rent` that decides what `handle` does, and it also passes dummy values (type 0, minutes 0) that the chosen branch ignores. | Must fix | Call two methods that take only the data that each needs. |
| 8 | 2 | BikeShare.java:55-56 | The parameter `id` means a member ID when `rent` is true and a bike ID when it is false, so the meaning of one argument depends on another. | Should fix | Use separate methods with clearly named parameters. |
| 9 | 2 | BikeShare.java:20-53 | `main` has procedural cohesion: it creates the data, then reads, parses, executes and prints, which are steps in an order rather than one purpose. | Should fix | Separate the setup, the menu and the commands. |
| 10 | 2 | BikeShare.java:10-82 | Weak class cohesion: BikeShare represents the menu, the fee rule, the bike records and the file saving in one class. | Must fix | Give the menu, the rules, the bike records and the saving their own classes. |
| 11 | 2 | BikeShare.java:14-18 | The mutable lists are public static fields, so any class can read and change them. This is common coupling on global data. | Must fix | Make the fields private instance fields. |
| 12 | 2 | BikeShare.java:11-12, 41, 63, 68 | Connascence of meaning: member types are the int codes 1 and 2, and 0 means "no renter". Every reader must know what each number means. | Should fix | Use an enum `MemberType`. |
| 13 | 2 | BikeShare.java:14-18, 61-71 | Connascence of position: the bike's name, station, renter and renter type are linked only by having the same index in five lists. | Should fix | Store the parts of a bike in one object. |
| 14 | 2 | BikeShare.java:56 | Connascence of position: `handle` takes five parameters, two of type String and three of type boolean or int, so a swap would still compile. | Should fix | Use smaller methods with fewer parameters. |
| 15 | 2 | BikeShare.java:68 | Connascence of algorithm: the fee rule is written in the same method that frees the bike and saves the ride, so a new member type means editing the rental logic. | Should fix | Keep the rate with the member type. |
| 16 | 2 | BikeShare.java:72-73 | The rules depend directly on the file `rides.csv` and its format, with no abstraction between them. | Should fix | Hide the saving behind an interface. |

Fields that are not private: BikeShare.java:11, 12 (public static final int constants), and BikeShare.java:14, 15, 16, 17, 18 (public static mutable lists).
Operations that return an internal collection: not found; the lists are reached directly through the public fields.

3. Number of findings: Check 1, 5 findings. Check 2, 11 findings.
