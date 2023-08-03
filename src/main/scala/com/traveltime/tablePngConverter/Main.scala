package com.traveltime.tablePngConverter

object Main {
  def main(args: Array[String]): Unit = {
    val table: List[List[String]] = List(
      List("", "column1111111111111111111111", "column2"),
      List("row1", "value", "value"),
      List("row2", "value", "value")
    )

    val outputPath = "output.png"

    TableImageService.runApplication(table, outputPath)
  }
}
