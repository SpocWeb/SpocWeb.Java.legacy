# HANDOFF — Java.ReadMeGenerator documentation run

## Status

**Complete (2026-09-06).** All 7 passes of the Java.ReadMeGenerator pipeline have been run
across the entire corpus: 1457/1457 `.java` files carry a `docstate` block, every folder has
a `ReadMe.md`, and the shared tag/index files (`raw-tags.tsv`, `D:/_/_AI/tags-schema.yaml`,
`D:/_/_AI/tags-index.tsv`) are up to date. Verified via a full repo-wide
`list-todo .` sweep, which returns exactly 8 rows - all confirmed tool false positives (see
Tool quirks below), not real gaps. Everything is committed and pushed to both
`D:/_/_Matthias/Code/Java` and `D:/_/_AI`.

**Optional follow-up, not applied:** the shared axis-A vocabulary (6421 tags) exceeds its
recommended 300-tag ceiling. A dry run of
`compact-vocabulary /d/_/_AI/tags-schema.yaml /d/_/_AI/tags-index.tsv .` proposes stripping
1104 tag references and rewriting 1152 `raw-tags.tsv` rows - left unapplied since it's a large
structural change to shared state, separate from finishing the documentation pass. Review the
dry-run diff before running with `--apply`.

Tooling: `D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/target/readmegenerator.jar`,
built with the colocated Maven Wrapper. All milestones (A/B/B+/C) are built.

## Claims

Every batch below is `done`. `Lines` and `Documented` are measured, not estimated;
`Documented` counts files carrying a `docstate` block.

| Batch | Files | Lines | Documented | Status |
|---|--:|--:|--:|---|
| `streamIO/copy` (root+boole+groupM+monoid+order+primitiveOp+shift) | 82 | 11329 | 82 | done |
| `streamIO/copy/group` | 124 | 31328 | 124 | done |
| `function` (root+index+real+string+vector+byref) | 98 | 12899 | 98 | done |
| `function/derive` | 106 | 14586 | 106 | done |
| `streamIO/object` (root+backTrack+filterIn+filterInOut+filterOut+integer+yaml+json) | 79 | 13501 | 79 | done |
| `streamIO/object/enumer` | 79 | 18229 | 79 | done |
| `streamIO/object/parser` | 27 | 6942 | 27 | done |
| `streamIO/integer` (root+adapter+file+multiplex+pipe) | 56 | 15359 | 56 | done |
| `streamIO/integer/encoding`+`filter`+`random` (incl. `encoding/redundancy`+`filter/stats`) | 62 | 10273 | 62 | done |
| `streamIO/integer/jdbc` (excl. `dbTest/`) | 31 | 11899 | 31 | done |
| `streamIO/integer/jdbc/dbTest` | 8 | ~1300 | 8 | done |
| `graphic` (root+example+implement+svg) | 55 | 14297 | 55 | done |
| `graphic/math2D`+`graphic/ms3d` | 18 | 3525 | 18 | done |
| `graphic/math3D` | 32 | 6425 | 32 | done |
| `graphic/mvc` | 26 | 4789 | 26 | done |
| `math` (root+algorithm+integration+wavelet) | 18 | 3123 | 18 | done |
| `math/fit`+`math/refiner` | 27 | 3644 | 27 | done |
| `math/minimizer` | 11 | 3043 | 11 | done |
| `math/matrix` | 13 | 15603 | 13 | done |
| `math/vector` | 15 | 33025 | 15 | done |
| `structure` | 52 | 4933 | 52 | done |
| `streamIO/real` | 51 | 6801 | 51 | done |
| `tester` | 49 | 3327 | 49 | done |
| `technology` | 41 | 9400 | 41 | done |
| `synch` | 32 | 4243 | 32 | done |
| `graphs` | 31 | 11258 | 31 | done |
| `asynch` | 28 | 3052 | 28 | done |
| `streamIO/(root)` | 28 | 8003 | 28 | done |
| `knowledge` | 27 | 3363 | 27 | done |
| `stringOp` | 16 | 4579 | 16 | done |
| `aspect` | 15 | 2493 | 15 | done |
| `flow` | 14 | 1022 | 14 | done |
| `reflect` | 12 | 2492 | 12 | done |
| `streamIO/diffPatch` | 11 | 2895 | 11 | done |
| `sound` | 10 | 1030 | 10 | done |
| `tools` | 17 | 3468 | 17 | done |
| `(root)` | 9 | 1073 | 9 | done |
| `streamIO/asyncMessage` | 7 | 541 | 7 | done |
| `analysis` | 6 | 319 | 6 | done |
| `streamIO/adapter` | 6 | 435 | 6 | done |
| `streamIO/vector` | 6 | 947 | 6 | done |
| `streamIO/exception` | 5 | 536 | 5 | done |
| `streamIO/fileSystem` | 4 | 288 | 4 | done |
| `streamIO/testing` | 3 | 433 | 3 | done |
| `swing` | 3 | 679 | 3 | done |
| `persistences` | 2 | 306 | 2 | done |
| `streamIO/character` | 2 | 244 | 2 | done |
| `streamIO/factory` | 2 | 205 | 2 | done |
| `streamIO/detector` | 1 | 102 | 1 | done |

## Tool quirks (still live - apply if resuming this pipeline)

- **`check-stale`/`list-todo`/`apply-tags` all read only their first path argument** when
  given multiple space-separated paths, silently skipping the rest. Always invoke per-file or
  scope to a single folder.
- **`check-stale` convergence (`fresh`) is not a completeness gate.** It only tracks whether
  the docstate digest matches current content, and will report `fresh` for a doc block with no
  extractable summary sentence. Only `list-todo <path>` returning zero data rows proves a batch
  is done; run it as an independent final check.
- **`list-todo` has a persistent false positive when a method's simple name equals its return
  type's simple name** (e.g. `AManifold.Interpolator(IManifold)` returning `Interpolator`;
  `AContainer.ChangeIterator()`; also `Function.findRootFast`, `Function.isBiUnique`,
  `RecordSet.main`, `Relation.getAt`, `Relation.isBiUnique`) - reports "no Javadoc comment"
  despite a valid one. Confirm via direct source read before treating as a real gap.
- **`list-todo` has a separate, unrelated false positive on `streamIO/object/AND.java:AND.testIt()`.**
- **`extract-tags`/`build-index` on a folder recurse into every sibling subfolder**, and a
  `grep -v "/subfolder/"` filter (with a trailing slash) does **not** catch that subfolder's own
  bare folder-level scaffold row (no trailing slash). When splitting a batch across sibling
  folders owned by different agents, filter out the bare folder path too, or a sibling's
  folder-level tags/concept can get silently overwritten with the wrong batch's data.
- **`check-stale` can corrupt a docstate block via duplicated comment fragments** (seen on
  `streamIO/copy/TestCopy.java`) - inspect and manually clean up if it recurs.
- Not every file in this tree is UTF-8 (`knowledge/IdKey.java` is Latin-1) - read/write with
  the file's existing encoding. The tree is CRLF throughout - match on LF, write back CRLF, and
  never rewrite a whole file with a naive text-mode Python script (silently converts to LF).

## Conventions used throughout

- Banner comments, `@author`, `@version` and `Created on` lines are kept in every rewritten
  class comment; only the redundant `Title: X` line is dropped, and `Description:` becomes the
  summary sentence.
- Secondary non-public top-level types are documented in place, not moved to their own file.
- Bugs are flagged inline (`// TODO: LOGIC:` / `// TODO: SECURITY:`, always outside the Javadoc
  block) and recorded below - never fixed in the same pass.
- Multi-agent batches: the orchestrator alone runs git/tags/HANDOFF; each dispatched agent
  handles Pass 1+2+3 for its own assigned folder only, never spawns its own sub-agents, and
  must independently verify `list-todo` returns zero rows before reporting done.

## Bug-fix run (2026-09-06, complete)

All 337 rows that were marked `open` in the table below have been worked through and are
committed and pushed. Eight batches, each handled by one agent; the orchestrator alone ran git.

| Batch | Scope | Rows | Extra siblings | Commit |
|---|---|--:|--:|---|
| B1 | `math/vector` | 60 | 2 | `97fabf7` |
| B5 | `math` rest (matrix, fit, refiner, minimizer, integration, loose files) + `graphs` | 32 | 3 | `11cdf56` |
| B4 | `streamIO` rest (copy, real, diffPatch, fileSystem, vector, testing, exception, adapter, loose files) | 32 | 2 | `cb41adc` |
| B3 | `streamIO/object` | 35 | 0 | `2d12262` |
| B2 | `streamIO/integer` | 55 | 0 | `288fe2f` |
| B7 | `function`, `tester`, `stringOp`, `technology`, `structure`, `aspect`, `flow` | 42 | 1 | `5b33ddb` |
| B6 | `graphic` | 51 | 1 | `05ab1f7` |
| B8 | `asynch`, `synch`, `sound`, `persistences`, `reflect`, `swing`, `knowledge`, root files | 30 | 7 | `d3888c1` |

Sixteen further defects were found that the documentation pass had not recorded, each an exact
sibling of a row being fixed in the same or an adjacent file (for example `VectorLong.MIN` next
to `VectorChar.MIN`, or `DirectPlayer.keyReleased` next to `keyPressed`). They are fixed in the
same commits and called out in each commit message.

### Decisions taken

Minimal fixes preserving the public API; no new dependencies; design-level security items
remediated within the existing API shape rather than redesigned. Verification is a per-package
`javac`, not behavioural testing.

### Rows that were not fixed as written

Five rows did not survive contact with the code. Their entries in the table below carry the
detail: the two `Matrix*StreamIn` constructor rows were false positives (the real defect was in
`currVector()`), `FilterASCII2Base64` and `Depeater` were misdiagnosed (both files had a genuine
defect in the named method, which was fixed instead), and `RSMetaData`'s 0-based indexing was
documented and bounds-checked rather than converted, because the whole surrounding family is
0-based.

