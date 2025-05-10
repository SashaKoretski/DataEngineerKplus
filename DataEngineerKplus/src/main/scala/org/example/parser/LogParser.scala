package org.example.parser

import org.example.model.EventRecord
import org.example.service.LogBatchProcessor
import org.example.utils.DateUtils

import java.nio.charset.Charset
import java.nio.file.Path
import java.time.LocalDate
import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.util.matching.Regex

object LogParser {

  case class SearchMeta(searchType: String, date: LocalDate, documentIds: List[String])

  def parseLogFile(file: Path): List[EventRecord] = {
    val lines: List[String] = java.nio.file.Files.readAllLines(file, Charset.forName("windows-1251")).asScala.toList

    val searchIdToMeta = mutable.Map[String, SearchMeta]()
    val rawRecords = mutable.ListBuffer[EventRecord]()

    var currentSearchType: Option[String] = None
    var currentSearchDate: Option[LocalDate] = None
    var pendingCardDate: Option[LocalDate] = None
    var lastSearchType: Option[String] = None
    var lastSearchDate: Option[LocalDate] = None

    val docOpenPattern: Regex = """DOC_OPEN (\d{2}\.\d{2}\.\d{4})_\d{2}:\d{2}:\d{2} (\d+) (\S+)""".r
    val resultLinePattern: Regex = """^(\d+)(?:\s+(.*))?""".r

    for (i <- lines.indices) {
      val line = lines(i)

      if (line.startsWith("QS")) {
        currentSearchType = Some("QS")
        currentSearchDate = DateUtils.extractDate(line)
        lastSearchType = currentSearchType
        lastSearchDate = currentSearchDate

      } else if (line.startsWith("CARD_SEARCH_START")) {
        pendingCardDate = DateUtils.extractDate(line)

      } else if (line.startsWith("CARD_SEARCH_END")) {
        lastSearchType = Some("CARD")
        lastSearchDate = pendingCardDate

      } else if (resultLinePattern.findFirstIn(line).isDefined) {
        val resultLinePattern(searchId, docsString) = line
        val documentIds = docsString.trim.split("\\s+").toList

        val sType = lastSearchType.getOrElse("UNKNOWN")
        val sDate = lastSearchDate.getOrElse(LocalDate.now())

        searchIdToMeta += (searchId -> SearchMeta(sType, sDate, documentIds))
        documentIds.foreach { docId =>
          rawRecords += EventRecord("SEARCH", sType, sDate, docId)
        }

        if (sType == "QS") {
          currentSearchType = None
          currentSearchDate = None
        }

        pendingCardDate = None
      }
    }

    for (line <- lines) {
      line match {
        case docOpenPattern(_, searchId, documentId) =>
          val openDateOpt = DateUtils.extractDate(line)
          searchIdToMeta.get(searchId) match {
            case Some(SearchMeta(sType, _, _)) =>
              openDateOpt.foreach { openDate =>
                rawRecords += EventRecord("OPEN", sType, openDate, documentId)
              }
            case None =>
              LogBatchProcessor.printWithProgressPreserved(
                s"Не найден search_id $searchId в файле ${file.getFileName} для строки: $line",
                LogBatchProcessor.currentFileIndex,
                LogBatchProcessor.totalFiles,
                LogBatchProcessor.currentFileName
              )
          }
        case _ =>
      }
    }

    rawRecords
      .groupBy(r => (r.actionType, r.searchType, r.date, r.documentId))
      .map {
        case ((actionType, searchType, date, docId), group) =>
          EventRecord(actionType, searchType, date, docId, group.map(_.count).sum)
      }
      .toList
  }
}
