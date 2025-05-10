package org.example.service

import org.example.model.EventRecord
import org.example.parser.LogParser

import java.nio.file.{Files, Path, Paths}
import scala.collection.JavaConverters._

object LogBatchProcessor {

  var currentFileIndex: Int = 0
  var totalFiles: Int = 0
  var currentFileName: String = ""

  def processAllLogs(baseProjectDir: Path): List[EventRecord] = {
    val sessionDir = baseProjectDir.resolveSibling("Sessions").toAbsolutePath.normalize()

    println(s"КРАЙНЕ РЕКОМЕНДЕТСЯ ПЕРЕД НАЧАЛОМ РАБОТЫ В ПРИЛОЖЕНИИ\n " +
      s"ОЗНАКОМИТЬСЯ С ОПИСАНИЕМ ПРОЕКТА https://github.com/SashaKoretski/DataEngineerKplus\n")

    if (!Files.exists(sessionDir) || !Files.isDirectory(sessionDir)) {
      println(s"Папка не найдена: $sessionDir")
      sys.exit(1)
    }

    val sessionFiles = Files.list(sessionDir).iterator().asScala.toList
      .filter(Files.isRegularFile(_))

    totalFiles = sessionFiles.size
    println(s"Найдено файлов: $totalFiles\n")
    println(s"Чтение файлов:")

    val allRecords = sessionFiles.zipWithIndex.flatMap { case (file, index) =>
      currentFileIndex = index + 1
      currentFileName = file.getFileName.toString
      printProgressBar(currentFileIndex, totalFiles, currentFileName)
      LogParser.parseLogFile(file)
    }

    println()
    allRecords
  }

  def printProgressBar(current: Int, total: Int, fileName: String): Unit = {
    val percent = current * 100 / total
    val barWidth = 30
    val filled = (percent * barWidth) / 100
    val bar = "#" * filled + "-" * (barWidth - filled)
    print(f"\r[$bar] $percent%3d%% ($current / $total) - $fileName")
  }

  def printWithProgressPreserved(warning: String, current: Int, total: Int, fileName: String): Unit = {
    val percent = current * 100 / total
    val barWidth = 30
    val filled = (percent * barWidth) / 100
    val bar = "#" * filled + "-" * (barWidth - filled)

    print("\r\u001b[2K")
    println(warning)
    print(f"\r[$bar] $percent%3d%% ($current / $total) - $fileName")
  }
}
