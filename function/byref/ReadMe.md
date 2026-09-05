---
digest:
  local-classes:
    ByRefBoolean:
      mtime: '2026-09-05T10:12:24Z'
      digest: ad867c5981d9ac52926a3913ac08b7d61d1a6a7aa1dbe1cc60ef648727dedfd1
    ByRefByte:
      mtime: '2026-09-05T10:12:24Z'
      digest: 42a89f81a7f30f24f951cac77179d8bc1a26b4038486694d02c514414deaebca
    ByRefChar:
      mtime: '2026-09-05T20:48:50Z'
      digest: c0e842f8670ddd42bf1fe09fbbedd30bb9cafc362ceea36276f7725ec89e4f94
    ByRefDouble:
      mtime: '2026-09-05T20:51:00Z'
      digest: 48817001edf5fada32b686c9167a6579891e4c196669ec535f9f36de0b3d95d3
    ByRefFloat:
      mtime: '2026-09-05T20:51:58Z'
      digest: 3a0fe22df57a6fde789287e94fdd755b89b500315c41793aa99c519dbfad0717
    ByRefInt:
      mtime: '2026-09-05T20:52:42Z'
      digest: 690c39a915bdefe96e7d6fdff284da279c82fc0f5bf421b9b9904c2b653f7a36
    ByRefLong:
      mtime: '2026-09-05T20:53:18Z'
      digest: 32c5aaf64643a8efe0bff78db39d2faaebcd345b48f073c6e3e8457455648703
    ByRefObject:
      mtime: '2026-09-05T20:48:33Z'
      digest: e8afd207f65348b163a962aaf8f2bdfaefd1eef9285784a2e22549f4b8734304
    ByRefShort:
      mtime: '2026-09-05T20:53:30Z'
      digest: 1d6a24c7d155f897361c9ec3d72854f5a60bba03ce0511badd714cf4196cb73b
    ByRefString:
      mtime: '2026-09-05T20:48:45Z'
      digest: 178ed43b17fdfbf4ebf461b0b82f1e6a5e86955816582054f2f602fa24846e78
    CachedCountAble:
      mtime: '2026-09-05T20:54:01Z'
      digest: bd3cb1b008548e5e3718ec24a3d82c518161305e21dacaa26844f13d2b32ba7d
    CachedDouble:
      mtime: '2026-09-05T20:54:49Z'
      digest: f1a3426d63b22ebee07b722c4e307e72572fd0896e0400b772b306fab52dbf4c
    CachedInteger:
      mtime: '2026-09-05T20:54:25Z'
      digest: 00b8b58e00e06726b1cc8b05b66570e7204358f5c9f3e5710adc7014ea9a3b7a
    CachedMeasurAble:
      mtime: '2026-09-05T20:54:43Z'
      digest: f3abb9edf8101c454b63d32a89c0a019cbe7f0cd8714447d628547eaa465a671
    IAdjustAble:
      mtime: '2026-09-05T10:12:24Z'
      digest: 2fe9fa190260535dab752dbf8e20f5b4d780ab4a4e51361e73c056e10bb7ae05
    ICategorizeAble:
      mtime: '2026-09-05T10:12:24Z'
      digest: f3890bdfee3a0cff5ef74da268fae1dcbce5fdc7e04b81f1fbfbea56d244ecbb
    IFloat:
      mtime: '2026-09-05T10:12:24Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    IInteger:
      mtime: '2026-09-05T10:12:24Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    ShortComparator:
      mtime: '2026-09-05T20:54:55Z'
      digest: f322d5e656d85a1d4e644c3ca172bd5cbd1060b8aedf37a7bdbfd2901aad7b8e
    TestByRef:
      mtime: '2026-09-05T10:12:24Z'
      digest: 48f785f60d207cf42e0a3eff03a3a046bede62d840f060029ece23101efdcbd8
  folders: {}
tags:
- code/function_wrapper
- code/caching
concepts:
- By-Reference Primitive Wrappers
facets:
  layer: utility
  status: legacy
  complexity: high
