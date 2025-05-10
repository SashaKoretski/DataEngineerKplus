// Решение второго задания

package org.example.tasks

import org.example.model.EventRecord
import scala.util.Try

object Task2 {

  def run(records: List[EventRecord]): Unit = {
    println("\nКоличество открытий каждого документа, найденного через быстрый поиск за каждый день.")
    println("\n Сколько строк результата вы хотите вывести?")
    val input = scala.io.StdIn.readLine()

    val limit = Try(input.toInt).toOption match {
      case Some(value) if value > 0 => value
      case _ =>
        println("Некорректный ввод. Выводим 10 строк.")
        10
    }

    val result = records
      .filter(r => r.actionType == "OPEN" && r.searchType == "QS")
      .groupBy(r => (r.date, r.documentId))
      .map { case ((date, docId), group) =>
        (date, docId, group.map(_.count).sum)
      }
      .toList
      .sortBy { case (date, docId, count) => (-count, date.toString, docId) }

    printf("\n%-12s %-20s %s\n", "Дата", "Документ", "Количество")
    println("-" * 45)
    result.take(limit).foreach { case (date, docId, count) =>
      println(f"$date%-12s $docId%-20s $count")
    }
  }
}
