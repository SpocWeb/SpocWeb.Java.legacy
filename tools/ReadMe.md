---
digest:
  local-classes:
    CallAble:
      mtime: '2026-09-04T16:35:46Z'
      digest: 29ce792e0f170f4a5861918f3f9a3f6abe30ac207369d0519646c395d5402ca4
    ErrorHandler:
      mtime: '2026-09-04T16:35:47Z'
      digest: 7af34e996ad6bb0bc270013229b5d1930feef37507dbd1b74c1986a48a9caf9d
    FilterCallTransAction:
      mtime: '2026-09-04T16:35:47Z'
      digest: ef8fa9819a8b11f24e5bb6304a3ebee0f62e782b84d9c7d626a15495e8d3866e
    IOError:
      mtime: '2026-09-04T16:35:47Z'
      digest: cdd8920622f80bd498f3d6b75356da24efc0ad3db860089b745d4d892fa01a68
    LockAble:
      mtime: '2026-09-04T16:35:47Z'
      digest: c961b477dbfa4cb63fd91dc4800eee03084f8df2a06818e2382e1a0bcdedd7bd
    LockImproved:
      mtime: '2026-09-04T16:35:47Z'
      digest: bef9aceb6f360b015bf24d1bb628e4bd52f8537c8b79a60a80035efe5ed83fee
    LockManager:
      mtime: '2026-09-04T16:35:47Z'
      digest: d95f5e83196d915be5861ca3b2ebeec6cf190218163e3f9861822ace71a04c9a
    LockTester:
      mtime: '2026-09-04T16:35:47Z'
      digest: 05b55d35d6baa1d7f03e96a7151c3cc608124f216be1a49c2e3d8f4ebc95f7d3
    LockedServer:
      mtime: '2026-09-04T16:35:47Z'
      digest: d1c10d0889c343247cff73da04a0f517de7e74d6021f0c164618030144c4fbcc
    LockedSimple:
      mtime: '2026-09-04T16:35:47Z'
      digest: b087577b23536c6a3e242fe9216dfe5576f91ea0437a27340287b6aa8e74d4d4
    Monitor:
      mtime: '2026-09-04T16:35:47Z'
      digest: baf135b4448d65fb02fbde8d2d307ac937af6f59162de3cc8447bd996d337047
    Parsing:
      mtime: '2026-09-04T16:35:47Z'
      digest: 6801afe74ea54865d538f792a9ae41f9daf1d54cd95b12b7ae9e93b1bf39fd1c
    ThreadLock:
      mtime: '2026-09-04T16:35:47Z'
      digest: 315276aff7ef4bdf961874d499b22e694647fbd899358180a05ffefc40dc726c
    ThreadLockTester:
      mtime: '2026-09-04T16:35:47Z'
      digest: 05b55d35d6baa1d7f03e96a7151c3cc608124f216be1a49c2e3d8f4ebc95f7d3
    TransactFTP:
      mtime: '2026-09-04T16:35:47Z'
      digest: 2e4477d4ace538c947c36ad08ec404b1774c4fefde988f2d7202d53b0aac851f
    WorkerThread:
      mtime: '2026-09-04T16:35:47Z'
      digest: 53420e758d9bb4e45a3481524aafa0ee2b948dd3cd9cf7b3cc9685d9940a3fbe
  folders:
    mementos/:
      mtime: '2026-09-04T16:35:47Z'
      digest: 5d73a0d682aaddd934cee0ff65b67d422f31843f8ba0318029a83671988c1ffb
    threads/:
      mtime: '2026-09-04T16:35:47Z'
      digest: d05f2e650fb997b10a686f008bd02ebb5ff01f8324a2e1987e04db7388b2195e
---

# tools

TODO: LLM — replace this mechanical placeholder with a domain narrative.
The `tools` folder groups 16 types, including `CallAble`, `ErrorHandler` and `FilterCallTransAction`.

## Classes

| Class | Responsibility |
|---|---|
| [CallAble](CallAble.java) | callAble Name adopted from the Book "Concurrent Java Programming 2nd Ed" by Doug Lea Created on 7. Januar 2001, 18:10 |
| [ErrorHandler](ErrorHandler.java) | ErrorHandler.java Description: This Class demonstrates Error Handling by encapsulating a Function Call and... |
| [FilterCallTransAction](FilterCallTransAction.java) | TransAction Wraps a Callable Object with Transaction Methods as well as Before and After Operations (e.g. for<br/>(un-)Locking). |
| [IOError](IOError.java) | Title: IOError.java Description: Designed to wrap an IOException so that it needn't be declared. |
| [LockAble](LockAble.java) | Title: LockAble.java Description: Defines the Interface to be used to maintain Locking of ModifyAble Components. |
| [LockImproved](LockImproved.java) | Title: LockImproved Description: TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation. |
| [LockManager](LockManager.java) | Title: LockManager Description: Extends the ThreadLock Class by Methods to lock this Manager Object itself. |
| [LockTester](LockedSimple.java) | Helper Class for testing Class ThreadLock Opened up in its own Thread to demonstrate concurrent Access. |
| [LockedServer](LockedServer.java) | Title: LockedServer Description: Example Base Class that uses the LockManager and implements the LockAble Interface. |
| [LockedSimple](LockedSimple.java) | Title: LockedSimple Description: Example for a simple Strategy for synchronized Access: asynchronous non-blocking Locks. |
| [Monitor](ThreadLock.java) | Helper Class for the ThreadLock Class Provides a modifyable Wrapper for public primitive int Values. |
| [Parsing](Parsing.java) | Title: Parsing Description: This Class is slightly obsolete, because all this is implemented in the streamIO<br/>Classes Scanner, StreamParser and StreamIterator(old) in a more elegant and consistent Manner, but it is still<br/>being used. |
| [ThreadLock](ThreadLock.java) | Title: ThreadLock Description: Manages the exclusive, blocking (Write-) Locking on an arbitrary (not<br/>predefined) Set of Objects or int Values. |
| [ThreadLockTester](ThreadLock.java) | Helper Class for testing Class ThreadLock Opened up in its own Thread to demonstrate concurrent Access. |
| [TransactFTP](TransactFTP.java) | Title: TransactFTP Description: Purpose: Transactional File Transfer Protocol This Class describes and<br/>encapsulates the different States of a File Transfer in progress. |
| [WorkerThread](WorkerThread.java) | Title: WorkerThread Description: Purpose: Thread to be safely started and also killed (to prevent Processor<br/>starving!) Used in MultiValidator and MultiCaster! It takes its non final Parameters as an Object[]<br/>Constructor Parameter and uses the same Array to return its Results. |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `mementos/` | TODO: LLM | `Memento` |
| `threads/` | TODO: LLM | `TimeOuter` |
