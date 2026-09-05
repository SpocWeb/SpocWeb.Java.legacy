---
digest:
  local-classes:
    FileReader:
      mtime: '2026-09-05T09:09:11Z'
      digest: 2c440a6aa16fb2a7c2d06bbf6927dd2db8cb62dc32990d1925c855ee7178c810
    FileWriter:
      mtime: '2026-09-05T09:09:21Z'
      digest: a61dc4343a9564fd7ca8bebfa9d7705c2505833c2b20d22bbe2eedb68566f70a
  folders: {}
tags:
- code/file_io
- code/encoding_handling
- code/file_polling
concepts:
- File I/O
- Text Encoding
facets:
  layer: infrastructure
  status: stable
  complexity: low
description: 'Encoding-aware file access built on the JDK''s character streams: `FileReader` extends `InputStreamReader` and adds static helpers for whole-file copy, whole-file read and polling for a file''s arrival (with a timeout), while `FileWriter` extends `OutputStreamWriter` and adds a static helper for writing a whole String to a file in one call. Both shadow their `java.io` namesakes by design, giving callers convenience overloads without abandoning the standard char-stream base classes.'
---

# character

Encoding-aware file access built on the JDK's character streams: `FileReader` extends
`InputStreamReader` and adds static helpers for whole-file copy, whole-file read and
polling for a file's arrival (with a timeout), while `FileWriter` extends
`OutputStreamWriter` and adds a static helper for writing a whole String to a file in one
call. Both shadow their `java.io` namesakes by design, giving callers convenience
overloads without abandoning the standard char-stream base classes.

## Classes

| Class | Responsibility |
|---|---|
| [FileReader](FileReader.java) | A character-decoding file reader with static helpers for whole-file copy, whole-file read and polling for a<br/>file's appearance, on top of the instance-level java.io.InputStreamReader behavior its constructors set up. |
| [FileWriter](FileWriter.java) | A character-encoding file writer with a static helper for writing a whole String to a file in one call, on top<br/>of the instance-level java.io.OutputStreamWriter behavior its constructors set up. |

## Entry Points

| Class.Method | Description |
|---|---|
| [FileReader.COPY_FILE(File, File)](FileReader.java#L55) | Copies one file's bytes to another. |
| [FileReader.SYNCH_READ_FILE(File, long, String)](FileReader.java#L122) | Waits for a file to appear, then reads it whole. |
| [FileWriter.WRITE_FILE(File, String, String)](FileWriter.java#L46) | Writes a whole String to a file using the given encoding. |
