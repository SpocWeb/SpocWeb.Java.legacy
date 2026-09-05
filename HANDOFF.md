# HANDOFF — Java.ReadMeGenerator documentation run

## Status

**2026-09-05: full-corpus autonomous run started** (main session, no sub-agents per
parallelism rules). Working the Claims table below smallest-unclaimed-first, one batch
(Pass 1+2 -> Pass 3 -> Pass 4-7) at a time, committing and pushing at each folder
boundary. This section and the Claims table are the resume point if the run stops.

Pilot folder `tools/` only, plus `knowledge/` (calibration) and now `streamIO/detector/`.
The rest of the tree (1,455 `.java` files, 136 folders, minus what's now `done` below) is
untouched.

**2026-09-05 - accidental repo-wide `check-stale .` during the `(root)` batch, found and
fully reverted.** Converging the `(root)` batch, `check-stale .` and `extract-tags .` were
run with `.` (whole-repo scope) instead of being scoped to the 9 root files - `.` recurses
the entire tree. This inserted a `<!-- docstate -->` block (`pass: 2, stale: false`) into
~1,361 files outside any claimed folder (e.g. `aspect/AAspect.java`, whose Javadoc is still
the unfilled IntelliJ/Eclipse template), and scaffolded ~1,790 stray tag rows via
`extract-tags`. **This was NOT pre-existing legacy content** - `git diff` confirmed every
inserted block was a fresh addition against HEAD, i.e. caused by this session, not
something dating from before this run. Recovery: `git status --short | wc -l` (1,373) minus
the 12 legitimate `(root)`-batch files identified the exact 1,361-file blast radius; those
were stashed (`git stash push --pathspec-from-file=...`, message starting "accidental
repo-wide check-stale side effect") and, since the stash's own working-tree revert failed
on a Windows command-line-length limit, restored to HEAD via `git checkout HEAD
--pathspec-from-file=<chunk>` in 100-file chunks (the stash entry is kept as a redundant
safety net, not needed for recovery - the content matches HEAD exactly). Verified after:
`git grep -l docstate -- '*.java' | wc -l` = 100, matching the batches genuinely completed
so far (no contamination remains). **Lesson for every future batch: always scope
`check-stale`/`extract-tags`/`update-readme --recurse` to the specific claimed file(s) or
folder, never to `.` or the repo root, unless the batch genuinely is the whole repo.**

**Adopted policy, going forward: spot-check docstate content when claiming a folder,
don't trust `stale: false` alone.** Before treating any folder's files as already done
(low `Documented`-column-implied remaining work, or a `list-todo`/`check-stale` empty
result), read a sample of the actual Javadoc content to confirm it is genuine authored
prose, not unfilled template boilerplate - `check-stale` only judges Javadoc *presence*, not
quality, so a placeholder-only class can already read as `stale: false`.

| Scope | Pass 1+2 | Pass 3 | Pass 4-7 | Notes |
|---|---|---|---|---|
| `tools/mementos/` | done | done | done | 2 interfaces |
| `tools/threads/` | done | done | done | 1 class, 2 defects flagged; no diagram (single type) |
| `tools/` (root of folder) | done | done | done | 16 types across 13 files |
| everything else | not started | not started | not started | see Claims below |

Tooling: `D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/target/readmegenerator.jar`,
built with the colocated Maven Wrapper. Milestones A and B are built; there is no
`scaffold` and no `list-dependencies` yet (Milestone C); `consolidate-vocabulary` and
`redistribute-merges` were pulled forward and are built.

## Claims

One batch at a time. Claim a row before starting, push the claim immediately, and commit
at each batch boundary. Batches are top-level folders, with `streamIO` split one level
deeper because it alone holds 46% of the corpus. `Lines` and `Documented` are measured,
not estimated; `Documented` counts files carrying a `docstate` block.

**2026-09-05: switched to up to 5 parallel agents, per explicit user instruction.**
Coordination to avoid the concurrent-write hazards the tags pipeline and git both have
(`cli-reference.md`: several Pass 4-7 commands are explicitly not locked and must never run
concurrently against the same file):
- The orchestrator alone claims/unclaims Claims-table rows, runs Pass 4-7
  (`extract-tags`/`build-vocabulary`/`apply-tags`/`build-index` against the shared
  `raw-tags.tsv`, `tags-schema.yaml`, `tags-index.tsv`), edits `HANDOFF.md`, and runs every
  git command (add/commit/push). Dispatched agents do none of that.
- Each agent does Pass 1+2 (Javadoc) and Pass 3 (folder `ReadMe.md`) for its own assigned
  folder(s) only, then reports back: Javadoc summary of what changed, any bugs found
  (file/method/line/description/severity, never fixed - only flagged inline and reported),
  and suggested tag rows (axis-A candidates, reusing schema tags where possible; axis-B
  concepts; facets) for the orchestrator to merge into `raw-tags.tsv` and run Pass 4-7 on
  afterward, one folder at a time (sequentially, even though the agents ran in parallel).
- Every agent must scope `check-stale`/`update-readme`/`extract-tags` to its own assigned
  folder path explicitly - never to `.` or the repo root - per the accidental repo-wide
  `check-stale .` run recorded above.

| Batch | Files | Lines | Documented | Status | Claimed by |
|---|--:|--:|--:|---|---|
| `streamIO/copy` | 206 | 42863 | 0 | unclaimed | - |
| `function` | 204 | 27689 | 0 | unclaimed | - |
| `streamIO/object` | 185 | 38857 | 0 | unclaimed | - |
| `streamIO/integer` | 157 | 39243 | 0 | unclaimed | - |
| `graphic` | 131 | 29665 | 0 | unclaimed | - |
| `math` | 84 | 58523 | 0 | unclaimed | - |
| `structure` | 52 | 4933 | 0 | unclaimed | - |
| `streamIO/real` | 51 | 6801 | 0 | unclaimed | - |
| `tester` | 49 | 3327 | 0 | unclaimed | - |
| `technology` | 41 | 9400 | 0 | unclaimed | - |
| `synch` | 32 | 4243 | 0 | unclaimed | - |
| `graphs` | 31 | 11258 | 0 | unclaimed | - |
| `asynch` | 28 | 3052 | 0 | unclaimed | - |
| `streamIO/(root)` | 28 | 8003 | 0 | unclaimed | - |
| `knowledge` | 27 | 3363 | 27 | done | main |
| `stringOp` | 16 | 4579 | 0 | unclaimed | - |
| `aspect` | 15 | 2493 | 0 | claimed | agent-aspect |
| `flow` | 14 | 1022 | 14 | done | agent-flow |
| `reflect` | 12 | 2492 | 12 | done | agent-reflect |
| `streamIO/diffPatch` | 11 | 2895 | 11 | done | agent-diffPatch |
| `sound` | 10 | 1030 | 0 | claimed | agent-sound |
| `tools` | 17 | 3468 | 17 | done | - |
| `(root)` | 9 | 1073 | 9 | done | main |
| `streamIO/asyncMessage` | 7 | 541 | 7 | done | main |
| `analysis` | 6 | 319 | 6 | done | main |
| `streamIO/adapter` | 6 | 435 | 6 | done | main |
| `streamIO/vector` | 6 | 947 | 6 | done | main |
| `streamIO/exception` | 5 | 536 | 5 | done | main |
| `streamIO/fileSystem` | 4 | 288 | 4 | done | main |
| `streamIO/testing` | 3 | 433 | 3 | done | main |
| `swing` | 3 | 679 | 3 | done | main |
| `persistences` | 2 | 306 | 2 | done | main |
| `streamIO/character` | 2 | 244 | 2 | done | main |
| `streamIO/factory` | 2 | 205 | 2 | done | main |
| `streamIO/detector` | 1 | 102 | 1 | done | main |

## Calibration - measured cost of one batch

`knowledge/` was documented end to end (Pass 1+2 on all 26 types, Pass 3 ReadMe, tag rows)
as a calibration batch on 2026-09-05.

| Measure | Value |
|---|--:|
| Files | 26 |
| Lines of source | 2,595 |
| Tokens, batch only | ~90,000 |
| Tokens, including the one-time reading of the four pass reference files | ~120,000 |
| Tokens per line of source | ~35 |
| Defects flagged | 12, of which 9 real (4 withdrawn, 1 found later under test) |

Extrapolated to the remaining 311,757 lines: **~11M tokens**, plus roughly 30k per session
for re-reading the pass references. That is the low end of the earlier 10-17M estimate.
Treat it as a floor rather than a forecast: `knowledge/` is dense, small, heavily
pre-commented legacy code, and the large batches (`math` at 697 lines per file, `streamIO`
at 46% of the corpus) have not been sampled.

## Tool quirks found while running the batch

- **`check-stale` reads only its first path argument.** Passing seven files silently
  processed one and reported the other six as clean. Loop per file.
- **Never write `*/` inside a `// TODO:` marker.** A marker quoting the token landed inside
  an unterminated block comment and closed it, turning the rest of the line into code;
  `list-stale` then reported a lexical error and would have skipped the file forever. The
  per-file parse gate caught it immediately, which is what that gate is for.
- **Not every file in this tree is UTF-8.** `knowledge/IdKey.java` is Latin-1; a UTF-8 read
  of it fails outright. Read and write with the encoding the file already has.
- **Match on LF, write back CRLF.** The tree is CRLF, so a patch script that matches
  multi-line strings has to normalise first or every match silently fails.

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

Flagged during documentation, never fixed in the same pass - fixing is a separate,
explicitly authorized task. The `Status` column records what happened afterwards. See the
matching `// TODO: LOGIC:` / `// TODO: SECURITY:` marker at each line for anything still
open.

Every row below was fixed under test on 2026-09-05, pinned by two suites that need no
build file, no test framework and no database:

```
javac -d out knowledge/*.java     && java -cp "out;." knowledge.KnowledgeTest
javac -d out tools/threads/*.java && java -cp "out;." tools.threads.TimeOuterTest
```

`TimeOuterTest` is the partial case worth knowing about. The interrupt-count defect is
directly observable and is tested for what it does. Unsafe publication is a data race and
cannot be reproduced on demand, so those checks pin the structural properties whose absence
made the race possible - that no constructor starts the monitoring thread, and that the
state it reads is final - rather than pretending to observe the race itself.

Every one of those checks was confirmed to fail against the pre-fix sources before the fix
landed, by compiling `git show HEAD:knowledge/*.java` into a separate tree and running the
same harness against it. A test that has not been seen red proves nothing.

| File Path | Class | Method | Line | Description | Severity | Status |
|---|---|---|---|---|---|---|
| tools/threads/TimeOuter.java | TimeOuter | run() | - | Class contract said the monitored Thread is interrupted "after the given TimeOut" (one shot), but the loop re-interrupted it every `sleepTime` while it stayed alive; `testIt()` had to clear `doInterrupt` by hand to get the documented behaviour. Confirmed red at 6 interruptions where 1 was contracted. `run()` is now one-shot and the demo no longer juggles the flag. | Medium | **fixed** |
| tools/threads/TimeOuter.java | TimeOuter | TimeOuter(Thread, long) | - | `this` was published to a new Thread from inside the constructor via `new Thread(this).start()`, and the fields that Thread reads were non-final and unsynchronized. Construction and starting are now separate: the constructor is private and starts nothing, `monitor(Thread, long)` starts the thread after construction completes, and all three fields are final. | Medium | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | insertObject(PersistAble) | - | The column list is built keys-then-fields ascending, but the value list appended fields first and then keys, each descending, so every value landed in the wrong column. Confirmed against the emitted SQL: `INSERT INTO MetricAttribute(StatusID,SubjectID,TypeID,Value) VALUES (4.5,11,22,33)`. | High | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | Condition(IPrimaryKey, String), insertObject, updateObject | - | Values were concatenated into the SQL text unquoted and unescaped, so a string containing an apostrophe produced invalid SQL and a hostile one could close the literal and append clauses. Now routed through `DBObjectFactory.literal(Object)`: numbers bare, everything else single-quoted with embedded quotes doubled. | High | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | updateObject(PersistAble) | - | Appended `STR_WHERE_` and then a `Condition()` that already opens with `WHERE`, emitting `SET Value=4.5  WHERE  WHERE (TypeID=11)`. Not flagged during documentation; found by inspecting the captured SQL. | High | **fixed** |
| knowledge/Objekt.java | Objekt | getType() | - | Keyed by `StatusID` instead of `TypeID` and routed through `FactoryStatus` rather than `FactoryType`, so it resolved and cached the Status row as this object's Type whenever the two IDs differ. | High | **fixed** |
| knowledge/BasicAttribute.java | BasicAttribute | getType() | - | Same defect as `Objekt.getType`. | High | **fixed** |
| knowledge/CachedValue.java | CachedValue | assertIsDirty(boolean) | - | The null test was inverted: `calculator.run()` sat inside the `calculator == null` branch, so the method threw NullPointerException when no calculator was set and never invoked one that was. | High | **fixed** |
| knowledge/Status.java | Status | finalize() | - | Empty in both branches while `DbCachedFactory`'s weak cache was documented on the assumption that it wrote back. The finalizer is removed rather than implemented: a finalizer runs on the collector's thread, arbitrarily late, and possibly never. Both class comments now say plainly that a collected object is not persisted. | Medium | **fixed** |
| knowledge/DbCachedFactory.java | DbCachedFactory | DbCachedFactory(Connection, PersistAble) | - | The `SELECT Max(ID)` failure was swallowed whole, leaving `MaxID` at 0 and making a failed query indistinguishable from a table whose largest ID really is 0. The constructor already declared `SQLException`; it now propagates. | Medium | **fixed** |
| knowledge/IdKey.java | IdKey | newInstance(IPrimaryKey) | - | The block comment disabling the dead overload was never closed, so it ran on through the following Javadoc and swallowed it. Closed; `list-todo knowledge` is now empty. | Low | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | DBObjectFactory(Connection, PersistAble) | - | `strDBFieldNames` is `ARRAY_TO_STRING(FieldNames, ",")` with the last character stripped, which throws `StringIndexOutOfBoundsException` on a prototype whose `Fields()` is empty (verified: the join returns "" and `substring(0, -1)` throws). No current class has empty Fields, so it is latent. Noted while fixing the insert order; deliberately not fixed, as it is outside the flagged set. | Low | open |
| knowledge/DBObjectFactory.java | DBObjectFactory | DBObjectFactory(Connection, PersistAble) | - | The column-name strings are built from `FieldNames()`/`KeyNames()`, the Java field names, rather than `DBFieldNames()`/`DBKeyNames()`. Every class in this package defines the two as the same array, so it is currently harmless, but it defeats the point of having separate DB names. Noted while fixing; not fixed, as it is outside the flagged set. | Low | open |
| knowledge/MetricAttribute.java, StringAttribute.java, TimeAttribute.java, EnumAttribute.java | - | - | - | Originally flagged High: the static `Fields` array seeded from an empty array rather than `BasicAttribute.Keys`, said to drop the inherited key columns. **Withdrawn - not a defect.** `Fields` means "non-key data columns" here and the keys arrive separately from `primaryKey().Keys()`; at runtime `MetricAttribute` reports `Fields=[Value]` and `Keys=[StatusID, SubjectID, TypeID]`, which covers the table exactly. Only the inherited javadoc phrase "including Parent Fields" was wrong. | - | **withdrawn** |
| persistences/PersistedObject.java | PersistedObject | PersistedObject(String) | 165 | Checks the field `ID` (always null at that point, before it is ever assigned) instead of the parameter `ID_`, so the guard is always false and `setId(ID_)` is never called from this constructor. Every instance built via `new PersistedObject(ID_)` or `PersistedObject(ResultSet)` (which delegates to it) keeps a `null` ID. | High | open |
| persistences/PersistedObject.java | PersistedObject | (field `objects`) | 41 | The static registry HashMap is never initialized (`= new HashMap()` missing), so `getObject(String)` and `setId(String)` both throw `NullPointerException` on the first real use. Currently unreached in practice only because the constructor bug above never calls `setId`. | High | open |
| swing/HashTreeNode.java | HashTreeNode | equals(Object) | 328 | When this Node's `userObject` is null (the Empty Constructor allows it) and `arg` is a non-null Object that is not a `DefaultMutableTreeNode`, the final branch calls `userObject.equals(arg)` on a null `userObject` and throws `NullPointerException`. | Low | open |
| streamIO/testing/ATestCase.java | ATestCase | test(Object, Method, IIStreamOut, IIStreamOut, IIStreamOut) | 214 | Both branches of the `InvocationTargetException` handler log/rethrow the wrapping `x` instead of `inner` (`x.getTargetException()`), the exception the test method actually threw. Every reflectively-run test failure is reported with the reflection wrapper's stack trace instead of the real cause. | Medium | open |
| streamIO/fileSystem/FileIterator.java | FileIterator | isValid() | 76 | Returns `!available`, inverted relative to its own `available` field and to the sibling `FileBackupIterator.isValid()` (which returns `available` directly): reports "valid" only once exhausted. | Medium | open |
| streamIO/fileSystem/FileIterator.java | FileIterator | currItem() | 100 | Always returns `null`: `nextItem()` never assigns the `currItem` field before returning its result, unlike the sibling `FileBackupIterator.nextItem()` which does `return filter = new FileOutputStream(...)`. | Low | open |
| streamIO/exception/ChainedException.java | ChainedException | printStackTrace(PrintStream) | 136 | The `PrintWriter` wrapping the given `PrintStream` is never flushed or closed, so the printed trace can remain buffered and never reach the stream. The sibling `BaseException.printStackTrace(PrintStream)` does the identical job but explicitly calls `pw.close()`, commented "important to flush!". | Low | open |
| streamIO/vector/CombinationStream2.java | CombinationStream2 | (field `L`) | 34 | The Logger is constructed with `CombinationStream.class` instead of `CombinationStream2.class` (copy-paste from the sibling class), so every message this Logger writes is mislabeled as coming from `CombinationStream`. | Low | open |
| streamIO/adapter/CValue2StreamIn.java | CValue2StreamIn | CValue2StreamIn() | 48 | The only constructor never assigns the `cValue` field, and there is no other constructor or setter to do so - unlike every sibling adapter in this package, which takes and assigns its wrapped dependency in its constructor. `cValue` stays null and `nextItem()` throws `NullPointerException` on first use. | High | open |
| DirToXml.java | DirToXml | execRecursive(File, PrintWriter, String[], String) | 71 | `SimpleDateFormat("yyyy-mm-dd'T'hh:MM:ss")` swaps the month and minute Format Letters (lowercase `mm` is minutes, uppercase `MM` is month), so the written date's Month field shows the current minute-of-hour and its Minute field shows the month number. | Medium | open |
| EchoFile.java | EchoFile | main(String[]) | 63 | The `default` case of the argument-count switch (0 or 3+ args) prints the Syntax message but does not return, so `echoFile(args[0], args[1])` afterward throws `ArrayIndexOutOfBoundsException` instead of exiting cleanly. | Low | open |
| FileHex.java | FileHex | main(String[]) | 37 | In the `IOException` catch block, `e.fillInStackTrace()` is called but its result is discarded and `e` is never used again - dead code, almost certainly meant to enrich `n` (the Exception actually thrown) instead. | Low | open |
| FilterFind.java | FilterFind | read() | 172 | `breakCountDown` defaults to 0 and is never set in the constructor, so on the very first call `--breakCountDown == -1` is immediately true and `read()` returns EOF before ever reading from the wrapped Stream or checking for the Separator. `main()`'s `while (streamIn_.available() > 0)` loop then never terminates, since the underlying Stream is never actually consumed through this filter. | Medium | open |
| FixRecordScrambler.java | FixRecordScrambler | main(String[]) | 149 | The 7 documented parameters require `args.length==7` and are read up to `args[6]`, but the guard only warns (without returning) when `args.length!=6` - passing exactly 6 arguments, which satisfies neither the guard nor the actual requirement, throws `ArrayIndexOutOfBoundsException` at `args[6]` instead of showing the Syntax message. | Low | open |
| reflect/Type.java | Type | Type(Class) | 126 | Checks the field `cls` (still `null` at that point) instead of the constructor argument `cls_`, so every call to `new Type(...)` throws `NullPointerException` - including the static `TYPE` initializers in `IThing`/`IIndividual`/`IIntangible`/`IMathThing`/`IType`. Should be `if (!cls_.isInterface())`. | High | open |
| reflect/Type.java | Type | isAssignableFrom(Class) | 304 | The parameter `cls` shadows the field `this.cls`, so this calls `cls.isAssignableFrom(cls)` - the argument compared to itself - always `true`, regardless of the wrapped Type's actual `Class`. Should be `return this.cls.isAssignableFrom(cls);`. | Medium | open |
| streamIO/diffPatch/VersionTree.java | VersionTree | LESS(int[], int[]) | ~107 | Lexicographic comparison only early-returns `true` on `arr1[i] < arr2[i]`, never early-returns `false` when `arr1[i] > arr2[i]` at an earlier, decisive index - e.g. `LESS([5,1],[3,9])` returns `true` even though `[5,1] > [3,9]`. Used in `readField()` to decide which Version a Branch tag points to after deserialization; can silently point a tag at the wrong Version. | Medium-High | open |
| streamIO/diffPatch/VersionTree.java | VersionTree | writeTo() | ~174 | When more than 10 non-Branch Tags exist, the resize block computes a doubled `tmp` array but never assigns it back to `tags`, and the resize check is off-by-one. Serializing a Tree with more than 10 real Tags throws `ArrayIndexOutOfBoundsException` on the 11th. | High | open |
| streamIO/diffPatch/VersionTree.java | VersionTree | addVersion(DiffSet) | ~384 | `(currDiff.getBranch() != diff.getBranch())` is duplicated verbatim as both clauses of an `&&`, almost certainly meant to be a `.equals()` check - relies on String reference identity, so after deserialization (fresh Branch-name Strings) a legitimate same-Branch append can be misidentified as a different Branch and throw a spurious `VersionException`. | Medium | open |
| flow/push/MultiCaster.java | MultiCaster | putA(Object) | ~60-65 | Creates one new unpooled `Thread` per pushed item to deliver to `next2`, with no pooling or throttling. Under sustained high-throughput input this exhausts OS thread resources and can throw `OutOfMemoryError: unable to create native thread`. | Low-Medium | open |

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
- **The Java tool can now consolidate the shared vocabulary itself (2026-09-05).**
  `consolidate-vocabulary` and `redistribute-merges` are built, pulled forward out of Milestone
  C because `tags-schema.yaml` is machine-wide and the Java side could otherwise only follow a
  C# consolidation by hand. Normalisation tables, merge semantics and the `confidence` column's
  spelling are copied from the C# consolidator on purpose: both tools write the same file, so a
  difference would make the vocabulary depend on which ran last. Verified against the live
  schema - both propose exactly `generics -> generic`, usage 10, `PluralSingular`.

  That pair is in the reject ledger, which is why `--reject-file` was added in the same change
  and should be passed on **every** run: the ledger is machine-wide because whether two tags
  mean the same thing does not depend on which repo, or which tool, asked. With it the Java
  tool proposes nothing, which is the correct answer for the current vocabulary.

  75 Java tests pass. Console messages now use plain hyphens; the em dashes were rendering as
  mojibake in the Windows console.

- **The vocabulary is consolidated on the singular form (2026-09-05).** The 115 reviewed
  near-duplicate merges are applied, with the singular canonical in every case rather than
  whichever spelling the index happened to use more - `--prefer=singular`, added for this,
  since the usage-driven direction had sent 90 pairs one way and 26 the other and resolved the
  same word in opposite directions across two namespaces. 19 of the 115 reverse merges an
  earlier session had already applied the other way, including `code/extension_method` ->
  `code/extension_methods` across 1,363 references.

  Final state, verified: `merged:` has 115 entries, no cycles, no chains, no plural canonical,
  no canonical missing from the free list; 6,407 free tags; and **no merged-away spelling
  remains in any of the 31 tag stores**. Total written: 9,845 tag references across the corpus
  and 4,086 store rows, in two passes.

  Four defects surfaced doing it, all now fixed and tested: `ApplyMerges` recorded both
  directions when a merge was reversed (a two-step cycle) and left the winning spelling out of
  the free list; redistribution reordered `[Tags]` arguments alphabetically on declarations
  where nothing merged (55,151 of the first pass's 66,373 references were that churn); the
  raw-tags rewriter normalised separators on untouched rows; and `SaveSchema` wrote CRLF into a
  file every other writer keeps LF.

  **That design gap is now closed.** `redistribute-merges <schema.yaml> <path> [--apply]`
  applies the schema's recorded `merged:` map directly, with no proposal step, and never writes
  the schema. It is idempotent, which makes it the cheapest check that a consolidation landed.
  Its very first run found a straggler two full passes had missed - and `--apply` then wrote
  nothing, which exposed a second bug: `MemberCommentWriter` located the `<example>` block with
  a regex that blew through its 5-second timeout on `Tensor.cs` (6,391 lines, 245 example
  blocks), and a timeout is treated as "no match" and skipped in silence. The block is located
  by a line scan now and the regex is gone. `redistribute-merges` over Code/NET reports 0
  references and 0 timeouts; 297 C# tests pass.

- **The `_org.structs` store's corrupted rows are repaired.** Nine rows had lost their tab
  separators (written as PowerShell's literal `` `t `` escape, or as a single space) and had
  backslashes in their tag paths, so their unit-ids parsed as nothing and they were inert.
  Seven are reconstructed by splitting on the level keyword. That made five units live which
  also appear further down the file with *different* tags, so each pair was collapsed by union
  rather than picking a winner. The store is now uniformly 8 columns, duplicate-free and LF,
  and all 2,401 of that tree's index rows carry tags.

  Seven rows are deliberately left as they are, because deleting them is a content decision:
  three name files that no longer exist (`StepRKQ.cs`, `TestRandom.cs`, `BitNoise.cs`), two are
  folder rows for `statistics/` and `modeling/` which are not on disk and carry no tags, one has
  level `derivative` and unit `mathematical-function` (a concept written into a code-unit row),
  and one `TestBodyFuncs.cs` row has its own path prefix pasted on twice.
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
- **The absolute-path stores are migrated, and the join had a second bug.** Five stores (not
  three) held absolute paths - 5,201 rows - and 5,273 of 5,280 rows were rewritten to the
  relative form on 2026-09-04, backups in the session scratchpad. Two of the five were the
  807-row class-level stores that the earlier sweep had *included*, so those runs attached no
  tags at all while reporting success. Re-measuring then went backwards, which exposed the
  bigger defect: one store records `_root/Db/...` where the filesystem says `_root/db/...`,
  and the unit-id lookup used `StringComparer.Ordinal`, so 1,131 rows of curated tags joined
  to nothing on casing alone. The lookup is `OrdinalIgnoreCase` now, both copies of it are one
  method (the duplication is why the earlier duplicate-unit-id fix reached `apply-tags` and
  not `build-index`), and `RawTagsLookupTests` pins all three behaviours. 285 C# tests pass.

  Coverage per root afterwards: `_root/db` 461/461 index rows tagged (was 15/461),
  `_org.structs` 2,401/2,401, `_SpocWeb.Root` 12,231/14,527, 26,507 of 32,810 index rows
  overall. The corpus-wide *distinct*-tag count is not a coverage measure and drifted down
  (3,942 -> 3,855) as rows were re-derived from their stores.

  Residual: an integrity check keyed on (file basename, class name) finds 353 of 15,668
  class-level store rows whose tag is still absent from the matching index row - about 2.3%,
  some of which is collision noise from that lossy key. Not chased further.
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
