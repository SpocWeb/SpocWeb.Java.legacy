---
digest:
  local-classes:
    APipeByte:
      mtime: '2026-09-05T21:55:13Z'
      digest: 45d78d34fd72f5eeada33a57ba0d68d64d46cd3264c70b590a7c15e0657027e7
    ByteStreamerThread:
      mtime: '2026-09-05T10:13:31Z'
      digest: f919234c0911a0628082f3e0faefa31e45e4fba78ec93f06b0e6296352c57819
    MemoryPipe:
      mtime: '2026-09-05T21:55:58Z'
      digest: 0f3d18e3031d0d701499afae6967b67efe1d92037537ea5e1a8246b308e3a93a
    MonitorByte:
      mtime: '2026-09-05T21:56:33Z'
      digest: f68ba096ab7ba34801c4057733db1a4c6959d6abc22bb5ffd0aa922a46454e4b
    PipeByte:
      mtime: '2026-09-05T21:57:38Z'
      digest: 5fc9a956e8101df1a593f3b06b7c29eee03d79bf065e7a3df5756df4d365ffce
  folders: {}
tags:
- code/pipe_abstraction
- code/pipe_implementation
concepts:
- In-Memory Producer-Consumer Byte Pipes
facets:
  layer: utility
  status: legacy
  complexity: high
description: This folder provides in-memory, producer/consumer-style Byte streams for passing Data between Threads without touching a File or Socket. `PipeByte`/`MemoryPipe` are Array-backed Stack-or-Queue buffers (a fast, unsynchronized DeQueue of int Values), `MonitorByte` is the synchronized, single-slot variant used for tightly-coupled handoff between exactly two Threads (with configurable read/write timeouts), and `ByteStreamerThread` is a small helper Thread that continuously copies one Stream into another until EOF - used together with `MonitorByte`/`PipeByte` to build the producer and consumer sides of a pipe. `APipeByte` is the shared abstract base class these implementations extend.
---

# pipe

This folder provides in-memory, producer/consumer-style Byte streams for passing Data between
Threads without touching a File or Socket. `PipeByte`/`MemoryPipe` are Array-backed Stack-or-Queue
buffers (a fast, unsynchronized DeQueue of int Values), `MonitorByte` is the synchronized,
single-slot variant used for tightly-coupled handoff between exactly two Threads (with configurable
read/write timeouts), and `ByteStreamerThread` is a small helper Thread that continuously copies
one Stream into another until EOF - used together with `MonitorByte`/`PipeByte` to build the
producer and consumer sides of a pipe. `APipeByte` is the shared abstract base class these
implementations extend.

## Classes

| Class | Responsibility |
|---|---|
| [APipeByte](APipeByte.java) | Title: APipeByte Description: Purpose: abstract Base Class for a Pipe processing Bytes and Integers Design<br/>Decisions / Implementation Details: If similar Classes exist (e.g. Polymorphism), characterize the specific<br/>Differences to compare these. |
| [ByteStreamerThread](ByteStreamerThread.java) | ByteStreamThread is a Thread which asynchronously copies it's input to it's output and terminates when the<br/>Input streamIO is empty (EOF = -1). |
| [MemoryPipe](MemoryPipe.java) | Memory (dynamic Array)- backed Stream, which can work in FIFo and LIFO Mode and can synchronize read/write<br/>Access between Threads. |
| [MonitorByte](MonitorByte.java) | This Class allows undisturbed streamIO communication between two Threads using a pipe-like Mechanism of<br/>writing and reading individual Chars to an unbuffered Object. |
| [PipeByte](PipeByte.java) | Implementation of a fast DeQueue for int Values using an Array in Memory. |
