package org.example.utils

import org.example.model.EventRecord

object AggregationUtils {

  def groupAndAggregate(records: List[EventRecord]): List[EventRecord] = {
    records
      .groupBy(r => (r.actionType, r.searchType, r.date, r.documentId))
      .map {
        case ((actionType, searchType, date, docId), group) =>
          EventRecord(actionType, searchType, date, docId, group.map(_.count).sum)
      }
      .toList
  }
}
