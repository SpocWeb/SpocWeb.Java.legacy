---
digest:
  local-classes:
    CmdLnBrowser:
      mtime: '2026-09-05T11:10:56Z'
      digest: 4b8f5fad08596c8bac6db2d669d3d32095c8ef20bb09dbe01763ce2f0bf8db86
    InitCtx:
      mtime: '2026-09-05T10:13:32Z'
      digest: 4fa79c755e69786f2f156e7d7ba4ec7ce7cd37b207e725c31f1ed2628faac9fc
  folders: {}
tags:
- code/directory_services
concepts:
- Naming and Directory Services
facets:
  layer: infrastructure
  status: legacy
  complexity: low
description: 'Small demonstrations of the Java Naming and Directory Interface (JNDI): acquiring an `InitialContext` against a file-system JNDI provider, and browsing/manipulating it interactively with Unix-like commands (`cd`, `ls`, `mv`, `mkdir`, `rmdir`, `cat`).'
---

# jndi

Small demonstrations of the Java Naming and Directory Interface (JNDI): acquiring an
`InitialContext` against a file-system JNDI provider, and browsing/manipulating it
interactively with Unix-like commands (`cd`, `ls`, `mv`, `mkdir`, `rmdir`, `cat`).

## Classes

| Class | Responsibility |
|---|---|
| [CmdLnBrowser](CmdLnBrowser.java) | Hierarchical JNDI Browser operated on the Command line using Unix like Commands cd ls mv mkdir |
| [InitCtx](InitCtx.java) | Demonstrates acquiring an Initial Context for accessing a JNDI Server. |

## Entry Points

| Class.Method | Description |
|---|---|
| [CmdLnBrowser.main(String[])](CmdLnBrowser.java#L237) | Starts an interactive command-line JNDI browsing session. |
| [InitCtx.main(String[])](InitCtx.java#L47) | Demonstrates acquiring an Initial Context and prints the result. |
