---
digest:
  local-classes:
    TimeOuter:
      mtime: '2026-09-04T16:35:47Z'
      digest: c9ab89ab14f263ad026115f71f670bd8f16294f7e6962e48e6820d132ff1113a
    TimeOuterTest:
      mtime: '2026-09-05T08:50:35Z'
      digest: 10449a95044a6b959b8becc2850a1ae38ff3f0d4c06f0937bfe2f3414b534136
  folders: {}
tags:
- code/watchdog_thread
- code/timeout_handling
concepts:
- Concurrency
facets:
  layer: infrastructure
  status: experimental
  complexity: low
description: 'A single watchdog: given a thread that is already running, interrupt it once its time is up.'
---

# threads

A single watchdog: given a thread that is already running, interrupt it once its time is up.

The design point is that the watchdog is external to the work.
A thread cannot put itself to sleep and simultaneously police its own deadline, so
`TimeOuter` spawns a second thread whose only job is to wait out the timeout and interrupt
the first. The interrupted thread sees an `InterruptedException` at whatever blocking call
it happens to be in, which is why the caller must be prepared to catch one.

Two defects found while documenting this folder have since been fixed under test. The
watchdog used to interrupt repeatedly rather than once, contradicting its own contract and
forcing callers to clear `doInterrupt` by hand, and it started its monitoring thread from
inside the constructor, handing that thread a half-built object. Construction and starting
are now separate: `TimeOuter.monitor(thread, millis)` builds the watchdog and only then
starts it, and the fields that thread reads are final.

`TimeOuterTest` pins both. The interrupt count is directly observable, so that half is
tested for what it does; unsafe publication is a race and cannot be reproduced on demand,
so those checks pin the structural properties whose absence made the race possible - no
constructor starts the thread, and the state it reads is final - rather than pretending to
observe the race.

```
javac -d out tools/threads/*.java
java -cp "out;." tools.threads.TimeOuterTest
```

## Classes

| Class | Responsibility |
|---|---|
| [TimeOuter](TimeOuter.java) | Interrupts another, already running Thread once its Timeout has elapsed. |
| [TimeOuterTest](TimeOuterTest.java) | Regression tests for the two concurrency defects found in TimeOuter. |

## Entry Points

| Class.Method | Description |
|---|---|
| [TimeOuter.stop()](TimeOuter.java#L97) | Ends the watchdog immediately, so it stops interrupting and its thread dies. |
| [TimeOuter.testIt()](TimeOuter.java#L108) | Runnable demonstration: times out the calling thread and catches the interruption. |
