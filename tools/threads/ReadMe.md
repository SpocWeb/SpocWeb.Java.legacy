---
digest:
  local-classes:
    TimeOuter:
      mtime: '2026-09-04T16:35:47Z'
      digest: 662207cbd8bb355598443b3878c17635b3ad73e6a6cafc68254d6def88b4d01e
  folders: {}
---

# threads

A single watchdog: given a thread that is already running, interrupt it once its time is up.

The design point is that the watchdog is external to the work.
A thread cannot put itself to sleep and simultaneously police its own deadline, so
`TimeOuter` spawns a second thread whose only job is to wait out the timeout and interrupt
the first. The interrupted thread sees an `InterruptedException` at whatever blocking call
it happens to be in, which is why the caller must be prepared to catch one.

Note that the implementation currently interrupts repeatedly rather than once, and
publishes `this` to its own thread from inside the constructor; both are recorded in the
repository's `HANDOFF.md` bug table rather than fixed here.

## Classes

| Class | Responsibility |
|---|---|
| [TimeOuter](TimeOuter.java) | Interrupts another, already running Thread once its Timeout has elapsed. |

## Entry Points

| Class.Method | Description |
|---|---|
| [TimeOuter.stop()](TimeOuter.java#L97) | Ends the watchdog immediately, so it stops interrupting and its thread dies. |
| [TimeOuter.testIt()](TimeOuter.java#L108) | Runnable demonstration: times out the calling thread and catches the interruption. |
