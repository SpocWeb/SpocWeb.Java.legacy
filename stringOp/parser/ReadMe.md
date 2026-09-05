---
digest:
  local-classes:
    IIStreamIn_Int:
      mtime: '2026-09-05T10:41:27Z'
      digest: 85765de1ec69ca4ea2b100fe53fbe5f1c6bbd47bc53327276e23bfe298b9d97c
    MathParser:
      mtime: '2026-09-05T10:13:32Z'
      digest: bb962de5689478ac2d10b11240caf57b10b940db465da67b4d32d897ee8d8c35
    Scanner:
      mtime: '2026-09-05T10:41:35Z'
      digest: 872e52819329deecdabcfe9bdd11fcae9c9386ee9975fcc1baf5be015c92ba69
  folders: {}
tags:
- code/parser
- code/expression_parser
- code/parser_utility
concepts:
- Math Expression Parser
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Small, self-contained recursive-descent parsing tools, each demonstrating a different piece of classic LL(1) parsing: `IIStreamIn_Int` is the minimal integer-stream interface parsers read from; `MathParser` parses arithmetic-style expressions (`+ - * / \ % > < & | ! ^`) directly off a Java `InputStream`; and `Scanner` is a more general-purpose, separator-driven tokenizer/assembler used to parse nested `(a,b,c)`-style structures (its own Javadoc marks it `@deprecated` in favor of newer `streamIO.object.parser` classes). None of the three depend on each other at the type level - `MathParser` and `Scanner` both reference `Scanner.IS_LETTER`, the one point of overlap.'
---

# parser

Small, self-contained recursive-descent parsing tools, each demonstrating a different piece of
classic LL(1) parsing: `IIStreamIn_Int` is the minimal integer-stream interface parsers read from;
`MathParser` parses arithmetic-style expressions (`+ - * / \ % > < & | ! ^`) directly off a Java
`InputStream`; and `Scanner` is a more general-purpose, separator-driven tokenizer/assembler used
to parse nested `(a,b,c)`-style structures (its own Javadoc marks it `@deprecated` in favor of
newer `streamIO.object.parser` classes). None of the three depend on each other at the type level -
`MathParser` and `Scanner` both reference `Scanner.IS_LETTER`, the one point of overlap.

## Entry Points

| Class.Method | Description |
|---|---|
| `MathParser.Expression()` | Parses one arithmetic Expression from the InputStream supplied to the constructor. |
| `Scanner.nextToken()` | Reads and classifies the next Token, returning its position in the configured Separator string. |
| `Scanner.readRelation(Vector)` | Parses a nested `Tag(Value1,...,ValueN)Tag`-style structure (equivalent to attribute-less XML). |

## Classes

| Class | Responsibility |
|---|---|
| [IIStreamIn_Int](IIStreamIn_Int.java) | Defines the Interface for a plain input Stream with discrete Objects/Values, expressed by an int, but without<br/>any Characteristics; Neither the algebraic, nor the topological Properties of int are used. |
| [MathParser](MathParser.java) | Implements a simple Parser for an LL(1) Grammar with the following Operators: +,-,*,/,\,%,>,<,&,\|,!,^<br/>Variables have a single Character (otherwise there must be a declaration Section, which feeds the Scanner) and<br/>the Multiplication Sign can be skipped in favor of faster notation. |
| [Scanner](Scanner.java) | Rewritten Scanner that makes use of a pushback-Stream. |
