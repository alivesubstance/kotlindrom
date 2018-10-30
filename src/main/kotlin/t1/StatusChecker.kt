package t1

import java.io.File

fun main(args: Array<String>) {
    analyzeLogsFromDb()

//    JdbcClient.getJobHistoryStats()
}

fun analyzeLogsFromDb() {
    val gzipFile: File?
    val gzipUrl = System.getProperty("logsUrl")
    if (gzipUrl.startsWith("s3")) {
        gzipFile = AmazonS3Client.get(gzipUrl)
    } else {
        gzipFile = File(gzipUrl)
    }

    if (!gzipFile?.exists()!!) {
        println("Archive file $gzipUrl is not exists")
        return
    }

    val ungzipDir: File = ArchiveUtil.ungzip(gzipFile)
    val logsDir = File(ungzipDir.absolutePath + File.separator + ungzipDir.name.substringAfterLast('-'))
    println("Logs ungzip to $logsDir")

    val brokenJobs = LogAnalyzer.findBrokenJobs(logsDir)
    println("List of broken jobs is:\n\t - ${brokenJobs.joinToString("\n\t - ")}\n")

    brokenJobs.forEach {
        val logFile = LogAnalyzer.findFileByName(logsDir, it)
        println("---$it, $logFile")
        println(LogAnalyzer.getExceptionMessages(logFile))
        println()
    }
}

