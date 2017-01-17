package hska.embedded

import java.io.{FileNotFoundException, IOException}
import com.typesafe.scalalogging.LazyLogging
import scala.io.Source

object Application extends LazyLogging {

  val max = 1023 - 3 * 65
  val min = -1023 + 3 * 65

  def decodeInformation(chipSequence: List[Int], delta: Int, sumSignalEntryList: Array[Int], satelliteID: Int): Option[DecodedInformation] = {
    var scalarProduct = 0
    for (bitPosition <- chipSequence.indices) {
      val position = (bitPosition + delta) % chipSequence.length
      scalarProduct += chipSequence(position) * sumSignalEntryList(bitPosition)
    }

    if (scalarProduct < max && scalarProduct > min) {
       None
    } else if (scalarProduct >= max) {
      Some(new DecodedInformation(satelliteID, 1, delta))
    } else if (scalarProduct <= min) {
      Some(new DecodedInformation(satelliteID, 0, delta))
    } else {
      None
    }
  }

  def decode(entries: Array[Int]): List[DecodedInformation] = {
    val resultList = scala.collection.mutable.MutableList[DecodedInformation]()

    logger.debug("Generating Chip Sequences")
    val chipSequencesWithZeros: Array[List[Int]] = ChipSequenceGenerator.generate
    val chipSequences: Array[List[Int]] = ChipSequenceGenerator.negateZeros(chipSequencesWithZeros)

    logger.debug("Start Decoding Procedure")
    for ((chipSequence, satelliteID) <- chipSequences.zipWithIndex) {
      logger.debug(s"Decoding Satellite ${satelliteID}")
      for (delta <- 0 until 1023) {
        val decodedInformation: Option[DecodedInformation] = decodeInformation(chipSequence, delta, entries, satelliteID)

        if (decodedInformation.isDefined) {
          println(decodedInformation.get)
        }
      }
    }
    resultList.toList
  }

  def dupliateEntriesMethod(list: Array[Int]): Array[Int] = {
    val resultArray: Array[Int] = new Array[Int](list.length * 2);
    for((entry, index) <- list.zipWithIndex) {
      resultArray(index) = list(index)
      resultArray(index + list.length) = list(index)
    }
    resultArray
  }

  def main(args: Array[String]): Unit = {
    logger.info("Starting program...")
    if (args.length <= 0) {
      println("No filename given. Will exit not.")
    } else {
      val filename: String = args(0)
      try {
        val fileContents = Source.fromFile(filename).getLines.mkString
        val entries: Array[Int] = fileContents.split(" ").map(_.toInt)
        val entriesDup: Array[Int] = dupliateEntriesMethod(entries)
        

        val resultSet: Seq[DecodedInformation] = decode(entries);
        //resultSet.foreach(println)
      } catch {
        case ex: FileNotFoundException => logger.error(s"File $filename cannot be found")
        case ex: IOException => logger.error(s"File $filename cannot be read")
      }
    }
  }
}
