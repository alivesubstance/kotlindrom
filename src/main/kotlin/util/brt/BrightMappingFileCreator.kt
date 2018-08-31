package util.brt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

private val outMappingFile = "/home/mirian/code/db-pipeline/db-pipeline-etl/src/main/resources/_common/config/bright/bright_firm_position_fields_config.json"

private var columns = "\"db_legal_entity\", \"position_type\", \"account_set_id\", \"account_id_source\", \"account_id\", \"account_type\", \"rmdl_security_id\", \"rmdl_fungibility_id\", \"rmdl_listing_id\", \"rmdl_primary_listing_id\", \"rmdl_ric\", \"rmdl_asset_class\", \"rmdl_isin\", \"rmdl_sedol\", \"rmdl_cusip\", \"rmdl_ticker\", \"rmdl_quick\", \"failed_receipt_qty\", \"failed_delivery_qty\", \"settled_qty\", \"pending_delivery_sd_qty1\", \"pending_receipt_sd_qty1\", \"pending_delivery_sd_qty2\", \"pending_receipt_sd_qty2\", \"pending_delivery_sd_qty3\", \"pending_receipt_sd_qty3\", \"pending_delivery_sd_qty4\", \"pending_receipt_sd_qty4\", \"pending_delivery_sd_qty5\", \"pending_receipt_sd_qty5\", \"pending_delivery_sd_qty6\", \"pending_receipt_sd_qty6\", \"pending_delivery_sd_qty7\", \"pending_receipt_sd_qty7\", \"pending_delivery_sd_qty8\", \"pending_receipt_sd_qty8\", \"pending_delivery_sd_qty9\", \"pending_receipt_sd_qty9\", \"pending_delivery_sd_qty10\", \"pending_receipt_sd_qty10\", \"orchestrator_event_id\""

fun main(args: Array<String>) {
    println("Start processing columns ${columns}")

    val fields = columns.split(",")
            .mapIndexed { index, fieldName -> JsonPathKey(fieldName.replace("\"", "").trim(), "\$.[$index]") }

    println("Found ${fields.size} columns")

    jacksonObjectMapper().writeValue(
            File(outMappingFile).printWriter(),
            JsonPathFieldsConfig("\$.table[*].row", fields)
    )

    println("Done writing JSON to CSV mapping file $outMappingFile")
}

data class JsonPathFieldsConfig(val rootPath: String, val fields: List<JsonPathKey>)
data class JsonPathKey(val fieldName: String, val jsonPath: String)