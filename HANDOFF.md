# HANDOFF — Java.ReadMeGenerator documentation run

## Status

Pilot folder `tools/` only. The rest of the tree (1,455 `.java` files, 136 folders) is
untouched and is its own multi-session effort.

| Scope | Pass 1+2 | Pass 3 | Pass 4-7 | Notes |
|---|---|---|---|---|
| `tools/mementos/` | done | done | done | 2 interfaces |
| `tools/threads/` | done | done | done | 1 class, 2 defects flagged; no diagram (single type) |
| `tools/` (root of folder) | done | done | done | 16 types across 13 files |
| everything else | not started | not started | not started | see Claims below |

Tooling: `D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/target/readmegenerator.jar`,
built with the colocated Maven Wrapper. Milestones A and B are built; there is no
`scaffold`, no `consolidate-vocabulary` and no `list-dependencies` yet (Milestone C).

## Claims

One folder at a time, deepest first. Claim a row before starting, push the claim
immediately, and commit at each folder boundary.

| Folder | Files | Claimed by | Status |
|---|--:|---|---|
| `tools/` | 16 | main | done |
| `streamIO/` | 674 | — | not started |
| `function/` | 204 | — | not started |
| `graphic/` | 131 | — | not started |
| `math/` | 84 | — | not started |
| _(remaining 16 folders)_ | 362 | — | not started |

## Decisions

- **Baseline commit before any documentation write**, so every Javadoc and ReadMe edit is
  reviewable as a diff. The `.class` files alongside sources stay untracked via the
  pre-existing `.gitignore`.
- **Line endings are preserved, not normalised.** This tree is CRLF. A Python strip script
  run during tool development converted 16 files to LF and turned a 114-line addition into
  a 2,509-line diff; the content was restored byte-exact from the pre-tool commit. Use
  targeted edits, never a whole-file rewrite.
- **Banner comments, `@author`, `@version` and `Created on` lines are kept** in every
  rewritten class comment. Only the `Title: X` line is dropped, since it repeats the type
  name and carries nothing else. `Description:` becomes the summary sentence.
- **Secondary non-public top-level types are documented in place** (`LockTester` in
  `LockedSimple.java`; `ThreadLockTester` and `Monitor` in `ThreadLock.java`). Moving them
  would be a refactor, not documentation.
- **No `## Quick Start` at the repository root** — there is no build file to describe. The
  root `ReadMe.md` says so in prose instead.
- **`check-stale` is run to convergence**, not twice: an edited summary reports `stale` on
  run 1, `fresh` on run 2, nothing on run 3.
- **Edit files with the editor tool, never with a Python rewrite.** Python's `read_text`
  applies universal newlines and `write_text` then emits LF, silently converting a CRLF
  file. It caught `tools/LockAble.java` (a 38-line change became a 101-line rewrite) after
  it had already caught 16 files earlier. If a script is unavoidable, read and write
  **bytes**, never text.

## Bugs Found

Flagged, never fixed — fixing is a separate, explicitly authorized task. See the matching
`// TODO: LOGIC:` / `// TODO: SECURITY:` marker at each line.

