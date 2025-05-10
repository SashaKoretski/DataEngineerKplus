package org.example.tasks

import org.example.model.EventRecord

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.io.StdIn
import scala.util.Try
import scala.math.Ordering

object FlexibleQuery {

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  def run(records: List[EventRecord]): Unit = {
    println("\n[ФИЛЬТРАЦИЯ ПО ПОЛЯМ] Нажмите Enter, чтобы пропустить поле.")

    println("1. Тип действия (1 - SEARCH, 2 - OPEN):")
    val actionTypeFilter = StdIn.readLine().trim match {
      case "1" => Some("SEARCH")
      case "2" => Some("OPEN")
      case _   => None
    }

    println("2. Тип поиска (1 - QS, 2 - CARD):")
    val searchTypeFilter = StdIn.readLine().trim match {
      case "1" => Some("QS")
      case "2" => Some("CARD")
      case _   => None
    }

    println("3. Дата (в формате yyyy-MM-dd):")
    val dateFilter = StdIn.readLine().trim match {
      case "" => None
      case str =>
        Try(LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE)).toOption match {
          case Some(date) => Some(date)
          case None =>
            println("Некорректный формат даты. Игнорируем фильтр по дате.")
            None
        }
    }

    println("4. Идентификатор документа:")
    val docIdFilter = StdIn.readLine().trim match {
      case "" => None
      case other => Some(other)
    }

    println("5. Точное значение count:")
    val countFilter = StdIn.readLine().trim match {
      case "" => None
      case str =>
        Try(str.toInt).toOption match {
          case Some(c) => Some(c)
          case None =>
            println("Некорректный формат числа.")
            None
        }
    }

    println("По какому столбцу из представленных выше отсортировать результат (введите число от 1 до 5)?")
    val sortField = StdIn.readLine().trim

    println("Сортировка по (1 - возрастанию, 2 - убыванию):")
    val sortDirection = StdIn.readLine().trim
    val ascending = sortDirection != "2"

    val sorted = sortField match {
      case "1" => if (ascending) records.sortBy(_.actionType) else records.sortBy(_.actionType).reverse
      case "2" => if (ascending) records.sortBy(_.searchType) else records.sortBy(_.searchType).reverse
      case "3" => if (ascending) records.sortBy(_.date)       else records.sortBy(_.date).reverse
      case "4" => if (ascending) records.sortBy(_.documentId) else records.sortBy(_.documentId).reverse
      case "5" => if (ascending) records.sortBy(_.count)      else records.sortBy(_.count).reverse
      case _   => if (ascending) records.sortBy(_.date)       else records.sortBy(_.date).reverse
    }

    println("Сколько строк результата вывести?")
    val limit = Try(StdIn.readLine().trim.toInt).toOption.getOrElse(10)

    val filtered = sorted
      .filter(r =>
        actionTypeFilter.forall(_ == r.actionType) &&
          searchTypeFilter.forall(_ == r.searchType) &&
          dateFilter.forall(_ == r.date) &&
          docIdFilter.forall(_ == r.documentId) &&
          countFilter.forall(_ == r.count)
      )
      .take(limit)

    if (filtered.isEmpty) {
      println("\nПодходящих записей не найдено.")
    } else {
      println(f"\nНайдено записей: ${filtered.size}")
      println(f"${"Тип"}%-10s ${"Поиск"}%-6s ${"Дата"}%-12s ${"Документ"}%-20s ${"Количество"}")
      println("-" * 65)
      filtered.foreach { r =>
        println(f"${r.actionType}%-10s ${r.searchType}%-6s ${r.date}%-12s ${r.documentId}%-20s ${r.count}")
      }
    }
  }
}
