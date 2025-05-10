package org.example

import org.example.model.EventRecord
import org.example.service.LogBatchProcessor
import org.example.utils.AggregationUtils

import java.nio.file.Paths

object MainApp {
  def main(args: Array[String]): Unit = {
    val baseDir = Paths.get(".").toAbsolutePath.normalize()
    val allRecordsRaw: List[EventRecord] = LogBatchProcessor.processAllLogs(baseDir)
    val allRecords: List[EventRecord] = AggregationUtils.groupAndAggregate(allRecordsRaw)

    Menu.show(allRecords)
  }
}
