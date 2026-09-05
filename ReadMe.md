---
digest:
  local-classes:
    CopyAllPossible:
      mtime: '2008-06-29T19:05:35Z'
      digest: c32b0a503ce739019039971c44a74130e5d16424b1e8fa01098e600a6d150c92
    DirToXml:
      mtime: '2026-09-05T09:55:28Z'
      digest: b29a09e7e8917ed8b4c440a19f5b22e4cbfe41ff93ce6c88190f31fd27df5a14
    EchoFile:
      mtime: '2026-09-05T09:54:52Z'
      digest: 7e0933ef7b03ad18f3399b92d16585cfab4e8e00a87376f9c8f40fe4d8d7a1b5
    FileHex:
      mtime: '2026-09-05T09:55:04Z'
      digest: ea5d68e468aa151c89e39c7df559b35730ac2ca42eb825029fcdd41fa15cb0a2
    FilterFind:
      mtime: '2026-09-05T09:55:11Z'
      digest: 8e6e31be2b12c9ab92963dd8fe058847ea7ab71290debe5a5d357ffd1e361c73
    FixRecordScrambler:
      mtime: '2026-09-05T09:55:23Z'
      digest: 1aa7ab87186cc7dfbd3dc4f8a271b92628d9754019e7df633156268b8f7a05ab
    PropertyHierarchy:
      mtime: '2026-09-05T09:55:33Z'
      digest: 41fecd3f2c11e43c1f1827c304a01535528c6b9605afc4df593c25712bc71093
    RecourseCmd:
      mtime: '2007-06-09T21:46:44Z'
      digest: 976cb538442bdcf1a2cc27f93e65f0db507d4bb5ade8ccf4cc6f02873f685e9e
    XRename:
      mtime: '2026-09-05T09:55:51Z'
      digest: 85872e6e7b431c54e4076f9d3eaa797fd69c93dcafa78b856a3ab1e3459a09ba
  folders:
    analysis/:
      mtime: '2026-09-05T09:42:10Z'
      digest: 641cbebf685e72199318bd94b35c4a469d1c9221e9caf2308ecbc52b5c3b5fbc
    knowledge/:
      mtime: '2026-09-05T08:36:43Z'
      digest: 71bf367fff7e7b9d8c8bfce6ae6c37b3261ff463fc609f170fddf6233a396f78
    persistences/:
      mtime: '2026-09-05T09:12:58Z'
      digest: 88851f72d0d4b5de18d57a3705c3c05f39fa4be4100fdb8c814b0c69662dc589
    swing/:
      mtime: '2026-09-05T09:16:33Z'
      digest: c63f6394f29b611db737d94443112a7d4e874700577ba7c6c47a84e6395ee1bb
    tools/:
      mtime: '2026-09-04T16:35:47Z'
      digest: 51faa9fc2af3ed8d2eaca3356304fea68351431dd64cf45d1cbc0f5556b0e517
tags:
- code/cli_tool
concepts:
- Command-Line Utilities
facets:
  layer: utility
  status: stable
  complexity: low
description: 'Personal Java codebase, accumulated from the JDK 1.2-6 era onward and still organised as a single flat source tree rather than a build-tool project. There is no `pom.xml`, `build.gradle` or `Makefile`: the tree carries Eclipse `.project`/ `.classpath` metadata only, and compiled `.class` files sit alongside their sources (ignored via `.gitignore`).'
---

# Java

Personal Java codebase, accumulated from the JDK 1.2-6 era onward and still organised as a
single flat source tree rather than a build-tool project.
There is no `pom.xml`, `build.gradle` or `Makefile`: the tree carries Eclipse `.project`/
`.classpath` metadata only, and compiled `.class` files sit alongside their sources
(ignored via `.gitignore`).

The 9 classes at the repository root are standalone command-line utilities (File copying,
Hex dumping, Directory-to-XML export, batch Renaming, nested `.properties` loading, fixed-record
scrambling) rather than a cohesive Subsystem - each has its own `main()` and no dependency on
the others. The `## Subsystems` table below covers only the Folders documented so far; most of
the tree (see `HANDOFF.md` for live progress) has not yet had a domain narrative written.

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `analysis/` | A pure-interface implementation of Martin Fowler's Party/Responsibility analysis pattern: | `Party` |
| `knowledge/` | A small object-relational layer for a self-describing knowledge model, in which the schema | `AttributeObject` |
| `persistences/` | A minimal two-level base-class hierarchy for persisted domain objects, identified by a | `Objekt` |
| `swing/` | Displays an arbitrary Graph, including one with diamonds (Nodes reachable through more | `HashTreeNode` |
| `tools/` | Reusable concurrency and call-wrapping primitives, written as a study of how to coordinate | `CallAble` |

## Documentation Status

Documentation is generated bottom-up by the `/Java.ReadMeGenerator` skill and its companion
CLI, whose source lives outside this repository at
`D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/`.

| Pass | Scope | Status |
|---|---|---|
| 1 — method Javadoc | 1,455 files | not started |
| 2 — class Javadoc + docstate | 1,455 files | not started |
| 3 — folder `ReadMe.md` | 136 folders | `tools/` only |
| 4-7 — tags, vocabulary, index | whole tree | not started |

Per-type staleness bookkeeping is stored in a machine-owned `<!-- docstate ... -->` block
inside each type's Javadoc comment; never hand-edit its `pass`, `mtime`, `digest` or `stale`
fields. Progress across sessions is tracked in `HANDOFF.md` once a multi-folder run starts.

## Build

There is no build tooling in this repository, so this ReadMe intentionally carries no
Quick Start section. Compilation, when needed, is driven from the Eclipse project metadata
or by invoking `javac` against the relevant package folder directly.

## Classes

| Class | Responsibility |
|---|---|
| [CopyAllPossible](CopyAllPossible.java) | Copies rapidly as much as possible from a Stream. |
| [DirToXml](DirToXml.java) | Title: DirToXml Description: Purpose: Creates an XML File with the Files nested correctly in Directories. |
| [EchoFile](EchoFile.java) | Title: EchoFile Description: Purpose: This Class prints out the Contents of the given File to it's Output<br/>Stream using the given Encoding (DOS is the Default). |
| [FileHex](FileHex.java) | Title: FileHex Description: prints out the Bytes of the File in the Command Line Parameter in Hex Notation<br/>Copyright: Copyright (c) Matthias Heuer Company: personal |
| [FilterFind](FilterFind.java) | Title: FilterFind Purpose: Filters a streamIO and ends it as soon as a certain String is found more often than<br/>the specified Number. |
| [FixRecordScrambler](FixRecordScrambler.java) | Title: FixRecordScrambler Purpose: Rewrites a fixed-size binary Record Stream, offsetting one Field within<br/>each Record by a single Record Size per Record - i.e. Field N of Record I is moved to Record I+1 - so a later<br/>Pass can align that Field across the whole File. |
| [PropertyHierarchy](PropertyHierarchy.java) | Title: PropertyHierarchy Description: Useful little Class to create nested Lists of Strings to parameterize<br/>your Application without resorting to Parsing XML. |
| [RecourseCmd](RecourseCmd.java) | Title: RecourseCmd Description: Purpose: Recursively applies the same Command to every File in all<br/>Subdirectories Purpose / Responsibilities of this Class Design Decisions / Implementation Details: If similar<br/>Classes exist (e.g. Polymorphism), characterize the specific Differences to compare these. |
| [XRename](XRename.java) | Batch-renames Files whose Name matches a given Prefix/Infix/Suffix Pattern, optionally recursing into<br/>Subdirectories and remapping individual Characters. |
