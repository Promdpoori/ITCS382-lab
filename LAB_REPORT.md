# Lab Report, Code Quality and Design Quality

Repository URL: https://github.com/Promdpoori/ITCS382-lab

## Tools

| Item | Version or name |
| --- | --- |
| JDK | Eclipse Temurin OpenJDK 21.0.12.1 (LTS) |
| Maven | Apache Maven 3.10.0 |
| IDE | Visual Studio Code 1.140.0 with Extension Pack for Java 0.31.2 |
| SonarQube for IDE | 6.2.0 |
| AI tool and exact model | Claude Code (Claude desktop app, Code tab), model Claude Opus 5.5 (`claude-opus-5-5`) |

How the SonarQube issues were collected: every SonarQube issue in this report comes from the analyzer of SonarQube for IDE 6.2.0. The language server of the extension (`server/sonarlint-ls.jar` with `analyzers/sonarjava.jar` and `analyzers/sonarjavasymbolicexecution.jar`) was started with the extension's default rules, each Java file of the project was opened in it, and the issues that it published were recorded, which are the issues that the Problems panel shows with the source sonarqube. Warnings from the Java extension, such as the unclosed Scanner, are left out, as step C3 asks.

## Task 1. SonarQube on the starter code

Commit of the starter, before any change: e07bd8f

What looked wrong when the starter ran `store P1 6501 2`, `pickup P1 5` and `staff`: the pickup printed `Fee: 0.0` although the parcel was kept 5 days, `staff` still showed `P1 6501 STORED` and `Free lockers: 2` after the pickup, and parcels.csv was created but empty (0 bytes).

Table 1. SonarQube issues in the starter code

| No. | Rule | File:line | Message |
| --- | --- | --- | --- |
| 1 | java:S106 | Main.java:9 | Replace this use of System.out by a logger. |
| 2 | java:S106 | Main.java:15 | Replace this use of System.out by a logger. |
| 3 | java:S106 | Main.java:17 | Replace this use of System.out by a logger. |
| 4 | java:S106 | Main.java:22 | Replace this use of System.out by a logger. |
| 5 | java:S2143 | ParcelService.java:1 | Use the "java.time" API for date and time. |
| 6 | java:S1128 | ParcelService.java:6 | Remove this unused import 'java.util.Date'. |
| 7 | java:S1068 | ParcelService.java:12 | Remove this unused "maxParcels" private field. |
| 8 | java:S106 | ParcelService.java:30 | Replace this use of System.out by a logger. |
| 9 | java:S4973 | ParcelService.java:42 | Strings and Boxed types should be compared using "equals()". |
| 10 | java:S106 | ParcelService.java:67 | Replace this use of System.out by a logger. |
| 11 | java:S106 | ParcelService.java:69 | Replace this use of System.out by a logger. |
| 12 | java:S2095 | ParcelService.java:76 | Use try-with-resources or close this "FileWriter" in a "finally" clause. |
| 13 | java:S108 | ParcelService.java:80 | Remove this block of code, fill it in, or add a comment explaining why it is empty. |

## Task 2. AI review of the starter code

| Item | Value |
| --- | --- |
| Date | 2026-10-25 |
| AI tool and exact model | Claude Code (Claude desktop app, Code tab), Claude Opus 5.5 (`claude-opus-5-5`) |
| Commit reviewed | e07bd8f |
| How the AI read the files (attached the files, opened the folder, or pasted the files) | Opened the folder: the AI read the four Java files from parcelpoint/src/main/java/parcelpoint on disk |
| Output file in the reviews folder | reviews/parcelpoint-review.md |

Table 2. All findings on the starter code

