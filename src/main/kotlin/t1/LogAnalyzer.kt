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
        val jobListStartIdx = lines.indexOf("Staging Execution details:")
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

    fun getExceptionMessages(logsDir: File, jobName: String): String {
        return FileReader(findFileByName(logsDir, jobName))
                .readLines()
                .filter { it.startsWith("Caused by") || it.contains("DataLoadingException") }
                .joinToString("\n")
    }

    private fun findFileByName(logsDir: File, fileName: String) = logsDir.listFiles().first { it.name.startsWith(fileName) }

}