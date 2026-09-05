---
digest:
  local-classes:
    AlliterationStreamIn:
      mtime: '2026-09-05T10:13:32Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    Array2Stream:
      mtime: '2026-09-05T21:40:35Z'
      digest: b9737e200868c84142be1e7d0bfa6d49dc2a3024fe00ca4f08e6bf08fdc024c9
    ByteStreamFormatter:
      mtime: '2026-09-05T21:40:10Z'
      digest: 5d94792e37e5d8da569bbc0a4da48cc8667ba990ab875929ec941935e4394514
    EscapeStreamIn:
      mtime: '2026-09-05T21:30:41Z'
      digest: 080e26462083a8059dc2d9a511300b1981123688b0f139426b69a3a95549a183
    FileStream2Stream:
      mtime: '2026-09-05T21:31:37Z'
      digest: 37d5d3eb128b8c447effe8b6b71a319a5b5cdf23a9c012a683cdf64f049ddb0e
    FileSystem2Stream:
      mtime: '2026-09-05T21:31:33Z'
      digest: 8ebc594c9e5b6822c3b989c2168429a87758ba6fa7067a1e73c2a39000e16581
    IParserIn:
      mtime: '2026-09-05T10:13:32Z'
      digest: 6a09d5f1f1122c5b037e43932ea17b51b72cee9b3877f7a13075fb7b5721bd66
    InputStream2StreamIn:
      mtime: '2026-09-05T21:32:39Z'
      digest: 098d41d729a7fa65a5577225e668077e58ddff67f71897668ee52f9206ba8539
    JTreeContainer:
      mtime: '2026-09-05T10:13:32Z'
      digest: b869262036b4c7ce4be25cb3774229bec7e2f6a63b5c4de2b36e18c6323e12f9
    MaskedStreamIn:
      mtime: '2026-09-05T21:32:49Z'
      digest: c5feda772ae51b479d816fe49a3070cc2f4b4ccdf0cdbc91f656a07dd73c6dfe
    ParserBracket2StreamIn:
      mtime: '2026-09-05T21:32:59Z'
      digest: 31df976fc5c4ba51dcd337b33541961c85c9afd42a99193d22ddc1ea39335f87
    ParserFromStreamIn:
      mtime: '2026-09-05T21:33:17Z'
      digest: ab3152d13829552868663c2bb3f463389742410bceb90481c0f046ca2d873c23
    SaxReader:
      mtime: '2026-09-05T21:33:52Z'
      digest: 05dec4e726e688bcae3970972e1a45bffbb0494e6ba88d670cf594b1545ba93f
    StreamInFromParser:
      mtime: '2026-09-05T21:34:04Z'
      digest: 885dcd3319582889a0704bf39d627ae72509922c147fb3c3057a5522a72065e4
    StreamOutXML:
      mtime: '2026-09-05T21:35:31Z'
      digest: 7dd033eeccaabf3e36f5d0998b55118dc8f1529ebff6d179ccff71cb7bd9ec07
    StreamStatistics:
      mtime: '2026-09-05T21:40:12Z'
      digest: 2447fe19369e9d0d8f9ea54e6a08fb30045516a3c2ed2d8dadb36ef8932d8b06
    XMLFormatter:
      mtime: '2026-09-05T21:40:13Z'
      digest: 956d17be9e0722fd90de8a634bc65377575faa9f9ca9d3fb99a67cf68e2a49cf
    XMLScannerStreamIn:
      mtime: '2026-09-05T10:13:32Z'
      digest: 1304d4dabaaf1135b16003650e4b26558f55037d8ed38cde1b5f9a1b23221ecf
    XMLStreamIn:
      mtime: '2026-09-05T21:35:59Z'
      digest: cfdeeffb6acfd50759d0107e26965adfb8b0604298f0b36e7afa5c2ddeb0e4f7
  folders:
    jdbc/:
      mtime: '2026-09-05T21:41:34Z'
      digest: 566e8d4b7c5a4d8012ac67f841dbf58708c8ec2d096b3ffeaf02284bc9bad483