| No. | File:line | Finding | Found by (SonarQube, AI or both) | SonarQube rule or course term | Correct? (yes or no, and why) |
| --- | --- | --- | --- | --- | --- |
| 1 | ParcelService.java:42 | Strings compared with `==` in `pickUp` | both | java:S4973, probable bug | yes, the run showed it: `pickup P1 5` printed `Fee: 0.0` and P1 stayed STORED, because the typed code is a different String object |
| 2 | ParcelService.java:76 | `FileWriter` never closed | both | java:S2095, probable bug | yes, parcels.csv was 0 bytes after the run, because the unclosed writer was never flushed |
| 3 | ParcelService.java:80 | Empty `catch` block | both | java:S108, code smell | yes, a failed save would be ignored with no message |
| 4 | ParcelService.java:6 | Unused import `java.util.Date` | both | java:S1128, code smell | yes, nothing in the file uses Date |
| 5 | ParcelService.java:1 | Use the java.time API for date and time | SonarQube | java:S2143 | yes, but only because of the unused Date import; it disappeared when the import was removed |
| 6 | ParcelService.java:12 | Unused private field `maxParcels` | both | java:S1068, code smell | yes, nothing reads maxParcels, so the limit of 100 is never applied |
| 7 | Main.java:9, 15, 17, 22 and ParcelService.java:30, 67, 69 | System.out used instead of a logger | SonarQube | java:S106 | partly: in Main it is the output of a command-line program, which is fine; in ParcelService it is right, because the rules class should not print |
| 8 | Main.java:8 | `Scanner` on System.in never closed | AI | probable bug (resource) | yes, but harmless, because closing System.in only matters at the end of the program; the Java extension also warns about it |
| 9 | Main.java:13, 17 | Command words and numbers used without checks | AI | probable bug | yes, `store P1` alone throws ArrayIndexOutOfBoundsException and ends the program |
| 10 | Parcel.java:13 and ParcelService.java:28, 44, 68 | Status strings "NEW", "STORED", "PICKED_UP" repeated in two classes | AI | string literals repeated, connascence of meaning | yes, a typo in one place would compile and break the status check in printParcels |
| 11 | ParcelService.java:57-61 | Magic numbers 3, 10.0, 15.0, 20.0 in the fee rule | AI | code smell | yes, the free days and the three rates have no names |
| 12 | ParcelService.java:49 | `pickUp` returns 0 for an unknown code | AI | probable bug | yes, it is why the `==` bug looked like a free pickup instead of an error |
| 13 | Main.java:6-27 | `main` is one long if/else chain | AI | method too long | partly: the method is 22 lines and still easy to read, so this is only a note |
| 14 | ParcelService.java:24-37 | `storeParcel` stores, changes status, sends an SMS and saves | AI | procedural cohesion | yes, the SMS and the save are separate steps done in order, not one purpose |
| 15 | ParcelService.java:39-50 | `pickUp` frees the locker, changes status, saves and calculates the fee | AI | procedural cohesion | partly: the fee uses the parcel found in the first step, which is closer to communicational cohesion, but the save step is procedural |
| 16 | ParcelService.java:64-72 | `printParcels(boolean forStaff)` chooses between two reports | AI | logical cohesion | yes, the flag selects one of two unrelated outputs |
| 17 | Main.java:19, 21 to ParcelService.java:64 | Main passes the flag `forStaff` | AI | control coupling | yes, the caller decides what the method does |
| 18 | ParcelService.java:9-83 | ParcelService allocates lockers, holds the fee rule, sends SMS, prints reports and saves the file | AI | weak class cohesion | yes, it has five reasons to change |
| 19 | ParcelService.java:52 | `fee(Parcel, int)` takes the whole Parcel but uses only its size | AI | stamp coupling | yes, only `getSize()` is read |
| 20 | Parcel.java:7 with ParcelService.java:28, 44, 67, 68, 78 | Public field `status` written directly by ParcelService | AI | common coupling, field not private | yes, any class can set any status; SonarQube for IDE did not report it with its default rules |
| 21 | ParcelService.java:20 with Main.java:22 | `getLockers()` returns the internal list and Main reaches through it into each Locker | AI | returns internal collection, reach-through (strong coupling) | yes, `service.getLockers().add(...)` would compile and change the service |
| 22 | Locker.java:5, Parcel.java:6, ParcelService.java:26, 56, 58, Main.java:13 | Size is an int where 1, 2, 3 mean small, medium, large | AI | connascence of meaning | yes, four classes must agree on the meaning of each number |
| 23 | Main.java:13 | `new Parcel(code, studentId, size)` depends on the argument order, two Strings | AI | connascence of position | yes, but minor for three arguments |
| 24 | ParcelService.java:30, 76 | The rules depend directly on System.out and on the file parcels.csv | AI | strong coupling | yes, the rules cannot be reused with another output or storage |
| 25 | Locker.java:25 | `setParcel` is public, so a parcel can be put in a locker without changing its status | AI | encapsulation | partly: true, but no code misuses it, so it is only a note |

