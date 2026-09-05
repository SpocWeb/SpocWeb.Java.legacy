---
digest:
  local-classes:
    FileStreamByte:
      mtime: '2026-09-05T21:49:55Z'
      digest: 157fd3c759838954b18c46052921adc5ec48ef6365f2822937d94da8c997a533
    FileStreamIn_Byte:
      mtime: '2026-09-05T21:51:27Z'
      digest: c377fcd82500653beda9264dec6e42fa25992640c9301f359266392f9e57d76d
    FileStreamOutByte:
      mtime: '2026-09-05T21:51:45Z'
      digest: bd02561d6ab8ca45e5d895bd9cb9087abc430fe990c094383abd09dcd81a7012
    FilterCrLfFromQuoted:
      mtime: '2026-09-05T21:52:10Z'
      digest: ea9c95507b5f02eb676d617274fb122e8084f312ef7f8478169746d6a8165c5d
  folders: {}
tags:
- code/file_io
- code/stream_io
concepts:
- File-Backed StreamIO Implementations
facets:
  layer: utility
  status: legacy
  complexity: high
description: This folder implements the `streamIO` interfaces directly on top of Java's File I/O classes. `FileStreamByte` extends `RandomAccessFile` to also implement `IStreamIn_Int`/`IStreamOutByte` (reading and writing the same File), while `FileStreamIn_Byte`/`FileStreamOutByte` extend `FileInputStream`/`FileOutputStream` respectively for read-only/write-only access - all three exist because `RandomAccessFile`/`FileInputStream`/`FileOutputStream` are Java classes rather than interfaces, so they cannot otherwise be made to implement the `streamIO` interfaces alongside their own base class. `FilterCrLfFromQuoted` is a small standalone command-line filter that strips CR/LF Characters found inside quoted sections of a text File.
---

# file

This folder implements the `streamIO` interfaces directly on top of Java's File I/O classes.
`FileStreamByte` extends `RandomAccessFile` to also implement `IStreamIn_Int`/`IStreamOutByte`
(reading and writing the same File), while `FileStreamIn_Byte`/`FileStreamOutByte` extend
`FileInputStream`/`FileOutputStream` respectively for read-only/write-only access - all three exist
because `RandomAccessFile`/`FileInputStream`/`FileOutputStream` are Java classes rather than
interfaces, so they cannot otherwise be made to implement the `streamIO` interfaces alongside their
own base class. `FilterCrLfFromQuoted` is a small standalone command-line filter that strips CR/LF
Characters found inside quoted sections of a text File.

## Classes

| Class | Responsibility |
|---|---|
| [FileStreamByte](FileStreamByte.java) | Title: FileStreamByte Description: This Interface substitutes the Class RandomAccessFile in all<br/>Implementations The Reason is that the RandomAccessFile Class implements all Methods of both OutputStream and<br/>InputStream but sun chose to define these Methods in classes rather than Interfaces, so it cannot be<br/>subclassed directly. |
| [FileStreamIn_Byte](FileStreamIn_Byte.java) | Title: FileStreamIn_Byte Description: This Interface substitutes the Class FileInputStream in all<br/>Implementations The Reason is that the RandomAccessFile Class implements all Methods of both OutputStream and<br/>InputStream but sun chose to define these Methods in classes rather than Interfaces, so it cannot be<br/>subclassed directly. |
| [FileStreamOutByte](FileStreamOutByte.java) | Title: FileStreamOutByte Description: This Class substitutes the Class FileOutputStream in all Implementations<br/>The Reason is that the RandomAccessFile Class implements all Methods of both OutputStream and InputStream but<br/>sun chose to define these Methods in classes rather than Interfaces, so it cannot be subclassed directly. |
| [FilterCrLfFromQuoted](FilterCrLfFromQuoted.java) | TrimFilter.java Throws all CR/LF Characters out of Quoted Sections in the File Created on 3. April 2001, 00:43 |