tags:
- code/stream_parsing
- code/parser
concepts:
- Separator-Driven Token Parsing and Stream Adapters
facets:
  layer: utility
  status: legacy
  complexity: high
description: This folder builds a small parsing framework directly on top of `streamIO.object.IStreamIn`/`IIStreamOut`, rather than on `java.io.Reader`/`Writer`. Byte- and Character-level Adapters (`InputStream2StreamIn`, `EscapeStreamIn`, `FileStream2Stream`, `FileSystem2Stream`) turn raw Input (an `InputStream`, a File, or a whole Directory Tree) into a Stream of Tokens, delimited by a configurable Separator String. `MaskedStreamIn` and the `*Bracket2StreamIn`/`StreamIn*Parser` Filter Classes then bridge between two competing nesting Conventions this Codebase uses for hierarchical Data - an older one where `nextItem()` returns a Separator Level and `currItem()` the Token, and a more consistent one where a nested `IStreamIn` Result signals descending a Level and `null` signals ascending one. On the XML side, `XMLScannerStreamIn`/`XMLStreamIn` parse XML into Object Graphs via Reflection, `XMLFormatter`/`StreamOutXML` do the reverse (also implementing `org.xml.sax.ContentHandler` so SAX-driven Code can write through them), and `SaxReader` adapts an `IStreamIn` into a minimal `org.xml.sax.XMLReader`. The `jdbc/` Subsystem reuses this same Separator-driven Parsing to implement a small JDBC 1.0/2.0 Driver over Files with Comma-, Tab- or custom-separated Content, so simple SQL Queries can run directly against Flat Files without a real Database. Most of this Code dates from an Era before Java's own `java.io` Readers/Writers and `javax.xml` APIs matured, and several Classes say so explicitly in their own Documentation (e.g. `EscapeStreamIn`'s "only retained to keep things running").
---

# parser

