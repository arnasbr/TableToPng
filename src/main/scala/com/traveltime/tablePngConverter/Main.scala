package com.traveltime.tablePngConverter

import java.awt.{Color, Font}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import scala.util.{Try, Failure, Success}

object Main {
  private def createTableImage(
      table: List[List[String]]
  ): Try[BufferedImage] =
    Try {
      val cellWidth = 100
      val cellHeight = 30
      val font = new Font("Arial", Font.PLAIN, 12)

      val imageWidth = cellWidth * table.head.size
      val imageHeight = cellHeight * table.size

      val bufferedImage =
        new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
      val g = bufferedImage.createGraphics()

      g.setFont(font)
      g.setColor(Color.white)
      g.fillRect(0, 0, imageWidth, imageHeight)
      g.setColor(Color.black)

      table.zipWithIndex.flatMap { case (row, i) =>
        row.zipWithIndex.map { case (cell, j) =>
          val x = j * cellWidth
          val y = i * cellHeight
          g.drawRect(x, y, cellWidth, cellHeight)
          g.drawString(cell, x + 5, y + (cellHeight / 2))
        }
      }

      g.dispose()

      bufferedImage
    }

  def main(args: Array[String]): Unit = {
    val table = List(
      List("", "column1", "column2"),
      List("row1", "value", "value"),
      List("row2", "value", "value")
    )
    val path = "output.png"

    val result = createTableImage(table)

    result match {
      case Success(bufferedImage) =>
        Try(ImageIO.write(bufferedImage, "png", new File(path))) match {
          case Success(_) => println("Image successfully written.")
          case Failure(exception) =>
            println(s"Failed to write image: $exception")
        }
      case Failure(exception) => println(s"Failed to create image: $exception")
    }
  }
}
