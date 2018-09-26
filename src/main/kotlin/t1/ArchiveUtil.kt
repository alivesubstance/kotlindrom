package t1

import org.rauschig.jarchivelib.ArchiverFactory
import java.io.File

object ArchiveUtil {

    fun ungzip(gzipFile: File?): File {
        val ungzipDir = File(gzipFile?.absolutePath?.substringBefore(".tar.gz"))

        println("Ungzip to directory $ungzipDir")

        ungzipDir.mkdir()

        val archiver = ArchiverFactory.createArchiver("tar", "gz")
        archiver.extract(gzipFile, ungzipDir)

        return ungzipDir;
    }


}