### Security items

| Item | Remediation |
|---|---|
| `XMLInputStream.fromXML` instantiating arbitrary classes from untrusted XML | `checkedForName()` denies by default, permitting only boxed primitives plus the configurable `ALLOWED_PACKAGE_PREFIXES` (`streamIO.`, `graphs.`); array types unwrapped first. |
| `JSONTokener.nextValue` unbounded recursion | Depth counter with a settable `DEFAULT_MAX_DEPTH` of 512, raising the tokener's own `JSONException`. |
| `AStatement.getResultSet(String)` path traversal | `CHECK_TABLE_NAME` rejects null/empty names, path separators, `:`, NUL, `.`, `..` and absolute paths before the `File` is built. |
| `FilterCrypt` home-grown XOR cipher | Kept and `@Deprecated`, with the Javadoc stating plainly that it is not cryptographically secure. No crypto library swapped in. |
| `SvgApplet.image` (found during the run) | `xlink:href` restricted to document-relative references; schemes, absolute paths and `..` rejected. |

### Verification, and its limits

`javac` proves syntax and types only. It cannot show that a corrected sign, an un-inverted
comparison or a shifted array index now computes the right answer, and this corpus has no test
suite to close that gap. **Every fix here should be treated as a draft needing review**, most of
all the arithmetic rows and the `asynch`/`synch` concurrency fixes, which are reasoned from the
code rather than demonstrated.

Facts established while verifying, worth keeping:

- **`javac` needs `-encoding UTF-8` on this tree.** Under the platform default (Cp1252 on
  Windows) files with non-ASCII bytes report spurious `illegal character` errors that are pure
  decoding artefacts. This is true at every commit, not only after these changes.
- **The tree does not build as a whole under Java 11**, for reasons that predate this run:
  `javax.xml.bind` and `sun.security.util` were removed from the JDK, and 16 `jdbc` classes
  predate JDBC 4.1. Package-level compilation is the practical ceiling.
- **41 of the 1457 `.java` files are LF-only**, not CRLF - so "the tree is CRLF throughout" is a
  near-generalization with roughly 3% exceptions. All of them were LF before this run.
- Stale `.class` files scattered through the tree can produce spurious `bad class file` errors;
  compile with `-cp <empty dir> -sourcepath .` to avoid them.

### Still open: `tools/`

`tools/` carries **20 inline `// TODO: LOGIC:` markers across 8 files** (`ErrorHandler`,
`FilterCallTransAction`, `LockedServer`, `LockedSimple`, `LockImproved`, `LockManager`,
`TransactFTP`, `WorkerThread`) that were **never recorded in the table below** - a gap in the
audit, not in this run. They are mostly locking and transaction defects: `if` instead of `while`
around `wait()`, discarded lock results, stub methods returning -1 unconditionally, an empty
`switch`, a commented-out method body, and two inverted timeout comparisons. Fixing them was out
of scope here because the instruction was to fix what HANDOFF.md documents.

## Bugs Found

Flagged during documentation, never fixed in the same pass. They were all fixed in the separate,
explicitly authorized bug-fix run recorded above, so **no row here is open any more** and no
`// TODO: LOGIC:` / `// TODO: SECURITY:` marker survives in any file this table names. The
`knowledge/` and `tools/threads/` rows were fixed and verified under test during an early pilot
batch; the rest were fixed in the eight batches above, with compile-only verification.

Read the `Status` column as "the fix is written and compiles", not "the behaviour is proven".
Five rows carry a longer status because the documented diagnosis did not hold - see
*Rows that were not fixed as written* above.

```
javac -d out knowledge/*.java     && java -cp "out;." knowledge.KnowledgeTest
javac -d out tools/threads/*.java && java -cp "out;." tools.threads.TimeOuterTest
```

