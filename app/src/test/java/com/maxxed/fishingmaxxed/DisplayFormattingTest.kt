package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test

class DisplayFormattingTest {
    @Test fun catchStatusLabelsAreHumanReadable() {
        assertEquals("Local catch", CatchStatus.LOCAL_CATCH.label)
        assertEquals("Unverified", CatchStatus.UNVERIFIED.label)
    }

    @Test fun speciesSubtitleIncludesVariantNames() {
        val trout = SpeciesCatalog.search("steelhead").first()
        assertTrue(trout.subtitle.contains("Oncorhynchus mykiss"))
        assertTrue(trout.subtitle.contains("Steelhead"))
    }

    @Test fun inchLabelsUseTwoDecimals() {
        assertEquals("12.35 in", 12.345.inchesLabel())
    }
}
