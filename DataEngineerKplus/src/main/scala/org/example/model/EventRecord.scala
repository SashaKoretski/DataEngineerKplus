// Модель данных, используемая для хранения таблицы

package org.example.model

import java.time.LocalDate

case class EventRecord(
                        actionType: String,
                        searchType: String,
                        date: LocalDate,
                        documentId: String,
                        count: Int = 1
                      )
