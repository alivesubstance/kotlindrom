package liquibase

import java.io.File
import java.io.FileWriter

fun main(args: Array<String>) {
    ChangeLogFactory.start()
}

object ChangeLogFactory {

    private const val BASE_DIR = "/home/mirian/code/db-pipeline/db-pipeline-migrator/src/main/resources"
    private const val OUT_DIR = "$BASE_DIR/migration"
    private const val SCHEMA_DIR = "$BASE_DIR/schema"

    private val header: String = ChangeLogFactory::class.java.getResource("/liquibase/databaseChangeLogHeader.xml").readText()
    private val footer = ChangeLogFactory::class.java.getResource("/liquibase/databaseChangeLogFooter.xml").readText()
    private val changeSet = ChangeLogFactory::class.java.getResource("/liquibase/changesetEntry.xml").readText()

    fun start() {
        val databaseChangeLog = "001_model.xml"
        val category = "model"

        val writer = FileWriter("$OUT_DIR/$databaseChangeLog")
        writer.write(header)

        File("$SCHEMA_DIR/$category")
                .listFiles()
                .filter { !it.isDirectory }
                .sortedBy { it.name }
                .forEach { writer.write(processTemplate(it, category)) }

        writer.write(footer)
        writer.close()
    }

    private fun processTemplate(it: File, category: String): String {
        return String(changeSet.toCharArray())
                .replace("_ID_", it.nameWithoutExtension)
                .replace("_PATH_", "schema/" + category + "/" + it.name)
    }

}