description: Boxed by-reference wrappers for every primitive type (`ByRefBoolean`/`Byte`/`Char`/`Double`/`Float`/`Int`/`Long`/`Short`/`Object`/`String`), each doubling as an `IFunction` that returns its own boxed value, plus `IAdjustAble`/`ICategorizeAble` mutation contracts, `Cached*` dirty-flag caching decorators, a `ShortComparator`, and bit-rotation helpers (`ROL`/`ROR`) on the integer wrappers. See `combinatoric/` for factorial/prime/probability functions built on these wrappers.
---

# byref

Boxed by-reference wrappers for every primitive type (`ByRefBoolean`/`Byte`/`Char`/`Double`/`Float`/`Int`/`Long`/`Short`/`Object`/`String`), each doubling as an `IFunction` that returns its own boxed value, plus `IAdjustAble`/`ICategorizeAble` mutation contracts, `Cached*` dirty-flag caching decorators, a `ShortComparator`, and bit-rotation helpers (`ROL`/`ROR`) on the integer wrappers. See `combinatoric/` for factorial/prime/probability functions built on these wrappers.

## Classes

| Class | Responsibility |
|---|---|
| [ByRefBoolean](ByRefBoolean.java) | Title: ByRefBoolean Description: This class is for transporting a boolean back from a Method Call. |
| [ByRefByte](ByRefByte.java) | Title: ByRefByte Description: This class is for transporting a byte back from a Method Call. |
| [ByRefChar](ByRefChar.java) | Title: ByRefChar Description: This class is for transporting a char back from a Method Call. |
| [ByRefDouble](ByRefDouble.java) | Title: ByRefDouble Description: This class is for transporting a double back from a Method Call. |
| [ByRefFloat](ByRefFloat.java) | Title: ByRefFloat Description: This class is for transporting a float back from a Method Call. |
| [ByRefInt](ByRefInt.java) | Title: ByRefInt Description: This class is for transporting an int back from a Method Call. |
| [ByRefLong](ByRefLong.java) | Title: ByRefShort Description: This class is for transporting a long back from a Method Call. |
| [ByRefObject](ByRefObject.java) | Title: ByRefObject Description: This class is for transporting an Object back from a Method Call. |
| [ByRefShort](ByRefShort.java) | Title: ByRefShort Description: This class is for transporting a short back from a Method Call. |
| [ByRefString](ByRefString.java) | Title: ByRefString Description: This class is for transporting a String back from a Method Call. |
| [CachedCountAble](CachedCountAble.java) | Caching decorator over an ICountAble, asserting the cache is clean before every read delegates to the wrapped value. |
| [CachedDouble](CachedDouble.java) | Caching decorator over an IFloat, marking the cache dirty before every write delegates to the wrapped value. |
| [CachedInteger](CachedInteger.java) | Caching decorator over an IInteger, marking the cache dirty before every write delegates to the wrapped value. |
| [CachedMeasurAble](CachedMeasurAble.java) | Caching decorator over an IMeasurAble, asserting the cache is clean before every read delegates to the wrapped value. |
| [IAdjustAble](IAdjustAble.java) | Title: enclosing_type Description: Purpose: Defines an Interface for Classes whose Value can be set on a<br/>continuous Scale Design Decisions / Implementation Details: Known SubClasses: Known Uses: Copyright: Copyright<br/>(c) Matthias Heuer Company: personal Created on 10-26-2002, 12:47 PM |
| [ICategorizeAble](ICategorizeAble.java) | Title: enclosing_type Description: Purpose: Defines an Interface for Classes whose Value can be set on a<br/>continuous Scale Design Decisions / Implementation Details: Known SubClasses: Known Uses: Copyright: Copyright<br/>(c) Matthias Heuer Company: personal Created on 10-26-2002, 12:47 PM |
| [IFloat](IFloat.java) | Title: IFloat Description: Purpose: Read/Write Access to the scalar internal Value; combines the Interfaces |
| [IInteger](IInteger.java) | Title: enclosing_type Description: Purpose: Purpose / Responsibilities of this Class Design Decisions /<br/>Implementation Details: If similar Classes exist (e.g. Polymorphism), characterize the specific Differences to<br/>compare these. |
| [ShortComparator](ShortComparator.java) | Title: ShortComparator Description: Allows to compare two Arrays of short[] Known SubClasses: Known Uses:<br/>Copyright: Copyright (c) Matthias Heuer Company: personal Created on 10-26-2002, 12:47 PM |
| [TestByRef](TestByRef.java) | Tests all Methods in the ByRef Package. |
