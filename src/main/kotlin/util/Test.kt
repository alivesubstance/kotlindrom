package util

import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.io.FileReader
import java.io.FileWriter

fun main(args: Array<String>) {
    cloneDbSqlFiles()
}

fun cloneDbSqlFiles() {
    val dir = "/home/mirian/data/cs_metrics_db/sql/"
    val originalDateStr = "2018-09-25"
    val originalFileName = "db_sql_$originalDateStr"
    val iterations = 30

    val originalLines = FileReader(dir + originalFileName).readLines()
    var originalDate = LocalDate.parse(originalDateStr)

    repeat(iterations) {
        val nextDate = originalDate.plusDays(1)
        FileWriter(dir + "db_sql_" + nextDate).use { writer ->
            originalLines.forEach { line ->
                writer.write(line.replace(originalDateStr, nextDate.toString()))
                writer.write("\n")
            }
        }

        originalDate = nextDate
    }
}
