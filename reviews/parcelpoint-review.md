# AI review of parcelpoint (Prompt A)

- AI tool and model: Claude Code (desktop app, Code tab), model Claude Opus 5.5 (`claude-opus-5-5`)
- Date: 2026-10-25
- How the AI read the files: it opened the folder and read the four Java files directly from disk
- Prompt: Prompt A from prompts.md, with `<folder>` replaced by `parcelpoint`

---

1. Files read: parcelpoint/src/main/java/parcelpoint/Locker.java, Main.java, Parcel.java, ParcelService.java

2. Findings

| No. | Check | Code location (file:line) | Finding | Severity | Suggested fix |
| --- | --- | --- | --- | --- | --- |
| 1 | 1 | ParcelService.java:42 | `parcel.getCode() == code` compares two String objects by reference. The code read from the keyboard is a different object from the stored code, so `pickUp` never finds the parcel and always returns 0. | Must fix | Compare with `parcel.getCode().equals(code)`. |
| 2 | 1 | ParcelService.java:76 | The `FileWriter` is never closed or flushed, so the buffered text is never written and parcels.csv stays empty, and the file handle leaks. | Must fix | Open the writer in a try-with-resources statement. |
| 3 | 1 | ParcelService.java:80 | The `catch (IOException e)` block is empty, so a failed save is silently ignored. | Must fix | Log or report the exception instead of swallowing it. |
| 4 | 1 | ParcelService.java:6 | Unused import `java.util.Date`. | Should fix | Remove the import. |
| 5 | 1 | ParcelService.java:12 | Private field `maxParcels` is never read, so the limit of 100 parcels is never enforced. | Should fix | Remove the field, or check it in `storeParcel`. |
| 6 | 1 | Main.java:8 | The `Scanner` on System.in is never closed. | Note | Open it in a try-with-resources statement. |
| 7 | 1 | Main.java:13 | `words[1]`, `words[2]`, `words[3]` and `Integer.parseInt` are used without checks, so a short or non-numeric command throws `ArrayIndexOutOfBoundsException` or `NumberFormatException` and ends the program. The same holds at Main.java:17. | Should fix | Check the number of words and catch `NumberFormatException` before using them. |
| 8 | 1 | ParcelService.java:28, 44, 68 and Parcel.java:13 | The status strings "NEW", "STORED" and "PICKED_UP" are repeated as literals in two classes. | Should fix | Replace them with an enum. |
| 9 | 1 | ParcelService.java:57-61 | Magic numbers 3, 10.0, 15.0 and 20.0 in the fee rule. | Note | Name them as constants or keep the rate with the size. |
| 10 | 1 | ParcelService.java:49 | `pickUp` returns 0 for an unknown code, which the caller cannot tell apart from a free pickup, so Main prints "Fee: 0.0" for a parcel that does not exist. | Should fix | Return an empty result or throw an exception for an unknown code. |
| 11 | 1 | Main.java:6-27 | `main` is one long if/else chain on `words[0]` that parses, calls and prints for every command. | Note | Move each command into its own small method. |
| 12 | 2 | ParcelService.java:24-37 | `storeParcel` has procedural cohesion: it finds a locker, changes the status, sends an SMS, and saves the file, which are steps in a sequence that do not share one purpose. | Should fix | Move the SMS and the saving out of the method, or into a separate collaborator. |
| 13 | 2 | ParcelService.java:39-50 | `pickUp` has procedural cohesion: it frees the locker, changes the status, saves the file and calculates the fee in one method. | Should fix | Let the caller ask for the fee in a separate call, and keep `pickUp` to the pickup itself. |
| 14 | 2 | ParcelService.java:64-72 | `printParcels(boolean forStaff)` has logical cohesion: the flag selects between two different reports in one method. | Should fix | Split it into two methods, one for each report. |
| 15 | 2 | Main.java:19 and Main.java:21 to ParcelService.java:64 | Control coupling: Main passes the flag `forStaff` that decides what `printParcels` does. | Should fix | Call two separate methods instead of passing a flag. |
| 16 | 2 | ParcelService.java:9-83 | Weak class cohesion: ParcelService represents more than one thing. It allocates lockers, holds the fee policy, sends SMS messages, formats reports for the console and saves the CSV file. | Should fix | Move the fee rule, the console output and the file saving into their own classes. |
| 17 | 2 | ParcelService.java:52 | `fee(Parcel, int)` takes the whole Parcel but uses only its size, which is stamp coupling, and it does not use any attribute of ParcelService. | Note | Pass only the size, or move the fee rule next to the size. |
| 18 | 2 | Parcel.java:7 with ParcelService.java:28, 44, 67, 68, 78 | The field `status` is public and ParcelService writes it directly, which is common coupling on shared data that is close to content coupling, because ParcelService changes Parcel's internal state without going through Parcel. | Must fix | Make `status` private and change it through methods such as `markStored()` and `markPickedUp()`. |
| 19 | 2 | ParcelService.java:20 with Main.java:22 | `getLockers()` returns the internal `lockers` list, so any caller can add or remove lockers. Main uses it to reach through the service into each Locker (`getLockers().stream().filter(Locker::isFree)`). | Should fix | Return an unmodifiable list, or offer `countFreeLockers()` on ParcelService. |
| 20 | 2 | Locker.java:5, Parcel.java:6, ParcelService.java:26, 56, 58 and Main.java:13 | Connascence of meaning (convention): the parcel size is an int whose values 1, 2 and 3 mean small, medium and large, and Locker, Parcel, ParcelService and Main must all read it in the same way. | Should fix | Replace the int with an enum `Size`. |
| 21 | 2 | Parcel.java:7 and ParcelService.java:28, 44, 68 | Connascence of meaning: the status strings must be spelled the same in Parcel and ParcelService. | Should fix | Replace the strings with an enum `Status`. |
| 22 | 2 | Main.java:13 | Connascence of position: Main must pass code, studentId and size to `new Parcel` in this order, and two of them are Strings, so a swap would compile. | Note | Accept this for three arguments, or use named factory methods. |
| 23 | 2 | ParcelService.java:76 and ParcelService.java:30 | ParcelService depends directly on the console (System.out) and on the file system (FileWriter with the fixed name parcels.csv), which ties the rules to one output and one storage. | Note | Hide the notification and the saving behind small interfaces. |
| 24 | 2 | Locker.java:25 | `setParcel` is public, so any class can put a parcel in a locker without changing the parcel's status. | Note | Keep it, or replace it with `store(Parcel)` and `empty()` that keep the state consistent. |

Fields that are not private: Parcel.java:7 (`status`). Every other field is private.
Operations that return an internal collection: ParcelService.java:20 (`getLockers`).

3. Number of findings: Check 1, 11 findings. Check 2, 13 findings.
