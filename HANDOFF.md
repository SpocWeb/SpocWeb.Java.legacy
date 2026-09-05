# HANDOFF — Java.ReadMeGenerator documentation run

## Status

**2026-09-05 (latest): all in-flight work from the previous pause has been closed out.**
`math/vector` (15 files) is now fully done - the rate-limited background agent had actually
finished 5 of its 6 remaining files before failing; a follow-up agent finished the last one
(`VectorDouble.java`), and the orchestrator independently verified `list-todo math/vector`
returns zero rows, ran Pass 3 (folder `ReadMe.md` for both `math/vector` and
`math/vector/statistic`, hand-authored opening paragraphs) and Pass 4-7 (1 new axis-A tag
`code/statistical_correlation`, 50 tag targets written, index built), recorded all 63 bugs
found across the folder in the Bugs Found table (16 from the first 9 files + 47 from the
6 large typed-array classes - many are the same handful of copy-paste-origin defects
repeated across `VectorChar`/`Long`/`Short`/`Int`/`Float`/`Double`: `mulAt`/`divAt`
delegating to `subAt`, scalar `mulAt`/`divAt` ignoring their argument, `removeAt` corrupting
`itemCount` before validating the index, `oneAt` filling with 0 instead of 1), and
committed+pushed both repos. Everything from this session is now fully committed and
pushed - there is no in-flight work and no uncommitted state.

**What's fully committed+pushed (both `D:/_/_Matthias/Code/Java` and `D:/_/_AI`):**
- Milestone C of the companion CLI (`D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/`)
  is **entirely built** - part 1 (repair/dedup: `list-corrupted`, `fix-doc-split`,
  `fix-javadoc-escape`, `find-duplicates`, `annotate-pairs`) and part 2 (migration/scaffold:
  `resolve-tag-conflicts`, `migrate-frontmatter`, `add-tree`/`remove-tree`,
  `match-axis-b`/`apply-axis-b-matches`, `migrate-collaborators`,
  `scaffold`/`scaffold-remarks`/`scaffold-collaborators`) both landed, 129 tests green, jar
  rebuilt at `.../ReadMeGenerator/target/readmegenerator.jar`. `SKILL.md`'s milestone table
  and `cli-reference.md` are up to date. **The `scaffold` command is now available** for
  future batches but has not yet been adopted in the workflow below - still using
  hand-authored Javadoc via Write/Edit per the existing per-batch agent prompts. Worth
  reconsidering for efficiency in a future session, not required.
- Claims-table batches closed out this session (Pass 1-3 + tags + HANDOFF + commit/push,
  all `done` below): `graphic` (root+example+implement+svg, 34 bugs), `graphic/math3D`
  (1 bug, +1 new axis-A tag `code/platonic_solids`), `graphic/mvc` (10 bugs, +4 new axis-A
  tags `code/matrix_operations`/`code/vector_operations`/`code/texture_mapping`/`code/z_ordering`),
  `math/matrix` (11 bugs, 0 new tags), `math/vector` (63 bugs, +1 new axis-A tag
  `code/statistical_correlation`).

**Next up - pick up from here:**
`streamIO/copy` (206 files, 42863 lines), `function` (204 files, 27689 lines),
`streamIO/object` (185 files, 38857 lines), `streamIO/integer` (157 files, 39243 lines) -
these are large enough to likely need sub-batch splitting by line count (see how `math` and
`graphic` were split into sub-rows earlier this session) before dispatching, to stay under
the ~400k-token-per-agent budget in the `parallelism-rules` skill.

**Standing instructions still in force:** "finish all passes of /Java.ReadMeGenerator on
the whole corpus autonomously, only stop when problems occur" + "continue as before with up
to 5 parallel agents" (reaffirmed via "build Milestone C, then continue"). The pause just
now was an explicit user interruption, not a stopping problem - resume the autonomous run
per those standing instructions once picked back up, following the exact orchestration
pattern used throughout this session (see "up to 5 parallel agents" coordination rules
below): orchestrator alone runs git/tags/HANDOFF edits; dispatched agents do Pass 1+2+3 only
for their assigned folder, never spawn sub-agents of their own, and must independently
verify `list-todo` returns zero rows before reporting done (not just `check-stale`).

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
| `streamIO/copy` (root+boole+groupM+monoid+order+primitiveOp+shift) | 82 | 11329 | 82 | done | agent-copy-misc |
| `streamIO/copy/group` | 124 | 31328 | 0 | claimed | agent-copy-group |
| `function` (root+index+real+string+vector+byref) | 98 | 12899 | 98 | done | agent-function-misc |
| `function/derive` | 106 | 14586 | 106 | done | agent-function-derive |
| `streamIO/object` (root+backTrack+filterIn+filterInOut+filterOut+integer+yaml+json) | 79 | 13501 | 79 | done | agent-object-misc |
| `streamIO/object/enumer` | 79 | 18229 | 0 | unclaimed | - |
| `streamIO/object/parser` | 27 | 6942 | 0 | unclaimed | - |
| `streamIO/integer` (needs further sub-batch splitting, 157 files/39243 lines total) | 157 | 39243 | 0 | unclaimed | - |
| `graphic` (root+example+implement+svg) | 50 | 14297 | 50 | done | agent-graphic-misc |
| `graphic/math2D`+`graphic/ms3d` | 18 | 3525 | 18 | done | agent-graphic-2d |
| `graphic/math3D` | 32 | 6425 | 32 | done | agent-graphic-math3D |
| `graphic/mvc` | 26 | 4789 | 26 | done | agent-graphic-mvc |
| `math` (root+algorithm+integration+wavelet) | 18 | 3123 | 18 | done | agent-math-core |
| `math/fit`+`math/refiner` | 27 | 3644 | 27 | done | agent-math-fit |
| `math/minimizer` | 11 | 3043 | 11 | done | agent-math-minimizer |
| `math/matrix` | 13 | 15603 | 13 | done | agent-math-matrix |
| `math/vector` | 15 | 33025 | 15 | done | agent-math-vector (+2 follow-up agents for VectorChar/Long/Short/Int/Float, then VectorDouble) |
| `structure` | 52 | 4933 | 52 | done | agent-structure |
| `streamIO/real` | 51 | 6801 | 51 | done | agent-streamIO-real |
| `tester` | 49 | 3327 | 49 | done | agent-tester |
| `technology` | 41 | 9400 | 41 | done | agent-technology |
| `synch` | 32 | 4243 | 32 | done | agent-synch |
| `graphs` | 31 | 11258 | 31 | done | agent-graphs |
| `asynch` | 28 | 3052 | 28 | done | agent-asynch |
| `streamIO/(root)` | 28 | 8003 | 28 | done | agent-streamIO-root |
| `knowledge` | 27 | 3363 | 27 | done | main |
| `stringOp` | 16 | 4579 | 16 | done | agent-stringOp |
| `aspect` | 15 | 2493 | 15 | done | agent-aspect |
| `flow` | 14 | 1022 | 14 | done | agent-flow |
| `reflect` | 12 | 2492 | 12 | done | agent-reflect |
| `streamIO/diffPatch` | 11 | 2895 | 11 | done | agent-diffPatch |
| `sound` | 10 | 1030 | 10 | done | agent-sound |
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
  processed one and reported the other six as clean. Loop per file. Confirmed again
  2026-09-05: `check-stale streamIO/A.java streamIO/B.java` reports only whichever file is
  listed first, in either order - never assume a multi-path invocation covered every path.
- **`check-stale` convergence is not a completeness gate - `list-todo` is.**
  `check-stale` only tracks whether the docstate digest matches current content; it will
  happily report `fresh` for a file that still has a doc block with no extractable summary
  sentence (e.g. `@return`-only Javadoc, or Javadoc that starts mid-tag). Four batches this
  session (`graphs`, `stringOp`, `synch`, `streamIO/(root)`) were each reported "fully
  converged" by their agent on the strength of `check-stale` alone, but `list-todo` run
  independently afterward still showed real gaps in all four (over 100 rows in `graphs`
  alone). Treat a batch as done only once `list-todo <path>` returns zero data rows, run
  as its own separate check after `check-stale` looks clean - never take `check-stale`
  convergence as sufficient proof by itself.
- **Never write `*/` inside a `// TODO:` marker.** A marker quoting the token landed inside
  an unterminated block comment and closed it, turning the rest of the line into code;
  `list-stale` then reported a lexical error and would have skipped the file forever. The
  per-file parse gate caught it immediately, which is what that gate is for.
- **Not every file in this tree is UTF-8.** `knowledge/IdKey.java` is Latin-1; a UTF-8 read
  of it fails outright. Read and write with the encoding the file already has.
- **Match on LF, write back CRLF.** The tree is CRLF, so a patch script that matches
  multi-line strings has to normalise first or every match silently fails.
- **`apply-tags` shares the same first-path-only quirk as `check-stale`/`list-todo`.**
  Confirmed 2026-09-05 on `streamIO/copy`: passing 5 root file paths in one `apply-tags`
  call wrote tags for only 1 of them (0 targets for the file that happened to already be
  tagged, "unchanged" for it, real writes for the rest) - always invoke per-file.
- **`check-stale` can corrupt a docstate block via duplicated comment fragments.**
  `streamIO/copy/TestCopy.java`'s docstate block had each line prefixed with a duplicated
  `//import Stream.*` fragment after a `check-stale` run - manually rewritten clean by the
  agent. Worth a closer look if it recurs elsewhere.
