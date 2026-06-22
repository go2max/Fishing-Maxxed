package com.maxxed.fishingmaxxed

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class JournalStore(context: Context) {
    private val prefs = context.getSharedPreferences("fishing_journal_v1", Context.MODE_PRIVATE)

    fun load(): List<CatchRecord> = runCatching {
        val array = JSONArray(prefs.getString("records", "[]"))
        List(array.length()) { decode(array.getJSONObject(it)) }.sortedByDescending { it.createdAt }
    }.getOrDefault(emptyList())

    fun save(records: List<CatchRecord>): Boolean = runCatching {
        val array = JSONArray()
        records.forEach { array.put(encode(it)) }
        prefs.edit().putString("records", array.toString()).commit()
    }.getOrDefault(false)

    companion object {
        fun exportCsv(records: List<CatchRecord>, includePrivateCoordinates: Boolean = false): String {
            val header = mutableListOf("id", "date_epoch_ms", "species", "length_inches", "uncertainty_inches", "status", "public_region", "notes")
            if (includePrivateCoordinates) header += listOf("latitude", "longitude")
            return buildString {
                appendLine(header.joinToString(","))
                records.forEach { record ->
                    val row = mutableListOf(record.id, record.createdAt.toString(), record.speciesName,
                        record.lengthInches?.toString().orEmpty(), record.uncertaintyInches?.toString().orEmpty(),
                        record.status.name, record.publicRegion, record.notes)
                    if (includePrivateCoordinates) row += listOf(record.latitude?.toString().orEmpty(), record.longitude?.toString().orEmpty())
                    appendLine(row.joinToString(",") { csv(it) })
                }
            }
        }
        private fun csv(value: String) = "\"${value.replace("\"", "\"\"")}\""
        private fun encode(r: CatchRecord) = JSONObject().apply {
            put("id", r.id); put("createdAt", r.createdAt); put("speciesId", r.speciesId); put("speciesName", r.speciesName)
            put("speciesConfirmed", r.speciesConfirmed); put("variant", r.variant); put("length", r.lengthInches)
            put("uncertainty", r.uncertaintyInches); put("confidence", r.confidence?.name); put("weight", r.weightPounds)
            put("notes", r.notes); put("method", r.method); put("status", r.status.name); put("photoPath", r.photoPath)
            put("latitude", r.latitude); put("longitude", r.longitude); put("publicRegion", r.publicRegion)
            put("origin", r.origin.name)
            put("decision", r.ruleDecision.name); put("ruleSummary", r.ruleSummary)
        }
        private fun decode(o: JSONObject) = CatchRecord(
            id = o.getString("id"), createdAt = o.getLong("createdAt"), speciesId = o.stringOrNull("speciesId"),
            speciesName = o.optString("speciesName", "Unknown species"), speciesConfirmed = o.optBoolean("speciesConfirmed"),
            variant = o.optString("variant"), lengthInches = o.doubleOrNull("length"), uncertaintyInches = o.doubleOrNull("uncertainty"),
            confidence = o.stringOrNull("confidence")?.let { runCatching { Confidence.valueOf(it) }.getOrNull() },
            weightPounds = o.doubleOrNull("weight"), notes = o.optString("notes"), method = o.optString("method"),
            status = runCatching { CatchStatus.valueOf(o.optString("status")) }.getOrDefault(CatchStatus.UNVERIFIED),
            photoPath = o.stringOrNull("photoPath"), latitude = o.doubleOrNull("latitude"), longitude = o.doubleOrNull("longitude"),
            publicRegion = o.optString("publicRegion", "Region withheld"),
            origin = runCatching { Origin.valueOf(o.optString("origin")) }.getOrDefault(Origin.UNKNOWN),
            ruleDecision = runCatching { RuleDecision.valueOf(o.optString("decision")) }.getOrDefault(RuleDecision.UNABLE_TO_VERIFY),
            ruleSummary = o.optString("ruleSummary")
        )
        private fun JSONObject.stringOrNull(key: String) = if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }
        private fun JSONObject.doubleOrNull(key: String) = if (isNull(key) || !has(key)) null else optDouble(key).takeIf { !it.isNaN() }
    }
}
