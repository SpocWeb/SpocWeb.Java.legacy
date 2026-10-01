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
  complexity: '3'
description: 'Small, self-contained recursive-descent parsing tools, each demonstrating a different piece of classic LL(1) parsing: `IIStreamIn_Int` is the minimal integer-stream interface parsers read from; `MathParser` parses arithmetic-style expressions (`+ - * / \ % > < & | ! ^`) directly off a Java `InputStream`; and `Scanner` is a more general-purpose, separator-driven tokenizer/assembler used to parse nested `(a,b,c)`-style structures (its own Javadoc marks it `@deprecated` in favor of newer `streamIO.object.parser` classes). None of the three depend on each other at the type level - `MathParser` and `Scanner` both reference `Scanner.IS_LETTER`, the one point of overlap.'
dv_has_:
  sub_:
    folders: 0
    files: 6
    units: 3
    facet_:
      layer_:
        utility: 3
      status_:
        legacy: 3
      complexity_:
        "2": 2
        "3": 1
    tag_:
      code_:
        lexer_parser: 1
        parser_interface: 1
        expression_parser: 1
        parser_utility: 1
        parser: 1
    concept_:
      character_scanner: 1
      integer_stream_input_interface: 1
      math_expression_parser: 1
has_sub_folders: 0
has_sub_files: 6
has_sub_units: 3
has_sub_facet_layer_utility: 3
has_sub_facet_status_legacy: 3
has_sub_facet_complexity_2: 2
has_sub_facet_complexity_3: 1
has_sub_tag_code_lexer_parser: 1
has_sub_tag_code_parser_interface: 1
has_sub_tag_code_expression_parser: 1
has_sub_tag_code_parser_utility: 1
has_sub_tag_code_parser: 1
has_sub_concept_character_scanner: 1
has_sub_concept_integer_stream_input_interface: 1
has_sub_concept_math_expression_parser: 1
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
