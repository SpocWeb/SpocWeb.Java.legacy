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
  complexity: 2
description: 'A single watchdog: given a thread that is already running, interrupt it once its time is up.'
dv_has_:
  sub_:
    folders: 0
    files: 6
    units: 3
    facet_:
      layer_:
        infrastructure: 1
        test: 1
      status_:
        experimental: 1
        stable: 1
      complexity_:
        "2": 1
        "3": 1
    tag_:
      code_:
        thread_interruption: 1
        watchdog_thread: 1
        timeout_handling: 1
    concept_:
      concurrency: 2
      testing: 1
has_sub_folders: 0
has_sub_files: 6
has_sub_units: 3
has_sub_facet_layer_infrastructure: 1
has_sub_facet_layer_test: 1
has_sub_facet_status_experimental: 1
has_sub_facet_status_stable: 1
has_sub_facet_complexity_2: 1
has_sub_facet_complexity_3: 1
has_sub_tag_code_thread_interruption: 1
has_sub_tag_code_watchdog_thread: 1
has_sub_tag_code_timeout_handling: 1
has_sub_concept_concurrency: 2
has_sub_concept_testing: 1
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
