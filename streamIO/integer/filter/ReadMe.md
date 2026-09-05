---
digest:
  local-classes:
    APlugAbleFilterByte:
      mtime: '2026-09-05T21:42:18Z'
      digest: 526556ce15e6cad11561cb8cc8c6a2e8044708edff2b89908aaf6eb37cc41692
    FilterBuffer:
      mtime: '2026-09-05T21:43:10Z'
      digest: e7e064d86e76db0eecd85789b226689249869f94cccc2f22af3ed28a2d475a34
    FilterByte:
      mtime: '2026-09-05T21:43:40Z'
      digest: f5296f0680b88413e9ed7b56479996f17feaef5e2d7adeecf9a15458629f663f
    FilterIn_Byte:
      mtime: '2026-09-05T21:44:23Z'
      digest: 3707c3143dd44249ef5d719ec6aefee0e04ef1b20f923fc520de549f724204a3
    FilterIn_BytePushBack:
      mtime: '2026-09-05T21:45:00Z'
      digest: 87288a792d46e7fcff6f209553c1c5dad7ce6a7f00c0af6dd9aecafc3bd25f03
    FilterIn_Int2Object:
      mtime: '2026-09-05T21:45:12Z'
      digest: a49e473fe406481549999bc8f5f52b6574ca38163306f449d0314504a75f5193
    FilterOutByte:
      mtime: '2026-09-05T21:45:34Z'
      digest: 270ee960a4911e868692f8afa81d2b37884d1da0767b7afc5ffbd659aa43b50f
    FilterReplaceSection:
      mtime: '2026-09-05T21:46:15Z'
      digest: 9e1036ce698dcd4172d097862e34517c85f2a76907fe9f7d7d167a126f1f7280
    FilterSplitAtFind:
      mtime: '2026-09-05T21:46:57Z'
      digest: e6fc6afcfdc79b1f6831cafc155a6a3caadc34009d8b9586ac69c454138fd48d
    IPlugAbleFilterByte:
      mtime: '2026-09-05T21:42:26Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    IPlugAbleFilterIn_Byte:
      mtime: '2026-09-05T21:42:30Z'
      digest: 7b23717e6f6e3cbbc50ce079a3aad28278c82a658b0e7b49d466d064525e8530
    IPlugAbleFilterOutByte:
      mtime: '2026-09-05T21:42:34Z'
      digest: 4e98d386c98c46ee6ee7f11c2abb3358ed119ca97e2b153d1170fac89eb1bcbf
    LimitedSizeInputStream:
      mtime: '2026-09-05T21:49:17Z'
      digest: 540f94e3bd7e95508214e33e0e519f8e8ef53052a93575969a76bc891450bff7
    LimitedSizeOutputStream:
      mtime: '2026-09-05T10:13:25Z'
      digest: 20e293be6698a3ba4592203413c1270c428cc082712a1f47e90cb2d7690e2018
  folders:
    stats/:
      mtime: '2026-09-05T21:48:44Z'
      digest: cc2d1f25766e8451f3414d53c15ceff017aeb1408c4887f4bf722d4aea305db4
tags:
- code/stream_filter
concepts:
- Pluggable Byte-Stream Filter Infrastructure and java.io Adapters
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Byte-stream plumbing infrastructure: pluggable filter base classes (`APlugAbleFilterByte` and the `IPlugAbleFilter*` interfaces) whose upstream/downstream `InputStream`/`OutputStream` can be swapped at runtime; delegation adapters (`FilterByte`, `FilterIn_Byte`, `FilterOutByte`, `FilterIn_Int2Object`) that bridge the `java.io` classes to this codebase''s own `IStreamIn_Byte`/`IStreamOutByte` interfaces; a growable async buffer/queue (`FilterBuffer`); a push-back wrapper (`FilterIn_BytePushBack`); size-limiting wrappers (`LimitedSizeInputStream`/`LimitedSizeOutputStream`); and two special-purpose text filters, `FilterReplaceSection` (cuts out a delimited section of a stream) and `FilterSplitAtFind` (truncates a stream once a marker string recurs too often). The `stats` subfolder adds stream-consuming byte/digraph/trigraph frequency counters. Several files carry known bugs flagged inline with `TODO: LOGIC`, including a missing-`return`-after-usage-message crash in `FilterReplaceSection`/`FilterSplitAtFind`''s `main()`, an uninitialized countdown in `FilterSplitAtFind`, and a self-recursive `skip()`/off-by-one `read()` in `LimitedSizeInputStream`.'
---

