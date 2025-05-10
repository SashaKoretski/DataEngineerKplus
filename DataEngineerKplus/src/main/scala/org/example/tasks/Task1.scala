// Решение первого задания

package org.example.tasks

import org.example.model.EventRecord

object Task1 {

  def run(records: List[EventRecord]): Unit = {
    val targetDoc = "ACC_45616"

    val totalCount = records
      .filter(r =>
        r.actionType == "SEARCH" &&
          r.searchType == "CARD" &&
          r.documentId == targetDoc
      )
      .map(_.count)
      .sum

    println(s"\nКоличество раз, когда в карточке производили поиск документа с идентификатором $targetDoc: $totalCount")
  }
}