2.1 Name two problems that only the AI review found.

`printParcels(boolean forStaff)` at ParcelService.java:64 has logical cohesion, and Main controls it with a flag, which is control coupling (rows 16 and 17). The parcel size is an int code that four classes must read in the same way, which is connascence of meaning (row 22). SonarQube reported neither.

2.2 Name one problem that only SonarQube found, or write none.

java:S2143 at ParcelService.java:1, "Use the java.time API for date and time". The AI reported the unused Date import but not this rule. It is really the same cause, because the rule disappeared when the import was removed.

2.3 Name one AI finding that was wrong or too vague, and explain why. If every finding was right, explain how you checked them.

The finding that `pickUp` (ParcelService.java:39-50) has procedural cohesion is only partly right. The fee is calculated from the parcel that the first step finds, so part of the method is communicational, and only the call to `saveToFile()` is a separate procedural step. The finding that `main` is too long (Main.java:6-27) is also too strong, because 22 lines with one branch for each command are still easy to read. I checked each finding by opening the file at the given line, and checked the bug findings by running the program with the four lines of input.

## Task 3. Fix and check

Table 3. What you did with each SonarQube issue in Table 1

| No. in Table 1 | Fixed or kept | Commit, or the reason for keeping it |
| --- | --- | --- |
| 1 | Kept | Printing in Main is the output of a command-line program. |
| 2 | Kept | Printing in Main is the output of a command-line program. |
| 3 | Kept | Printing in Main is the output of a command-line program. |
| 4 | Kept | Printing in Main is the output of a command-line program. |
| 5 | Fixed | cb6d0eb (the rule went away with the unused Date import) |
| 6 | Fixed | cb6d0eb |
| 7 | Fixed | cb6d0eb |
| 8 | Fixed | 1c824f7 (the SMS line moved from ParcelService to Main, where it is kept as console output) |
| 9 | Fixed | d47f0f9 |
| 10 | Fixed | 1c824f7 (the report output moved from ParcelService to Main, where it is kept as console output) |
| 11 | Fixed | 1c824f7 (the report output moved from ParcelService to Main, where it is kept as console output) |
| 12 | Fixed | 32549c6 |
| 13 | Fixed | 32549c6 (the catch block now logs the error with java.util.logging) |

Number of SonarQube issues that remain after your fixes: 7, all java:S106 in Main.java (lines 9, 16, 19, 21, 26, 36 and 43), kept because Main is the command-line output of the program.

The two design problems that you fixed

| Design problem (course term) | What you changed | Commit |
| --- | --- | --- |
| Weak class cohesion of ParcelService, logical cohesion of `printParcels(boolean)` with control coupling from Main, and Main reaching through `getLockers()` | Removed all console output from ParcelService. Main now prints the SMS line and the two reports from `getParcels()`, which returns an unmodifiable list, and asks `countFreeLockers()` instead of reading the internal locker list. | 1c824f7 |
| Connascence of meaning: the size codes 1, 2 and 3 that Locker, Parcel, ParcelService and Main must read in the same way | Added the enum `ParcelSize` (SMALL, MEDIUM, LARGE) that holds the daily rate, the free days, `fitsIn` and `fee`. Locker and Parcel store a ParcelSize, and only Main turns the typed number into a size. | cec7ebe |
| Also fixed: common coupling through the public field `Parcel.status` and connascence of meaning on the status strings | Made `status` private with the enum `ParcelStatus`, and added `markStored()` and `markPickedUp()`. | 0c2e7bf |

