---
digest:
  local-classes:
    BigEndianReader:
      mtime: '2026-09-05T21:31:44Z'
      digest: fad034d0251ff62da5e9e4226ed870df5f1d161f754cfa42b2f43c6608e37fe8
    BigEndianWriter:
      mtime: '2026-09-05T21:32:10Z'
      digest: 20226b2f56587a1233a2224507ab05f0a6dd29a26e59a4e8b6decf042c7884a2
    EscapeInputFilter:
      mtime: '2026-09-05T21:32:34Z'
      digest: 449f4ba1061fe1494fb646109bcb58d8cfa51ec016596e30d64e73fa050f9f3a
    EscapeOutputFilter:
      mtime: '2026-09-05T10:13:25Z'
      digest: 816d47f3dc53f1c77ec6301180f92d2873d582a274c618d9923e4ed76edbf022
    FilterASCII2Base64:
      mtime: '2026-09-05T21:33:44Z'
      digest: afb6eea92b47952e345bd7da32af6244b2355fa93c1364ee7614b63e361fee6c
    FilterBase64ToASCII:
      mtime: '2026-09-05T21:34:21Z'
      digest: d97b1120861f2dda471b0b8e92cedb8fdf1c05461f7e22a4aa5898bc8787b75d
    FilterBinHex2Byte:
      mtime: '2026-09-05T21:35:13Z'
      digest: bff0086e88c24faddec77393c777a419e48fe7ed30da6a54e55075e406120fe6
    FilterByte2BinHex:
      mtime: '2026-09-05T21:35:24Z'
      digest: bc8b7b79e15e020dad96613e44b88d6fcfa644792d80a6b3804ea63ba054d4d4
    FilterByte2UTF8:
      mtime: '2026-09-05T10:13:25Z'
      digest: a14f2d3b7e4d32e2c2f8ba71128a8437e5491f9a19d758a6c462f7908183a805
    FilterCRC16:
      mtime: '2026-09-05T21:36:06Z'
      digest: 541d300ecc45909b00f71d44a31989d265fdba8dd7d41eac139dfe0b987dee69
    FilterCRC32:
      mtime: '2026-09-05T21:36:11Z'
      digest: 541d300ecc45909b00f71d44a31989d265fdba8dd7d41eac139dfe0b987dee69
    FilterChar2BinHex:
      mtime: '2026-09-05T21:36:15Z'
      digest: 10518c654e0b81448833575ed2a7303122aef8bc195e9e78809cf469e9dba5d9
    FilterChar2Entity:
      mtime: '2026-09-05T21:36:19Z'
      digest: 4695c9fe54350d7096a0053c2cc8f5ec84c9fd0e4f1f1b2fca5179318d4bef2a
    FilterChar2String:
      mtime: '2026-09-05T21:36:23Z'
      digest: 294eec0fa19e37253e056d50f83f7c2b37d730224b60cb94fb20f9e34a7eeae5
    FilterCrypt:
      mtime: '2026-09-05T21:37:08Z'
      digest: c736bce67bf6281651afd8fd8e30243dbe3c73f3ce49818884dba3cb4985b8f3
    FilterEntity2Char:
      mtime: '2026-09-05T21:37:15Z'
      digest: 8845de45bb9d499b6676def5a46925950faa349363a46efd38f5490c8cd269a5
    FilterLookup:
      mtime: '2026-09-05T21:37:19Z'
      digest: df8f38248cdb002aa6ea669a2e300c047592a70d47170e84b95e611a56618f07
    FilterString2Char:
      mtime: '2026-09-05T21:39:24Z'
      digest: b18d775d60d627d93688515ff4b12c0ab9767617608e6fa071e34cb498b78ba7
    FilterUTF8ToByte:
      mtime: '2026-09-05T10:13:25Z'
      digest: 4c6348caaeb3a97e04fae2163f6aa30393cf12fa18ba57b4f1fb6e7145c403d9
    FilterUrlDecode:
      mtime: '2026-09-05T21:37:23Z'
      digest: 5f90850b1a3bf39c700e5c4aac3432e4c5100be2c1743596f5d00d0c2879a9d0
    FilterUrlEncode:
      mtime: '2026-09-05T21:37:27Z'
      digest: e33ecfb2e3e5c7873d0389c0f820bb50e34f7a088d38d14c6270957e3556189c
    SynchPipeByte:
      mtime: '2026-09-05T21:37:31Z'
      digest: affd69101579568c3fb620d6fa907199682ab8f71102c881faebcec8cbfa5883
  folders:
    redundancy/:
      mtime: '2026-09-05T21:41:27Z'
      digest: 94bfed546b13a7ad86b83393a85bb13f9f40afdff9ee8abe534beeea300b46b8
