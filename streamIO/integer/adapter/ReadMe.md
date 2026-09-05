---
digest:
  local-classes:
    ArrayStreamIn_Int:
      mtime: '2026-09-05T21:46:07Z'
      digest: 323c58b361aae4ac9b43f006ac4ac17a625baf832852b889fcea3396c9dffde7
    InputStreamToStreamIn_Byte:
      mtime: '2026-09-05T21:46:20Z'
      digest: a405b5cd402b1d951fd5600d0676296aaa34cbee332e31d82ea31d28755a0548
    OutputStreamToStreamOutByte:
      mtime: '2026-09-05T21:46:51Z'
      digest: 47b31f9716f579432cc0db0e75cd74f0360620b1371d2755db4efefb7bbf75e8
    ReaderToStreamIn_Byte:
      mtime: '2026-09-05T21:47:34Z'
      digest: 12da2255e7250e7b21dc7d362769e6934e8502b6a70d9d3941ea189dcbeec2da
    StreamIn_ByteToInputStream:
      mtime: '2026-09-05T21:48:02Z'
      digest: 9d561a6b1be5cd67abd8addec23f6bae7a8dd4a9470f984166f0fb9d956e3317
    WriterToStreamOutByte:
      mtime: '2026-09-05T21:48:49Z'
      digest: e2b9cb336c5c9b4985d57da6b6e8517dc4b8e6153386fcf07dd9472f60c5fb02
  folders: {}
tags:
- code/stream_adapter
- code/stream_bridging
- code/stream_wrapper
concepts:
- Bridges streamIO Interfaces to java.io and Arrays
facets:
  layer: utility
  status: legacy
  complexity: high
description: 'This folder adapts the `streamIO` family of interfaces to and from standard Java I/O and in-memory Arrays: `InputStreamToStreamIn_Byte`/`OutputStreamToStreamOutByte` and `ReaderToStreamIn_Byte`/ `WriterToStreamOutByte` wrap a `java.io.InputStream`/`OutputStream` or `Reader`/`Writer` so it can be used wherever an `IStreamIn_Byte`/`IStreamOutByte` is expected, `StreamIn_ByteToInputStream` goes the other way (wrapping an `IStreamIn_Byte` as a plain `InputStream`), and `ArrayStreamIn_Int` exposes a plain `int[]`/`long[]` Array through the `IStreamIn_Int` Interface. These adapters trade some performance for flexibility, since a purpose-built subclass of the abstract `AStream*` base classes is usually faster than wrapping an existing stream.'
---

# adapter

This folder adapts the `streamIO` family of interfaces to and from standard Java I/O and in-memory
Arrays: `InputStreamToStreamIn_Byte`/`OutputStreamToStreamOutByte` and `ReaderToStreamIn_Byte`/
`WriterToStreamOutByte` wrap a `java.io.InputStream`/`OutputStream` or `Reader`/`Writer` so it can
be used wherever an `IStreamIn_Byte`/`IStreamOutByte` is expected, `StreamIn_ByteToInputStream` goes
the other way (wrapping an `IStreamIn_Byte` as a plain `InputStream`), and `ArrayStreamIn_Int`
exposes a plain `int[]`/`long[]` Array through the `IStreamIn_Int` Interface. These adapters trade
some performance for flexibility, since a purpose-built subclass of the abstract `AStream*` base
classes is usually faster than wrapping an existing stream.

## Classes

| Class | Responsibility |
|---|---|
| [ArrayStreamIn_Int](ArrayStreamIn_Int.java) | Title: ArrayStreamIn_Int Description: Purpose: Provides the IStreamIn_Int Interface for int[] Arrays. |
| [InputStreamToStreamIn_Byte](InputStreamToStreamIn_Byte.java) | Title: enclosing_type Description: A faster Alternative is to derive from the given InputStream directly,<br/>since both Interfaces have the same Signatures. |
| [OutputStreamToStreamOutByte](OutputStreamToStreamOutByte.java) | Title: enclosing_type Description: Purpose: Purpose / Responsibilities of this Class Design Decisions /<br/>Implementation Details: If similar Classes exist (e.g. Polymorphism), characterize the specific Differences to<br/>compare these. |
| [ReaderToStreamIn_Byte](ReaderToStreamIn_Byte.java) | Title: ReaderToStreamIn_Byte Description: Adapter that wraps any Writer Interface into an IStreamIn_Byte<br/>Interface Design Decisions / Implementation Details: Bytes are created simply by truncating the Words. |
| [StreamIn_ByteToInputStream](StreamIn_ByteToInputStream.java) | Title: StreamIn_ByteToInputStream Description: Purpose: Purpose / Responsibilities of this Class Design<br/>Decisions / Implementation Details: If similar Classes exist (e.g. Polymorphism), characterize the specific<br/>Differences to compare these. |
| [WriterToStreamOutByte](WriterToStreamOutByte.java) | Title: WriterToStreamOutByte Description: Adapter that wraps any Writer Interface into an IStreamOutByte<br/>Interface Design Decisions / Implementation Details: Bytes are created simply by truncating the Words. |