# filter

Byte-stream plumbing infrastructure: pluggable filter base classes (`APlugAbleFilterByte` and
the `IPlugAbleFilter*` interfaces) whose upstream/downstream `InputStream`/`OutputStream` can be
swapped at runtime; delegation adapters (`FilterByte`, `FilterIn_Byte`, `FilterOutByte`,
`FilterIn_Int2Object`) that bridge the `java.io` classes to this codebase's own
`IStreamIn_Byte`/`IStreamOutByte` interfaces; a growable async buffer/queue (`FilterBuffer`); a
push-back wrapper (`FilterIn_BytePushBack`); size-limiting wrappers
(`LimitedSizeInputStream`/`LimitedSizeOutputStream`); and two special-purpose text filters,
`FilterReplaceSection` (cuts out a delimited section of a stream) and `FilterSplitAtFind`
(truncates a stream once a marker string recurs too often). The `stats` subfolder adds
stream-consuming byte/digraph/trigraph frequency counters. Several files carry known bugs
flagged inline with `TODO: LOGIC`, including a missing-`return`-after-usage-message crash in
`FilterReplaceSection`/`FilterSplitAtFind`'s `main()`, an uninitialized countdown in
`FilterSplitAtFind`, and a self-recursive `skip()`/off-by-one `read()` in
`LimitedSizeInputStream`.

## Classes

| Class | Responsibility |
|---|---|
| [APlugAbleFilterByte](APlugAbleFilterByte.java) | Implements a pluggable / configurable Filter for Object Streams. |
| [FilterBuffer](FilterBuffer.java) | Implements a dynamically growing asynchronous Buffer / Cache / Queue between an InputStream and an OutputStream. |
| [FilterByte](FilterByte.java) | Title: FilterByte Description: Provides a Default Delegation of an Input streamIO Filter Additional Value is<br/>provided by converting from the Class OutputStream to the Interface IStreamOutByte. |
| [FilterIn_Byte](FilterIn_Byte.java) | Title: FilterIn_Byte Description: Provides a Default Delegation of an Input streamIO Filter Additional Value<br/>is provided by converting from the Class InputStream to the Interface IStreamIn_Byte. |
| [FilterIn_BytePushBack](FilterIn_BytePushBack.java) | Adds the PushBack Functionality to any Byte Stream. |
| [FilterIn_Int2Object](FilterIn_Int2Object.java) | Title: FilterIn_Char2Object Description: Bridges the IStreamIn_Int Interface to the StreamIn Interface. |
| [FilterOutByte](FilterOutByte.java) | Title: FilterOutByte Description: Provides a Default Delegation of an Input streamIO Filter Additional Value<br/>is provided by converting from the Class OutputStream to the Interface IStreamOutByte. |
| [FilterReplaceSection](FilterReplaceSection.java) | Filters a streamIO and cuts out a Section between the beginning and Ending String. |
| [FilterSplitAtFind](FilterSplitAtFind.java) | Filters a streamIO and ends it (-1) as soon as a certain String is found more often than the specified Number<br/>Design Decisions / Implementation Details: The Counter is integrated into this Class to save coupling Streams<br/>at different Levels. |
| [IPlugAbleFilterByte](IPlugAbleFilterByte.java) | A Filter whose Input and Output Streams can be replaced during Runtime. |
| [IPlugAbleFilterIn_Byte](IPlugAbleFilterIn_Byte.java) | A Filter whose Input Stream can be replaced during Runtime. |
| [IPlugAbleFilterOutByte](IPlugAbleFilterOutByte.java) | A Filter whose Output Stream can be replaced during Runtime. |
| [LimitedSizeInputStream](LimitedSizeInputStream.java) | LimitedSizeInputStream limits the Number of Bytes to be read from a streamIO. |
| [LimitedSizeOutputStream](LimitedSizeOutputStream.java) | LimitedSizeOutputStream Maps a simple (unlimited) Output streamIO to an Enumeration Output Streams with limited Size. |