Output of the program after your fixes, for the commands `store P1 6501 2`, `pickup P1 5` and `staff`, and the content of parcels.csv:

```text
Commands: store <code> <studentId> <size 1-3>, pickup <code> <days>, list, staff, quit
SMS to 6501: parcel P1 is in locker 2
Locker 2
Fee: 30.0
P1 6501 PICKED_UP
Free lockers: 3

parcels.csv:
P1,PICKED_UP
```

## Task 4. AI-generated poor and good designs

| Item | bikeshare-poor | bikeshare-good |
| --- | --- | --- |
| Date | 2026-10-25 | 2026-10-25 |
| AI tool and exact model | Claude Code, Claude Opus 5.5 (`claude-opus-5-5`) | Claude Code, Claude Opus 5.5 (`claude-opus-5-5`) |
| Prompt (B or C as given, or your changed prompt in full) | Prompt B as given, with the seven lines of the BikeShare specification pasted in | Prompt C as given, with the seven lines of the BikeShare specification pasted in |
| Builds with mvn package (yes or no) | yes | yes; after the first build one string literal was corrected from `"\s+"` to `"\\s+"` so that the input is split on any whitespace, as in the poor version |
| Number of classes, interfaces, enums and records | 1 class (BikeShare) | 4 classes (Main, BikeShareService, Bike, CsvRideRepository), 1 interface (RideRepository), 1 enum (MemberType), 2 records (Rental, Ride) |
| SonarQube issues, total | 10 | 2 |
| SonarQube issues, by rule | java:S2386 (mutable public static fields) 5, java:S106 (System.out) 5 | java:S106 (System.out) 2 |
| AI review findings in Check 1 | 5 | 3, all notes |
| AI review findings in Check 2 | 11 (4 Must fix, 7 Should fix) | 5, all notes |
| Review output file in the reviews folder | reviews/bikeshare-poor-review.md | reviews/bikeshare-good-review.md |

4.1 Did SonarQube report any cohesion or coupling problem in bikeshare-poor? Quote the rule and the message if it did.

No. The only design-related issue was java:S2386, "Make this member "protected"", at BikeShare.java:14-18. It is about mutable public static fields, so it touches the global data that causes common coupling, but it neither names nor detects coupling. SonarQube did not report the boolean flag of `handle`, the int member codes, or the one class that holds the menu, the rules, the records and the file saving.

4.2 Did the AI review tell the two versions apart? Give one finding for bikeshare-poor that does not appear for bikeshare-good.

Yes. The review of bikeshare-poor has 16 findings, 4 of them Must fix, and the review of bikeshare-good has 8 findings, all of them notes. For example, the poor review reports that `handle(boolean rent, ...)` at BikeShare.java:56 has logical cohesion, with control coupling from `main` at lines 42 and 44. No method in bikeshare-good takes a flag.

4.3 Which version would you rather extend with a new member type, such as a visitor who pays 4 baht per minute, and why?

bikeshare-good. A visitor needs only one new constant, `VISITOR(4)`, in MemberType.java. `MemberType.valueOf` in Main already accepts the new word, and the fee rule lives with the type. In bikeshare-poor I would add a new int code, change the type parsing at BikeShare.java:41 and the fee expression at line 68, and hope that no other place compares the codes.

## Task 5. Reflection

5.1 What does SonarQube check, and what does it not check? Answer with examples from this lab.