| File Path | Class | Method | Line | Description | Severity | Status |
|---|---|---|---|---|---|---|
| tools/threads/TimeOuter.java | TimeOuter | run() | - | Class contract said the monitored Thread is interrupted "after the given TimeOut" (one shot), but the loop re-interrupted it every `sleepTime` while it stayed alive. | Medium | **fixed** |
| tools/threads/TimeOuter.java | TimeOuter | TimeOuter(Thread, long) | - | `this` was published to a new Thread from inside the constructor, and the fields that Thread reads were non-final and unsynchronized - unsafe publication data race. | Medium | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | insertObject(PersistAble) | - | The column list is built keys-then-fields ascending, but the value list appended fields first then keys, each descending, so every value landed in the wrong column. | High | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | Condition(IPrimaryKey, String), insertObject, updateObject | - | Values were concatenated into SQL text unquoted and unescaped - SQL injection. | High | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | updateObject(PersistAble) | - | Appended `STR_WHERE_` and then a `Condition()` that already opens with `WHERE`, emitting a doubled `WHERE WHERE`. | High | **fixed** |
| knowledge/Objekt.java | Objekt | getType() | - | Keyed by `StatusID` instead of `TypeID` and routed through `FactoryStatus` rather than `FactoryType`. | High | **fixed** |
| knowledge/BasicAttribute.java | BasicAttribute | getType() | - | Same defect as `Objekt.getType`. | High | **fixed** |
| knowledge/CachedValue.java | CachedValue | assertIsDirty(boolean) | - | Null test inverted: threw NPE when no calculator was set, never invoked one that was. | High | **fixed** |
| knowledge/Status.java | Status | finalize() | - | Empty in both branches while depended-on cache was documented as writing back; finalizer removed, contract corrected instead. | Medium | **fixed** |
| knowledge/DbCachedFactory.java | DbCachedFactory | DbCachedFactory(Connection, PersistAble) | - | `SELECT Max(ID)` failure was swallowed whole; now the already-declared `SQLException` propagates. | Medium | **fixed** |
| knowledge/IdKey.java | IdKey | newInstance(IPrimaryKey) | - | An unclosed block comment swallowed the following Javadoc. | Low | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | DBObjectFactory(Connection, PersistAble) | - | `substring(0, -1)` throws `StringIndexOutOfBoundsException` on a prototype with empty `Fields()`; latent, no current class hits it. | Low | **fixed** |
| knowledge/DBObjectFactory.java | DBObjectFactory | DBObjectFactory(Connection, PersistAble) | - | Column-name strings are built from the Java field names rather than `DBFieldNames()`/`DBKeyNames()`; currently harmless since every class defines the two identically. | Low | **fixed** |
| persistences/PersistedObject.java | PersistedObject | PersistedObject(String) | 165 | Checks the field `ID` (always null) instead of the parameter `ID_`, so `setId(ID_)` is never called from this constructor. | High | **fixed** |
| persistences/PersistedObject.java | PersistedObject | (field `objects`) | 41 | The static registry HashMap is never initialized - NPE on first real use. | High | **fixed** |
| swing/HashTreeNode.java | HashTreeNode | equals(Object) | 328 | Calls `userObject.equals(arg)` on a possibly-null `userObject` - NPE. | Low | **fixed** |
| streamIO/testing/ATestCase.java | ATestCase | test(...) | 214 | Both `InvocationTargetException` handler branches log/rethrow the wrapper instead of `inner` (`x.getTargetException()`). | Medium | **fixed** |
| streamIO/fileSystem/FileIterator.java | FileIterator | isValid() | 76 | Returns `!available`, inverted vs. its own field and the sibling `FileBackupIterator.isValid()`. | Medium | **fixed** |
| streamIO/fileSystem/FileIterator.java | FileIterator | currItem() | 100 | Always returns `null` - `nextItem()` never assigns `currItem`. | Low | **fixed** |
| streamIO/exception/ChainedException.java | ChainedException | printStackTrace(PrintStream) | 136 | The wrapping `PrintWriter` is never flushed/closed, unlike the sibling `BaseException` version. | Low | **fixed** |
| streamIO/vector/CombinationStream2.java | CombinationStream2 | (field `L`) | 34 | Logger constructed with `CombinationStream.class` instead of `CombinationStream2.class`. | Low | **fixed** |
| streamIO/adapter/CValue2StreamIn.java | CValue2StreamIn | CValue2StreamIn() | 48 | The only constructor never assigns `cValue` - NPE on first use. | High | **fixed** |
| DirToXml.java | DirToXml | execRecursive(...) | 71 | `SimpleDateFormat("yyyy-mm-dd'T'hh:MM:ss")` swaps month and minute format letters. | Medium | **fixed** |
| EchoFile.java | EchoFile | main(String[]) | 63 | Missing `return` after the usage message on invalid arg count - AIOOBE follows. | Low | **fixed** |
| FileHex.java | FileHex | main(String[]) | 37 | `e.fillInStackTrace()`'s result is discarded and `e` never used - dead code. | Low | **fixed** |
| FilterFind.java | FilterFind | read() | 172 | `breakCountDown` defaults to 0, never set in the constructor - fires immediately on first call. | Medium | **fixed** |
| FixRecordScrambler.java | FixRecordScrambler | main(String[]) | 149 | Guard checks `args.length!=6` but the method needs 7 args - passing exactly 6 satisfies neither check nor requirement. | Low | **fixed** |
| reflect/Type.java | Type | Type(Class) | 126 | Checks the field `cls` (still null) instead of the constructor argument `cls_` - NPE on every `new Type(...)`. | High | **fixed** |
| reflect/Type.java | Type | isAssignableFrom(Class) | 304 | Parameter `cls` shadows the field `this.cls`, so this compares the argument to itself - always `true`. | Medium | **fixed** |
| streamIO/diffPatch/VersionTree.java | VersionTree | LESS(int[], int[]) | ~107 | Lexicographic comparison never early-returns `false` on a decisive `arr1[i] > arr2[i]`. | Medium-High | **fixed** |
| streamIO/diffPatch/VersionTree.java | VersionTree | writeTo() | ~174 | Resize block computes a doubled array but never assigns it back; off-by-one resize check too. | High | **fixed** |
| streamIO/diffPatch/VersionTree.java | VersionTree | addVersion(DiffSet) | ~384 | Duplicated identical clause instead of an equals-check; relies on String reference identity, breaking after deserialization. | Medium | **fixed** |
| flow/push/MultiCaster.java | MultiCaster | putA(Object) | ~60-65 | Spawns one new unpooled Thread per pushed item - resource exhaustion under load. | Low-Medium | **fixed** |
| sound/WaveDataChunk.java | WaveDataChunk | WaveDataChunk(...) (16-bit case) | ~66 | `readInt()` (4 bytes) fills a 16-bit (2-byte) Sample - over-reads by 2x. | High | **fixed** |
| sound/MidiChunk.java | MidiChunk | MidiChunk(...) | ~44 | `events` array is allocated but never filled/advanced past, breaking multi-track MIDI parsing. | High | **fixed** |
| sound/WaveFile.java | WaveFile | main(String[]) | ~92 | `streamOut.close()` called unconditionally while `streamOut` is null for any real invocation - NPE. | Medium | **fixed** |
| sound/WaveStreamOut.java | WaveStreamOut | addInt(int) (24-bit case) | ~54 | Writes 4 bytes for a 24-bit Sample instead of 3 (author's own pre-existing TODO). | Medium | **fixed** |
| sound/WaveStreamIn.java | WaveStreamIn | WaveStreamIn(...) | ~45 | `IOException` from the initial `skipBytes` is silently swallowed. | Medium | **fixed** |
| sound/WaveStreamIn.java | WaveStreamIn | nextLongInternal() (24-bit case) | - | Reads via `readInt()` (4 bytes) for a 24-bit Sample - symmetric read-side of the WaveStreamOut bug (author's own pre-existing TODO). | Medium | **fixed** |
| sound/DirectPlayer.java | DirectPlayer | keyPressed(...) | ~294 | `GET_KEY` can return 65535, out of bounds for the 256-entry array; AIOOBE silently swallowed. | Low | **fixed** |
| aspect/AHierarchyAspect.java | AHierarchyAspect | update(Object, Object, Object) | ~233 | Type check tests `IAspect` but casts to `IHierarchyAspect` - latent `ClassCastException`. | Low-Medium | **fixed** |
| aspect/ListAspect.java | ListAspect | removeVal(int) | ~243 | Bounds guard uses `>` instead of `>=` - unchecked `IndexOutOfBoundsException` one past the end. | Low | **fixed** |
| aspect/ListAspect.java | ListAspect | testList() | ~324 | References a field-name prefix that does not exist on the target Aspect - NPE in its own self-test. | Medium | **fixed** |
| aspect/dialog/BoolQuestion.java | BoolQuestion | setValue(Object) | ~95 | Empty trimmed input or null `val` both crash instead of re-prompting/defaulting. | Medium | **fixed** |
| asynch/Barrier.java | Barrier | (field/constructor) | - | Count field left uninitialized before use in the wait/release path. | Medium | **fixed** |
| asynch/BlockedThreadExecutor.java | BlockedThreadExecutor | execute/run path | - | Stale `Runnable` reference and a missing `notify()` on completion - a waiting caller can block indefinitely. | High | **fixed** |
| asynch/Scheduler.java | Scheduler | scheduling loop | - | Busy-waits instead of blocking/parking, burning CPU while idle. | Low-Medium | **fixed** |
| asynch/ThreadExecutor.java | ThreadExecutor | numTasks bookkeeping | - | Drifts from actual queue/assignment state (matches the class's own Javadoc admission). | Medium | **fixed** |
| asynch/ThreadPoolExecutor.java | ThreadPoolExecutor | (synchronization) | - | `wait()`/`notify()` called outside a `synchronized` block - `IllegalMonitorStateException`. | High | **fixed** |
| asynch/QueuedSemaphore.java | QueuedSemaphore | acquire/release | - | Lost-wakeup race between a waiter's failed acquire check and its `wait()` call. | High | **fixed** |
| stringOp/Grammar.java | Grammar | evolve(...) | ~51 | Off-by-one bound check against `Productions.length`. | Medium | **fixed** |
| stringOp/SentenceComparer.java | SentenceComparer | getMostSimilarSentence(...) | ~124 | Best-match index is never recorded - always returns -1. | High | **fixed** |
| stringOp/SentenceComparer.java | SentenceComparer | getWordSet(String, boolean) | ~151 | Unimplemented - always returns an empty `BitSet`. | High | **fixed** |
| stringOp/search/SearcherBM.java | SearcherBM | constructor and search loop | ~48, ~68 | Negative `hashCode()` used as an array index without masking - AIOOBE. | Medium | **fixed** |
| synch/UniCastConstrained.java | UniCastConstrained | addValidator(IValidator) | ~67 | Dead `instanceof` branch aside, every call after the first silently drops the new validator. | High | **fixed** |
| synch/ValidationRuleList.java | ValidationRuleList | validateInThread(Object) | ~146 | Reads `params[3]` from a 2-element array - AIOOBE on every timed validation. | High | **fixed** |
| synch/APubUniLinkSub.java | APubUniLinkSub | update(Object, Object, Object) | ~117 | No null check on `subscriber` before propagating to the chain-terminal node - NPE. | High | **fixed** |
| synch/PropDouble.java | PropDouble | setValue(double) | ~48 | Never calls `subscriber.update(...)` - the notification contract is a no-op. | Medium | **fixed** |
| synch/StateMachine.java | StateMachine | toString() | ~110 | Inner loop bound uses the row count instead of the column count. | Medium | **fixed** |
| streamIO/AReSetAble.java | AReSetAble | JUMP(IReSetAble, long) | ~106 | Loop guard only works for positive offsets; a negative offset (as `PUSH_BACK` relies on) always returns 0. | Medium | **fixed** |
| streamIO/Log.java | Log | XML_DATE_FORMATTER (field) | ~247 | Shared static `SimpleDateFormat` is not thread-safe - concurrent logging can corrupt formatting. | Medium | **fixed** |
| streamIO/StringBufferOutputStream.java | StringBufferOutputStream | addBuffer(StringBuffer, int) | ~208 | Calls a 3-arg overload with reversed parameter order - silently appends nothing for `stop > 0`. | Medium | **fixed** |
| graphs/AGraph.java | AGraph | (edge-filtering method) | ~116 | Filters by the target Node index instead of edge weight. | Medium | **fixed** |
| graphs/SparseMatrix.java | SparseMatrix | getDegree/getInDegree helper | ~241 | Calls itself instead of `getOutDegree(j)` - infinite recursion. | High | **fixed** |
| technology/RandomGUID.java | RandomGUID | getRandomGUID(boolean) | 160 | `NoSuchAlgorithmException` is only logged, then execution falls through to a null-dereferencing use of `md5`. | Low | **fixed** |
| technology/xml/XmlToDirHandler.java | XmlToDirHandler | main(String[]) | 209 | Prints the array reference instead of its contents. | Low | **fixed** |
| technology/xml/XmlUnmarshaller.java | XmlUnmarshaller | setBuffer(String) | 298 | Duplicate `long.class` check, almost certainly meant `double.class` - double fields never convert. | Medium | **fixed** |
| technology/xml/test/KundeInSystem.java | KundeInSystem | ZKDBBaseType() (accessor) | 83 | Getter named literally `ZKDBBaseType()` instead of `getTyp()` - breaks reflection-based access by naming convention. | Medium | **fixed** |
| tester/logic/ConditionTable.java | ConditionTable | constructor | ~39 | Validation loop reads the instance field (still null) instead of the constructor parameter - NPE on every construction. | Critical | **fixed** |
| tester/MetricMeasurAble.java | MetricMeasurAble | dist(Object, Object) | ~49 | Both operands read from `a` - `b`'s value is never read, always returns 0. | High | **fixed** |
| tester/FilterTestWaiter.java | FilterTestWaiter | test(Object) | ~56 | `wait(waitTime)` called without holding the monitor - `IllegalMonitorStateException`. | High | **fixed** |
| tester/fuzzy/FuzzyDictionary.java | FuzzyDictionary | getMostSimilarItem(Object, double) | ~75 | Never actually searches - `minIndex` is hardcoded, always returns -1. | High | **fixed** |
| tester/fuzzy/FuzzySentenceComparator.java | FuzzySentenceComparator | read(...) | ~208 | Loop condition compares against a hardcoded char instead of the `sep` parameter. | Medium | **fixed** |
| tester/process/StreamProcessor.java | StreamProcessor | getPosition() | ~93 | Delegates to `availAble()` instead of an actual position method. | Medium | **fixed** |
| tester/process/IOEProcess.java | IOEProcess | testIt() | ~77 | Wrong fully-qualified class name passed to `Runtime.exec` - child process fails immediately. | Low | **fixed** |
| structure/Context.java | Context | send() | ~77 | Calls itself instead of delegating to `currState` - infinite recursion. | High | **fixed** |
| structure/Delegate.java | Delegate | raiseEvent() | ~153 | Both catch blocks are empty, silently stopping notification of every Delegate after the failure. | Medium | **fixed** |
| structure/HistoryList.java | HistoryList | addItem(Object) | ~90 | Grows the backing array one element too late - AIOOBE. | Medium | **fixed** |
| structure/Visitor1.java / Visitor2.java | Visitor1, Visitor2 | visit(ElementA)/visit(ElementB) | ~42-55 | Unconditional mutual recursion with `invite()` - `StackOverflowError`. | High | **fixed** |
| structure/aspect/DoubleAspect.java | DoubleAspect | getLong()/getDouble() | ~139, ~152 | Reads a field no setter overload ever assigns - stays permanently 0.0. | High | **fixed** |
| structure/aspect/ListAspect.java | ListAspect | constructor(String, Aspect[]) | ~60 | Constructor parameter never assigned to the `list` field. | Medium | **fixed** |
| streamIO/real/FilterInMul.java | FilterInMul | getMinDouble() | - | Sign-flip bug, wrong-signed minimum bound. | Medium | **fixed** |
| streamIO/real/StreamIn_Geometric.java | StreamIn_Geometric | 2-arg constructor | - | Field-order/assignment bug. | Medium | **fixed** |
| streamIO/real/random/RandomGauss.java | RandomGauss | nextDoubleInternal() | - | Always-false self-comparison, dead rejection branch. | Medium | **fixed** |
| streamIO/real/random/RandomGauss2.java | RandomGauss2 | nextDoubleInternal() | - | Same always-false self-comparison bug as `RandomGauss`. | Medium | **fixed** |
| streamIO/real/random/RandomPoisson.java | RandomPoisson | reSet() | - | Unconditional call on a field not constructed on this branch - NPE. | High | **fixed** |
| math/minimizer/SinOfDistDivDist.java | SinOfDistDivDist | Map(double[]) / Map(float[]) | 59, 78 | `0.0/0.0 = NaN` at the function's own minimum instead of the correct limit -1.0. | Medium | **fixed** |
| graphic/math2D/Map2DModel.java | Map2DModel | addPoint(int, float, float, String) | 215 | Missing null guard on `coordTrafo` (present in the sibling double overload) - NPE. | Medium | **fixed** |
| graphic/math2D/Map2DModel.java | Map2DModel | addPoint(float, float, String) | 228 | Same missing-null-guard defect. | Medium | **fixed** |
| graphic/math2D/Map2DModel.java | Map2DModel | addPoint(float[], String) | 260 | Same missing-null-guard defect. | Medium | **fixed** |
| graphic/ms3d/Ms3d.java | Ms3d | calcRotation(Ms3dJoint, float) | 673 | Compares against the wrong keyframe-count field - AIOOBE when counts differ. | Medium-High | **fixed** |
| graphic/ms3d/Ms3d.java | Ms3d | streamJoints(OutputStream) | 162 | Delegates to `streamVertices(...)` instead of `streamJoints(...)` - copy-paste error. | Medium | **fixed** |
| graphic/ms3d/Ms3dVertex.java | Ms3dVertex | toStream(OutputStream) | 105 | Calls itself via overload resolution - `StackOverflowError`. | High | **fixed** |
| math/fit/weight/WeightExp.java | WeightExp | (field `SINGLETON`) | 32 | Non-static instance field recursing into its own initializer - `StackOverflowError`. Needs `static`. | High | **fixed** |
| math/fit/weight/WeightGauss.java | WeightGauss | (field `SINGLETON`) | 33 | Same non-static-`SINGLETON`-recursion defect. | High | **fixed** |
| math/fit/weight/WeightLorentz.java | WeightLorentz | (field `SINGLETON`) | 30 | Same non-static-`SINGLETON`-recursion defect. | High | **fixed** |
| math/fit/weight/WeightExp.java | WeightExp | probCum(double) | 41 | Returns the unintegrated density instead of a cumulative probability (author's own TODO). | Low | **fixed** |
| math/fit/weight/WeightLorentz.java | WeightLorentz | probCum(double) | 43 | Returns the wrong (WeightExp) formula, inconsistent with its own siblings (author's own TODO). | Low | **fixed** |
| math/fit/FitFields.java | FitFields | map(double[], double[]) | 22 | Always returns `null` instead of the populated array. | Medium | **fixed** |
| math/fit/FitFields.java | FitFields | map(float[], float[]) | 31 | Same always-`null`-return defect. | Medium | **fixed** |
| math/fit/FitGauss.java | FitGauss | map(double[], double[], double[]) | 75 | Unimplemented stub, silently returns 0 instead of an error. | Medium | **fixed** |
| math/fit/FitGauss.java | FitGauss | map(float[], float[], float[]) | 89 | Same unimplemented-stub defect. | Medium | **fixed** |
| math/fit/FittingFloat.java | FittingFloat | svdfit(...) | 45 | Required decomposition/back-substitution calls are commented out - never actually solves. | High | **fixed** |
| math/refiner/AFloatRefinerQ.java | AFloatRefinerQ | BRACKET(IFloatFunction, float, float, int) | 103 | Allocates the result array to the actual bracket count but copies the larger requested count into it - AIOOBE. | High | **fixed** |
| math/refiner/NewtonFloatRefiner.java | NewtonFloatRefiner | refine() | 71 | Reads a field one constructor path never sets - NPE on first call. | High | **fixed** |
| math/Vector3D.java | Vector3D | angles()/Sphaeric2Rect()/Rect2Sphaeric()/Quadrik(...) | 336,361,380,428 | Indexes one past the end of 2/3-element arrays - AIOOBE every call. | Critical | **fixed** |
| math/Vector3D.java | Vector3D | mul(double) | 148 | Mutates `this.a[]` inside constructor arguments, contradicting the non-mutating method contract. | Medium | **fixed** |
| math/Vector2D.java | Vector2D | Equality(...) (2 overloads) | 95, 300 | Operator-precedence bug: `x << 2 + y` parses as `x << (2+y)`. | Medium | **fixed** |
| math/Vector2D.java | Vector2D | DET2x2(Vector2D) | 307 | Indexes one past the end of a 2-element array - AIOOBE. | Critical | **fixed** |
| math/NumberFormatter.java | NumberFormatter | isNumber(String) | 26 | Always returns `true` - unimplemented. | Low | **fixed** |
| math/integration/StratifiedMCIntegrator.java | StratifiedMCIntegrator | integrate(...) | 95 | Contract documents a nullable parameter, but the method dereferences it unconditionally - NPE. | Medium | **fixed** |
| graphic/AGraph2D.java | AGraph2D | sizePolygonAt(int[][], int, int) | 106 | Duplicated condition clause instead of an OR, silently skipping a scale axis. | Medium | **fixed** |
| graphic/AGraph2D.java | AGraph2D | setThickPixel() | 270 | Wrong coordinate variable used for one `fillRect` argument. | Medium | **fixed** |
| graphic/AGraph2DOut.java | AGraph2DOut | setColor(Color) | 52 | Uninitialized field dereferenced on the very first call - NPE. | Medium | **fixed** |
| graphic/Figures.java | Figures | VectorGrid(int[], int[], int[][], int[][]) | 351 | Pre-increment loops skip row/column 0 entirely. | Low | **fixed** |
| graphic/Figures.java | Figures | (ellipse-radial-lines loop) | 439 | Uses the wrong coordinate accessor for the start point, skewing radial lines. | Medium | **fixed** |
| graphic/Graph2D.java | Graph2D | drawImage(...) | 228 | Stub always returns `true` without drawing anything. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | create() | 74 | Always returns `null`, contradicting the documented contract - NPE downstream. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | setPixel(Color) | 548 | The `color` parameter is never used - always paints with the stale current color. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillPolygon(int[], int[], Color, Color) | 809 | Border/inner colors swapped relative to their parameter names. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | drawEllipse(Point2D, int) | 863 | Center/radius contract mismatch vs. the underlying `drawOval` call. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | drawEllipse(Point2D, Point2D) | 877 | Same center/radius contract mismatch. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillEllipse(Point2D, int) | 964 | Same center/radius contract mismatch. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillEllipse(Point2D, Point2D) | 977 | Same center/radius contract mismatch. | Medium | **fixed** |
| graphic/GraphicsAdapter.java | GraphicsAdapter | fillRoundRect(Point2D, Point2D, int, int) | 1043 | Absolute coordinates passed as width/height instead of the computed difference. | Medium | **fixed** |
| graphic/Hidden.java | Hidden | setPixel(int, int, Color) | 139 | Off-by-one bounds guard - AIOOBE. | High | **fixed** |
| graphic/JavaGraphic.java | JavaGraphic | fillEllipse(Point2D, Point2D) | 503 | Calls the outline-only `drawOval` instead of `fillOval`. | Medium | **fixed** |
| graphic/JavaGraphic.java | JavaGraphic | fillRoundRect(Line2D, Point2D) | 539 | Dispatches to the outline method instead of the fill method. | Medium | **fixed** |
| graphic/JavaGraphic.java | JavaGraphic | fillRoundRect(Line2D, Point2D, Point2D) | 554 | Same outline-instead-of-fill dispatch bug. | Medium | **fixed** |
| graphic/PaletteRGB.java | PaletteRGB | HUE2COLOR(int) | 182 | Wrong sign in negative wrap-around arithmetic - wrong color for negative input. | Medium | **fixed** |
| graphic/PaletteShading.java | PaletteShading | getColor(int) | 80 | No guard against a zero divisor - `ArithmeticException`. | Medium | **fixed** |
| graphic/Point2D.java | Point2D | MinAt(Point2D) | 139 | Compares/assigns against the wrong axis (`x` instead of `y`). | Medium | **fixed** |
| graphic/Point2D.java | Point2D | MaxAt(Point2D) | 152 | Same axis mixup as `MinAt()`. | Medium | **fixed** |
| graphic/Polygon2D.java | Polygon2D | getExtent() | 92 | Overwrites the real point array with nulls before reading it - destroys the polygon's data. | High | **fixed** |
| graphic/ScalarPlotNew.java | ScalarPlotNew | (color-segment stepping) | 379 | Never tracks a segment-start position, unlike the sibling multi-arg overload. | Medium | **fixed** |
| graphic/VectorPoint2D.java | VectorPoint2D | (resize helper) | 470 | Returns the original unresized array instead of the newly resized one. | Medium | **fixed** |
| graphic/VectorPoint2D.java | VectorPoint2D | stream(PrintStream, int, int) | 651 | Pre-increment loop skips the first row. | Medium | **fixed** |
| graphic/ZBuffer.java | ZBuffer | setPixel(int, int, float) | 134 | Y-bound is never checked (X-bound checked twice instead) - AIOOBE. | High | **fixed** |
| graphic/example/AntHillInside.java | AntHillInside | moveAnt(...) | 169 | Coordinates never clamped/wrapped to bounds - eventual AIOOBE. | Medium | **fixed** |
| graphic/example/Erosion.java | Erosion | MakeTerrainFault(...) | 169 | Normalize/copy step was commented out and never replaced - output buffer stays all-zero. | High | **fixed** |
| graphic/implement/GrayColor.java | GrayColor | initPass(int, int, int, int) | 84 | Operator-precedence bug: `<<` binds looser than `+`. | Medium | **fixed** |
| graphic/implement/GreyColor.java | GreyColor | setPixel() | 116 | Column index can reach 8 in a length-8 array - AIOOBE. | High | **fixed** |
| graphic/svg/SvgApplet.java | SvgApplet | getTrafo(Rectangle) | 240 | Parameter ignored - effectively a no-arg getter. | Low | **fixed** |
| graphic/svg/SvgApplet.java | SvgApplet | setTrafo(Coordinates2D) | 247 | Adds new listeners on every call without removing prior ones - listener leak. | Medium | **fixed** |
| graphic/svg/SvgApplet.java | SvgApplet | image(Attributes) | 488 | `xlink:href` from untrusted SVG is fetched with no scheme/path validation - SSRF-like. | Medium | **fixed** |
| graphic/math3D/OdePlotter.java | OdePlotter | drawLoop() | 101 | Loop condition is false from the start when `Rect` is null - draws exactly one step instead of running unbounded. | Medium | **fixed** |
| graphic/mvc/BufferedPainter.java | BufferedPainter | BufferedPainter(ICanvas) | 83 | Width/height swapped allocating the offscreen buffer. | High | **fixed** |
| graphic/mvc/plane2D/VectorPolygon.java | VectorPolygon | drawInOrder(IGraphText) | 353 | The branch that rebuilds `zIndex` is commented out - always NPEs. | High | **fixed** |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | SET_DIM_AT(short[][], int) | 480 | Returns the original array instead of the resized one. | Medium | **fixed** |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | MatrixShort(Object) | 819 | Wrong constructor overload resolved - capacity increment passed as row dimension. | Medium | **fixed** |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | newInstance() | 1017 | Same constructor-overload mismatch. | Medium | **fixed** |
| graphic/mvc/plane2D/VectorPolygon.java | VectorPolygon | copyAt(MatrixShort[]) | 293 | Never grows capacity first, unlike the sibling Object-typed overload - AIOOBE. | Medium | **fixed** |
| graphic/mvc/BaseApplet.java | BaseApplet | imageUpdate(...) | 360 | `ERROR`/`ABORT` never handled - a failed image load never stops the update loop. | Medium | **fixed** |
| graphic/mvc/BaseApplet.java | BaseApplet | getFaultySuffix(String) | 108 | Throws on filenames shorter than 4 characters. | Low | **fixed** |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | STREAM(...) | 650 | Pre-increment loop skips the first row. | Low | **fixed** |
| graphic/mvc/plane2D/MatrixShort.java | MatrixShort | normalizeAt() | 1031 | Decrements past 0 to -1 when already empty - AIOOBE. | Low | **fixed** |
| math/vector/VectorObject.java | VectorObject | removeAt(int) | 167 | `itemCount` decremented as a side effect even when the index is out of range. | High | **fixed** |
| math/vector/VectorObject.java | VectorObject | copyInto(int[]) | 449 | Copies an `Object[]` into an `int[]` destination - `ArrayStoreException`. | Critical | **fixed** |
| math/vector/VectorObject.java | VectorObject | toArray() | 466 | Same `ArrayStoreException` hazard as `copyInto(int[])`. | Critical | **fixed** |
| math/vector/HunterInt.java | HunterInt | GET_STATISTIC_POS(int[], int, int, int) | 603 | `PARTITION`'s bound arguments are passed swapped. | High | **fixed** |
| math/vector/HunterInt.java | HunterInt | GET_STATISTIC_POS(int[], int[], int, int, int) | 844 | Same swapped-bounds defect, indexed variant. | High | **fixed** |
| math/vector/HunterFloat.java | HunterFloat | GET_STATISTIC_POS(float[], int[], int, int, int) | 879 | Same swapped-bounds defect, indexed variant. | High | **fixed** |
| math/vector/HunterDouble.java | HunterDouble | GET_STATISTIC(double[], int[], int, int, int) | 756 | Same swapped-bounds defect, indexed variant. | High | **fixed** |
| math/vector/VectorChar.java | VectorChar | MIN(char[][], int) | 60 | Comparison inverted - always returns `Character.MAX_VALUE`. | Critical | **fixed** |
| math/vector/VectorString.java | VectorString | ALIGN_RIGHT(String, String) | 453 | Negative substring index when `str` is shorter than `format` - the normal case. | High | **fixed** |
| math/vector/VectorString.java | VectorString | PAD(String, int, char, boolean) | 723 | Returned String is 1 character shorter than requested. | Medium | **fixed** |
| math/vector/VectorString.java | VectorString | Collection2StringArray(Collection) | 829 | Pre-increment write skips index 0 and overruns the end - AIOOBE. | Critical | **fixed** |
| math/vector/VectorString.java | VectorString | REPLACE_ALL(String, String) | 967 | Off-by-one drops the character before the first separator match. | Medium | **fixed** |
| math/vector/VectorString.java | VectorString | toString(byte[], StringBuffer) | 1343 | Off-by-one byte vs. the documented "terminated by 0" contract. | Low | **fixed** |
| math/vector/VectorString.java | VectorString | removeAt(int) | 1760 | Same unconditional-decrement bug as `VectorObject.removeAt(int)`. | High | **fixed** |
| math/vector/VectorString.java | VectorString | copyInto(int[]) | 1894 | Same `ArrayStoreException` hazard as `VectorObject.copyInto(int[])`. | Critical | **fixed** |
| math/vector/VectorString.java | VectorString | toArray() | 1912 | Same `ArrayStoreException` hazard. | Critical | **fixed** |
| math/matrix/MatrixFloatStreamIn.java | MatrixFloatStreamIn | constructor | 45 | `currPos` initialized one past the last valid index - AIOOBE before any `nextVector()`. | Medium | **not a bug** - `nextVector()` pre-decrements, so the initial position is correct. The real AIOOBE is `currVector()` before the first `nextVector()`, which now raises `IllegalStateException`. |
| math/matrix/MatrixDouble.java | MatrixDoubleStreamIn | constructor | 4224 | Same off-by-one as `MatrixFloatStreamIn`'s constructor. | Medium | **not a bug** - same as `MatrixFloatStreamIn`; `currVector()` guarded instead. |
| math/matrix/MatrixObject.java | MatrixObject | copyInto(int[]) | 255 | `Object[][]` copied into an `int[]` destination - `ArrayStoreException`. | High | **fixed** |
| math/matrix/MatrixObject.java | MatrixObject | toArray() | 270 | Same `Object[][]`-into-`int[]` defect. | High | **fixed** |
| math/matrix/MatrixInt.java | MatrixInt | COPY_AT(int[][], int[], int, int) | 891 | Arraycopy direction reversed - overwrites the input instead of filling the output. | High | **fixed** |
| math/matrix/MatrixFloat.java | MatrixFloat | COPY_AT(float[][], float[], int, int) | 1182 | Same reversed-direction defect. | High | **fixed** |
| math/matrix/MatrixDouble.java | MatrixDouble | COPY_AT(double[][], double[][], int, int) | 1112 | Same reversed-direction defect, matrix-to-matrix overload. | High | **fixed** |
| math/matrix/MatrixDouble.java | MatrixDouble | COPY_AT(double[][], double[], int, int) | 1125 | Same reversed-direction defect, vector overload. | High | **fixed** |
| math/matrix/MatrixTriDiagonal.java | MatrixTriDiagonal | solveCyclicAt(...) | 165 | Mutable shared arrays not defensively copied - stale interior values silently reused across calls. | Medium | **fixed** |
| math/matrix/Quaternion.java | Quaternion | getAxis() | 181 | Indexes `q[4]` on a `float[4]` - AIOOBE every call. | Critical | **fixed** |
| math/matrix/Quaternion.java | Quaternion | set(float[]) | 557 | Ignores its own parameter, copies the array onto itself - a no-op. | High | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | MUL_CROSS_AT(double[], double[]) | 671, 676 | Writes one past the end of a 3-element array - AIOOBE. | High | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | (resize helper, ~1021) | 1021 | Returns the original array instead of the resized one. | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | ONE_AT(double[], int, int) | 1061 | Fills with 0 instead of 1 (copy-pasted from `ZERO_AT`). | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | (array-fill loop, ~1339) | 1339 | Off-by-one loop bound writes one past the array end - AIOOBE. | High | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | (copy helper, ~2610) | 2610 | Reads from the destination array instead of the source. | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | MUL_AT(double[], int, double[], int) | 3319 | Leftover elements neither multiplied nor zeroed when the result is longer than the input. | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | MUL_AT(double[], int, float[], int) | 3339 | Same leftover-elements defect, `float[]` overload. | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | removeAt(int) | 5003 | Same unconditional-decrement-on-failure bug as `VectorObject.removeAt(int)`. | High | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | equals(Object) | 5970 | Violates the null-argument contract - NPE instead of returning false. | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | toStream(Writer) | 6074 | Off-by-one write range, plus `itemCount` written as a raw character code instead of digits. | Medium | **fixed** |
| math/vector/VectorDouble.java | VectorDouble | fullDiffAt() | 6166 | Infinite loop once `itemCount` reaches 0. | High | **fixed** |
| math/vector/VectorChar.java | VectorChar | oneAt(char[], int, int) | 541 | Fills with 0, not 1. | Medium | **fixed** |
| math/vector/VectorChar.java | VectorChar | AbsDiffNorm(char[], char[], char[]) | 804 | Output element left unassigned on the non-positive branch. | Medium | **fixed** |
| math/vector/VectorChar.java | VectorChar | removeAt(int) | 2214 | Same unconditional-decrement-before-range-check bug as `VectorObject.removeAt`. | High | **fixed** |
| math/vector/VectorChar.java | VectorChar | mulAt(VectorChar) | 2372 | Calls `subAt(...)` instead of `mulAt(...)` - copy-paste. | High | **fixed** |
| math/vector/VectorChar.java | VectorChar | divAt(VectorChar) | 2379 | Same copy-paste defect, calling `subAt(...)` instead of `divAt(...)`. | High | **fixed** |
| math/vector/VectorChar.java | VectorChar | mulAt(int) | 2412 | Ignored scalar parameter - squares every element instead. | High | **fixed** |
| math/vector/VectorChar.java | VectorChar | divAt(int) | 2419 | Ignored scalar parameter - self-divides every element instead. | High | **fixed** |
| math/vector/VectorLong.java | VectorLong | oneAt(long[], int, int) | 532 | Same fills-with-0-instead-of-1 defect as `VectorChar.oneAt`. | Medium | **fixed** |
| math/vector/VectorLong.java | VectorLong | AbsDiffNorm(long[], long[], long[]) | 796 | Same unassigned-output defect as `VectorChar.AbsDiffNorm`. | Medium | **fixed** |
| math/vector/VectorLong.java | VectorLong | removeAt(int) | 2207 | Same unconditional-decrement defect as `VectorChar.removeAt(int)`. | High | **fixed** |
| math/vector/VectorLong.java | VectorLong | mulAt(VectorLong) | 2365 | Same `subAt`-instead-of-`mulAt` copy-paste defect. | High | **fixed** |
| math/vector/VectorLong.java | VectorLong | divAt(VectorLong) | 2372 | Same `subAt`-instead-of-`divAt` copy-paste defect. | High | **fixed** |
| math/vector/VectorLong.java | VectorLong | mulAt(int) | 2405 | Same ignored-scalar-parameter defect (squares instead). | High | **fixed** |
| math/vector/VectorLong.java | VectorLong | divAt(int) | 2413 | Same ignored-scalar-parameter defect (self-divides instead). | High | **fixed** |
| math/vector/VectorShort.java | VectorShort | oneAt(short[], int, int) | 770 | Same fills-with-0-instead-of-1 defect. | Medium | **fixed** |
| math/vector/VectorShort.java | VectorShort | removeAt(int) | 3027 | Same unconditional-decrement defect. | High | **fixed** |
| math/vector/VectorShort.java | VectorShort | mulAt(VectorShort) | 3334 | Same `subAt`-instead-of-`mulAt` copy-paste defect. | High | **fixed** |
| math/vector/VectorShort.java | VectorShort | divAt(VectorShort) | 3342 | Same `subAt`-instead-of-`divAt` copy-paste defect. | High | **fixed** |
| math/vector/VectorShort.java | VectorShort | mulAt(int) | 3378 | Same ignored-scalar-parameter defect. | High | **fixed** |
| math/vector/VectorShort.java | VectorShort | divAt(int) | 3387 | Same ignored-scalar-parameter defect. | High | **fixed** |
| math/vector/VectorInt.java | VectorInt | ONE_AT(int[], int, int) | 1010 | Same fills-with-0-instead-of-1 defect. | Medium | **fixed** |
| math/vector/VectorInt.java | VectorInt | MUL_CROSS_AT(int[], int[]) | 2042, 2047 | Same one-past-the-end write defect as `VectorDouble.MUL_CROSS_AT`. | High | **fixed** |
| math/vector/VectorInt.java | VectorInt | removeAt(int) | 3572 | Same unconditional-decrement defect. | High | **fixed** |
| math/vector/VectorInt.java | VectorInt | mulAt(VectorInt) | 3726 | Same `subAt`-instead-of-multiplicative-op copy-paste defect. | High | **fixed** |
| math/vector/VectorInt.java | VectorInt | divAt(VectorInt) | 3731 | Same `subAt`-instead-of-divisive-op copy-paste defect. | High | **fixed** |
| math/vector/VectorInt.java | VectorInt | mulAt(int) | 3762 | Same ignored-scalar-parameter defect. | High | **fixed** |
| math/vector/VectorInt.java | VectorInt | divAt(int) | 3767 | Same ignored-scalar-parameter defect. | High | **fixed** |
| math/vector/VectorFloat.java | VectorFloat | ABS(float[], int, int, float[]) | 2107 | Reads from the destination array instead of the source. | Medium | **fixed** |
| math/vector/VectorFloat.java | VectorFloat | removeAt(int) | 4297 | Same unconditional-decrement defect. | High | **fixed** |
| math/vector/VectorFloat.java | VectorFloat | mulAt(VectorFloat) | 4574 | Same `subAt`-instead-of-multiplicative-op defect. | High | **fixed** |
| math/vector/VectorFloat.java | VectorFloat | divAt(VectorFloat) | 4580 | Same `subAt`-instead-of-divisive-op defect. | High | **fixed** |
| math/vector/VectorFloat.java | VectorFloat | mulAt(double) | 4630 | Ignores its parameter, multiplies items by themselves instead. | High | **fixed** |
| math/vector/VectorFloat.java | VectorFloat | divAt(double) | 4636 | Ignores its parameter, divides items by themselves instead (yielding all 1s). | High | **fixed** |
| function/derive/Enum.java | Enum | succ() | 264 | Indexes by raw `Value` instead of list position `Value-Offset` - skips an element or AIOOBE depending on Offset. | High | **fixed** |
| function/derive/Enum.java | Enum | pred() | 271 | Same Offset-blind indexing defect. | Medium | **fixed** |
| function/derive/Ternary.java | Ternary | fromString(String) | 224 | Ignores its parameter and always returns `this` - parsing unimplemented (author's own TODO). | Medium | **fixed** |
| function/derive/neuron/Network.java | Network | randomizeWeights() | 144 | Loop bound skips `Layers[0]` entirely. | Medium | **fixed** |
| function/derive/ring/LinAt.java | LinAt | LinAt(Object, Object, IInvertAble) | 39 | Validates fields (still null) instead of the constructor parameters - checks never trigger. | Low | **fixed** |
| function/derive/ring/LinAt.java | LinAt | Map(Object) | 65 | Uses the wrong field for pure scaling, breaking `a*x` whenever `b` is null. | High | **fixed** |
| function/derive/ring/body/Logarithm.java | Logarithm | getDerivative(double) | 58 | Returns `-Math.log(x)` instead of the correct derivative `1/x`. | High | **fixed** |
| function/derive/ring/body/Logarithm.java | Logarithm | getFuncDerive(double, ByRefDouble) | 65 | Returns `-Math.log(x)` instead of `Math.log(x)`, disagreeing with `Map(x)`. | High | **fixed** |
| function/derive/ring/body/vector/fSum.java | fSum | Map(Object) | 18 | Double-counts coordinate 0 for every tensor of dimension >= 1. | High | **fixed** |
| streamIO/copy/primitiveOp/AOpDouble.java | AOpDouble | 11 `long`-overload methods | 43-79 | Each calls itself instead of delegating to the `double`-based op - `StackOverflowError` on any call. | Critical | **fixed** |
| streamIO/copy/primitiveOp/AOpMeasurAble.java | AOpMeasurAble | LinAt(long, long) | 60 | No-op - both parameters ignored. | Medium | **fixed** |
| streamIO/copy/shift/AShiftAble.java | AShiftAble | aslAt/asrAt/lsrAt(int, Object) | 143, 161, 178 | `carry` parameter accepted but never read or written (author's own inline TODO). | Medium | **fixed** |
| streamIO/copy/order/Interval.java | Interval | ANDAt(Interval) | 201 | Missing `return this;` falls through into partial-containment logic. | High | **fixed** |
| streamIO/copy/monoid/integer/ASetInteger.java | ASetInteger | clear(int) | 38 | Uses XOR instead of AND-NOT to clear a bit - can set an already-0 bit to 1. | High | **fixed** |
| streamIO/copy/monoid/integer/Permutation.java | Permutation | Multi_Fact(Permutation) | 1600 | Parameter never read - always uses its own array instead. | Medium | **fixed** |
| streamIO/copy/monoid/AssociationEquivalence.java | AssociationEquivalence | equals(Object, Object) | 42 | Checks the wrong operand's type - the intended branch never fires. | Medium | **fixed** |
| streamIO/copy/boole/TesterBond.java | TesterBond | ORat(Object) | 112 | Copy-paste from `ANDat` - sets the wrong boolean result. | High | **fixed** |
| streamIO/copy/boole/TesterBond.java | TesterBond | ORat(Object) | 114-122 | All four constant-argument branches backwards for OR semantics. | Critical | **fixed** |
| streamIO/copy/boole/fuzzy/FuzzyEQV.java | FuzzyEQV | getMembership(Object) | 47 | Missing complement - returns the raw difference instead of `1 -` it. | High | **fixed** |
| streamIO/copy/ACopyAble.java | ACopyAble | toStream(IFormatOut) | 386 | Risks infinite recursion (author's own comment already notes it). | Medium | **fixed** |
| function/CatProcessor.java | CatProcessor | constructor(IProcessor, IProcessor) | ~ | Null-check reads the wrong (always-null) field instead of the constructor parameter. | Medium | **fixed** |
| function/Projections.java | Projections | Mercator(double[]) | 243 | Indexes `V[2]` on a 2-element position vector - AIOOBE. | High | **fixed** |
| function/real/Product.java | Product | getHMV() | 50 | Computes the Geometric Mean while named/documented as the Harmonic Mean. | Medium | **fixed** |
| function/string/AStringFunction.java | AStringFunction | TO_CAMEL | 89 | Reads one past the end of the string when it ends with `_`. | Medium | **fixed** |
| function/vector/OdeLorentz.java | OdeLorentz | Funktion(double, double[], double[]) | 48 | Sign of the y-term flipped - diverges from the intended Lorenz attractor. | High | **fixed** |
| function/byref/ByRefInt.java | ByRefInt | ROR(int, int) | 64 | Dropped bit shifted one position too high, outside the range `ROL` uses. | Medium | **fixed** |
| function/byref/ByRefLong.java | ByRefLong | ROR(long, int) | 259 | Same defect as `ByRefInt.ROR`. | Medium | **fixed** |
| streamIO/object/StreamParser.java | StreamParser | (array-resize helper) | 177 | Source and destination of the resize `arraycopy` are the same new empty array - old contents lost. | High | **fixed** |
| streamIO/object/Union.java | Union | OR(IStreamIn, IStreamIn) | 73 | Writes to index 3 of a length-3 array - AIOOBE on every call. | Critical | **fixed** |
| streamIO/object/backTrack/Grammar.java | GrammarState | hashCode() | 138 | Reads a field never assigned by any constructor - NPE. | High | **fixed** |
| streamIO/object/backTrack/TravelProblem.java | TravelState | equals(Object) | 486 | Compares an `int[]` field via reference identity instead of contents. | Medium | **fixed** |
| streamIO/object/filterIn/FilterInByBitMask.java | FilterInByBitMask | (position-reset helper) | 82 | `1 << _position` computed in `int` arithmetic - large positions silently wrap. | Medium | **fixed** |
| streamIO/object/filterIn/FilterIn_PushBack.java | FilterIn_PushBack | nextItemInternal() | 39 | Recurses into itself instead of delegating to the wrapped stream - infinite recursion. | Critical | **fixed** |
| streamIO/object/filterOut/ThreadOut.java | ThreadOut | addItem(Object) | 47 | Spawned Runnable calls back into this same method - recurses spawning threads forever. | Critical | **fixed** |
| streamIO/object/integer/XMLInputStream.java | XMLInputStream | fromXML() | 199 | Instantiates an arbitrary class named in untrusted XML with no allow-list. | High (security) | **fixed** |
| streamIO/object/integer/XMLInputStream.java | XMLInputStream | fromXMLField(...) | 238 | `ensureCapacity()` grows capacity but not logical size - `IndexOutOfBoundsException`. | Medium | **fixed** |
| streamIO/object/integer/XMLScanner.java | XMLScanner | (tag-type constants) | 97 | Two distinct tag-type constants share the same value. | Medium | **fixed** |
| streamIO/object/json/JSONTokener.java | JSONTokener | next(int) | 207 | Off-by-one rejects a valid EOF boundary case. | Medium | **fixed** |
| streamIO/object/json/JSONTokener.java | JSONTokener | nextValue() | 358 | Unbounded recursion on nested JSON - stack-overflow DoS on untrusted input. | Medium (security) | **fixed** |
| streamIO/copy/group/ring/metric/body/Fraction.java | Fraction | Floor() | 207 | Computes the reciprocal ratio instead of the intended one. | High | **fixed** |
| streamIO/copy/group/ring/metric/body/FractionLong.java | FractionLong | Floor() | 216 | Same swapped-operand defect, copy-pasted. | High | **fixed** |
| streamIO/copy/group/ring/metric/body/units/QuantityDouble.java | QuantityDouble | QuantityDouble(double, Unit) | 104 | `unit` parameter never assigned to `mUnit` - NPE on every unit-dependent call. | Critical | **fixed** |
| streamIO/copy/group/ring/metric/body/units/QuantityDouble.java | QuantityDouble | QuantityDouble(double, Unit, double) | 110 | Same missing `mUnit` assignment. | Critical | **fixed** |
| streamIO/object/parser/Array2Stream.java | Array2Stream | testIt() | ~143 | Calls `arr.set(...)` on a freshly constructed empty ArrayList instead of `add()`. | Low | **fixed** |
| streamIO/object/parser/FileStream2Stream.java | FileStream2Stream | getMaxMarkSize() | ~234 | Dereferences `stream` unguarded when it can be null. | Medium | **fixed** |
| streamIO/object/parser/FileStream2Stream.java | FileStream2Stream | getPosition() | ~250 | Same unguarded `stream` dereference. | Medium | **fixed** |
| streamIO/object/parser/InputStream2StreamIn.java | InputStream2StreamIn | readParameters(Map, boolean, String, String) | 528 | Unconditional cast to a specific implementation type - `ClassCastException`. | Medium | **fixed** |
| streamIO/object/parser/ParserFromStreamIn.java | ParserFromStreamIn | nextItem() | 65 | Never restores the parent Stream after descending a nesting level. | High | **fixed** |
| streamIO/object/parser/SaxReader.java | SaxReader | getFeature/setFeature/getProperty/setProperty | 107 | Features and Properties share one HashMap keyed only by name - namespace collision. | Low | **fixed** |
| streamIO/object/parser/SaxReader.java | SaxReader | parse(String) | 158 | `systemId` unused; root Element name hard-coded instead of derived. | Medium | **fixed** |
| streamIO/object/parser/StreamOutXML.java | StreamOutXML | addItems(Object[], int, int) | 966 | Dangling-else corrupts output for most Element types. | High | **fixed** |
| streamIO/object/parser/XMLStreamIn.java | XMLStreamIn | field currXMLToken | 113 | Own independent field instead of aliasing the scanner's - stuck at 0. | High | **fixed** |
| streamIO/object/parser/jdbc/CallStatementSep.java, PrepStatementSep.java | CallStatementSep, PrepStatementSep | getResultSet(File, String) | 34, 56 | Unimplemented stub - always returns null. | Medium | **fixed** |
| streamIO/object/parser/jdbc/ConnectionSep.java | ConnectionSep | prepareStatement(...) (3 overloads) | 214, 226, 236 | Extra parameter accepted but never consulted. | Low | **fixed** |
| streamIO/object/parser/jdbc/DriverSep.java | DriverSep | connect(String, Properties) | 97 | `acceptsURL` checked against the wrong string - always-true guard. | High | **fixed** |
| streamIO/object/enumer/FilterEnumerator.java | FilterEnumerator | replaceCurr(Object) | ~ | Calls itself instead of delegating - `StackOverflowError`. | High | **fixed** |
| streamIO/object/enumer/container/tree/SubTreeMap.java | SubTreeMap | both non-default constructors | 55, 68 | Field read before assignment. | High | **fixed** |
| streamIO/object/enumer/DblListItem.java | DblListItem | replaceCurr(Object) | 111 | Ignores its parameter - no state change occurs. | High | **fixed** |
| streamIO/object/enumer/ListItem.java | ListItem | getRootSimple(ILinked) | 65 | Loop condition inverted. | High | **fixed** |
| streamIO/object/enumer/container/SortedArray.java | SortedArray | setAt(), addAt(int, Object) | 549, 567 | Calls the Comparator unconditionally even when built without one - NPE. | High | **fixed** |
| streamIO/object/enumer/container/HashSet.java | HashSet | inner HashSetIterator | 626 | Every method is an unfinished stub - iteration entirely non-functional. | High | **fixed** |
| streamIO/object/enumer/AIndexEnumerator.java | AIndexEnumerator | reset(long) | 96 | Named `reset` instead of `reSet` - never actually overrides the interface method. | Medium | **fixed** |
| streamIO/object/enumer/container/RecordSet.java | RecordSet | reset(long Position) | 170 | Same `reSet`/`reset` naming-mismatch bug. | Medium | **fixed** |
| streamIO/object/enumer/container/DeQueueArr.java | DeQueueArr | isFull() | 227 | Doesn't account for ring-buffer wraparound. | Medium | **fixed** |
| streamIO/object/enumer/Iterator2Enumerator.java | Iterator2Enumerator | reSet(long) | 110 | Recurses with an identical argument instead of resetting then advancing. | High | **fixed** |
| streamIO/object/enumer/ReverseEnumerator.java | ReverseEnumerator | (near removeCurr) | 69 | Unterminated Javadoc block swallowed the next declaration (doc-corruption, fixed). | Low | **fixed** |
| streamIO/integer/encoding/BigEndianReader.java | BigEndianReader | MAX_UNSIGNED_INT / readLong() | 153, 170 | Shifts an int operand by 32 - Java reduces the shift mod 32, corrupting the high word. | High | **fixed** |
| streamIO/integer/encoding/EscapeInputFilter.java | EscapeInputFilter | constructor | 63 | Missing length guard present in the sibling output filter. | Medium | **fixed** |
| streamIO/integer/encoding/FilterASCII2Base64.java | FilterASCII2Base64 | read() | 451 | Integer division truncates the final partial group. | Medium | **fixed (misdiagnosed)** - the division is correct; the real corruption was stale trailing buffer bytes, now zeroed before encoding. |
| streamIO/integer/encoding/FilterBase64ToASCII.java | FilterBase64ToASCII | write() | 453 | Ignores the valid-byte count, always writes the full buffer. | Medium | **fixed** |
| streamIO/integer/encoding/FilterBinHex2Byte.java | FilterBinHex2Byte | read(), write() | 163, 196 | Wrong nibble conversion in both directions. | High | **fixed** |
| streamIO/integer/encoding/FilterByte2BinHex.java | FilterByte2BinHex | hexCode(char) | 79 | Wrong offset subtracted for a digit character. | High | **fixed** |
| streamIO/integer/encoding/FilterCRC16.java | FilterCRC16 | read() | 212 | Excludes a genuine NUL data byte from the CRC. | Medium | **fixed** |
| streamIO/integer/encoding/FilterCRC32.java | FilterCRC32 | read() | 210 | Same NUL-byte exclusion bug. | Medium | **fixed** |
| streamIO/integer/encoding/FilterCrypt.java | FilterCrypt | (class) | 32 | Home-grown, unreviewed XOR stream cipher presented as real encryption. | High (security) | **fixed** |
| streamIO/integer/encoding/FilterString2Char.java | FilterString2Char | read(), write() | 224, 243 | State never cleared/reset between uses. | Medium | **fixed** |
| streamIO/integer/encoding/FilterUrlDecode.java | FilterUrlDecode | write() | 119 | Checks the wrong character to enter escape-decode state. | Medium | **fixed** |
| streamIO/integer/encoding/redundancy/ConvolutionBitEncode.java | ConvolutionBitEncode | (polynomial table indexing) | 225 | Indexes the table directly by K instead of the derived index (currently unreachable). | Low | **fixed** |
| streamIO/integer/encoding/redundancy/Depeater.java | Depeater | flush() | 161 | Never writes out the buffered tail bytes. | Medium | **fixed (misdiagnosed)** - the shortened length is the protocol tail signal; the real defect was the missing `super.flush()`. |
| streamIO/integer/filter/FilterIn_Byte.java | FilterIn_Byte | (mapper input) | 310 | Signed/unsigned widening inconsistency. | Medium | **fixed** |
| streamIO/integer/filter/FilterReplaceSection.java | FilterReplaceSection | main(String[]) | 429 | Missing return after the usage message - AIOOBE follows. | Medium | **fixed** |
| streamIO/integer/filter/FilterSplitAtFind.java | FilterSplitAtFind | main(String[]) | 259 | Same missing-return-after-usage-message pattern. | Medium | **fixed** |
| streamIO/integer/filter/FilterSplitAtFind.java | FilterSplitAtFind | (breakCountDown) | 201 | Defaults to 0 and fires immediately before being armed. | High | **fixed** |
| streamIO/integer/filter/LimitedSizeInputStream.java | LimitedSizeInputStream | skip(long) | 128 | Calls itself instead of delegating - infinite self-recursion. | High | **fixed** |
| streamIO/integer/filter/LimitedSizeInputStream.java | LimitedSizeInputStream | read() | 140 | Off-by-one, stops one byte short. | Medium | **fixed** |
| streamIO/integer/random/AStreamIn_BoundInt.java | AStreamIn_BoundInt | nextLong(long) | 57 | Silently truncates any bound larger than `Integer.MAX_VALUE`. | High | **fixed** |
| streamIO/integer/random/BitNoise.java | BitNoise | Map(long) vs Map(int) | 58 | Inconsistent bit-consumption cadence between the two overloads. | Medium | **fixed** |
| streamIO/integer/random/RandomBit2.java | RandomBit2 | getPosition() | 56 | Returns the last bit instead of the full shift-register state - breaks mark/reSet replay. | High | **fixed** |
| streamIO/integer/random/RandomMix.java | RandomMix | reset(long) | 120 | Unconditionally throws instead of setting the seed/state. | Medium | **fixed** |
| streamIO/integer/AStreamOutStruct.java | AStreamOutStruct | open_Struct(String, Object) | 247 | Key/value reversed vs. the lookup above it - back-references never found, infinite recursion on circular graphs. | High | **fixed** |
| streamIO/integer/StreamIn_Struct.java | StreamIn_Struct | nextString(), nextStrings(int, int), nextItems(int, int) | 265, 776, 833 | Compares against the wrong EOF sentinel - end-of-input never detected. | High | **fixed** |
| streamIO/integer/StreamOutInstantiator.java | StreamOutInstantiator | addShorts/addLongs/addFloats/addDoubles/addItems/addStrings(Array, int, int) | 503, 559, 586, 613, 640, 667 | Each sizes/copies off the wrong field - corrupts all six accumulator arrays. | Critical | **fixed** |
| streamIO/integer/StreamOutInstantiator.java | StreamOutInstantiator | peekDouble() | 458 | Calls the wrong sibling method (`curr` instead of `peek`). | Medium | **fixed** |
| streamIO/integer/StreamIn_Primitive.java | StreamIn_Primitive | nextEnum(String[]) | 154 | Int shift before widening to long - breaks for 32-63 name enums. | Medium | **fixed** |
| streamIO/integer/adapter/ArrayStreamIn_Int.java | ArrayStreamIn_Int | nextLongInternal() | 124 | Delegation chain loops back into itself - `StackOverflowError`. | Critical | **fixed** |
| streamIO/integer/adapter/ReaderToStreamIn_Byte.java | ReaderToStreamIn_Byte | read(int[], int, int) | 128 | Narrows to `byte` before storing into an `int[]` - corrupts non-ASCII code points. | Medium | **fixed** |
| streamIO/integer/adapter/WriterToStreamOutByte.java | WriterToStreamOutByte | write(char[], int, int) | 70 | Ignores `off`/`len`, always writes the whole array. | Medium | **fixed** |
| streamIO/integer/AStreamIn_Char.java, streamIO/integer/StreamOutStructCollection.java | AStreamIn_Char, StreamOutStructCollection | (whole class) | 36, 35 | Unimplemented IDE stubs presented as real implementations; the latter also discards its constructor parameter. | High | **fixed** |
| streamIO/integer/multiplex/DeMultiplexerIn_Raid5.java | DeMultiplexerIn_Raid5 | read() | 160 | Error message logs a stale value instead of the one actually checked. | Low | **fixed** |
| streamIO/integer/pipe/APipeByte.java | APipeByte | main() | 177 | Calls a method overload that doesn't exist - does not compile as written. | High | **fixed** |
| streamIO/integer/pipe/MemoryPipe.java | MemoryPipe | (constructor) | 83 | Copy-paste field mixup, currently benign. | Low | **fixed** |
| streamIO/integer/file/FilterCrLfFromQuoted.java | FilterCrLfFromQuoted | main() | 34 | Streams never closed in a `finally`. | Low | **fixed** |
| streamIO/integer/file/FileStreamIn_Byte.java | FileStreamIn_Byte | read(byte[], int, int)-family default | 500 | Narrows to `char` before storing into an `int[]` - corrupts the EOF sentinel on write. | Medium | **fixed** |
| streamIO/integer/jdbc/AConnection.java | AConnection | getWarnings() | 363 | Off-by-one array access - always throws. | High | **fixed** |
| streamIO/integer/jdbc/AConnection.java | AConnection | close() | 261 | Never closes open ResultSets/Statements (author's own pre-existing TODO). | Medium | **fixed** |
| streamIO/integer/jdbc/ADBMetaData.java | ADBMetaData | getTables(...) | 236 | Accepted filter parameters never applied. | Medium | **fixed** |
| streamIO/integer/jdbc/AResultSet.java | AResultSet | fillFlags() | 1251 | Off-by-one loop leaves the first flag unset. | Medium | **fixed** |
| streamIO/integer/jdbc/AStatement.java | AStatement | getResultSet(String) | 404 | Unsanitized table name built into a File path - path traversal. | High (security) | **fixed** |
| streamIO/integer/jdbc/AStatement.java | AStatement | close() | 1000 | Unconditional call on a possibly-null field - NPE. | Medium | **fixed** |
| streamIO/integer/jdbc/AStatement.java | AStatement | getResultSetConcurrency()/getResultSetType()/getResultSetHoldability() | 1058, 1071, 1086 | Each ignores its own instance field, returns the class default instead. | Medium | **fixed** |
| streamIO/integer/jdbc/AStatement.java | AStatement | (analyzeConditions helper) | 846 | Unreachable dead branch. | Low | **fixed** |
| streamIO/integer/jdbc/RSMetaData.java | RSMetaData | (every column-indexed method) | 7 | Assumes 0-based indexing against the 1-based `java.sql.ResultSetMetaData` contract - systemic mismatch. | High | **deferred by design** - the whole family (`AResultSet.findColumn`, `FilterResultSet`, `ResultSetArray`) is 0-based; making this one class 1-based would leave it the odd one out. The 0-based contract is now documented and every accessor bounds-checked. Converting the family to JDBC-standard 1-based indexing is a separate change. |
| streamIO/integer/jdbc/DriverFix.java | DriverFix | connect(String, Properties) | 106 | `acceptsURL` checked against the wrong constant - always-true guard. | High | **fixed** |
| streamIO/integer/jdbc/CallStatementFix.java | CallStatementFix | (whole class) | 43 | Entirely non-functional - its own factory is an unimplemented stub. | High | **fixed** |
| streamIO/integer/jdbc/PrepStatementFix.java | PrepStatementFix | (whole class) | 46 | Same non-functional pattern as `CallStatementFix`. | High | **fixed** |
| streamIO/integer/jdbc/EqualCondition.java | EqualCondition | equals(ResultSet, ResultSet) | 66 | No null check before string comparison - NPE on a SQL NULL value. | Medium | **fixed** |
| streamIO/integer/jdbc/ResultSetCrossJoin.java | ResultSetCrossJoin | relative(int) | 165 | Unconditionally returns false - effectively a no-op stub. | Medium | **fixed** |
| streamIO/integer/jdbc/ResultSetArray.java | ResultSetArray | isAfterLast() (and relative()'s clamp) | 251 | Cursor position clamped so it can never be observed past the last row. | Medium | **fixed** |
| streamIO/integer/jdbc/dbTest/DbTestLess.java | DbTestLess | newInstance(DbColumn, DbColumn) | ~31 | Returns a plain `DbTestEquals` instead of `DbTestLess` - silently downgrades semantics. | Medium | **fixed** |
| streamIO/integer/jdbc/dbTest/DbTestOuter.java | DbTestOuter | newInstance(DbColumn, DbColumn) | ~32 | Same defect as `DbTestLess.newInstance()`. | Medium | **fixed** |
| streamIO/integer/jdbc/dbTest/DbTestLess.java | DbTestLess | test() | ~45 | No null check before `compareTo()` - NPE on a SQL NULL value. | Medium | **fixed** |
