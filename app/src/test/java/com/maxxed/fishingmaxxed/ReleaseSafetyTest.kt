package com.maxxed.fishingmaxxed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ReleaseSafetyTest {
    @Test
    fun measurementCalculatorProducesExpectedLengthAndConfidence() {
        val measurement = MeasurementCalculator.calculate(
            MeasurementInput(
                fishStart = Point(0.10f, 0.50f),
                fishEnd = Point(0.90f, 0.50f),
                referenceStart = Point(0.10f, 0.80f),
                referenceEnd = Point(0.30f, 0.80f),
                referenceLength = 6.0,
                referenceUncertainty = 0.05
            )
        )

        assertNotNull(measurement)
        assertEquals(24.0, measurement!!.length, 0.01)
        assertEquals(Confidence.HIGH, measurement.confidence)
        assertTrue(measurement.uncertainty > 0.0)
    }

    @Test
    fun invalidReferenceDoesNotCreateMeasurement() {
        val measurement = MeasurementCalculator.calculate(
            MeasurementInput(
                fishStart = Point(0.10f, 0.50f),
                fishEnd = Point(0.90f, 0.50f),
                referenceStart = Point(0.10f, 0.80f),
                referenceEnd = Point(0.30f, 0.80f),
                referenceLength = 0.0
            )
        )

        assertNull(measurement)
    }

    @Test
    fun defaultCsvExportRedactsExactCoordinates() {
        val record = sampleRecord(latitude = 38.75, longitude = -121.29)
        val csv = JournalStore.exportCsv(listOf(record))

        assertFalse(csv.contains("latitude"))
        assertFalse(csv.contains("longitude"))
        assertFalse(csv.contains("38.75"))
        assertFalse(csv.contains("-121.29"))
        assertTrue(csv.contains("Sacramento Valley"))
    }

    @Test
    fun explicitPrivateCsvExportCanIncludeCoordinates() {
        val record = sampleRecord(latitude = 38.75, longitude = -121.29)
        val csv = JournalStore.exportCsv(listOf(record), includePrivateCoordinates = true)

        assertTrue(csv.contains("latitude"))
        assertTrue(csv.contains("longitude"))
        assertTrue(csv.contains("38.75"))
        assertTrue(csv.contains("-121.29"))
    }

    @Test
    fun bundledRegulationPackageCannotAuthorizeKeeperStatus() {
        val result = BundledRules.engine.evaluate(
            RuleRequest(
                zone = "Sacramento Valley",
                speciesId = "largemouth_bass",
                date = LocalDate.of(2026, 6, 29),
                lengthInches = 14.0,
                method = "rod and reel",
                retainedToday = 0
            )
        )

        assertEquals(RuleDecision.UNABLE_TO_VERIFY, result.decision)
        assertFalse(result.maySetKeeper)
    }

    @Test
    fun ambiguousRegionCannotVerifyRegulation() {
        val result = BundledRules.engine.evaluate(
            RuleRequest(
                zone = "Sacramento Valley",
                speciesId = "largemouth_bass",
                date = LocalDate.of(2026, 6, 29),
                lengthInches = 14.0,
                method = "rod and reel",
                retainedToday = 0,
                boundaryAmbiguous = true
            )
        )

        assertEquals(RuleDecision.UNABLE_TO_VERIFY, result.decision)
        assertFalse(result.maySetKeeper)
    }

    @Test
    fun localLeaderboardRanksMeasuredCatchesOnly() {
        val smaller = sampleRecord(id = "small", length = 10.0)
        val bigger = sampleRecord(id = "big", length = 18.0)
        val unmeasured = sampleRecord(id = "none", length = null)

        val ranked = CatchAnalytics.localLeaderboard(listOf(smaller, unmeasured, bigger))

        assertEquals(listOf("big", "small"), ranked.map { it.id })
    }

    private fun sampleRecord(
        id: String = "record-1",
        length: Double? = 12.5,
        latitude: Double? = null,
        longitude: Double? = null
    ) = CatchRecord(
        id = id,
        createdAt = 1_719_600_000_000L,
        speciesId = "largemouth_bass",
        speciesName = "Largemouth bass",
        speciesConfirmed = true,
        lengthInches = length,
        uncertaintyInches = length?.let { 0.5 },
        confidence = length?.let { Confidence.MEDIUM },
        weightPounds = null,
        notes = "test catch",
        method = "rod and reel",
        status = CatchStatus.LOCAL_CATCH,
        photoPath = null,
        latitude = latitude,
        longitude = longitude,
        publicRegion = "Sacramento Valley",
        origin = Origin.INTRODUCED,
        ruleDecision = RuleDecision.UNABLE_TO_VERIFY,
        ruleSummary = "Unable to verify - check official regulations"
    )
}
