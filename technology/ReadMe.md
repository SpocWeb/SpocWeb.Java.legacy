---
digest:
  local-classes:
    RandomGUID:
      mtime: '2026-09-05T11:09:55Z'
      digest: 76213e73a1edaa0edc27691998b5d2e08b3fe6c7353f2dd7e634a9b0410df3a5
    Threading:
      mtime: '2026-09-05T11:09:59Z'
      digest: da405cd27bd46defda54db866a9682ac6f1ac91839bb4651d4015ad1892a9b57
  folders:
    jndi/:
      mtime: '2026-09-05T11:10:56Z'
      digest: b088c26199dda1f6236f4723b0fec67dcceae96e144bb0876b3a02eae423b9d0
    sound/:
      mtime: '2026-09-05T11:11:09Z'
      digest: 008c677fae5e64095969bc129886292e7adc5f75fc3255d40463064d7ed0ba2b
    stream/:
      mtime: '2026-09-05T11:12:09Z'
      digest: 119d0c586d323cba905aba6b5b85dfc39073ddd010c4de41dc37dcc75940a666
    xml/:
      mtime: '2026-09-05T11:16:37Z'
      digest: b50abd33702bd07f0cdb4f45502c20460b848ccb426e6d8716e9ff859ea3b053
tags:
- code/guid_generation
- code/directory_services
- code/xml_parsing
concepts:
- Technology Integration Demos
facets:
  layer: infrastructure
  status: legacy
  complexity: '3'
description: 'A grab-bag of small, mostly standalone demonstrations of individual Java platform technologies and third-party integration points, each self-contained rather than part of a shared application: GUID generation and thread-synchronization semantics at the root, JNDI directory browsing (`jndi/`), the Java Sound API (`sound/`), a reflection-attribute stream processing framework (`stream/`), and a substantial SAX/DOM/XSLT toolkit built around reflection-based event dispatch, together with the Castor-generated ZKDB message data model it unmarshals into (`xml/`). `jdbc/` and `rmi/` currently hold only non-Java package descriptors, with no Java sources of their own.'
dv_has_:
  sub_:
    folders: 5
    files: 92
    units: 47
    facet_:
      layer_:
        domain: 20
        infrastructure: 18
        utility: 6
        test: 3
      status_:
        legacy: 43
        broken: 4
      complexity_:
        "2": 37
        "3": 9
        "4": 1
    tag_:
      code_:
        data_transfer_object: 21
        sax_parsing: 5
        stream_adapter: 8
        xml_parsing: 5
        reflection_dispatch: 3
        directory_services: 3
        xml_deserialization: 2
        xslt_transformation: 2
        audio: 2
        thread_synchronization: 1
    concept_:
      castor_data_transfer_object: 16
      data_transfer: 2
      custom_exception_type: 2
      attributed_stream_base_class: 1
      attributed_stream_input_interface: 1
      attributed_stream_output_interface: 1
      attributed_stream_processing: 1
      base: 1
      castor_generated_data_model: 1
      command_line_jndi_browser: 1
has_sub_folders: 5
has_sub_files: 92
has_sub_units: 47
has_sub_facet_layer_domain: 20
has_sub_facet_layer_infrastructure: 18
has_sub_facet_layer_utility: 6
has_sub_facet_layer_test: 3
has_sub_facet_status_legacy: 43
has_sub_facet_status_broken: 4
has_sub_facet_complexity_2: 37
has_sub_facet_complexity_3: 9
has_sub_facet_complexity_4: 1
has_sub_tag_code_data_transfer_object: 21
has_sub_tag_code_sax_parsing: 5
has_sub_tag_code_stream_adapter: 8
has_sub_tag_code_xml_parsing: 5
has_sub_tag_code_reflection_dispatch: 3
has_sub_tag_code_directory_services: 3
has_sub_tag_code_xml_deserialization: 2
has_sub_tag_code_xslt_transformation: 2
has_sub_tag_code_audio: 2
has_sub_tag_code_thread_synchronization: 1
has_sub_concept_castor_data_transfer_object: 16
has_sub_concept_data_transfer: 2
has_sub_concept_custom_exception_type: 2
has_sub_concept_attributed_stream_base_class: 1
has_sub_concept_attributed_stream_input_interface: 1
has_sub_concept_attributed_stream_output_interface: 1
has_sub_concept_attributed_stream_processing: 1
has_sub_concept_base: 1
has_sub_concept_castor_generated_data_model: 1
has_sub_concept_command_line_jndi_browser: 1
---

# technology

A grab-bag of small, mostly standalone demonstrations of individual Java platform
technologies and third-party integration points, each self-contained rather than part of a
shared application: GUID generation and thread-synchronization semantics at the root, JNDI
directory browsing (`jndi/`), the Java Sound API (`sound/`), a reflection-attribute stream
processing framework (`stream/`), and a substantial SAX/DOM/XSLT toolkit built around
reflection-based event dispatch, together with the Castor-generated ZKDB message data model
it unmarshals into (`xml/`). `jdbc/` and `rmi/` currently hold only non-Java package
descriptors, with no Java sources of their own.

## Classes

| Class | Responsibility |
|---|---|
| [RandomGUID](RandomGUID.java) | Generates a globally-unique, cryptographically-seeded 128-bit GUID by MD5-hashing the local host address, the<br/>current time and a random number. |
| [Threading](Threading.java) | Demonstrates that Thread Synchronization in Java is reentrant (unlike .NET!) |

## Entry Points

| Class.Method | Description |
|---|---|
| [RandomGUID.main(String[])](RandomGUID.java#L227) | Generates and prints 100 sample GUIDs to demonstrate and self-test the class. |
| [Threading.main(String[])](Threading.java#L31) | Runs recourse(5) to demonstrate reentrant synchronization. |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `jndi/` | Small demonstrations of the Java Naming and Directory Interface (JNDI): acquiring an | `CmdLnBrowser` |
| `sound/` | A single demonstration of the Java Sound API: reading a WAV file's format and stream, and | `WaveReader` |
| `stream/` | A small framework for passing an `InputStream` plus a `Map` of attributes along a processing | `AAttributedStream` |
| `xml/` | A collection of SAX/DOM/XSLT tools built around a reflection-based dispatch pattern: | `DamlHandler` |
