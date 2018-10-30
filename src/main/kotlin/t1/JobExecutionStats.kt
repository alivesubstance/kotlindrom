package t1

import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.io.File
import java.lang.RuntimeException
import java.nio.file.Files

fun main(args: Array<String>) {
    val logsDir = "/home/mirian/downloads/prod_20181029_075946"
//    val logsDir = "/home/mirian/downloads/uat_20181029_030002"

    File(logsDir).listFiles { dir, name -> name.endsWith(".log") }
            .filter { !it.nameWithoutExtension.contains("poll") }
            .sortedBy { it.nameWithoutExtension }
            .forEach {
                val file = File(it.toURI())

                val lines = file.readLines()
                val begin = readDateTime(lines[7])
                val end = readDateTime(lines[lines.size - 1])

                if (begin != null && end != null) {
                    print("${file.nameWithoutExtension} -> ")
                    println(Seconds.secondsBetween(begin, end).seconds)
                }
            }

}

private fun readDateTime(log: String): DateTime? {
    return try {
        val datetime = log.substring(7, 30).replace(" ", "T").replace(",", ".")
        DateTime.parse(datetime, ISODateTimeFormat.dateHourMinuteSecondFraction())
    } catch (e: Exception) {
        null
    }

}