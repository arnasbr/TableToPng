package com.traveltime.tablePngConverter

import java.awt.{Color, Font, Graphics2D}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import scala.util.{Failure, Success, Try}

object Main {
  private def createTableImage(
      table: List[List[String]]
  ): Try[BufferedImage] = {
    val font = new Font("Courier", Font.PLAIN, 32)
    val headerFont = font.deriveFont(Font.BOLD)

    Try {
      val gTemp =
        new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics()
      gTemp.setFont(font)
      val metrics = gTemp.getFontMetrics(font)

      // Calculate cell dimensions for each column
      val columnWidths = table.transpose.map(column =>
        column.map(cell => metrics.stringWidth(cell)).max + 20
      )
      val cellHeight = metrics.getHeight + 10

      val imageWidth = columnWidths.sum
      val imageHeight = cellHeight * table.size

      val bufferedImage =
        new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
      val g = bufferedImage.createGraphics()

      g.setFont(font)
      g.setColor(Color.white)
      g.fillRect(0, 0, imageWidth, imageHeight)

      val cellOps = table.zipWithIndex.flatMap { case (row, i) =>
        row.zipWithIndex.map { case (cell, j) =>
          val x = columnWidths.slice(0, j).sum
          val y = i * cellHeight
          val cellWidth = columnWidths(j)

          (g: Graphics2D) => {
            // Color alternate rows
            if (i % 2 == 1) {
              g.setColor(new Color(235, 235, 235))
              g.fillRect(x, y, cellWidth, cellHeight)
            }

            // Draw cell border
            g.setColor(Color.black)
            g.drawRect(x, y, cellWidth, cellHeight)

            // Use header font for first row
            if (i == 0) {
              g.setFont(headerFont)
              g.setColor(Color.blue)
            } else {
              g.setFont(font)
              g.setColor(Color.black)
            }

            // Adjust text placement in the cell
            g.drawString(cell, x + 10, y + cellHeight - 10)
          }
        }
      }

      cellOps.foreach(op => op(g))

      g.dispose()

      bufferedImage
    }
  }

  def main(args: Array[String]): Unit = {
    val table = List(
      List("", "column1111111111111111111111", "column2"),
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
