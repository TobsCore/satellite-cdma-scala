package hska.embedded

class DecodedInformation(val satelliteId: Int, val bitValue: Int, val delta: Int) {

  override def toString(): String = {
    // Ähnliche Syntax wie printf. Formatiert den String
    f"Satellite ${satelliteId}%2d has sent bit ${bitValue}%d (delta ${delta}%4d)"
  }
}
