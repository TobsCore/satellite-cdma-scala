package hska.embedded

import java.io.FileNotFoundException

import scala.io.Source

object Application {

  def decode(entries: Array[Int]): List[DecodedInformation] = {

    null
  }

  def main(args: Array[String]): Unit = {

    val filename: String = args(0)

    try {
      val fileContents = Source.fromFile(filename).getLines.mkString
      val entries: Array[Int] = fileContents.split(" ").map(_.toInt)
      val resultSet: Seq[DecodedInformation] = decode(entries);
    } catch {
      case ex: FileNotFoundException => println(s"File ${filename} cannot be found")
      case ex: Exception => println(s"File ${filename} cannot be read")
    }
  }
}