This folder builds a small parsing framework directly on top of `streamIO.object.IStreamIn`/`IIStreamOut`,
rather than on `java.io.Reader`/`Writer`. Byte- and Character-level Adapters
(`InputStream2StreamIn`, `EscapeStreamIn`, `FileStream2Stream`, `FileSystem2Stream`) turn raw Input
(an `InputStream`, a File, or a whole Directory Tree) into a Stream of Tokens, delimited by a
configurable Separator String. `MaskedStreamIn` and the `*Bracket2StreamIn`/`StreamIn*Parser` Filter
Classes then bridge between two competing nesting Conventions this Codebase uses for hierarchical
Data - an older one where `nextItem()` returns a Separator Level and `currItem()` the Token, and a
more consistent one where a nested `IStreamIn` Result signals descending a Level and `null` signals
ascending one. On the XML side, `XMLScannerStreamIn`/`XMLStreamIn` parse XML into Object Graphs via
Reflection, `XMLFormatter`/`StreamOutXML` do the reverse (also implementing `org.xml.sax.ContentHandler`
so SAX-driven Code can write through them), and `SaxReader` adapts an `IStreamIn` into a minimal
`org.xml.sax.XMLReader`. The `jdbc/` Subsystem reuses this same Separator-driven Parsing to implement
a small JDBC 1.0/2.0 Driver over Files with Comma-, Tab- or custom-separated Content, so simple SQL
Queries can run directly against Flat Files without a real Database. Most of this Code dates from an
Era before Java's own `java.io` Readers/Writers and `javax.xml` APIs matured, and several Classes say
so explicitly in their own Documentation (e.g. `EscapeStreamIn`'s "only retained to keep things
running").

## Classes

| Class | Responsibility |
|---|---|
| [AlliterationStreamIn](AlliterationStreamIn.java) | This Filter removes Alliterations of Separators. |
| [Array2Stream](Array2Stream.java) | This Class encapsulates an ArrayList Object and allows to performed buffered Reads and Writes up to a Limit. |
| [ByteStreamFormatter](ByteStreamFormatter.java) | Streams out the Contents of the Input streamIO to the Output streamIO. |
| [EscapeStreamIn](EscapeStreamIn.java) | EscapeStreamIn deprecated Implementation, replacing those in Package Byte, only retained to keep things running... |
| [FileStream2Stream](FileStream2Stream.java) | Merges the Contents of a streamIO of File Names (and Flags for Directories) into a single streamIO of Bytes. |
| [FileSystem2Stream](FileSystem2Stream.java) | Returns all Files and Directories in a FileSystem as a streamIO of FileNames. |
| [IParserIn](IParserIn.java) | Interface defining a Method to set the Separators for an Input streamIO Parser. |
| [InputStream2StreamIn](InputStream2StreamIn.java) | InputStream2StreamIn New Approach to define performant (and pluggable) Parsing, replacing those in Package Byte. |
| [JTreeContainer](JTreeContainer.java) | Bridge Class to mediate between StreamIn and JTree Implements two Methods to fill Trees from Streams: 1) using<br/>nested Streams to an arbitrary Level. |
| [MaskedStreamIn](MaskedStreamIn.java) | MaskedStreamIn This is a possible Element of a Parser Chain: It assembles Strings from a StreamIn to Strings,<br/>hands over the Strings to the Parser and masks out Segments, e.g. using quotes or double Quotes. |
| [ParserBracket2StreamIn](ParserBracket2StreamIn.java) | Bridges / Filters the older StreamIn Implementation that returns the Level of the Separator in nextItem() and<br/>the actual Object in currItem() to the more consistent nested StreamIn Interface which returns an Iterator<br/>when the Level increases and EOF=null when the Level decreases. |
| [ParserFromStreamIn](ParserFromStreamIn.java) | Bridges / Filters the more consistent nested StreamIn Interface, which returns an Iterator when the Level<br/>increases and an intermediate 'EOI' ('null') when the Level decreases, to the 'older' StreamIn Implementation<br/>that returns the Level of the Separator in nextItem() and the actual Object in currItem() This is more<br/>consistent than the mixed Meaning of nextItem() and currItem() in the InputStream2StreamIn Implementation, but<br/>only useful for LL(0) Languages regular Data Structures, not for complex Languages Parsing! |
| [SaxReader](SaxReader.java) | Implements the org.xml.sax.XMLReader Interface for an XML Parser implementing the @see streamIO.IStreamIn Interface. |
| [StreamInFromParser](StreamInFromParser.java) | Bridges / Filters the older StreamIn Implementation that returns the Level of the Separator in nextItem() and<br/>the actual Object in currItem() to the more consistent nested StreamIn Interface which returns an Iterator<br/>when the Level increases and null when the Level decreases. |
| [StreamOutXML](StreamOutXML.java) | Helper Class for writing XML Data to a Character Output streamIO (e.g. a 'Writer'). |
| [StreamStatistics](StreamStatistics.java) | Collects Statistics of Tokens in a streamIO Known SubClasses: Known Uses: Copyright: Copyright (c) Matthias<br/>Heuer Company: personal Created on 12-25-2002, 06:26 PM |
| [XMLFormatter](XMLFormatter.java) | This Class provides Methods to write Objects to an XML streamIO Uses the Reflection API to explore the Object<br/>and write primitive Fields as Attributes and Objects as nested Elements Main Method: addItem() Previously<br/>named 'XMLOutputStream' |
| [XMLScannerStreamIn](XMLScannerStreamIn.java) | This Class defines an Event Based Interface to SGML-like Streams similar to the SAX Interface. |
| [XMLStreamIn](XMLStreamIn.java) | Methods to read complete Object Trees from an XML streamIO and static Methods to set the Values in Arrays and Objects. |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `jdbc/` | Minimal JDBC Driver over separated-Format Files | `ConnectionSep` |