| File Path | Class | Method | Line | Description | Severity |
|---|---|---|---|---|---|
| tools/threads/TimeOuter.java | TimeOuter | run() | 81 | Class contract says the monitored Thread is interrupted "after the given TimeOut" (one shot), but the loop re-interrupts it every `sleepTime` while it stays alive; `testIt()` has to clear `doInterrupt` by hand to get the documented behaviour. | Medium |
| tools/threads/TimeOuter.java | TimeOuter | TimeOuter(Thread, long) | 62 | `this` is published to a new Thread from inside the constructor via `new Thread(this).start()`; the fields that Thread reads are non-final and unsynchronized, so a subclass constructor or a reordered write could let it observe `sleepTime == 0` or `monitoredThread == null`. | Medium |
| tools/ErrorHandler.java | ErrorHandler | call(Object) | 120 | `ask` is initialised to `askIgnore` and never reassigned - the "ask the User" query the switch is written for does not exist - so `askAbort` and the default branch are unreachable, the do/while never retries, and every Exception from the Delegate is swallowed silently while `call()` returns null. That is the opposite of the class's stated purpose. | High |
| tools/ErrorHandler.java | ErrorHandler |  | 82 | `BeforeOp` and `AfterOp` are never assigned: no constructor parameter and no setter reaches either field, so the documented "bounding by two Operations" feature is unreachable and both null checks in `call()` are always false. | Medium |
| tools/FilterCallTransAction.java | FilterCallTransAction |  | 51 | `BeforeOp` and `AfterOp` are never assigned, so the documented (un-)Locking bracket around the transaction is unreachable - the same dead-feature defect as in `ErrorHandler`. | Medium |
| tools/FilterCallTransAction.java | FilterCallTransAction | commitTrans() | 82 | The comment claims the commit "is atomic even if it is not synchronized, because only a single Object has to be swapped", but two non-volatile fields are written unsynchronized: a concurrent reader can observe the new `Subject` while `Subst` still aliases it, or see either write out of order. Safe single-threaded only, which undercuts the locking use case the class documents. | Medium |
| tools/LockImproved.java | LockImproved |  | 65 | `writeLock` is default-initialised to 0 while "free" is encoded as `LOCK_NONE` (-1), so a freshly constructed instance looks permanently write-locked by client 0. Both `getWriteLock(int)` and `getRead_Lock(int)` take their early exit and return `LOCK_NONE` forever: no lock of either kind can ever be acquired. | High |
| tools/LockImproved.java | LockImproved | lockRead2write(boolean, int) | 228 | The rollback guards are unreachable for a wrong LockID: `setRead_Lock`/`setWriteLock` throw `IllegalArgumentException` instead of returning `LOCK_NONE`, so the caller gets an exception rather than the documented `LOCK_NONE`, and the released lock is never restored on that path. | Medium |
| tools/LockedSimple.java | LockedSimple | lockRead2write(boolean) | 145 | The `numReadLocks > 1` guard makes the whole method a no-op returning false. Promotion needs exactly one read lock (the caller's own), but the guard only enters with at least two - and then `lockWrite(true)` is refused because a read lock remains. Demotion needs the write lock held, which implies zero read locks, so that branch is unreachable. | High |
| tools/LockedSimple.java | LockTester | run() | 232 | `lockWrite(true)`'s result is discarded, so a refused lock is treated as acquired: the thread prints "Lock acquired", sleeps, then calls `lockWrite(false)`, releasing a lock it never held or throwing. The test reports success in exactly the contended case it exists to exercise. | Medium |
| tools/LockManager.java | LockManager | lock(Object) | 166 | The method is `synchronized` on `this` and then calls `super.lock(item)`, which blocks on the item's own monitor. The blocking thread keeps this object's monitor for the whole wait, so no other thread can enter `lock`, `unlock` or the global `lock()` to release it: one thread waiting for an item deadlocks every other client of the manager. | High |
| tools/LockManager.java | LockManager | lock() | 217 | `if` around `wait()` instead of `while`. The comment argues this is safe because no `notifyAll()` is used, but `unlock()` calls `super.unlock()` to "notify ALL of the other waiting Threads", and the JLS permits spurious wakeups regardless - so `wait()` can return while no-resource-locked is still false and the thread proceeds as if it held the global lock. | High |
| tools/LockManager.java | LockManager | unlock() | 250 | The counter is decremented before anything checks that the global lock is actually held; the comment says so and no check follows. An unmatched `unlock()` drives `count` to -2, releases waiters that hold nothing, and is only noticed by the last branch, which cannot run because the earlier `count >= 0` branch already consumed the common case. | Medium |
| tools/LockedServer.java | LockedServer | getLock(boolean) | 99 | Stub returning -1 unconditionally: the class satisfies `LockAble` at compile time while silently refusing every lock request at runtime. A client following the contract sees `LOCK_NONE` and cannot tell an unimplemented server from a genuinely contended one. | High |
| tools/LockedServer.java | LockedServer | setLock(boolean, int) | 115 | Stub returning -1 unconditionally - the release counterpart of the defect above. Nothing is released and no caller can distinguish that from a bad LockID. | High |
| tools/LockedServer.java | LockedServer | setLock(byte, int) | 158 | The `switch` contains no statements at all, only fall-through comments describing what each level was meant to do, so no level change ever happens and `LockLevel` is read and discarded. | Medium |
| tools/LockedServer.java | LockedServer | setLocked(Object, boolean) | 187 | The entire body is commented out, so the method silently does nothing. Callers believing they hold a lock proceed straight into the critical section. | High |
| tools/LockedServer.java | LockedServer | lockWrite() | 213 | `if` around `wait()` instead of `while`. A spurious wakeup, or a `notify()` aimed at another waiter, lets the thread fall through and set `writeLocked = true` while another thread still holds the lock, so two writers run at once. | High |
| tools/TransactFTP.java | TransactFTP | sendFile(File, long) | 122 | The timeout comparison is inverted: `timeout >= System.currentTimeMillis()` reports a timeout while the deadline is still in the future, so any call that finds the flag file present returns false on the first iteration and never waits at all. | High |
| tools/TransactFTP.java | TransactFTP | sendFile(File, long) | 133 | `renameTo`'s boolean result is discarded. On a failed rename (cross-volume, permissions, destination locked) the payload stays where it was, yet the flag file is raised anyway, so the receiver is told a transfer completed and then reads a stale or absent data file. | High |
| tools/TransactFTP.java | TransactFTP | receiveFile(File, long) | 161 | The same inverted timeout comparison as in `sendFile`: a receiver that finds no payload returns false immediately instead of waiting for one. | High |
| tools/WorkerThread.java | WorkerThread | startWithTimeOut(long) | 110 | `Thread.stop()` throws `ThreadDeath` at an arbitrary point in the worker, so it can leave a held monitor released mid-update and the shared `Params` array half written - the caller then reads a torn result rather than seeing a failure. It is also removed from the JDK: on 20 and later this line throws `UnsupportedOperationException`, making the timeout path fail outright. | High |

## Tool defects found and fixed during the pilot

Recorded here because they were found on this codebase's own code; the durable versions
live in the skill's `cli-reference.md` and in `ProgramTests.java`, one named test each.

| Symptom on this tree | Root cause |
|---|---|
| `tools/Parsing.java`'s documented `ClassSep` reported undocumented | a trailing `//` comment on a field's line displaces its Javadoc in JavaParser's attribution |
| `tools/ThreadLock.java`'s three types recorded mtimes 18 years apart | the file mtime was read per type, after each type's own write |
| `tools/ReadMe.md` grew a second title and placeholder on every run | `--scaffold-opening` treated its own placeholder as absent |
| Classes rows cut short at `Created on 7.` and `(e.g.` | first-sentence extraction split at abbreviation and date periods |
| `tools/mementos/Originator.java`'s block became `<!-- docstate* pass: 2` | the block pattern consumed the newline after its marker on rewrite |
| `tools/threads/TimeOuter.java`'s documented constructor reported undocumented | a `// TODO: LOGIC:` marker between Javadoc and declaration detached the Javadoc |

## Tags pipeline (Pass 4-7)

`raw-tags.tsv` at the repository root is the store of record and is committed: 22 confirmed
rows covering all 19 types plus the three folders, with both tag axes and all three facets
filled. `raw-tags-enriched.tsv` and `tagchunks/` are intermediates and are gitignored.

Every command runs **from this directory**: a `unit-id` is a working-directory-relative
path, so invoking one from a sub-folder produces keys nothing can join on.

- **Shared, not repo-local:** `$CLAUDE_CONFIG_DIR/tags-schema.yaml` and
  `$CLAUDE_CONFIG_DIR/tags-index.tsv` (`D:/_/_AI`) span every project on this machine, C#
  included. Pull that repo before Pass 5 and Pass 7 and push after.
- This run added **28 axis-A tags** (3,759 -> 3,787) and **19 index rows**. Both files
  flipped from CRLF to LF, which is the convention the C# skill's own documentation says to
  converge on; the diff is that flip plus this run's rows, with no unit-id lost (verified by
  comparing the sorted unit-id sets).
