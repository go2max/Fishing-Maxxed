package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test

class CatchAnalyticsTest {
    private fun record(
        id: String,
        createdAt: Long,
        length: Double?,
        status: CatchStatus
    ) = CatchRecord(
        id = id,
        createdAt = createdAt,
        speciesId = "fixture",
        speciesName = "Fixture fish",
        speciesConfirmed = true,
        lengthInches = length,
        uncertaintyInches = length?.let { 0.5 },
        confidence = length?.let { Confidence.LOW },
        weightPounds = null,
        notes = "",
        method = "rod and reel",
        status = status,
        photoPath = null,
        latitude = null,
        longitude = null,
        publicRegion = "Fixture Region",
        origin = Origin.UNKNOWN,
        ruleDecision = RuleDecision.UNABLE_TO_VERIFY,
        ruleSummary = "fixture"
    )

    @Test fun summarizesJournalCountsAndLengths() {
        val records = listOf(
            record("one", 1, 12.0, CatchStatus.RELEASED),
            record("two", 2, 18.0, CatchStatus.LOCAL_CATCH),
            record("three", 3, null, CatchStatus.UNVERIFIED)
        )

        val summary = CatchAnalytics.summarize(records)

        assertEquals(3, summary.total)
        assertEquals(2, summary.measured)
        assertEquals(1, summary.released)
        assertEquals(1, summary.unverified)
        assertEquals(0, summary.keeper)
        assertEquals(18.0, summary.bestLengthInches!!, 0.001)
        assertEquals(15.0, summary.averageLengthInches!!, 0.001)
    }

    @Test fun leaderboardSortsMeasuredRecordsByLengthThenNewest() {
        val records = listOf(
            record("older-tie", 10, 20.0, CatchStatus.LOCAL_CATCH),
            record("shorter", 30, 12.0, CatchStatus.RELEASED),
            record("unmeasured", 40, null, CatchStatus.UNVERIFIED),
            record("newer-tie", 20, 20.0, CatchStatus.LOCAL_CATCH)
        )

        val ranked = CatchAnalytics.localLeaderboard(records, limit = 2)

        assertEquals(listOf("newer-tie", "older-tie"), ranked.map { it.id })
    }
}