tags:
- code/stream_filter
- code/base64_encoding
- code/crc
- code/xor_cipher
concepts:
- Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'This folder collects stream filters that re-encode bytes or characters from one wire representation into another: little-endian primitive I/O (`BigEndianReader`/`BigEndianWriter`), binary-to-text schemes (Base64/UUEncode via `FilterASCII2Base64`/`FilterBase64ToASCII`, BinHex, URL encoding, HTML entities, UTF-8), lookup-table recoding (`FilterLookup`, `FilterChar2String`, `FilterString2Char`), checksums (`FilterCRC16`, `FilterCRC32`), a symmetric XOR/UUEncode cipher (`FilterCrypt`), escape-character insertion/removal, and a synchronizing byte pipe (`SynchPipeByte`). The `redundancy` subfolder adds forward-error-correction codecs (convolutional encoding, repetition-based redundancy) built on top of this layer. Most classes come in paired Input/Output or encode/decode halves that are meant to be composed with each other or with a plain `InputStream`/`OutputStream`. Several legacy encoders carried integer-truncation, off-by-one or nibble-conversion bugs (`FilterASCII2Base64`, `FilterBase64ToASCII`, `FilterBinHex2Byte`, `FilterByte2BinHex`, `FilterCRC16`, `FilterCRC32`, `FilterUrlDecode`, `FilterString2Char`, `BigEndianReader`); all were fixed in the 2026-09-06 bug-fix run. `FilterCrypt` is a home-grown XOR cipher and is now `@Deprecated`: it is not cryptographically secure and must not be used for confidentiality.'
---

# encoding

This folder collects stream filters that re-encode bytes or characters from one wire
representation into another: little-endian primitive I/O (`BigEndianReader`/`BigEndianWriter`),
binary-to-text schemes (Base64/UUEncode via `FilterASCII2Base64`/`FilterBase64ToASCII`, BinHex,
URL encoding, HTML entities, UTF-8), lookup-table recoding (`FilterLookup`, `FilterChar2String`,
`FilterString2Char`), checksums (`FilterCRC16`, `FilterCRC32`), a symmetric XOR/UUEncode cipher
(`FilterCrypt`), escape-character insertion/removal, and a synchronizing byte pipe
(`SynchPipeByte`). The `redundancy` subfolder adds forward-error-correction codecs (convolutional
encoding, repetition-based redundancy) built on top of this layer. Most classes come in
paired Input/Output or encode/decode halves that are meant to be composed with each other or
with a plain `InputStream`/`OutputStream`. Several legacy encoders carried integer-truncation,
off-by-one or nibble-conversion bugs (`FilterASCII2Base64`, `FilterBase64ToASCII`,
`FilterBinHex2Byte`, `FilterByte2BinHex`, `FilterCRC16`, `FilterCRC32`, `FilterUrlDecode`,
`FilterString2Char`, `BigEndianReader`); all were fixed in the 2026-09-06 bug-fix run. `FilterCrypt` is a
home-grown XOR cipher and is now `@Deprecated`: it is not cryptographically secure and must not
be used for confidentiality.

