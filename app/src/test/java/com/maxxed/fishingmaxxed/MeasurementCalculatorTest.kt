package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test

class MeasurementCalculatorTest {
    @Test fun scalesFishAgainstKnownReference() {
        val result = MeasurementCalculator.calculate(MeasurementInput(Point(0f, 0f), Point(400f, 0f), Point(0f, 0f), Point(100f, 0f), 6.0))!!
        assertEquals(24.0, result.length, 0.001)
        assertTrue(result.uncertainty > 0)
        assertEquals(Confidence.MEDIUM, result.confidence)
    }
    @Test fun scalesNormalizedUiHandlesAgainstKnownReference() {
        val result = MeasurementCalculator.calculate(MeasurementInput(Point(.18f, .52f), Point(.82f, .52f), Point(.18f, .78f), Point(.42f, .78f), 6.0))!!
        assertEquals(16.0, result.length, 0.001)
        assertTrue(result.uncertainty > 0)
        assertEquals(Confidence.MEDIUM, result.confidence)
    }
    @Test fun rejectsMissingScaleAndCollapsedHandles() {
        assertNull(MeasurementCalculator.calculate(MeasurementInput(Point(0f, 0f), Point(10f, 0f), Point(0f, 0f), Point(0f, 0f), 0.0)))
    }
}