- **`list-todo` has a persistent false positive on `streamIO/object/AND.java:AND.testIt()`**,
  reporting "no Javadoc comment" even though the method has a valid one-line Javadoc -
  confirmed via `list-corrupted` and direct source inspection. Treat this one specific row
  as a known tool limitation, not an outstanding documentation gap.

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
| sound/WaveDataChunk.java | WaveDataChunk | WaveDataChunk(...) (16-bit case) | ~66 | `streamIn.readInt()` (4 bytes) fills `stream16`, but a 16-bit Sample is only 2 bytes - over-reads the data Chunk by 2x for the common 16-bit PCM case, misaligning/exhausting the Stream. Should use `readShort()`. | High | open |
| sound/MidiChunk.java | MidiChunk | MidiChunk(...) | ~44 | `events = new byte[chunkSize]` is allocated but never filled (no `readFully`), and the Stream position is never advanced past this Chunk's bytes - `events` stays all-zero and the next Chunk read (e.g. the next Track in `MidiFile`) misreads the unread bytes as a new Chunk Header, breaking multi-track MIDI parsing entirely. | High | open |
| sound/WaveFile.java | WaveFile | main(String[]) | ~92 | `streamOut.close()` is called unconditionally, but `streamOut` is `null` whenever `args` is non-empty. Any real command-line invocation with a file argument throws NPE. | Medium | open |
| sound/WaveStreamOut.java | WaveStreamOut | addInt(int) (24-bit case) | ~54 | `writer.writeInt(b)` writes 4 bytes for a 24-bit Sample instead of 3, corrupting Sample Values and producing a data Chunk longer than the Size header written in the constructor. Mirrors a pre-existing `//TODO: write 3 Bytes` comment left by the original author. | Medium | open |
| sound/WaveStreamIn.java | WaveStreamIn | WaveStreamIn(...) | ~45 | `IOException` from the initial `skipBytes(channel*...)` is silently swallowed; if the skip fails partway, every subsequent Sample is read from the wrong Offset with no error signal. | Medium | open |
| sound/WaveStreamIn.java | WaveStreamIn | nextLongInternal() (24-bit case) | - | Reads via `readInt()` (4 bytes) for a 24-bit Sample; the symmetric read-side counterpart to the WaveStreamOut 24-bit bug above. Already flagged by the original author's own `//TODO: read only 3 Bytes!` comment - real and still present, reported here per protocol, left as the author's existing comment. | Medium | open |
| sound/DirectPlayer.java | DirectPlayer | keyPressed(...) | ~294 | `GET_KEY` can return `65535` (the CapsLock key value), out of bounds for the 256-entry `NOTES_BY_KEY` array; the resulting `ArrayIndexOutOfBoundsException` is silently swallowed, so CapsLock plays no note (harmless, but silent). | Low | open |
| aspect/AHierarchyAspect.java | AHierarchyAspect | update(Object, Object, Object) | ~233 | Type check tests `IAspect.class.isAssignableFrom(field.getType())` but the result is cast to `IHierarchyAspect`; every other reflection loop in the class checks `IHierarchyAspect.class`. A public field typed as a plain (non-hierarchy) `IAspect` would pass the check and throw `ClassCastException` at the cast. No such field exists among the 15 in-scope classes today, but the interface permits it. | Low-Medium | open |
| aspect/ListAspect.java | ListAspect | removeVal(int) | ~243 | Bounds guard uses `Index > list.size()` instead of `>=`, so `Index == list.size()` (one past the last element) slips through to `list.remove(Index)`, which throws an unchecked `IndexOutOfBoundsException` instead of the graceful `null` this method returns for every other invalid index. | Low | open |
| aspect/ListAspect.java | ListAspect | testList() | ~324 | `asp2.set(PersonAspect.HOME + SEP + AddressAspect.CITY, ...)` uses `PersonAspect.HOME` ("home") as a field-name prefix, but `PersonAspect` has no field named "home" - its address field is `PersonAspect.ADDRESS`. `getLocalField()` fails to resolve and returns `null`, so `set()` throws `NullPointerException`; running `ListAspect.testIt()`/`main()` crashes immediately at this line. | Medium | open |
| aspect/dialog/BoolQuestion.java | BoolQuestion | setValue(Object) | ~95 | `str.charAt(0)` throws `StringIndexOutOfBoundsException` on empty trimmed input, and `val.toString()` throws `NullPointerException` if `val` is null. A user running the console `Dialog` who presses Enter with no input at a Yes/No prompt crashes the dialog instead of re-prompting or defaulting. | Medium | open |
| asynch/Barrier.java | Barrier | (field/constructor) | - | Barrier's count field is left uninitialized before use in the wait/release path. | Medium | open |
| asynch/BlockedThreadExecutor.java | BlockedThreadExecutor | execute/run path | - | Reuses a stale `Runnable` reference and is missing a `notify()` on the completion path, so a waiting caller can block indefinitely. | High | open |
| asynch/Scheduler.java | Scheduler | scheduling loop | - | Busy-waits instead of blocking/parking, burning CPU while idle. | Low-Medium | open |
| asynch/ThreadExecutor.java | ThreadExecutor | numTasks bookkeeping | - | `numTasks` drifts from the actual queue/assignment state, matching the pre-existing note in `SimpleThreadPoolExecutor`'s own Javadoc that this feedback mechanism "is not reliable". | Medium | open |
| asynch/ThreadPoolExecutor.java | ThreadPoolExecutor | (synchronization) | - | A code path calls `wait()`/`notify()` outside a `synchronized` block on the relevant monitor, which throws `IllegalMonitorStateException` at runtime. | High | open |
| asynch/QueuedSemaphore.java | QueuedSemaphore | acquire/release | - | Lost-wakeup race: a release can occur between a waiter's failed acquire check and its `wait()` call, with no re-check afterward, leaving the waiter blocked with no further signal. | High | open |
| stringOp/Grammar.java | Grammar | evolve(...) | ~51 | Off-by-one: bound check uses `>` instead of `>=` against `Productions.length` (128), so the last index is accessible when it shouldn't be / one past overflows silently depending on direction. | Medium | open |
| stringOp/SentenceComparer.java | SentenceComparer | getMostSimilarSentence(String, boolean, int) | ~124 | The index of the best-matching Sentence is never recorded (only the match count `maxMatch` is tracked), so the method always returns -1 regardless of the actual best match found. | High | open |
| stringOp/SentenceComparer.java | SentenceComparer | getWordSet(String, boolean) | ~151 | Unimplemented: the Sentence is never parsed into Words and the Dictionary is never consulted; always returns an empty `BitSet`. | High | open |
| stringOp/search/SearcherBM.java | SearcherBM | constructor and search loop | ~48, ~68 | `Object.hashCode()` can be negative; Java's `%` keeps the sign, so a negative hash used as an array index throws `ArrayIndexOutOfBoundsException` instead of wrapping into a valid bucket. | Medium | open |
| synch/UniCastConstrained.java | UniCastConstrained | addValidator(IValidator) | ~67 | Checks `instanceof MultiCaster`, an unrelated class - `validator` is only ever null, a plain `IValidator`, or a `MultiValidator`. The dead branch aside, the `else` path re-wraps the existing validator into a new `MultiValidator` without ever adding the new argument, so every call after the first silently drops the new validator. | High | open |
| synch/ValidationRuleList.java | ValidationRuleList | validateInThread(Object) | ~146 | `params` is a 2-element array (`{Value, null}`) but the method unconditionally reads `params[3]` after the worker thread returns, throwing `ArrayIndexOutOfBoundsException` on every timed validation. | High | open |
| synch/APubUniLinkSub.java | APubUniLinkSub | update(Object, Object, Object) | ~117 | No null check on `subscriber` before propagating; the chain-terminal node (built with `subscriber == null` by `addSubscriber()`) throws `NullPointerException` as soon as propagation reaches it - every other propagation method in the package guards this. | High | open |
| synch/PropDouble.java | PropDouble | setValue(double) | ~48 | Never calls `subscriber.update(...)`, so the documented "notifies its Subscriber on change" contract is a no-op; any caller relying on the notification silently gets none. | Medium | open |
| synch/StateMachine.java | StateMachine | toString() | ~110 | Inner loop bound uses `a.length` (row count/`numInputs`) instead of `a[i].length` (column count/`numStates`); throws `ArrayIndexOutOfBoundsException` or silently omits columns for any non-square matrix. | Medium | open |
| streamIO/AReSetAble.java | AReSetAble | JUMP(IReSetAble, long) | ~106 | Loop guard `++i < offset` is only ever true for a positive offset; a negative offset (as passed by `PUSH_BACK`, which relies on this returning `-1`) always returns `0` without calling `iter.jump()`, so `pushBack()` can never succeed through this static helper. | Medium | open |
| streamIO/Log.java | Log | XML_DATE_FORMATTER (field) | ~247 | A single static `SimpleDateFormat` instance is shared and invoked from `GET_XML_DATE()`/`GET_XML_DATE(Date)` across all Loggers and threads; `SimpleDateFormat` is not thread-safe, so concurrent logging calls can corrupt the formatted date or throw. | Medium | open |
| streamIO/StringBufferOutputStream.java | StringBufferOutputStream | addBuffer(StringBuffer, int) | ~208 | Calls `addBuffer(b, 0, stop)` against a 3-arg overload whose parameter order is `(b, stop, start)` - reversed vs. the correct sibling `addString(String, int)`. For any `stop > 0` this silently appends nothing. | Medium | open |
| graphs/AGraph.java | AGraph | (edge-filtering method) | ~116 | Filters by `curr.val` (the target Node index) instead of `curr.weight`, so weight-based edge filtering silently filters on the wrong field. | Medium | open |
| graphs/SparseMatrix.java | SparseMatrix | getDegree/getInDegree helper | ~241 | Calls itself instead of `getOutDegree(j)`, causing infinite recursion and a `StackOverflowError` on every call. | High | open |
| technology/RandomGUID.java | RandomGUID | getRandomGUID(boolean) | 160 | If `MessageDigest.getInstance("MD5")` throws `NoSuchAlgorithmException`, the catch block only logs it and leaves `md5 == null`; execution falls through to `md5.update(...)`, throwing an unhandled `NullPointerException` instead of failing with the original cause. | Low | open |
| technology/xml/XmlToDirHandler.java | XmlToDirHandler | main(String[]) | 209 | `System.out.println(args)` prints the `String[]` array's reference/hashcode (e.g. `[Ljava.lang.String;@...`) instead of its contents; likely meant `Arrays.toString(args)` or a loop over the elements. | Low | open |
| technology/xml/XmlUnmarshaller.java | XmlUnmarshaller | setBuffer(String) | 298 | Duplicate `argType == long.class` check (already handled two branches above), almost certainly meant `argType == double.class`. A double-typed field is never converted here and falls through to the no-op else branch, leaving the raw String instead of a `Double`. | Medium | open |
| technology/xml/test/KundeInSystem.java | KundeInSystem | ZKDBBaseType() (accessor) | 83 | The getter for field `typ` is named literally `ZKDBBaseType()` instead of `getTyp()` (compare the sibling `setTyp(ZKDBBaseType)` and every other class in the package's `getTyp()`) - almost certainly a copy/rename mistake. Reflection-based access by naming convention, as used elsewhere in this codebase (Accessor/SaxDispatcher), will not find a `getTyp` method on this class. | Medium | open |
| tester/logic/ConditionTable.java | ConditionTable | constructor | ~39 | Validation loop reads the instance field `Conditions` (still null) instead of the constructor parameter `Conditions_`, before the field is assigned - every construction throws `NullPointerException`. | Critical | open |
| tester/MetricMeasurAble.java | MetricMeasurAble | dist(Object, Object) | ~49 | Both operands call `a.getDouble()`; `b`'s value is never read, so `dist()` always returns 0 for distinct objects. | High | open |
| tester/FilterTestWaiter.java | FilterTestWaiter | test(Object) | ~56 | `wait(waitTime)` is called without holding this instance's monitor (no `synchronized` block) - always throws `IllegalMonitorStateException` at runtime. | High | open |
| tester/fuzzy/FuzzyDictionary.java | FuzzyDictionary | getMostSimilarItem(Object, double) | ~75 | `minIndex` is a hardcoded constant, never computed by comparison; reads `distances.getDoubleAt(-1)` - the method never actually searches and always returns -1. | High | open |
| tester/fuzzy/FuzzySentenceComparator.java | FuzzySentenceComparator | read(InputStream, StringBuffer, char) | ~208 | Loop condition compares against a hardcoded literal char instead of the `sep` parameter - a caller passing a different separator never sees the loop terminate on it. | Medium | open |
| tester/process/StreamProcessor.java | StreamProcessor | getPosition() | ~93 | Delegates to `availAble()` instead of an actual position method - returns items-remaining, not read position. | Medium | open |
| tester/process/IOEProcess.java | IOEProcess | testIt() | ~77 | `Runtime.exec("java Process.IOEProcess")` uses the wrong fully-qualified class name (actual: `tester.process.IOEProcess`) - the child process fails immediately. | Low | open |
| structure/Context.java | Context | send() | ~77 | Calls itself instead of delegating to `currState`, unconditional infinite recursion - `StackOverflowError` on every call. | High | open |
| structure/Delegate.java | Delegate | raiseEvent() | ~153 | Both catch blocks (`IllegalAccessException`, `InvocationTargetException`) are empty, silently discarding the exception and stopping notification of every Delegate after the one that failed. | Medium | open |
| structure/HistoryList.java | HistoryList | addItem(Object) | ~90 | Grows the backing array only when `currPtr > stack.length`, one element too late - when `currPtr == stack.length` the write throws `ArrayIndexOutOfBoundsException` instead of growing. | Medium | open |
| structure/Visitor1.java / Visitor2.java | Visitor1, Visitor2 | visit(ElementA)/visit(ElementB) | ~42-55 | Delegates back to `el.invite(this)`, but `ElementA/ElementB.invite(Visitor)` calls `v.visit(this)` right back - unconditional mutual recursion, `StackOverflowError` on the first call. | High | open |
| structure/aspect/DoubleAspect.java | DoubleAspect | getLong()/getDouble() | ~139, ~152 | Reads the primitive field `value`, which no `setValue(...)` overload ever assigns (they all write the boxed `Value` field) - stays permanently 0.0, so these methods ignore every Value actually set. | High | open |
| structure/aspect/ListAspect.java | ListAspect | constructor(String, Aspect[]) | ~60 | `list_` is never assigned to the `list` field - `list` stays permanently null regardless of what's passed in. | Medium | open |
| streamIO/real/FilterInMul.java | FilterInMul | getMinDouble() | - | Sign-flip bug: returns the wrong-signed minimum bound. | Medium | open |
| streamIO/real/StreamIn_Geometric.java | StreamIn_Geometric | 2-arg constructor | - | Field-order/assignment bug in the constructor. | Medium | open |
| streamIO/real/random/RandomGauss.java | RandomGauss | nextDoubleInternal() | - | Self-comparison that is always false, a dead rejection branch. | Medium | open |
| streamIO/real/random/RandomGauss2.java | RandomGauss2 | nextDoubleInternal() | - | Identical always-false self-comparison bug as `RandomGauss`. | Medium | open |
| streamIO/real/random/RandomPoisson.java | RandomPoisson | reSet() | - | Unconditional `ranLorentz.reSet()` call throws `NullPointerException` when `EW<12` (`ranLorentz` is not constructed in that branch). | High | open |
| math/minimizer/SinOfDistDivDist.java | SinOfDistDivDist | Map(double[]) / Map(float[]) | 59, 78 | `ret` is exactly 0.0 when the input equals the center (or origin), so `-Math.sin(ret)/ret` computes `0.0/0.0 = NaN` instead of the mathematical limit -1.0 - a minimizer converging onto this function's own minimum observes NaN. | Medium | open |
| graphic/math2D/Map2DModel.java | Map2DModel | addPoint(int, float, float, String) | 215 | `coordTrafo` may be null (it is only ever set via `setTrafo()`/`getTrafo()`), but this overload calls `coordTrafo.mapPt(x, y)` with no guard, unlike the double-parameter `addPoint(double, double, String)` overload which checks `coordTrafo != null` first. Throws `NullPointerException` when a point is added before a transform exists. | Medium | open |
| graphic/math2D/Map2DModel.java | Map2DModel | addPoint(float, float, String) | 228 | Same missing-null-guard defect as the row above, on the 3-arg float overload. | Medium | open |
| graphic/math2D/Map2DModel.java | Map2DModel | addPoint(float[], String) | 260 | Same missing-null-guard defect as the two rows above, on the `float[]` overload. | Medium | open |
| graphic/ms3d/Ms3d.java | Ms3d | calcRotation(Ms3dJoint, float) | 673 | Compares the rotation-frame loop index `uiFrame` against `pJoint.numTransFrames` (the translation-keyframe count) instead of `numRotFrames`/`rotKeyFrames.length`. When a joint's rotation and translation keyframe counts differ (the common case), this either runs the SLERP-interpolation branch when it shouldn't or indexes `rotKeyFrames[uiFrame]` out of bounds, throwing `ArrayIndexOutOfBoundsException`. | Medium-High | open |
| graphic/ms3d/Ms3d.java | Ms3d | streamJoints(OutputStream) | 162 | Delegates to `streamVertices(new PrintStream(ps))` instead of `streamJoints(new PrintStream(ps))` - a copy-paste error from the neighboring overload. Calling this writes vertex data instead of joint data to the stream. | Medium | open |
| graphic/ms3d/Ms3dVertex.java | Ms3dVertex | toStream(OutputStream) | 105 | Calls `toStream(new PrintStream(streamOut))`, i.e. itself, since `PrintStream` is-an `OutputStream` and there is no `toStream(PrintStream)` overload to resolve to instead. Every call recurses until `StackOverflowError`; almost certainly meant to call `stream(new PrintStream(streamOut))`, the method that actually writes the vertex data. | High | open |
| math/fit/weight/WeightExp.java | WeightExp | (field `SINGLETON`) | 32 | `SINGLETON` is a non-static instance field initialized by `new WeightExp()`, but the only constructor is this same private one, so instantiating the class recurses into this same field initializer forever - `StackOverflowError`. Needs `static`. | High | open |
| math/fit/weight/WeightGauss.java | WeightGauss | (field `SINGLETON`) | 33 | Same non-static-`SINGLETON`-recursion defect as `WeightExp`. | High | open |
| math/fit/weight/WeightLorentz.java | WeightLorentz | (field `SINGLETON`) | 30 | Same non-static-`SINGLETON`-recursion defect as `WeightExp`. | High | open |
| math/fit/weight/WeightExp.java | WeightExp | probCum(double) | 41 | Returns the same unintegrated exponential density as `prob()` instead of a cumulative probability; already marked by the original author's own `//TODO:`. | Low | open |
| math/fit/weight/WeightLorentz.java | WeightLorentz | probCum(double) | 43 | Returns `WeightExp`'s exponential-tail formula instead of a Lorentzian-consistent cumulative, inconsistent with this class's own `prob()`/`weight()`/`weightCum()`; already marked by the original author's own `//TODO:`. | Low | open |
| math/fit/FitFields.java | FitFields | map(double[], double[]) | 22 | Always returns `null` instead of the populated `yOut` array, breaking the `IFloatVectorField#map(double[], double[])` contract for any caller that uses the return value rather than only the out-parameter. | Medium | open |
| math/fit/FitFields.java | FitFields | map(float[], float[]) | 31 | Same always-`null`-return defect as the row above, on the `float[]` overload. | Medium | open |
| math/fit/FitGauss.java | FitGauss | map(double[], double[], double[]) | 75 | Unimplemented stub that always returns 0 without evaluating anything or filling `dyda`, unlike the single-`x` overload in the same class; any caller relying on the vector-`x` overload silently gets a wrong (zero) result instead of an error. | Medium | open |
| math/fit/FitGauss.java | FitGauss | map(float[], float[], float[]) | 89 | Same unimplemented-stub defect as the row above, on the `float[]` overload. | Medium | open |
| math/fit/FittingFloat.java | FittingFloat | svdfit(...) | 45 | Ported from Numerical Recipes SVDFIT, which requires `svdcmp` to decompose `u` into `U*W*V^T` and `svbksb` to back-substitute the solution into `a`. Both calls are commented out, so `w`/`v` must already hold a valid decomposition supplied by the caller and `a` is never solved for at all - the chi-squared computed evaluates whatever `a` the caller passed in. `testSvdFit()` itself passes an unfilled `a`, so its own self-test is exercising this. | High | open |
| math/refiner/AFloatRefinerQ.java | AFloatRefinerQ | BRACKET(IFloatFunction, float, float, int) | 103 | `ret` is allocated with the actual bracket count `numIntevals` (the value returned by the inner `BRACKET` call), but `arraycopy` is then told to copy `numIntervals` elements - the original, larger requested count - into it. Whenever fewer brackets are found than requested (the normal case), this overruns `ret` and throws `ArrayIndexOutOfBoundsException`. | High | open |
| math/refiner/NewtonFloatRefiner.java | NewtonFloatRefiner | refine() | 71 | Reads the inherited field `f` instead of this class's own `f0`, but `init(x, f0, f1)` reaches this state via the `(x, double)` super-`init` overload, which explicitly sets `f=null`; `f0` is stored but never read anywhere. Any refiner built via the `(double, IFloatFunction, IFloatFunction)` constructor/init throws `NullPointerException` on its very first `refine()` call. | High | open |
| math/Vector3D.java | Vector3D | angles()/Sphaeric2Rect()/Rect2Sphaeric()/Quadrik(...) | 336,361,380,428 | Multiple methods index `a[3]` (or a 2-element `Vector2D`'s `a[2]`) on arrays allocated `new double[3]`/`new double[2]` - guaranteed `ArrayIndexOutOfBoundsException` on every call. | Critical | open |
| math/Vector3D.java | Vector3D | mul(double) | 148 | Compound assignments `a[0]*=v` etc. mutate `this.a[]` inside the `new Vector3D(...)` constructor arguments, contradicting the non-mutating method name/contract (unlike `mulAt`). | Medium | open |
| math/Vector2D.java | Vector2D | Equality(...) (2 overloads) | 95, 300 | `x << 2 + y` parses as `x << (2+y)` in Java (operator precedence), not the intended `(x<<2)+y` - corrupts the combined overlap code for most inputs. | Medium | open |
| math/Vector2D.java | Vector2D | DET2x2(Vector2D) | 307 | Reads `a[2]`/`du.a[2]` on 2-element `Vector2D` arrays - guaranteed `ArrayIndexOutOfBoundsException`; affects `Line2D.Area`/`intersects`, which call it. | Critical | open |
| math/NumberFormatter.java | NumberFormatter | isNumber(String) | 26 | Always returns `true` regardless of input - the check is unimplemented. | Low | open |
| math/integration/StratifiedMCIntegrator.java | StratifiedMCIntegrator | integrate(...) | 95 | Own Javadoc documents `variance` as nullable ("if not null..."), but the method unconditionally dereferences `variance[0]` twice - passing `null` throws `NullPointerException`, contradicting the documented contract. | Medium | open |
| graphic/AGraph2D.java | AGraph2D | sizePolygonAt(int[][], int, int) | 106 | Condition checks `X != 1` twice instead of `X != 1 \|\| Y != 1` (contrast the correct `(X != 0) \|\| (Y != 0)` in `movePolygonAt` just below); when `X == 1` but `Y != 1` the whole condition is false and the resize loop is skipped, so the Y scale factor is silently never applied. | Medium | open |
| graphic/AGraph2D.java | AGraph2D | setThickPixel() | 270 | The fourth `fillRect` argument (`y1`) is `x+LineWidth` instead of `y+LineWidth`; the filled box's bottom edge is computed from `x` instead of `y`, mispositioning/mis-sizing the box whenever `x != y`. | Medium | open |
| graphic/AGraph2DOut.java | AGraph2DOut | setColor(Color) | 52 | `col` is null until the first color is set (no field initializer); when `col == null` and `color != null`, `col.equals(color)` throws `NullPointerException`, so the very first `setColor(Color)` call with a non-null color on a fresh instance fails instead of just assigning `col`. | Medium | open |
| graphic/Figures.java | Figures | VectorGrid(int[], int[], int[][], int[][]) | 351 | Both loops pre-increment from 0 (`while (++i < ...)`), so the first iteration uses index 1, never 0 - row 0 and column 0 of the grid are silently never drawn. | Low | open |
| graphic/Figures.java | Figures | (ellipse-radial-lines loop) | 439 | The y-coordinates use `R.getX()` instead of `R.getY()` for the start point (the end point correctly uses `R.getY()`), skewing the ellipse's radial lines whenever the radii differ in x and y. | Medium | open |
| graphic/Graph2D.java | Graph2D | drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver) | 228 | Stub always returns `true` without drawing anything - every caller silently gets "success" while no pixels are actually painted. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | create() | 74 | Always returns `null` instead of a copy of the underlying graphics context, contradicting `Graphics#create()`'s documented contract; any caller that follows the normal contract and invokes a method on the result throws `NullPointerException`. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | setPixel(Color) | 548 | The `color` parameter is never used; this always paints with the field `col` (the previously-set current color) instead of the color the caller explicitly passed in. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillPolygon(int[], int[], Color, Color) | 809 | The parameter names imply `BorderColor` strokes the outline and `InnerColor` fills the interior, but the calls do the opposite - the two colors are swapped relative to their names. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | drawEllipse(Point2D, int) | 863 | `IGraphAddtl#drawEllipse(Point2D, int)` documents "Center in M, Radius r", but `drawOval(x, y, width, height)` takes a top-left corner and full width/height; M and r are passed directly, so the ellipse is neither centered on M nor sized by 2*r as the contract requires. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | drawEllipse(Point2D, Point2D) | 877 | Same contract mismatch as `drawEllipse(Point2D, int)` - M and R are passed straight into `drawOval(x, y, width, height)` without centering on M or using R as radii. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillEllipse(Point2D, int) | 964 | Same contract mismatch as `drawEllipse(Point2D, int)` - M and r are passed straight through `fillOval(x, y, width, height)` without centering or doubling. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillEllipse(Point2D, Point2D) | 977 | Same contract mismatch as `drawEllipse(Point2D, Point2D)` - M and R are passed straight into `fillOval(x, y, width, height)` without centering on M or doubling R into a width/height. | Medium | open |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillRoundRect(Point2D, Point2D, int, int) | 1043 | `fillRoundRect(int,int,int,int,int,int)` expects `(x, y, width, height, arcWidth, arcHeight)`, but `P2.x`/`P2.y` are passed directly as width/height instead of `P2.x-P1.x`/`P2.y-P1.y` as the sibling `drawRoundRect(Point2D, Point2D, Point2D)` correctly computes; the rectangle is sized and positioned wrong. | Medium | open |
| graphic/Hidden.java | Hidden | setPixel(int, int, Color) | 139 | Off-by-one: `UG`/`OG` are sized `XMax` (valid indices 0..XMax-1), but the guard only rejects `x > XMax`, letting `x == XMax` through to `OG[x]`/`UG[x]` and throwing `ArrayIndexOutOfBoundsException`. | High | open |
| graphic/JavaGraphic.java | JavaGraphic | fillEllipse(Point2D, Point2D) | 503 | Calls `g.drawOval` (outline only) instead of `g.fillOval`, unlike every sibling `fillEllipse` overload in this class; silently draws an unfilled ellipse. | Medium | open |
| graphic/JavaGraphic.java | JavaGraphic | fillRoundRect(Line2D, Point2D) | 539 | Dispatches to `MethodDrawRRect` (outline) instead of `MethodFillRRect`, so this "fill" method actually draws an unfilled rounded rectangle. | Medium | open |
| graphic/JavaGraphic.java | JavaGraphic | fillRoundRect(Line2D, Point2D, Point2D) | 554 | Same `MethodDrawRRect`-instead-of-`MethodFillRRect` bug as the overload above. | Medium | open |
| graphic/PaletteRGB.java | PaletteRGB | HUE2COLOR(int) | 182 | Negative wrap-around uses `6-hue` instead of `6+hue` (contrast the correct `hue += 6` wrap in `RGB2HSB`); for `hue=-1` this computes 7 instead of 5, so `hue %= 6` yields 1, not 5, returning the wrong color for any negative argument. | Medium | open |
| graphic/PaletteShading.java | PaletteShading | getColor(int) | 80 | Division by `maxShade` with no guard against `maxShade == 0`; a caller passing `maxShade_ == 0` at construction throws `ArithmeticException` on every `getColor()` call. | Medium | open |
| graphic/Point2D.java | Point2D | MinAt(Point2D) | 139 | Compares `y > P.x` and assigns `y = P.x` instead of using `P.y`; corrupts every bounding-box computation that relies on this method whenever `P.x != P.y`. | Medium | open |
| graphic/Point2D.java | Point2D | MaxAt(Point2D) | 152 | Same `P.x`/`P.y` mixup as `MinAt()` above (compares/assigns against `P.x` instead of `P.y`). | Medium | open |
| graphic/Polygon2D.java | Polygon2D | getExtent() | 92 | `this.Points` is overwritten with a fresh all-null array before the loop reads `Points[Length]`, so every iteration sees null and `mergeAt()` never runs beyond the first point; the polygon's real point data is destroyed for every subsequent `getPoints()` call. | High | open |
| graphic/ScalarPlotNew.java | ScalarPlotNew | (color-segment stepping) | 379 | Calls the single-arg `drawHLine(x0)`, which draws from the graphics context's current x position, but unlike the 6-arg `ScalarRow` overload this method never tracks or sets a "segment start" x - color segments are drawn from stale/wrong positions. | Medium | open |
| graphic/VectorPoint2D.java | VectorPoint2D | (resize helper) | 470 | Builds the correctly-resized array `ret` but returns the original, unresized `a` instead; callers relying on the return value to get an array of the requested `dim` silently get the old size back. | Medium | open |
| graphic/VectorPoint2D.java | VectorPoint2D | stream(PrintStream, int, int) | 651 | `for (int i = startRow; ++i < stopRow;)` pre-increments before the first use of `i`, so `vals[startRow]` itself is never streamed; the loop effectively covers `[startRow+1, stopRow)`. | Medium | open |
| graphic/ZBuffer.java | ZBuffer | setPixel(int, int, float) | 134 | The clip guard checks `x >= XMax` twice and never checks `y >= YMax`; a caller passing `y >= YMax` reaches `MinZ[x][y]` below and throws `ArrayIndexOutOfBoundsException` instead of being clipped like every other out-of-range coordinate. | High | open |
| graphic/example/AntHillInside.java | AntHillInside | moveAnt(...) | 169 | `x`/`y` are never clamped or wrapped to `[0, WIDTH)`/`[0, HEIGHT)`; since the ant runs forever it will eventually walk off the buffer and throw `ArrayIndexOutOfBoundsException`. | Medium | open |
| graphic/example/Erosion.java | Erosion | MakeTerrainFault(...) | 169 | `fTempBuffer` holds the computed heights, but the commented-out normalize/copy step was never replaced with real code, so `m_ucpData` stays all-zero after this method returns. | High | open |
| graphic/implement/GrayColor.java | GrayColor | initPass(int, int, int, int) | 84 | `<<` binds looser than `+` in Java, so `grayMatrix[Z4][Z5] << 2 + increment` evaluates as `grayMatrix[Z4][Z5] << (2 + increment)` instead of the presumably intended `(grayMatrix[Z4][Z5] << 2) + increment` (a base-4 accumulation matching the 0..3 range of `increment`). | Medium | open |
| graphic/implement/GreyColor.java | GreyColor | setPixel() | 116 | Column index `1 + (P.getY() & 7)` ranges 1..8, but each `GreyFillPalette` row has only 8 columns (valid indices 0..7); when `(P.getY() & 7) == 7` this throws `ArrayIndexOutOfBoundsException`. Likely meant to index by `(P.getY() & 7)` directly. | High | open |
| graphic/svg/SvgApplet.java | SvgApplet | getTrafo(Rectangle) | 240 | The `bounds` parameter is ignored and the transform is never recomputed from it; every caller passes null, so this is effectively a no-arg getter for `#trafo`. | Low | open |
| graphic/svg/SvgApplet.java | SvgApplet | setTrafo(Coordinates2D) | 247 | Adds a new `Coord2DMouseController` via `addMouseListener`/`addMouseMotionListener` on every call without removing the listeners installed by a previous call; repeated invocations accumulate duplicate listeners. | Medium | open |
| graphic/svg/SvgApplet.java | SvgApplet | image(Attributes) | 488 | The `xlink:href` attribute of an `<image>` element is untrusted content from the parsed SVG document; it is concatenated into a URL and fetched with no validation (scheme allow-list, path-traversal check), letting a malicious SVG file make this Applet fetch an arbitrary URL (SSRF-like) or read an arbitrary local file. | Medium | open |
| graphic/math3D/OdePlotter.java | OdePlotter | drawLoop() | 101 | `Rect` is a legal constructor argument that may be `null`; the do/while loop condition `(Rect != null) && Rect.contains(...)` is then false from the start, so the trajectory draws exactly one step instead of running unbounded until the drawing area is left, as the surrounding comments describe. | Medium | open |
| graphic/mvc/BufferedPainter.java | BufferedPainter | BufferedPainter(ICanvas) | 83 | `new BufferedImage(dim.height, dim.width, ...)` swaps width and height, transposing the offscreen buffer for any non-square canvas and corrupting `getSize()`/every subsequent `drawImage()`. | High | open |
| graphic/mvc/plane2D/VectorPolygon.java | VectorPolygon | drawInOrder(IGraphText) | 353 | The branch meant to (re)build `zIndex` when null/stale is entirely commented out; `zIndex`'s only assignment anywhere is `setChanged()` setting it back to null, so this always throws `NullPointerException` at `items[zIndex[i]]`. | High | open |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | SET_DIM_AT(short[][], int) | 480 | Returns the original array `a` instead of the resized `ret` allocated and filled just above; every caller expecting a length-`dim` array back silently gets the unchanged original-length array. | Medium | open |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | MatrixShort(Object) | 819 | Resolves to `MatrixShort(int initialCapacity, int dim)`; `DEFAULT_CAPACITY_INCR` is passed as the row dimension `dim`, not as a capacity increment, silently giving every row the wrong width. | Medium | open |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | newInstance() | 1017 | Same constructor-overload mismatch as above: `capacityIncrement` passed as `dim` for the new instance's row width. | Medium | open |
| graphic/mvc/plane2D/VectorPolygon.java | VectorPolygon | copyAt(MatrixShort[]) | 293 | Unlike the Object-typed `copyAt()` overload, never calls `setCapacity()` first; when `arg_.length` exceeds the current backing-array length, `arraycopy` throws `ArrayIndexOutOfBoundsException` instead of growing the array. | Medium | open |
| graphic/mvc/BaseApplet.java | BaseApplet | imageUpdate(...) | 360 | Only `ImageObserver.ALLBITS` is checked; `ERROR`/`ABORT` are never handled, so a failed/aborted asynchronous image load never notifies the observer chain and this method keeps returning true (keep sending updates) forever for that image. | Medium | open |
| graphic/mvc/BaseApplet.java | BaseApplet | getFaultySuffix(String) | 108 | `fileName.substring(fileName.length()-4)` throws `StringIndexOutOfBoundsException` for filenames shorter than 4 characters, uncaught by any caller. | Low | open |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | STREAM(...) | 650 | `for (int i = startRow; ++i < stopRow;)` pre-increments before the bound check, so `vals[startRow]` itself is never streamed - only rows `startRow+1..stopRow-1` are printed. | Low | open |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | normalizeAt() | 1031 | When `itemCount` is already 0 (or every item is null), `while (items[--itemCount] == null);` decrements past 0 to -1 and indexes `items[-1]`, throwing `ArrayIndexOutOfBoundsException` instead of leaving an empty matrix normalized. | Low | open |

| math/vector/VectorObject.java | VectorObject | removeAt(int) | 167 | `--itemCount` runs as a side effect of evaluating the right operand of `\|\|` whenever `index >= 0`; on an out-of-range index the method still returns null but `itemCount` has already been permanently decremented, corrupting the vector's size even though no element was removed. | High | open |
| math/vector/VectorObject.java | VectorObject | copyInto(int[]) | 449 | `items` is an `Object[]` here (unlike `VectorInt`, whose items are `int[]`); copying it into an `int[]` destination via `System.arraycopy` throws `ArrayStoreException` at runtime on every call once `itemCount > 0` - apparently copy-pasted from `VectorInt` without adjusting for `VectorObject`'s element type. | Critical | open |
| math/vector/VectorObject.java | VectorObject | toArray() | 466 | Same `ArrayStoreException` hazard as `copyInto(int[])` above. | Critical | open |
| math/vector/HunterInt.java | HunterInt | GET_STATISTIC_POS(int[], int, int, int) | 603 | `PARTITION` is declared `PARTITION(items, stop, start)`, but this call passes `(start, stop)` - the two bounds are swapped positionally, breaking median/percentile/order-statistic results and risking unbounded recursion. | High | open |
| math/vector/HunterInt.java | HunterInt | GET_STATISTIC_POS(int[], int[], int, int, int) | 844 | Same `PARTITION` argument-order defect as above, on the indexed variant. | High | open |
| math/vector/HunterFloat.java | HunterFloat | GET_STATISTIC_POS(float[], int[], int, int, int) | 879 | Same `PARTITION` argument-order defect (indexed variant only - the non-indexed overload in this file is correct). | High | open |
| math/vector/HunterDouble.java | HunterDouble | GET_STATISTIC(double[], int[], int, int, int) | 756 | Same `PARTITION` argument-order defect, indexed variant. | High | open |
| math/vector/VectorChar.java | VectorChar | MIN(char[][], int) | 60 | Comparison is inverted (should be `min > matrix[i][col]`); `min` starts at `Character.MAX_VALUE` and the condition can never be true, so this always returns `Character.MAX_VALUE` regardless of the array's content - looks copy-pasted from `MAX(char[][], int)` without flipping the operator. | Critical | open |
| math/vector/VectorString.java | VectorString | ALIGN_RIGHT(String, String) | 453 | `substring(str.length()-format.length())` goes negative whenever `str` is shorter than `format` (the normal case this method exists for), throwing `StringIndexOutOfBoundsException`. | High | open |
| math/vector/VectorString.java | VectorString | PAD(String, int, char, boolean) | 723 | Loop appends one filler character short of what's needed to reach the requested `length` - the returned String is 1 character shorter than requested. | Medium | open |
| math/vector/VectorString.java | VectorString | Collection2StringArray(Collection) | 829 | `++i` is a pre-increment, so the first element is written to `ret[1]` (leaving `ret[0]` permanently null) and the last write goes out of bounds, throwing `ArrayIndexOutOfBoundsException` for any non-empty collection. | Critical | open |
| math/vector/VectorString.java | VectorString | REPLACE_ALL(String, String) | 967 | Seeds `ret` via `APPEND(str, 0, i-1)` instead of `i`, dropping the character immediately before the first separator match. | Medium | open |
| math/vector/VectorString.java | VectorString | toString(byte[], StringBuffer) | 1343 | Loop condition checks `tmp` from the previous iteration, so a genuine terminating 0 byte at index 0 is appended as a character before the loop notices and stops - off by one byte versus the documented "terminated by a 0 byte" contract. | Low | open |
| math/vector/VectorString.java | VectorString | removeAt(int) | 1760 | Same unconditional-`--itemCount`-on-failure bug as `VectorObject.removeAt(int)` above. | High | open |
| math/vector/VectorString.java | VectorString | copyInto(int[]) | 1894 | Same `ArrayStoreException` hazard as `VectorObject.copyInto(int[])` (`items` is `String[]`, not `int[]`). | Critical | open |
| math/vector/VectorString.java | VectorString | toArray() | 1912 | Same `ArrayStoreException` hazard as above. | Critical | open |
| math/matrix/MatrixFloatStreamIn.java | MatrixFloatStreamIn | constructor | 45 | `currPos` is initialized to `matrix.getInt()` (== `items.length`), one past the last valid index; calling `currVector()` before any `nextVector()` reads `matrix.items[itemCount]`, throwing `ArrayIndexOutOfBoundsException`. | Medium | open |
| math/matrix/MatrixDouble.java | MatrixDoubleStreamIn | constructor | 4224 | Same off-by-one as `MatrixFloatStreamIn`'s constructor (parallel class). | Medium | open |
| math/matrix/MatrixObject.java | MatrixObject | copyInto(int[]) | 255 | `items` is `Object[][]` but `anArray` is `int[]`; `System.arraycopy` compiles (both are `Object`) but throws `ArrayStoreException` at runtime for any non-empty matrix because the component types are incompatible. | High | open |
| math/matrix/MatrixObject.java | MatrixObject | toArray() | 270 | Same `Object[][]`-into-`int[]` arraycopy defect as `copyInto(int[])` above. | High | open |
| math/matrix/MatrixInt.java | MatrixInt | COPY_AT(int[][], int[], int, int) | 891 | Arraycopy direction is reversed - copies `ret[stop]` into `arr` (the single row argument) on every iteration, overwriting `arr` instead of filling `ret` as the Javadoc and parameter naming describe; the method never actually fills `ret`. | High | open |
| math/matrix/MatrixFloat.java | MatrixFloat | COPY_AT(float[][], float[], int, int) | 1182 | Same reversed-direction defect as `MatrixInt.COPY_AT` (parallel class). | High | open |
| math/matrix/MatrixDouble.java | MatrixDouble | COPY_AT(double[][], double[][], int, int) | 1112 | Same reversed-direction defect, uniquely also present in the matrix-to-matrix overload here (copies `ret[stop]` into `arr[stop]`, overwriting `arr`). | High | open |
| math/matrix/MatrixDouble.java | MatrixDouble | COPY_AT(double[][], double[], int, int) | 1125 | Same reversed-direction defect as `MatrixInt`/`MatrixFloat`'s single-vector `COPY_AT` overload. | High | open |
| math/matrix/MatrixTriDiagonal.java | MatrixTriDiagonal | solveCyclicAt(...) | 165 | `nonCyclic` is cached and only its corner diagonal entries (0 and n-1) are refreshed on repeat calls; since `subDiag`/`diag`/`superDiag` are shared, mutable arrays not defensively copied by the constructor, stale interior values from an earlier call are silently reused if `diag`'s interior (1..n-2) changes between two calls on the same instance. | Medium | open |
| math/matrix/Quaternion.java | Quaternion | getAxis() | 181 | `q` is a `float[4]` (valid indices 0-3), so `q[4]` is always out of bounds, throwing `ArrayIndexOutOfBoundsException` on every call - should read `q[3]`, the scalar/real component, matching `getAngle()`'s use of `q[3]`. | Critical | open |
| math/matrix/Quaternion.java | Quaternion | set(float[]) | 557 | Ignores the `fpQuat` parameter entirely and calls `copyAt(q)`, copying the backing array onto itself (a no-op) instead of `copyAt(fpQuat)`; this method never actually changes the quaternion's value. | High | open |
| math/vector/VectorDouble.java | VectorDouble | MUL_CROSS_AT(double[], double[]) | 671, 676 | `result` is `new double[3]` (valid indices 0-2); both short-array branches write `result[3]`, throwing `ArrayIndexOutOfBoundsException` instead of returning a 2D cross-product result (likely intended `result[2]`). | High | open |
| math/vector/VectorDouble.java | VectorDouble | (resize helper, ~1021) | 1021 | Returns the original array `a` instead of the newly-resized `ret`; the resized array is discarded and callers relying on the return value get the un-resized input back. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | ONE_AT(double[], int, int) | 1061 | Fills the range with 0 instead of 1 (copy-pasted from `ZERO_AT`); callers expecting a one-vector get a zero-vector instead. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | (array-fill loop, ~1339) | 1339 | Loop condition `++i <= ret.length` lets `i` reach `ret.length`, so the final iteration writes `ret[ret.length]`, throwing `ArrayIndexOutOfBoundsException` whenever `ret.length > 1`. | High | open |
| math/vector/VectorDouble.java | VectorDouble | (copy helper, ~2610) | 2610 | Reads `tmp = ret[stop]` instead of `arg[stop]`; whenever `ret` is a distinct fresh array from `arg`, the source values in `arg` are ignored and only `ret`'s garbage/zero prior content is used. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | MUL_AT(double[], int, double[], int) | 3319 | When `retLength > arrLength`, `ret`'s elements from `arrLength..retLength-1` are neither multiplied nor zeroed (the `FILL_AT` call that would zero them is commented out), silently keeping their pre-call values instead of becoming 0, as the disabled line's own comment says they should. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | MUL_AT(double[], int, float[], int) | 3339 | Same left-over-elements defect as the `double[]` overload above, when `retLength > arrLength`. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | removeAt(int) | 5003 | `--itemCount` runs unconditionally as part of evaluating the guard; when `index` is out of range this still returns 0 but `itemCount` has already been permanently decremented, corrupting the vector's size even though no element was removed. Same defect as `VectorObject.removeAt(int)`/`VectorString.removeAt(int)`. | High | open |
| math/vector/VectorDouble.java | VectorDouble | equals(Object) | 5970 | Violates the `equals()` contract for a null argument: `arg instanceof VectorDouble` is false for null, so this falls through to `arg.equals(this)` and throws `NullPointerException` instead of returning false. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | toStream(Writer) | 6074 | Loop `for (i=0; ++i<=itemCount;)` writes `items[1]..items[itemCount]` in addition to `items[0]` written just above - one element past the logically valid range; also `stream.write(itemCount)` writes `itemCount` as a raw character code, not its decimal digits. | Medium | open |
| math/vector/VectorDouble.java | VectorDouble | fullDiffAt() | 6166 | Infinite loop once `itemCount` reaches 0: `diffAt()` guards `if (itemCount <= 0) return this;` and no longer decrements, but this loop's own condition `itemCount >= 0` stays true forever once `itemCount==0`, so this call never returns. | High | open |
| math/vector/VectorChar.java | VectorChar | oneAt(char[], int, int) | 541 | Fills with `(char) 0`, not 1 - contradicts both this method's name (`oneAt`) and its own one-arg wrapper `oneAt(char[])`'s documented contract ("set to 1"). | Medium | open |
| math/vector/VectorChar.java | VectorChar | AbsDiffNorm(char[], char[], char[]) | 804 | `diff[i]` is never assigned on the `dif <= 0` branch, even though the contract says `diff` is "an Output Parameter being filled with the Difference Vector" - only positive-difference elements get written; callers reading `diff[i]` elsewhere see stale content. | Medium | open |
| math/vector/VectorChar.java | VectorChar | removeAt(int) | 2214 | `--itemCount` runs unconditionally before the range check; an out-of-range `index` still returns 0 as if nothing happened, but `itemCount` has already been permanently decremented, corrupting the vector's size. | High | open |
| math/vector/VectorChar.java | VectorChar | mulAt(VectorChar) | 2372 | Calls `subAt(...)` instead of `mulAt(...)` - copy-pasted from `subAt(VectorChar)` without updating the delegated call, so this silently subtracts the given vector's values instead of multiplying by them. | High | open |
| math/vector/VectorChar.java | VectorChar | divAt(VectorChar) | 2379 | Same copy-paste defect as `mulAt(VectorChar)` above, calling `subAt(...)` instead of `divAt(...)`. | High | open |
| math/vector/VectorChar.java | VectorChar | mulAt(int) | 2412 | The `value` parameter is never used - calls the `(char[], int, int)` overload with `items` itself as the array argument, so it squares every element instead of multiplying by the given scalar. | High | open |
| math/vector/VectorChar.java | VectorChar | divAt(int) | 2419 | The `value` parameter is never used - divides every element by itself (yielding 1, or an arithmetic error on a zero element) instead of dividing by the given scalar. | High | open |
| math/vector/VectorLong.java | VectorLong | oneAt(long[], int, int) | 532 | Same fills-with-0-instead-of-1 defect as `VectorChar.oneAt`. | Medium | open |
| math/vector/VectorLong.java | VectorLong | AbsDiffNorm(long[], long[], long[]) | 796 | Same unassigned-`diff[i]`-on-non-positive-branch defect as `VectorChar.AbsDiffNorm`. | Medium | open |
| math/vector/VectorLong.java | VectorLong | removeAt(int) | 2207 | Same unconditional-`--itemCount`-before-range-check defect as `VectorChar.removeAt(int)`. | High | open |
| math/vector/VectorLong.java | VectorLong | mulAt(VectorLong) | 2365 | Same `subAt(...)`-instead-of-`mulAt(...)` copy-paste defect as `VectorChar.mulAt(VectorChar)`. | High | open |
| math/vector/VectorLong.java | VectorLong | divAt(VectorLong) | 2372 | Same `subAt(...)`-instead-of-`divAt(...)` copy-paste defect as `VectorChar.divAt(VectorChar)`. | High | open |
| math/vector/VectorLong.java | VectorLong | mulAt(int) | 2405 | Same ignored-scalar-parameter defect (squares elements instead) as `VectorChar.mulAt(int)`. | High | open |
| math/vector/VectorLong.java | VectorLong | divAt(int) | 2413 | Same ignored-scalar-parameter defect (self-divides elements instead) as `VectorChar.divAt(int)`. | High | open |
| math/vector/VectorShort.java | VectorShort | oneAt(short[], int, int) | 770 | Same fills-with-0-instead-of-1 defect as `VectorChar.oneAt`. | Medium | open |
| math/vector/VectorShort.java | VectorShort | removeAt(int) | 3027 | Same unconditional-`--itemCount`-before-range-check defect as `VectorChar.removeAt(int)`. | High | open |
| math/vector/VectorShort.java | VectorShort | mulAt(VectorShort) | 3334 | Same `subAt(...)`-instead-of-`mulAt(...)` copy-paste defect as `VectorChar.mulAt(VectorChar)`. | High | open |
| math/vector/VectorShort.java | VectorShort | divAt(VectorShort) | 3342 | Same `subAt(...)`-instead-of-`divAt(...)` copy-paste defect as `VectorChar.divAt(VectorChar)`. | High | open |
| math/vector/VectorShort.java | VectorShort | mulAt(int) | 3378 | Same ignored-scalar-parameter defect (squares elements instead) as `VectorChar.mulAt(int)`. | High | open |
| math/vector/VectorShort.java | VectorShort | divAt(int) | 3387 | Same ignored-scalar-parameter defect (self-divides elements instead) as `VectorChar.divAt(int)`. | High | open |
| math/vector/VectorInt.java | VectorInt | ONE_AT(int[], int, int) | 1010 | Fills with 0 instead of 1 (copy-pasted from `ZERO_AT`); same defect as `VectorChar/VectorLong/VectorShort.oneAt`. | Medium | open |
| math/vector/VectorInt.java | VectorInt | MUL_CROSS_AT(int[], int[]) | 2042, 2047 | `result` is `new int[3]` (valid indices 0-2); both short-array branches write `result[3]`, throwing `ArrayIndexOutOfBoundsException` (likely intended `result[2]`) - same defect as `VectorDouble.MUL_CROSS_AT`. | High | open |
| math/vector/VectorInt.java | VectorInt | removeAt(int) | 3572 | Same unconditional-`--itemCount`-before-range-check defect as `VectorChar.removeAt(int)`. | High | open |
| math/vector/VectorInt.java | VectorInt | mulAt(VectorInt) | 3726 | Same `subAt(...)`-instead-of-multiplicative-op copy-paste defect as `VectorChar.mulAt(VectorChar)`. | High | open |
| math/vector/VectorInt.java | VectorInt | divAt(VectorInt) | 3731 | Same `subAt(...)`-instead-of-divisive-op copy-paste defect as `VectorChar.divAt(VectorChar)`. | High | open |
| math/vector/VectorInt.java | VectorInt | mulAt(int) | 3762 | Same ignored-scalar-parameter defect (squares elements instead) as `VectorChar.mulAt(int)`. | High | open |
| math/vector/VectorInt.java | VectorInt | divAt(int) | 3767 | Same ignored-scalar-parameter defect (self-divides elements instead) as `VectorChar.divAt(int)`. | High | open |
| math/vector/VectorFloat.java | VectorFloat | ABS(float[], int, int, float[]) | 2107 | Reads `tmp = ret[stop]` instead of `arg[stop]`; the source array `arg` is ignored whenever `ret != arg` (e.g. a fresh/different `ret` array yields an all-zero result). | Medium | open |
| math/vector/VectorFloat.java | VectorFloat | removeAt(int) | 4297 | Same unconditional-decrement-before-range-check defect as `VectorChar/VectorLong/VectorShort/VectorInt.removeAt`. | High | open |
| math/vector/VectorFloat.java | VectorFloat | mulAt(VectorFloat) | 4574 | Same `subAt`-instead-of-multiplicative-op defect as the sibling classes' `mulAt(VectorX)`. | High | open |
| math/vector/VectorFloat.java | VectorFloat | divAt(VectorFloat) | 4580 | Same `subAt`-instead-of-divisive-op defect as the sibling classes' `divAt(VectorX)`. | High | open |
| math/vector/VectorFloat.java | VectorFloat | mulAt(double) | 4630 | Ignores the `value` parameter and instead multiplies items by itself element-wise; same defect as the sibling classes' `mulAt(scalar)`. | High | open |
| math/vector/VectorFloat.java | VectorFloat | divAt(double) | 4636 | Ignores the `value` parameter and instead divides items by itself element-wise (yielding all 1s); same defect as the sibling classes' `divAt(scalar)`. | High | open |
| function/derive/Enum.java | Enum | succ() | 264 | Indexes `list[(int)Value+1]` and bounds-checks `Value>=list.length` instead of using the list position `Value-Offset`. For any Enum with non-zero Offset (`Month`, Offset=1) this skips an element (`January.succ()` returns March); for Offset=0 with the last element's Value==list.length-1 (`Week`, Sunday.Value=6, list.length=7) it throws `ArrayIndexOutOfBoundsException` instead of returning null. | High | open |
| function/derive/Enum.java | Enum | pred() | 271 | Indexes `list[(int)Value-1]` and checks `Value==0` instead of using the list position `Value-Offset`. For any Enum with non-zero Offset (`Month`, Offset=1) the first element's `pred()` returns itself instead of null (`January.pred()` returns January). | Medium | open |
| function/derive/Ternary.java | Ternary | fromString(String) | 224 | Ignores the `ST` parameter entirely and always returns `this`; the class's own TODO admits parsing "-1"/"0"/"1" and "true"/"false"/"null" is unimplemented, so any caller round-tripping a serialized Ternary silently gets back the wrong constant. | Medium | open |
| function/derive/neuron/Network.java | Network | randomizeWeights() | 144 | Loop uses `while (--i > 0)` instead of `>= 0`, so `Layers[0]`'s Weights are never randomized while every other Layer's are; reachable on any multi-Layer Network after construction. | Medium | open |
| function/derive/ring/LinAt.java | LinAt | LinAt(Object, Object, IInvertAble) | 39 | Validates the fields `a`/`b` (still null before assignment) instead of the constructor parameters `a_`/`b_`, so these `instanceof IFunction` checks can never trigger regardless of what callers pass in. | Low | open |
| function/derive/ring/LinAt.java | LinAt | Map(Object) | 65 | When `b==null` (pure scaling) calls `MulAt.MUL_AT(arg, b)` with the null `b` instead of `MulAt.MUL_AT(arg, a)`, breaking the intended `a*x` scaling for every `LinAt` constructed with a null `b`. | High | open |
| function/derive/ring/body/Logarithm.java | Logarithm | getDerivative(double) | 58 | Returns `-Math.log(x)` instead of the correct derivative `1/x`; reachable on every call. | High | open |
| function/derive/ring/body/Logarithm.java | Logarithm | getFuncDerive(double, ByRefDouble) | 65 | Returns `-Math.log(x)` as the function value instead of `Math.log(x)` (disagrees with `Map(x)`); the derivative ByRef output is correct, but the returned value is negated for every caller. | High | open |
| function/derive/ring/body/vector/fSum.java | fSum | Map(Object) | 18 | `Sum` is initialized as a copy of `V.a[0]`, but the loop then runs `i` from `Dim-1` down to `0` inclusive and adds `V.getAt(0)` again, double-counting coordinate 0 in the result for every tensor of dimension >= 1. | High | open |
| streamIO/copy/primitiveOp/AOpDouble.java | AOpDouble | copyAt/equals/less/grtr/MaxAt/MinAt/addAt/subAt/mulAt/divAt(long), LinAt(long,long) | 43,46,49,52,55,58,61,64,67,70,79 | Each of these 11 `long`-overload methods calls itself with an identical signature/arguments instead of delegating to the corresponding `double`-based op - infinite recursion, `StackOverflowError` on any call. | Critical | open |
| streamIO/copy/primitiveOp/AOpMeasurAble.java | AOpMeasurAble | LinAt(long, long) | 60 | No-op - both parameters are ignored and the object is returned unchanged. | Medium | open |
| streamIO/copy/shift/AShiftAble.java | AShiftAble | aslAt/asrAt/lsrAt(int, Object) | 143, 161, 178 | The `carry` parameter is accepted but never read or written - silently discarded (the original author left an inline TODO on `aslAt`). | Medium | open |
| streamIO/copy/order/Interval.java | Interval | ANDAt(Interval) | 201 | Missing `return this;` after the no-containment branch - falls through into the partial-containment logic, corrupting the just-mutated state. | High | open |
| streamIO/copy/monoid/integer/ASetInteger.java | ASetInteger | clear(int) | 38 | Uses XOR instead of AND-NOT to clear a bit - clearing an already-0 bit sets it to 1 instead. | High | open |
| streamIO/copy/monoid/integer/Permutation.java | Permutation | Multi_Fact(Permutation) | 1600 | Parameter `p` is never read - always uses its own array, ignoring the documented "Carry Element used for the Base". | Medium | open |
| streamIO/copy/monoid/AssociationEquivalence.java | AssociationEquivalence | equals(Object, Object) | 42 | Copy-paste: checks `A instanceof ICPair` instead of `B instanceof ICPair`, so the intended branch never fires when B is the `ICPair`. | Medium | open |
| streamIO/copy/boole/TesterBond.java | TesterBond | ORat(Object) | 112 | Copy-paste from `ANDat`: detects `a OR !a` and sets `mTest = False`, should be `True`. | High | open |
| streamIO/copy/boole/TesterBond.java | TesterBond | ORat(Object) | 114-122 | Copy-paste from `ANDat`: all four constant-argument branches are backwards for OR semantics. | Critical | open |
| streamIO/copy/boole/fuzzy/FuzzyEQV.java | FuzzyEQV | getMembership(Object) | 47 | Missing `1 -` prefix - returns the raw difference instead of its complement, inverting equivalence semantics. | High | open |
| streamIO/copy/ACopyAble.java | ACopyAble | toStream(IFormatOut) | 386 | Default `ST.addItem(this)` + `toString()` calling `toStream(...)` risks infinite recursion (already noted by the original author's own comment "leads to infinite Recursion!"). | Medium | open |
| function/CatProcessor.java | CatProcessor | constructor(IProcessor, IProcessor) | ~ | Null-check reads the instance fields `inner`/`outer` (always null at this point in the constructor) instead of the `Inner` parameter; the apparent fallback-to-`Outer`-when-`Inner`-is-null intent never happens, so a caller passing `Inner == null` gets `inner == null` and a later `NullPointerException` from `MapAt()`/`equals()`. | Medium | open |
| function/Projections.java | Projections | Mercator(double[]) | 243 | Indexes `V[2]`, but every other projection in this class treats `V` as a 2-element (x,y) position; calling this with the same 2-element vectors used everywhere else throws `ArrayIndexOutOfBoundsException`. Likely meant `V[1]`, mirroring `Cyl_MercatorAt` below. | High | open |
| function/real/Product.java | Product | getHMV() | 50 | Method name and Javadoc call this the Harmonic Mean, but `Math.pow(_Value, 1.0/_Count)` computes the Geometric Mean; callers relying on the name/doc for the harmonic mean get the wrong statistic. | Medium | open |
| function/string/AStringFunction.java | AStringFunction | TO_CAMEL | 89 | If `_` is the last character of the input, `arg.charAt(++i)` reads one past the end of the string, throwing `StringIndexOutOfBoundsException` (reachable whenever a hungarian-notation input ends with an underscore, e.g. `TO_CAMEL.Map("FOO_")`). | Medium | open |
| function/vector/OdeLorentz.java | OdeLorentz | Funktion(double, double[], double[]) | 48 | The standard Lorenz equations are `dy/dt = x*(r-z) - y`, but this computes `x[1] - x[0]*(x[2]-r)`, which equals `x[0]*(r-x[2]) + x[1]` - the sign of the y-term is flipped; every integration step diverges from the intended chaotic Lorenz attractor. | High | open |
| function/byref/ByRefInt.java | ByRefInt | ROR(int, int) | 64 | The dropped low bit is shifted into position `octave` (`corr = (x&1)<<octave`), one bit above the top of the octave-bit range `ROL` uses (`maxVal = 1<<octave`); e.g. `ROR(5, 3)` returns 10, outside the 3-bit range `ROL(5, 3)` operates in. Likely should be `<<(octave-1)`. | Medium | open |
| function/byref/ByRefLong.java | ByRefLong | ROR(long, int) | 259 | Same defect as `ByRefInt.ROR` - the dropped low bit is shifted one bit above the top of the octave-bit range. | Medium | open |
| streamIO/object/StreamParser.java | StreamParser | (array-resize helper) | 177 | `lList` is reassigned to the freshly-allocated `list` array before the `arraycopy` below, so the copy's source and destination are the same new (empty) array - the old contents are lost instead of preserved across the resize. | High | open |
| streamIO/object/Union.java | Union | OR(IStreamIn, IStreamIn) | 73 | `Parts` is allocated with length 3 (valid indices 0-2), but a following line writes to index 3, throwing `ArrayIndexOutOfBoundsException` on every call to `OR()`. | Critical | open |
| streamIO/object/backTrack/Grammar.java | GrammarState | hashCode() | 138 | `Remark` is never assigned by any constructor (only `Contents` is set), so it is always null here; every call to `hashCode()` or `equals()` throws `NullPointerException`. Any hash-based use of `GrammarState` (e.g. `Grammar.testIt()`) fails immediately. | High | open |
| streamIO/object/backTrack/TravelProblem.java | TravelState | equals(Object) | 486 | `sequence` is `int[]`, which does not override `equals()`, so `sequence.equals(...)` reduces to reference identity rather than comparing array contents; two `TravelState`s with identical city orderings but distinct array instances always compare unequal, making duplicate detection (`mTestStore`/`mBackup`) in `BackTracker` ineffective for this generator. | Medium | open |
| streamIO/object/filterIn/FilterInByBitMask.java | FilterInByBitMask | (position-reset helper) | 82 | `1 << _position` is computed in `int` arithmetic (the literal `1` is an int), so per JLS 15.19 only the low 5 bits of `_position` are used as the shift distance - large position values silently wrap instead of shifting as far as intended. | Medium | open |
| streamIO/object/filterIn/FilterIn_PushBack.java | FilterIn_PushBack | nextItemInternal() | 39 | The fallback branch calls `nextItemInternal()` recursively on itself instead of delegating to the wrapped stream (`in.nextItem()`); every call made while nothing is pushed back recurses infinitely. | Critical | open |
| streamIO/object/filterOut/ThreadOut.java | ThreadOut | addItem(Object) | 47 | The spawned `Runnable`'s `run()` calls `addItem(arg)`, which resolves to this same `ThreadOut.addItem()` rather than the wrapped output's `out.addItem(arg)` (or `super.addItem`) - every call spawns another thread that spawns another thread, recursing without ever forwarding to the real output. | Critical | open |
| streamIO/object/integer/XMLInputStream.java | XMLInputStream | fromXML() | 199 | The class name is read directly from untrusted XML input and instantiated via reflection (`Class.forName`+`newInstance`) with no allow-list, letting a malicious XML document force instantiation of an arbitrary class on the classpath. | High | open |
| streamIO/object/integer/XMLInputStream.java | XMLInputStream | fromXMLField(...) | 238 | `ensureCapacity()` only grows the backing array's capacity, not the `ArrayList`'s logical size; if `ID` is greater than the cache's current size, `Cache.add(ID, inner)` throws `IndexOutOfBoundsException` when some object IDs are missing (cut out etc.). | Medium | open |
| streamIO/object/integer/XMLScanner.java | XMLScanner | (tag-type constants) | 97 | `XML_TAG_PROCESS` is defined as 6, the same value as `XML_TAG_TEXT`; any code distinguishing a Processing Instruction from Text Data by comparing against this constant cannot actually do so. | Medium | open |
| streamIO/object/json/JSONTokener.java | JSONTokener | next(int) | 207 | Off-by-one: `String.substring(i, j)` is valid for `j == mySource.length()` (it can return the final characters of the source), but this check rejects that valid boundary case - e.g. a `\uXXXX` escape ending exactly at EOF. | Medium | open |
| streamIO/object/json/JSONTokener.java | JSONTokener | nextValue() | 358 | Object/array nesting recurses (`nextValue` -> `JSONObject`/`JSONArray` constructor -> `nextValue` -> ...) with no depth limit, unlike `JSONStringer`'s own `maxdepth=20`; deeply nested untrusted JSON input can cause a stack-overflow denial of service. | Medium | open |

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
