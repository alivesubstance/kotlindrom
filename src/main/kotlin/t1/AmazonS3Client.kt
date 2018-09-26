package t1

import com.amazonaws.auth.SystemPropertiesCredentialsProvider
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.AmazonS3URI
import com.amazonaws.util.IOUtils
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

object AmazonS3Client {

    private val s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(System.getProperty("AWS_DEFAULT_REGION", "us-west-2"))
                .withCredentials(SystemPropertiesCredentialsProvider())
                .build()!!

    fun get(path: String) : File? {
        println("Start downloading file $path")

        val uri = AmazonS3URI(path)
        val s3Object = s3Client.getObject(uri.bucket, uri.key)
        if (s3Object == null) {
            println("S3 object is not found by path $path")

            return null
        }

        val tempFile = File(SystemUtils.JAVA_IO_TMPDIR, s3Object.key.substringAfterLast('/'))
        FileOutputStream(tempFile).use {
            IOUtils.copy(s3Object.objectContent, FileOutputStream(tempFile))
        }

        println("File $path downloaded to $tempFile")

        return tempFile
    }

}