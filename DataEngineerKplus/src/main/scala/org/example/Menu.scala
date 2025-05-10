// Вывод меню и работа с ним

package org.example

import org.example.model.EventRecord
import org.example.tasks.Task1
import org.example.utils.CsvExporter
import java.nio.file.Paths

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.io.StdIn
import scala.util.Try
import scala.math.Ordering

object Menu {

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  def show(records: List[EventRecord]): Unit = {
    implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
    var running = true

    while (running) {
      println(
        """
          |Выберите действие:
          |1 - Выполнить задание 1
          |2 - Сохранить в Results/output.csv результаты задания 2
          |3 - Вывести статистику открытых документов, найденных быстрым поиском за выбранную дату
          |4 - Поиск по всей базе
          |0 - Выход
          |""".stripMargin)

      print("Введите номер: ")
      StdIn.readLine().trim match {
        case "1" =>
          Task1.run(records)

        case "2" =>
          val filtered = records
            .filter(r => r.actionType == "OPEN" && r.searchType == "QS")
            .sortBy(_.date)
          val outputPath = Paths.get("Results/output.csv").toAbsolutePath.toString
          CsvExporter.export(filtered, outputPath)
          println(s"Файл сохранён: $outputPath")

        case "3" =>
          println("Введите дату в формате yyyy-MM-dd:")
          val inputDate = StdIn.readLine().trim
          val parsedDate = Try(LocalDate.parse(inputDate, DateTimeFormatter.ISO_LOCAL_DATE)).toOption

          parsedDate match {
            case Some(date) =>
              println("Сколько строк результата вы хотите вывести?")
              val inputLimit = Try(StdIn.readLine().trim.toInt).toOption.getOrElse(10)

              val filtered = records
                .filter(r => r.actionType == "OPEN" && r.searchType == "QS" && r.date == date)
                .sortBy(r => (-r.count, r.documentId))
                .take(inputLimit)

              if (filtered.isEmpty)
                println(s"Нет записей за $date.")
              else {
                println(f"\nРезультаты за $date:")
                println(f"${"Тип"}%-10s ${"Поиск"}%-6s ${"Дата"}%-12s ${"Документ"}%-20s ${"Количество"}")
                println("-" * 65)
                filtered.foreach { r =>
                  println(f"${r.actionType}%-10s ${r.searchType}%-6s ${r.date}%-12s ${r.documentId}%-20s ${r.count}")
                }
              }

            case None =>
              println("Некорректный формат даты.")
          }
        case "4" =>
          org.example.tasks.FlexibleQuery.run(records)

        case "0" =>
          println("Завершение программы.")
          running = false

        case other =>
          println(s"Некорректный ввод.")
      }
    }
  }
}
