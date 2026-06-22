package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class RegulationEngineTest {
    private val source = RuleSource("Fixture agency", "https://example.invalid/rules", LocalDate.of(2026, 1, 1))
    private fun rule(authoritative: Boolean = true, closed: Boolean = false, min: Double? = 12.0, max: Double? = 20.0) = RegulationRule(
        "Fixture Zone", "test_fish", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), LocalDate.of(2026, 3, 1),
        LocalDate.of(2026, 10, 31), min, max, 2, setOf("rod and reel"), closed, Origin.NATIVE, source, authoritative)
    private fun request(length: Double? = 15.0, date: LocalDate = LocalDate.of(2026, 6, 1), retained: Int = 0, method: String = "rod and reel", ambiguous: Boolean = false) =
        RuleRequest("Fixture Zone", "test_fish", date, length, method, retained, ambiguous)

    @Test fun verifiedFixtureCanReturnPotentiallyLegal() { assertTrue(RegulationEngine(listOf(rule())).evaluate(request()).maySetKeeper) }
    @Test fun undersizeAndOverslotRequireRelease() {
        assertEquals(RuleDecision.RELEASE_REQUIRED, RegulationEngine(listOf(rule())).evaluate(request(11.9)).decision)
        assertEquals(RuleDecision.RELEASE_REQUIRED, RegulationEngine(listOf(rule())).evaluate(request(20.1)).decision)
    }
    @Test fun seasonBagGearAndClosureRequireRelease() {
        val engine = RegulationEngine(listOf(rule()))
        assertEquals(RuleDecision.RELEASE_REQUIRED, engine.evaluate(request(date = LocalDate.of(2026, 2, 1))).decision)
        assertEquals(RuleDecision.RELEASE_REQUIRED, engine.evaluate(request(retained = 2)).decision)
        assertEquals(RuleDecision.RELEASE_REQUIRED, engine.evaluate(request(method = "net")).decision)
        assertEquals(RuleDecision.RELEASE_REQUIRED, RegulationEngine(listOf(rule(closed = true))).evaluate(request()).decision)
    }
    @Test fun staleAmbiguousUnknownAndUnsupportedFailClosed() {
        val engine = RegulationEngine(listOf(rule()))
        assertEquals(RuleDecision.UNABLE_TO_VERIFY, engine.evaluate(request(date = LocalDate.of(2027, 1, 1))).decision)
        assertEquals(RuleDecision.UNABLE_TO_VERIFY, engine.evaluate(request(ambiguous = true)).decision)
        assertEquals(RuleDecision.UNABLE_TO_VERIFY, engine.evaluate(request(length = null)).decision)
        assertEquals(RuleDecision.UNABLE_TO_VERIFY, RegulationEngine(listOf(rule(authoritative = false))).evaluate(request()).decision)
    }
    @Test fun bundledRulesNeverAuthorizeKeeper() {
        val result = BundledRules.engine.evaluate(RuleRequest("Sacramento Valley", "largemouth_bass", LocalDate.of(2026, 6, 1), 15.0, "rod and reel", 0))
        assertFalse(result.maySetKeeper)
        assertEquals(RuleDecision.UNABLE_TO_VERIFY, result.decision)
    }
    @Test fun coordinateResolverHandlesSupportedUnknownAndAmbiguousLocations() {
        assertEquals("Sacramento Valley", RegionResolver.resolve(39.0, -121.5).region)
        assertNull(RegionResolver.resolve(34.0, -118.2).region)
        assertTrue(RegionResolver.resolve(38.1, -121.8).ambiguous)
    }
}