SonarQube checks rules inside single files and methods: probable bugs such as the `==` String comparison (java:S4973) and the unclosed FileWriter (java:S2095), and code smells such as the unused field (java:S1068), the unused import (java:S1128) and the empty catch block (java:S108). It does not judge design. It missed the five jobs of ParcelService, the flag of `printParcels`, the int size codes, and the reach-through `getLockers()`. Its counts for bikeshare-poor and bikeshare-good (10 and 2) differ mostly because of S2386 and S106, not because it saw the weak cohesion or the control coupling.

5.2 When should you not trust the AI review? Give an example from this lab.

Do not trust a finding until you have opened the file at its line, because the AI can give wrong line numbers or classify a method too confidently. In this lab the first draft of the bikeshare-poor review placed the magic number 2 (bikes per station) at lines 19 and 30, but it is at line 25, and the cohesion level of `pickUp` in parcelpoint was arguable. Be especially careful when the same AI wrote the code it reviews: the bikeshare-good review came from the model that wrote bikeshare-good, so it may be too kind to its own design.

5.3 What will you do differently in Project Phase 2 after this lab?

I will run SonarQube for IDE from the start and fix or justify each issue before I commit, instead of at the end. I will also run Prompt A on every larger change and check each finding at its line, because SonarQube alone does not see cohesion and coupling. In the design, I will use enums instead of number or string codes, avoid boolean flags that switch what a method does, keep fields private, and keep the menu and the console output apart from the rules.

## AI usage declaration

The lab's Level 3 AI Collaboration requirement applies. AI was used for the tool setup, the SonarQube run, the reviews, the two BikeShare variants, the ParcelPoint fixes, debugging and the drafting of this report. The findings and changes were checked against the source code, the SonarQube results, the Maven builds, the program runs and the CSV files, as described in each row below. All uses listed took place on 2026-10-25.

| Date | AI tool and exact model | Purpose and files or parts produced or changed | How I checked the output |
| --- | --- | --- | --- |
| 2026-10-25 | Claude Code (Claude desktop app), Claude Opus 5.5 (`claude-opus-5-5`) | Set up the tools (Maven 3.10.0, Extension Pack for Java, SonarQube for IDE 6.2.0), collected the SonarQube issues for Tables 1 and 4 with the analyzer of SonarQube for IDE, and made every commit in the repository. | Confirmed the versions with `java -version` and `mvn -version`; ran SonarQube again after the fixes; checked the commit history with `git log --oneline`. |
| 2026-10-25 | Claude Code, Claude Opus 5.5 | Ran Prompt A on the ParcelPoint starter for Task 2 and wrote `reviews/parcelpoint-review.md`; drafted Table 2 and the verification notes. | Compared each finding with the source line that it names; reproduced the zero fee and the empty parcels.csv with the required run. |
| 2026-10-25 | Claude Code, Claude Opus 5.5 | Answered Prompt B to make the poor BikeShare project in `bikeshare-poor`, and ran Prompt A on it to write `reviews/bikeshare-poor-review.md`. | Built it with Maven, ran student and staff rentals and returns, checked the receipts and the rows of rides.csv, and compared each review finding with the source; one wrong line number was corrected. |
| 2026-10-25 | Claude Code, Claude Opus 5.5 | Answered Prompt C to make the good BikeShare project in `bikeshare-good`, and ran Prompt A on it to write `reviews/bikeshare-good-review.md`. | Built it with Maven, ran the same student and staff scenarios, checked the receipts and the rows of rides.csv, and compared each review finding with the source; one regex literal and one wrong line number were corrected. |
| 2026-10-25 | Claude Code, Claude Opus 5.5 | Made the ParcelPoint fixes and refactoring of Task 3 (all files in `parcelpoint/src/main/java/parcelpoint`, plus the new `ParcelSize.java` and `ParcelStatus.java`), and drafted the report answers. | Built ParcelPoint with Maven and Temurin 21.0.12.1 after each fix; ran the required commands; checked the output, parcels.csv, the fees for small and large parcels, and the source changes. |
