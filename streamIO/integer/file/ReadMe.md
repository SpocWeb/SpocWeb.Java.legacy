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
  complexity: '4'
description: This folder implements the `streamIO` interfaces directly on top of Java's File I/O classes. `FileStreamByte` extends `RandomAccessFile` to also implement `IStreamIn_Int`/`IStreamOutByte` (reading and writing the same File), while `FileStreamIn_Byte`/`FileStreamOutByte` extend `FileInputStream`/`FileOutputStream` respectively for read-only/write-only access - all three exist because `RandomAccessFile`/`FileInputStream`/`FileOutputStream` are Java classes rather than interfaces, so they cannot otherwise be made to implement the `streamIO` interfaces alongside their own base class. `FilterCrLfFromQuoted` is a small standalone command-line filter that strips CR/LF Characters found inside quoted sections of a text File.
dv_has_:
  sub_:
    folders: 0
    files: 8
    units: 4
    facet_:
      layer_:
        utility: 4
      status_:
        legacy: 4
      complexity_:
        "4": 4
    tag_:
      code_:
        file_io: 4
        stream_io: 4
    concept_:
      file_backed_streamio_implementations: 4
has_sub_folders: 0
has_sub_files: 8
has_sub_units: 4
has_sub_facet_layer_utility: 4
has_sub_facet_status_legacy: 4
has_sub_facet_complexity_4: 4
has_sub_tag_code_file_io: 4
has_sub_tag_code_stream_io: 4
has_sub_concept_file_backed_streamio_implementations: 4
related:
- path: ../_Matthias/Code/NET/_SpocWeb.Root/_std/SpocWeb.Pairs/Data/ini
  shared-tags:
  - code/file_io
- path: ../_Matthias/Code/NET/_root/Files
  shared-tags:
  - code/file_io
- path: ../_Matthias/Code/NET/_root/Files/Collections
  shared-tags:
  - code/file_io
- path: ../_Matthias/Code/NET/_root/Files/Systems
  shared-tags:
  - code/file_io
- path: ../_Matthias/Code/NET/_root/Files/Zip
  shared-tags:
  - code/file_io
- path: ../_Matthias/Code/NET/_root/Files/paths
  shared-tags:
  - code/file_io
- path: ../_Matthias/Code/NET/_std/IGraphs/streams/ReaderWriter
  shared-tags:
  - code/stream_io
- path: ../_Matthias/Code/NET/_std/IMathsImpl/io
  shared-tags:
  - code/stream_io
- path: ../_Matthias/Code/NET/_std/IMathsImpl/streams/ReaderWriter
  shared-tags:
  - code/stream_io
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
