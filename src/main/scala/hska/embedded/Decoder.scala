package hska.embedded

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

class Decoder (chipSequence: List[Int], satelliteID: Int, sumSignals: Array[Int]) extends Actor {

  val max: Int = 1023 - 3 * 65
  val min: Int = -1023 + 3 * 65

  def decodeInformation(chipSequence: List[Int], delta: Int, sumSignalEntryList: Array[Int], satelliteID: Int): Option[DecodedInformation] = {
    var scalarProduct = 0
    for (bitPosition <- chipSequence.indices) {
      val position = bitPosition + delta
      scalarProduct += chipSequence(bitPosition) * sumSignalEntryList(position)
    }

    if (scalarProduct < max && scalarProduct > min) {
      // Put this statement in the beginning to improve performance as this is the most common case to reach in this method.
      None
    } else if (scalarProduct >= max) {
      Some(new DecodedInformation(satelliteID, 1, 1023 - delta))
    } else if (scalarProduct <= min) {
      Some(new DecodedInformation(satelliteID, 0, 1023 - delta))
    } else {
      None
    }
  }

  override def receive = {
    case "decode" => (0 until 1023).foreach { delta =>

      val decodedInformation: Option[DecodedInformation] = decodeInformation(chipSequence, delta, sumSignals, satelliteID)
      if (decodedInformation.isDefined) println(decodedInformation.get)
      context.stop(self)
    }
    case _ => throw new IllegalArgumentException()
  }

}
