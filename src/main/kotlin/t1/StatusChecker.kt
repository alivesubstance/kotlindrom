package t1

import java.io.File

fun main(args: Array<String>) {
    analyzeLogsFromDb("/home/mirian/downloads/t1-pipeline-logs-20180925_000001.tar.gz")

//    JdbcClient.getJobHistoryStats()
}

fun analyzeLogsFromDb(gzipUrl: String) {
    val gzipFile: File?
    if (gzipUrl.startsWith("s3")) {
        gzipFile = AmazonS3Client.get(System.getProperty("s3LogsUrl"))
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
        println("Analyzing job ---$it---")
        println(LogAnalyzer.getExceptionMessages(logsDir, it))
        println()
    }
}

