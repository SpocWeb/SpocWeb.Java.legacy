---
digest:
  local-classes:
    AStringFunction:
      mtime: '2026-09-05T20:43:14Z'
      digest: d76ff8ecddbf2b888af28c46ba8b933108be2ffb145892d11916071d944c41a5
    Char2String:
      mtime: '2026-09-05T20:43:43Z'
      digest: 4e1d690f1ceba860b900c76a79c9d2727bb21a542594ac3a96199de14560725a
    IStringFunction:
      mtime: '2026-09-05T20:43:48Z'
      digest: 25dbe9419f92962ca1fe974eabffd88ad92611d399184c039936e45bd31afe7b
    StringFunction:
      mtime: '2026-09-05T20:43:53Z'
      digest: 3451d1c74fe4ba80aff0cf02d4a4cdb389a68c3767450a6f80da6c4949574c1b
    SubString:
      mtime: '2026-09-05T20:43:36Z'
      digest: 204b07024f34ef4796f32ec599a522086d9fa8172de3a695cbdc4102c9ff1d8a
  folders: {}
tags:
- code/string_transform
concepts:
- String Transform Functions
facets:
  layer: utility
  status: broken
  complexity: low
description: 'String-valued function transforms implementing `IStringFunction`/`StringFunction`: `AStringFunction` provides case-conversion helpers (including hungarian-notation-to-camelCase), `Char2String` wraps a single character, and `SubString` extracts a substring.'
---

# string

String-valued function transforms implementing `IStringFunction`/`StringFunction`: `AStringFunction` provides case-conversion helpers (including hungarian-notation-to-camelCase), `Char2String` wraps a single character, and `SubString` extracts a substring.

## Classes

| Class | Responsibility |
|---|---|
| [AStringFunction](AStringFunction.java) | Abstract Base Class for most String Functions. |
| [Char2String](Char2String.java) | Maps single characters (or the character at a fixed position of a String) to their encoded String replacement,<br/>via a direct array lookup rather than a hash table. |
| [IStringFunction](IStringFunction.java) | Type-safe refinement of function.IFunction for functions that map a String to a String. |
| [StringFunction](StringFunction.java) | Maps an input String (or one of its characters) to a replacement String via an internal Map, e.g. for encoding<br/>special characters into XML or ANSI. |
| [SubString](SubString.java) | Generic parameterized SubString Function. |
