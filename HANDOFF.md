# HANDOFF — Java.ReadMeGenerator documentation run

## Status

Pilot folder `tools/` only. The rest of the tree (1,455 `.java` files, 136 folders) is
untouched and is its own multi-session effort.

| Scope | Pass 1+2 | Pass 3 | Notes |
|---|---|---|---|
| `tools/mementos/` | done | done | 2 interfaces |
| `tools/threads/` | done | done | 1 class, 2 defects flagged |
| `tools/` (root of folder) | in progress | generated, narrative pending | 16 types across 13 files |
| everything else | not started | not started | see Claims below |

Tooling: `D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/target/readmegenerator.jar`,
built with the colocated Maven Wrapper. Milestone A commands only — there is no `scaffold`,
no tags pipeline and no `list-dependencies` yet.

## Claims

One folder at a time, deepest first. Claim a row before starting, push the claim
immediately, and commit at each folder boundary.

| Folder | Files | Claimed by | Status |
|---|--:|---|---|
| `tools/` | 16 | main | in progress |
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

## Next Action

Finish Pass 1+2 for the 13 remaining files directly in `tools/`, re-run
`update-readme tools --recurse --subsystems`, then write the `## Architecture`,
`## Entry Points` and opening narrative for `tools/ReadMe.md` and its two sub-folders.
Then decide with the user whether to continue into `streamIO/` or build Milestone B first.
