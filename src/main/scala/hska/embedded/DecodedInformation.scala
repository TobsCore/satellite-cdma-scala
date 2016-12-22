package hska.embedded

/**
  * Created by Tobias Kerst on 22.12.16.
  */
class DecodedInformation(val satelliteId: Int, val bitValue: Int, val delta: Int) {

  override def toString(): String = {
    s"Satellite ${satelliteId} has sent bit ${bitValue} (delta ${delta})"
  }
}
