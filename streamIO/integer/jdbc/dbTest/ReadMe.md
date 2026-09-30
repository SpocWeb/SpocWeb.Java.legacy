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
  complexity: 3
description: 'A small hierarchy of row-filter Tests (`IDbTest`/`DbTestEquals` and its Less-Than/Outer-Join/ Full-Outer-Join/Negate/SwapOperands variants) that compare two `DbColumn` Fields, used by `FilterRsRows` and the join-oriented `ResultSet` implementations in the parent `jdbc/` package to evaluate `WHERE`/`ON` conditions. Two Tests (`DbTestLess`, `DbTestOuter`) had a `newInstance()` bug that silently downgraded them to a plain `DbTestEquals`; it was fixed in the 2026-09-06 bug-fix run.'
dv_has_:
  sub_:
    folders: 0
    files: 16
    units: 8
    facet_:
      layer_:
        domain: 8
      status_:
        legacy: 6
        broken: 2
      complexity_:
        "2": 8
    tag_:
      code_:
        predicate: 7
        predicate_evaluation: 4
        predicate_delegate: 2
        predicate_interface: 1
        predicate_filter: 1
    concept_:
      filters_resultset_rows_where_a_predicate_is_false: 1
      full_outer_join_row_predicate: 1
      left_outer_join_row_predicate: 1
      less_than_row_predicate: 1
      negating_row_predicate_wrapper: 1
      operand_swapping_row_predicate_wrapper: 1
      row_level_equality_test_between_two_dbcolumn_fields: 1
      row_predicate_contract_between_two_dbcolumn_fields: 1
has_sub_folders: 0
has_sub_files: 16
has_sub_units: 8
has_sub_facet_layer_domain: 8
has_sub_facet_status_legacy: 6
has_sub_facet_status_broken: 2
has_sub_facet_complexity_2: 8
has_sub_tag_code_predicate: 7
has_sub_tag_code_predicate_evaluation: 4
has_sub_tag_code_predicate_delegate: 2
has_sub_tag_code_predicate_interface: 1
has_sub_tag_code_predicate_filter: 1
has_sub_concept_filters_resultset_rows_where_a_predicate_is_false: 1
has_sub_concept_full_outer_join_row_predicate: 1
has_sub_concept_left_outer_join_row_predicate: 1
has_sub_concept_less_than_row_predicate: 1
has_sub_concept_negating_row_predicate_wrapper: 1
has_sub_concept_operand_swapping_row_predicate_wrapper: 1
has_sub_concept_row_level_equality_test_between_two_dbcolumn_fields: 1
has_sub_concept_row_predicate_contract_between_two_dbcolumn_fields: 1
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
