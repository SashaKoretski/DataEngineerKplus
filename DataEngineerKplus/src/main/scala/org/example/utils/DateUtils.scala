package org.example.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
  private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  def extractDate(line: String): Option[LocalDate] = {
    val pattern = """(\d{2}\.\d{2}\.\d{4})""".r
    pattern.findFirstIn(line).map(LocalDate.parse(_, formatter))
  }
}
