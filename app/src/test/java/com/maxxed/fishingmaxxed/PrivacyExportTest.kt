package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test

class PrivacyExportTest {
    private val record = CatchRecord(speciesId = "x", speciesName = "Test fish", speciesConfirmed = true, lengthInches = 12.0,
        uncertaintyInches = .3, confidence = Confidence.MEDIUM, weightPounds = null, notes = "private spot", method = "rod and reel",
        status = CatchStatus.RELEASED, photoPath = "/private/photo.jpg", latitude = 38.123456, longitude = -121.123456,
        publicRegion = "Sacramento Valley", origin = Origin.UNKNOWN, ruleDecision = RuleDecision.UNABLE_TO_VERIFY, ruleSummary = "fixture")
    @Test fun defaultExportRedactsCoordinatesAndPhotoPath() {
        val csv = JournalStore.exportCsv(listOf(record))
        assertFalse(csv.contains("38.123456")); assertFalse(csv.contains("-121.123456")); assertFalse(csv.contains("/private/photo.jpg"))
        assertTrue(csv.contains("Sacramento Valley"))
    }
    @Test fun privateExportRequiresExplicitOptIn() { assertTrue(JournalStore.exportCsv(listOf(record), true).contains("38.123456")) }
}
