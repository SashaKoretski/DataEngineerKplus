package org.example.utils

import org.example.model.EventRecord

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}
import java.time.LocalDate
import scala.math.Ordering

object CsvExporter {

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  def export(records: List[EventRecord], filename: String): Unit = {
    val resultsDir = Paths.get("Results").toFile
    if (!resultsDir.exists()) resultsDir.mkdirs()

    val writer = new BufferedWriter(new FileWriter(filename))

    writer.write("action_type,search_type,date,document_id,count\n")

    records.foreach { r =>
      writer.write(s"${r.actionType},${r.searchType},${r.date},${r.documentId},${r.count}\n")
    }

    writer.close()
  }
}
