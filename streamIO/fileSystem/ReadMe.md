---
digest:
  local-classes:
    DirectoryFilter:
      mtime: '2026-09-05T09:21:54Z'
      digest: 0734be025e87c733d435bf8bbcfc084a8b7ccff5a62071b3a1324472ab701ec1
    FileBackupIterator:
      mtime: '2026-09-05T09:22:19Z'
      digest: a6054bee85e49f1e2b235ee07e06a486207af5939b3e7314a88f80ccfb412eb4
    FileIterator:
      mtime: '2026-09-05T09:22:52Z'
      digest: 10322a3cdadd83896c33d86765bd742c7a61e5dfeb59d962fa2816d0e77cc5f9
    SuffixFileNameFilter:
      mtime: '2026-09-05T09:22:03Z'
      digest: ba1ce3c2600885a7f94570a9153ad71e8f422698aa486d73de7297e620ee7ae6
  folders: {}
tags:
- code/file_filtering
- code/file_io
- code/iterator_pattern
- code/file_backup
concepts:
- File System
- File I/O
facets:
  layer: infrastructure
  status: broken
  complexity: medium
description: 'Filesystem-facing helpers with no shared abstraction between them beyond the `java.io` interfaces they implement: `DirectoryFilter` and `SuffixFileNameFilter` are `FilenameFilter`/ `FileFilter` predicates for `File.list()`/`listFiles()`, while `FileIterator` and `FileBackupIterator` are `AStreamIn`-based generators that hand out a fresh numbered File (or a File whose predecessor was just moved to a backup location) on each call.'
---

# fileSystem

Filesystem-facing helpers with no shared abstraction between them beyond the `java.io`
interfaces they implement: `DirectoryFilter` and `SuffixFileNameFilter` are `FilenameFilter`/
`FileFilter` predicates for `File.list()`/`listFiles()`, while `FileIterator` and
`FileBackupIterator` are `AStreamIn`-based generators that hand out a fresh numbered File
(or a File whose predecessor was just moved to a backup location) on each call.

**Known defects** (see `## Bugs Found` in the repository root `HANDOFF.md`):
`FileIterator.isValid()` is inverted relative to its own `available` field and to the
sibling `FileBackupIterator.isValid()`, and `FileIterator.currItem()` always returns
`null` because `nextItem()` never assigns the field it reads from.

## Classes

| Class | Responsibility |
|---|---|
| [DirectoryFilter](DirectoryFilter.java) | Filters Files, so that only Directories are returned. |
| [FileBackupIterator](FileBackupIterator.java) | Returns a new File and in parallel renames the former File to a Backup Location. |
| [FileIterator](FileIterator.java) | streamIO of new Input- or Output- Streams with File Names just being counted up. |
| [SuffixFileNameFilter](SuffixFileNameFilter.java) | Implementation of the FileNameFilter that accepts only those Files ending with a certain Suffix. |

## Entry Points

| Class.Method | Description |
|---|---|
| [DirectoryFilter.FILTER](DirectoryFilter.java#L35) | Singleton instance, since the Filter is stateless. |
| [FileIterator.nextItem()](FileIterator.java#L110) | Opens the next numbered File as a Stream, or signals end/exhaustion. |
| [FileBackupIterator.nextItem()](FileBackupIterator.java#L69) | Backs up the current File, then opens a fresh Output Stream to it. |