- The index shrank from 29,995 to 25,220 rows because the Java `build-index` merges by
  unit-id where the C# one appends. The 4,775 removed rows were exact duplicate unit-ids
  from repeated C# runs; all 25,201 distinct unit-ids survive.
- **`compact-vocabulary` was not run, and must not be run yet - now for a structural
  reason, not a fixable one.** The index rebuild that was supposed to unblock it has been
  done: `build-index` was re-run over 27 of the 31 tag stores (2026-09-04), growing
  `tags-index.tsv` from 25,220 to 32,477 rows and index-visible tags from 3,559 to 3,942.
  The invisible remainder barely moved, 2,725 -> 2,257, because **1,815 of those tags
  (80%) exist only on member-level rows** (method/field/property) and `tags-index.tsv`
  carries one row per *class* by construction. No `build-index` run can ever count them.
  So usage counting cannot see roughly a third of the vocabulary, and the third it cannot
  see is the specific, low-usage, high-information end. At the 300-tag default the cut
  lands above "used 13 times", leaving 5,406 documented units with no tag at all.
  Measured figures and the argument are in the C# skill's `tags-pipeline.md`.
  The lossless alternative is consolidating the 990 stem-collapsible tags through the
  schema's `merged:` map, which is `consolidate-vocabulary`, and both tools now rewrite
  `raw-tags.tsv` on `--apply` so such a decision actually sticks.
