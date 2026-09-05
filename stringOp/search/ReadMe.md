---
digest:
  local-classes:
    RegExp:
      mtime: '2026-09-05T10:41:40Z'
      digest: e2ef2041e61987e31fa9841753ebe94e4a46065381e041f2a5005302bc37fb94
    SearcherBM:
      mtime: '2026-09-05T10:41:54Z'
      digest: 5c4b6a52a2ddf98df17b278141a8aaaea6ef49ad1f971a45f23755379d33d7d2
    SearcherRK:
      mtime: '2026-09-05T10:13:32Z'
      digest: 8cc143d6e7961383b4c796b0e4c999559338b90fdb1a27e7246ddb80ef9bc025
    StrSearcher:
      mtime: '2026-09-05T10:13:32Z'
      digest: 50a3f64faeacf260e2a91ec77e64d62890c957164a94a17741e9248af91266ae
  folders: {}
tags:
- code/string_search
- code/regex
- code/search_algorithm
concepts:
- String Search Algorithms
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Four classic substring-search algorithms, each documented with the trade-off that makes it the right choice in a given situation: `StrSearcher` (Knuth-Morris-Pratt) builds a small finite-state machine from the pattern''s own prefix function and is best for long patterns in a small alphabet, consuming the input strictly sequentially so it works over streams; `SearcherBM` (Boyer-Moore) scans from the end of the pattern using a precomputed skip table and is fastest for non-recursive patterns in a large alphabet, at the cost of not being stream-friendly; `SearcherRK` (Rabin-Karp) compares rolling polynomial hash codes instead of characters, most effective with very large alphabets; and `RegExp` is a small non-deterministic automaton that matches a parsed regular expression (concatenation, `|` alternation, `*` closure) against a stream, more general than a fixed-string search. All four are independent, alternative implementations of the same `indexOf`-style problem rather than layers on top of one another.'
---

# search

Four classic substring-search algorithms, each documented with the trade-off that makes it the
right choice in a given situation: `StrSearcher` (Knuth-Morris-Pratt) builds a small finite-state
machine from the pattern's own prefix function and is best for long patterns in a small alphabet,
consuming the input strictly sequentially so it works over streams; `SearcherBM` (Boyer-Moore)
scans from the end of the pattern using a precomputed skip table and is fastest for non-recursive
patterns in a large alphabet, at the cost of not being stream-friendly; `SearcherRK` (Rabin-Karp)
compares rolling polynomial hash codes instead of characters, most effective with very large
alphabets; and `RegExp` is a small non-deterministic automaton that matches a parsed regular
expression (concatenation, `|` alternation, `*` closure) against a stream, more general than a
fixed-string search. All four are independent, alternative implementations of the same
`indexOf`-style problem rather than layers on top of one another.

## Entry Points

| Class.Method | Description |
|---|---|
| `StrSearcher.indexOf(Object[])` | Finds the pattern's first occurrence in a List using the KMP automaton. |
| `SearcherBM.indexOf(Object[])` | Finds the pattern's first occurrence in a List using Boyer-Moore skip jumps. |
| `SearcherRK.indexOf(Object[])` | Finds the pattern's first occurrence in a List by comparing rolling hash codes. |
| `RegExp.matchFirst(String, int)` | Finds the first match of the parsed regular expression starting at a given position. |

## Classes

| Class | Responsibility |
|---|---|
| [RegExp](RegExp.java) | Class that parameterizes an Automaton, so it can search for Patterns described by regular Expressions in a String. |
| [SearcherBM](SearcherBM.java) | Boyer- Moore Algorithm to search a String within another String. |
| [SearcherRK](SearcherRK.java) | Rabin-Karp Algorithm to search, creates a polynomial Hash Code to compare it with the Hash Code of the String. |
| [StrSearcher](StrSearcher.java) | Knuth Morris Pratt Algorithm to search a String within another. |
