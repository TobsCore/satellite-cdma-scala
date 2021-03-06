package hska.embedded

import java.io.{FileNotFoundException, IOException}

import akka.actor.{ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import hska.embedded.Reaper.WatchMe

import scala.io.Source

object Application extends LazyLogging {

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

  def decode(sumSignals: Array[Int]): List[DecodedInformation] = {
    val resultList = scala.collection.mutable.MutableList[DecodedInformation]()

    logger.debug("Generating Chip Sequences")
    val chipSequencesWithZeros: Array[List[Int]] = ChipSequenceGenerator.generate()
    val chipSequences: Array[List[Int]] = ChipSequenceGenerator.negateZeros(chipSequencesWithZeros)

    logger.debug("Start Decoding Procedure")
    val system = ActorSystem("DecoderSystem")
    val reaper = system.actorOf(Props(new ProductionReaper()))
    chipSequences.zipWithIndex.foreach { case (chipSequence, satelliteID) =>
      val worker = system.actorOf(Props(new Decoder(chipSequence, satelliteID, sumSignals)), name = s"DecodeActorForSatellite$satelliteID")
      worker ! "decode"
      reaper ! WatchMe(worker)
    }
    resultList.toList
  }

  def dupliateEntriesMethod(list: Array[Int]): Array[Int] = {
    val resultArray: Array[Int] = new Array[Int](list.length * 2)
    for ((_, index) <- list.zipWithIndex) {
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
        logger.info(s"Reading file $filename")
        val sumSignals: Array[Int] = fileContents.split(" ").map(_.toInt)
        val entriesDup: Array[Int] = dupliateEntriesMethod(sumSignals)


        decode(entriesDup)
        //val resultSet: Seq[DecodedInformation] = decode(entriesDup);
        //resultSet.foreach(println)
      } catch {
        case ex: FileNotFoundException => logger.error(s"File $filename cannot be found")
        case ex: IOException => logger.error(s"File $filename cannot be read")
      }
    }
  }
}
