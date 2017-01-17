package hska.embedded

import scala.collection.mutable.ListBuffer

object ChipSequenceGenerator {


  val satelliteBitMap: Seq[(Int, Int)] = Seq((2, 6), (3, 7), (4, 8), (5, 9), (1, 9), (2, 10), (1, 8), (2, 9), (3, 10), (2, 3), (3, 4), (5, 6), (6, 7), (7, 8), (8, 9), (9, 10), (1, 4), (2, 5), (3, 6), (4, 7), (5, 8), (6, 9), (1, 3), (4, 6))
  val defaultUpperBitSequence: Array[Int] = Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
  val defaultLowerBitSequence: Array[Int] = Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
  var allSeq: Array[List[Int]] = _

  def generate(): Array[List[Int]] = {
    val allSequences: Array[List[Int]] = new Array(24)
    for (i <- 1 to 24) {
      val chipSequence: List[Int] = calcChipSequence(i)
      allSequences(i - 1) = chipSequence
    }
    allSequences
  }

  def negateZeros(list: Array[List[Int]]): Array[List[Int]] = {
    val newArray = new Array[List[Int]](list.size)
    for ((satellite, index) <- list.zipWithIndex) {
      val buffer: ListBuffer[Int] = new ListBuffer[Int]
      satellite.map(e => if (e == 0) {buffer += -1} else {buffer += 1})
      newArray(index) = buffer.toList
    }

    newArray
  }

  def calcUpperSequence(bitSequence: Array[Int], depth: Int): List[Int] = {
    if (depth >= 1023) {
      Nil
    }
    else if (depth < 0) {
      throw new IllegalArgumentException(s"Depth cannot be less than 0, but is [${depth}]")
    }
    else {
      val firstBit = bitSequence(9) ^ bitSequence(2);

      val next: Array[Int] = Array.tabulate(10)(_ => 0)
      next(0) = firstBit
      for ((element, index) <- bitSequence.zipWithIndex) {
        if (index < 9) {
          next(index + 1) = element
        }
      }
      val generateBit = bitSequence(9)
      generateBit :: calcUpperSequence(next, depth + 1)
    }
  }

  def calcLowerSequence(bitSequence: Array[Int], satelliteID: Int, depth: Int): List[Int] = {
    if (depth >= 1023) {
      Nil
    } else if (depth < 0) {
      throw new IllegalArgumentException(s"Depth cannot be less than 0, but is [${depth}]")
    } else {
      val firstBit = bitSequence(1) ^ bitSequence(2) ^ bitSequence(5) ^ bitSequence(7) ^ bitSequence(8) ^ bitSequence(9)

      val next: Array[Int] = Array.tabulate(10)(_ => 0)
      next(0) = firstBit
      for ((element, index) <- bitSequence.zipWithIndex) {
        if (index < 9) {
          next(index + 1) = element
        }
      }

      val satellite = satelliteBitMap(satelliteID - 1)
      val generateBit = bitSequence(satellite._1 - 1) ^ bitSequence(satellite._2 - 1 )
      generateBit :: calcLowerSequence(next, satelliteID, depth + 1)
    }
  }

  def calcChipSequence(satelliteID: Int): List[Int] = {
    val upper: List[Int] = calcUpperSequence(defaultUpperBitSequence, 0)
    val lower: List[Int] = calcLowerSequence(defaultLowerBitSequence, satelliteID, 0)

    var resultList: ListBuffer[Int] = new ListBuffer[Int]
    for ((value, i) <- upper.zipWithIndex) {
      val bit = upper(i) ^ lower(i)
      resultList += bit
    }
    resultList.toList
  }
}
