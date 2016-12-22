package hska.embedded

import java.io.{FileNotFoundException, IOException}

import scala.collection.mutable.ListBuffer
import scala.io.Source

object Application {

  def decode(entries: Array[Int]): List[DecodedInformation] = {

    val chipSequences: Array[List[Int]] = ChipSequenceGenerator.generate();

    for (i <- 0 until 1023) {

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
