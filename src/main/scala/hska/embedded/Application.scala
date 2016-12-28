package hska.embedded

import java.io.{FileNotFoundException, IOException}

import scala.io.Source

object Application {

  def decodeInformation(chipSequence: List[Int], delta: Int, sumSignalEntryList: Array[Int], satelliteID: Int): Option[DecodedInformation] = {
    var scalarProduct = 0;
    for (bitPosition <- 0 until chipSequence.length) {
      val position = (bitPosition + delta) % chipSequence.length
      scalarProduct += chipSequence(position) * sumSignalEntryList(bitPosition)
    }

    scalarProduct = scalarProduct / 10

    if (scalarProduct >= 63) {
      Some(new DecodedInformation(satelliteID, 1, delta))
    } else if (scalarProduct <= -65) {
      Some(new DecodedInformation(satelliteID, 0, delta))
    } else {
      None
    }
  }

  def decode(entries: Array[Int]): List[DecodedInformation] = {

    val chipSequences: Array[List[Int]] = ChipSequenceGenerator.generate();

    val resultList: List[DecodedInformation] = Nil
    for ((chipSequence, satelliteID) <- chipSequences.zipWithIndex) {
      val calcSequence = chipSequence.map(e => if(e == 0) { -1 } else {1})
      for (delta <- 0 until 1023) {
        val decodedInformation: Option[DecodedInformation] = decodeInformation(calcSequence, delta, entries, satelliteID)

        if (decodedInformation.isDefined) {
          println(decodedInformation.get.toString())
        }
      }
    }
    null
  }

  def main(args: Array[String]): Unit = {

    val filename: String = args(0)

    try {
      val fileContents = Source.fromFile(filename).getLines.mkString
      val entries: Array[Int] = fileContents.split(" ").map(_.toInt)
      val resultSet: Seq[DecodedInformation] = decode(entries);
    } catch {
      case ex: FileNotFoundException => println(s"File ${
        filename
      } cannot be found")
      case e: IOException => println(s"File ${
        filename
      } cannot be read")
    }
  }
}