- **Three tag stores are unreachable and need a decision.** Their `unit-id` columns are
  absolute paths from checkout locations that no longer exist, so no fresh scan can
  reproduce their keys: `.../SpocWeb.ReadMeGenerator/ReadMeGenerator/raw-tags.tsv` (681
  rows), `_SpocWeb.Root/_std/SpocWeb.ReadMeGenerator/ReadMeGenerator/raw-tags.tsv` (681,
  byte-identical to the first) and `_org.structs/.readme-generator/raw-tags.tsv` (2,234).
  Their tags are registered in the schema and permanently uncountable. The first two look
  like stale copies of stores that already exist one directory up. Either migrate the
  unit-id paths to the current relative form or delete the stores; leaving them inflates
  every vocabulary measure with rows nothing can ever join on.
- Nine axis-B candidates were reported for review: Callable Abstraction, Concurrency, Error
  Handling, File Transfer, Interprocess Communication, Memento Pattern, Resource
  Coordination, Text Parsing, Transaction Semantics. Axis B stays a raw string in
  `concepts:` until `match-axis-b` exists (Milestone C).

## Verification of the pilot

Run from the repository root against `tools/`, after the last documentation edit:

- `list-todo --recurse` - no rows, so every type and member carries a summary.
- `check-stale --recurse` - 2 rows, 2 rows, 0 rows, 0 rows over four runs: the expected
  two-run stale-to-fresh convergence, silent thereafter.
- `list-stale --recurse` - no rows.
- `update-readme tools --recurse --subsystems --scaffold-opening` - reports `unchanged`,
  and the hand-written opening narrative, `## Architecture` and `## Entry Points` sections
  all survive the re-run untouched.
- `apply-tags --raw-tags=raw-tags.tsv` - 22 targets written, then 0 written / 22 unchanged
  on the second run. `check-stale` stays silent afterwards: the digest covers only member
  summaries, so applying tags never flips a class to stale.
- `update-readme --recurse --subsystems --scaffold-opening` still reports `unchanged` with
  the new `tags:`/`concepts:`/`facets:`/`description:` front matter in place.
- `search "read write lock arbitrary objects"` ranks `tools/LockImproved.java` third, behind
  two C# `ILock` rows - the cross-project corpus works.
- Every `.java` file under `tools/` is still CRLF.

## Next Action

The pilot folder is finished through Pass 7, and Milestones A and B of the generator are
built and proven on it. Two independent choices remain, for the user to make:

1. **Milestone C of the generator** - repair/dedup/extras: `list-corrupted`,
   `fix-doc-split`, `find-duplicates`, `scaffold`/`scaffold-remarks`,
   `consolidate-vocabulary`, `match-axis-b`/`apply-axis-b-matches`,
   `resolve-tag-conflicts`, `migrate-collaborators`.
2. **Document the remaining 1,439 `.java` files** - a multi-session effort in its own
   right. Claim a folder in the table above before starting one, deepest first, and commit
   at each folder boundary. `streamIO/` (674 files) is the largest and would itself need
   several sessions.

Whether to prune the shared vocabulary at all is a third, separate decision - see the Tags
pipeline section above. It is blocked on rebuilding `tags-index.tsv` over the C# roots
(C# tool) and, for the lossless path, on `consolidate-vocabulary` from Milestone C.
