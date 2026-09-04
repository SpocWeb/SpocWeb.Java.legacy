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
  */
public interface IStreamSet
extends BoolRing, IStreamIn {
}