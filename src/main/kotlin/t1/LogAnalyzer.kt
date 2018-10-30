package t1

import java.io.File
import java.io.FileReader
import java.util.regex.Pattern

object LogAnalyzer {

    private val JOB_MSG_PATTERN: Pattern = Pattern.compile("([\\w_]+)\\s+\\[([\\w:\\d]+)]");

    fun findBrokenJobs(logsDir: File): List<String> {
        val messageFile = findFileByName(logsDir, "message")

        val brokenJobs = arrayListOf<String>()

        val lines = FileReader(messageFile).readLines()
        //TODO remove this chit and analyze logs directly
        val jobListStartIdx = lines.withIndex().filter { it.value.contains("Execution details:") }.map { it.index }[0]
        lines.drop(jobListStartIdx).forEach {
            val matcher = JOB_MSG_PATTERN.matcher(it)
            if (matcher.find()) {
                val jobName = matcher.group(1)
                val jobStatus = matcher.group(2)
                if ("OK" != jobStatus) {
                    brokenJobs.add(jobName)
                }
            }
        }

        return brokenJobs;
    }

    fun getExceptionMessages(logFile: File): String {
        return FileReader(logFile)
                .readLines()
                .filter { it.startsWith("Caused by") || it.contains("DataLoadingException") }
                .joinToString("\n")
    }

    fun findFileByName(logsDir: File, fileName: String) = logsDir.listFiles().first { it.name.startsWith(fileName) }

}