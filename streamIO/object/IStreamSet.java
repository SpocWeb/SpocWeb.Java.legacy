package streamIO.object;

import streamIO.copy.group.ring.BoolRing;
import streamIO.object.enumer.container.AContainer;

/**
  * Merges the StreamIn Interface with the Boolean and IntegrityRing Interfaces
  * to work on (possibly streaming) individual and integer Objects.
  * Interface Merge for both StreamSet and AContainer
  * Defines all common Operations of these two Classes.
  *
  * Subclasses:
  * @see StreamSet
  * @see AContainer
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:31Z
  * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public interface IStreamSet
extends BoolRing, IStreamIn {
}