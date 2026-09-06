---
digest:
  local-classes:
    DbTestEquals:
      mtime: '2026-09-05T22:18:59Z'
      digest: 544ef4df8ce5e56a8a97d71f61c4a595cffd5d30bf2c5c8dc7c44f9e9996c818
    DbTestFullOuter:
      mtime: '2026-09-05T22:17:35Z'
      digest: 7be498360435b417ddb6b6ef159913802c5ca8c8a76164017a71cc9de680f50e
    DbTestLess:
      mtime: '2026-09-05T22:17:57Z'
      digest: e45fbb91a0a37d08fcfba14b4bc44c1d3cbe0d882abf2b6c624732bf5f1f3f3d
    DbTestNegate:
      mtime: '2026-09-05T22:18:15Z'
      digest: e926ada95ba966183f5205ecccadf3092cb4285a772f0880df7a7fc4ba5cf2e5
    DbTestOuter:
      mtime: '2026-09-05T22:18:31Z'
      digest: dfa45b15d5dbf4f8a6c535eff7551e4382bb20cf5fb30c1fef0ce523bc492d49
    DbTestSwapOperands:
      mtime: '2026-09-05T22:18:44Z'
      digest: 3fccee7c8bcdc37c1537547d77befda017ee27c8c6ea246c505641b614058860
    FilterRsRows:
      mtime: '2026-09-05T10:13:31Z'
      digest: 6f93e30cb1f190c0ab4767680aa447cbd3c49a9dad18c5ace2b354da1a5300f5
    IDbTest:
      mtime: '2026-09-05T22:16:53Z'
      digest: f1680d6e03a371709f0888ec44c45db6f9dd24c9b6823ca5b5e664e1dc58f63f
  folders: {}
tags:
- code/predicate
- code/predicate_evaluation
concepts:
- Row-Filter Predicate Hierarchy for jdbc ResultSet Joins and Conditions
facets:
  layer: domain
  status: broken
  complexity: medium
description: 'A small hierarchy of row-filter Tests (`IDbTest`/`DbTestEquals` and its Less-Than/Outer-Join/ Full-Outer-Join/Negate/SwapOperands variants) that compare two `DbColumn` Fields, used by `FilterRsRows` and the join-oriented `ResultSet` implementations in the parent `jdbc/` package to evaluate `WHERE`/`ON` conditions. Two Tests (`DbTestLess`, `DbTestOuter`) had a `newInstance()` bug that silently downgraded them to a plain `DbTestEquals`; it was fixed in the 2026-09-06 bug-fix run.'
---

# dbTest

A small hierarchy of row-filter Tests (`IDbTest`/`DbTestEquals` and its Less-Than/Outer-Join/
Full-Outer-Join/Negate/SwapOperands variants) that compare two `DbColumn` Fields, used by
`FilterRsRows` and the join-oriented `ResultSet` implementations in the parent `jdbc/` package
to evaluate `WHERE`/`ON` conditions. Two Tests (`DbTestLess`, `DbTestOuter`) had a
`newInstance()` bug that silently downgraded them to a plain `DbTestEquals`; it was fixed in
the 2026-09-06 bug-fix run.

## Classes

| Class | Responsibility |
|---|---|
| [DbTestEquals](DbTestEquals.java) | Encapsulates a Test for a Relation between two Fields. |
| [DbTestFullOuter](DbTestFullOuter.java) | Full Outer Join variant of the Equals Test: treats either Field being null as a Match, as long as the other<br/>side has not already matched a different Row. |
| [DbTestLess](DbTestLess.java) | Tests that the left Field's String Value sorts strictly before the right Field's. |
| [DbTestNegate](DbTestNegate.java) | Wraps another Test and inverts its Result. |
| [DbTestOuter](DbTestOuter.java) | Left Outer Join variant of the Equals Test: treats the left Field being null as a Match, as long as it has not<br/>already matched a different Row. |
| [DbTestSwapOperands](DbTestSwapOperands.java) | Wraps another Test and swaps its Operand order when creating a new Instance - reuses DbTestNegate's<br/>delegate/operator fields but does not negate the Result. |
| [FilterRsRows](FilterRsRows.java) | filters all rows out where the Condition is false |
| [IDbTest](IDbTest.java) | Encapsulates the Test for a (crisp) Relation between two Fields |