## Classes

| Class | Responsibility |
|---|---|
| [BigEndianReader](BigEndianReader.java) | Wraps a DataInput to read Intel-native (little-endian) primitive values, assembling each multi-byte value<br/>least-significant-byte first. |
| [BigEndianWriter](BigEndianWriter.java) | Wraps a DataOutputStream to write Intel-native (little-endian) primitive values, emitting each multi-byte<br/>value least-significant-byte first, the write-side counterpart of BigEndianReader. |
| [EscapeInputFilter](EscapeInputFilter.java) | Implements a Filter that either inserts or filters out Escape Characters. |
| [EscapeOutputFilter](EscapeOutputFilter.java) | Implements a Filter that either inserts or filters out Escape Characters. |
| [FilterASCII2Base64](FilterASCII2Base64.java) | Implements both the Base64 and the so called UUEncode "Unix to Unix Encode" Format, converting three 8-bit<br/>input bytes into four 6-bit output characters. |
| [FilterBase64ToASCII](FilterBase64ToASCII.java) | Implements both the Base64 and the so called UUEncode "Unix to Unix Encode" Format, decoding four 6-bit input<br/>characters back into three 8-bit output bytes. |
| [FilterBinHex2Byte](FilterBinHex2Byte.java) | Decodes a stream of two-character hexadecimal ("BinHex") digit pairs back into bytes. |
| [FilterByte2BinHex](FilterByte2BinHex.java) | Encodes the Bytes coming through this Output streamIO by completely converting their Values into a Hexadecimal Encoding. |
| [FilterByte2UTF8](FilterByte2UTF8.java) | Title: FilterByte2UTF8 Description: Encodes the Characters (int) coming through this Output or Input streamIO<br/>by converting their Values from/into UTF-8 Encoding. |
| [FilterCRC16](FilterCRC16.java) | This class calculates the 16-bit CRC of a file or string. |
| [FilterCRC32](FilterCRC32.java) | This class calculates the 32-bit CRC of a file or string. |
| [FilterChar2BinHex](FilterChar2BinHex.java) | Encodes the Characters coming through this Output streamIO by converting those Values above a certain<br/>Threshold (usually 128) into a Hexadecimal Encoding. |
| [FilterChar2Entity](FilterChar2Entity.java) | Recodes the Characters coming through this Input streamIO by converting their Values into a decimal or<br/>hexadecimal Representation. |
| [FilterChar2String](FilterChar2String.java) | Recodes the Characters coming through this Input streamIO by looking up their Values in a String[] Array. |
| [FilterCrypt](FilterCrypt.java) | This class encrypts/decrypts an Input or Output streamIO. |
| [FilterEntity2Char](FilterEntity2Char.java) | Recodes the Characters coming through this Input streamIO by converting their Values from a decimal or<br/>hexadecimal Representation into Characters. |
| [FilterLookup](FilterLookup.java) | Recodes the Bytes coming through this Input streamIO by looking up their Values in a byte[] Array. |
| [FilterString2Char](FilterString2Char.java) | Recodes the Bytes coming through this Input streamIO by looking up their Values in a String[] Array. |
| [FilterUTF8ToByte](FilterUTF8ToByte.java) | Title: FilterUTF8ToByte Description: Encodes the Characters coming through this Output streamIO by converting<br/>their Values into UTF-8 Encoding. |
| [FilterUrlDecode](FilterUrlDecode.java) | Decodes the Bytes coming through this Input or Output streamIO by converting their Values from a URL Encoding. |
| [FilterUrlEncode](FilterUrlEncode.java) | Encodes the Bytes coming through this Input or Output streamIO by converting their Values to a Byte URL Encoding. |
| [SynchPipeByte](SynchPipeByte.java) | Converts a StreamOutByte into a StreamIn_Byte by buffering the Output and optionally triggering re-reads. |
