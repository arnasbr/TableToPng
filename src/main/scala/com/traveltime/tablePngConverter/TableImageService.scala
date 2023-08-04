package com.traveltime.tablePngConverter

import java.awt.{Color, Font, FontMetrics, Graphics2D}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

object TableImageService {
  private val font = new Font("Courier", Font.PLAIN, 32)
  private val headerFont = font.deriveFont(Font.BOLD)

  private def createTableImage(
      table: List[List[String]],
      outputPath: String
  ): Unit = {
    val metrics = getFontMetrics(font)
    val columnWidths = getColumnWidths(table, metrics)
    val cellHeight = metrics.getHeight + 10

    val imageWidth = columnWidths.sum
    val imageHeight = cellHeight * table.size

    val bufferedImage =
      new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
    val g = bufferedImage.createGraphics()

    g.setFont(font)
    g.setColor(Color.white)
    g.fillRect(0, 0, imageWidth, imageHeight)

    val cellOps = getCellOps(table, columnWidths, cellHeight)

    cellOps.foreach(op => op(g))

    g.dispose()

    writeImageToFile(bufferedImage, outputPath)
  }

  private def getFontMetrics(font: Font) = {
    val gTemp =
      new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics()
    gTemp.setFont(font)
    gTemp.getFontMetrics(font)
  }

  private def getColumnWidths(
      table: List[List[String]],
      metrics: FontMetrics
  ): List[Int] = {
    table.transpose.map(column =>
      column.map(cell => metrics.stringWidth(cell)).max + 20
    )
  }

  private def getCellOps(
      table: List[List[String]],
      columnWidths: List[Int],
      cellHeight: Int
  ): List[Graphics2D => Unit] = {
    table.zipWithIndex.flatMap { case (row, i) =>
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
  }

  private def writeImageToFile(image: BufferedImage, path: String): Unit = {
    Try(ImageIO.write(image, "png", new File(path))) match {
      case Success(_) => println("Image successfully written.")
      case Failure(exception) =>
        println(s"Failed to write image: $exception")
    }
  }

  private def validateStrings(
      table: List[List[String]]
  ): Either[String, List[List[String]]] = {
    val tableEithers = table.map(_.map(AdaptiveString.apply))
    val errors = tableEithers.flatten.collect { case Left(error) => error }
    if (errors.isEmpty)
      Right(tableEithers.map(_.collect { case Right(value) => value.value }))
    else
      Left(errors.mkString("\n"))
  }

  def runApplication(table: List[List[String]], outputPath: String): Unit = {
    TableImageService.validateStrings(table) match {
      case Left(error) =>
        println(s"Failed to validate table: $error")
      case Right(validTable) =>
        TableImageService.createTableImage(validTable, outputPath)
    }
  }
}
