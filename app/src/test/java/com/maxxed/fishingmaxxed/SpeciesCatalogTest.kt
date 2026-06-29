package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test

class SpeciesCatalogTest {
    @Test fun searchesCommonScientificAndVariantNames() {
        assertEquals("Largemouth bass", SpeciesCatalog.search("largemouth").first().commonName)
        assertTrue(SpeciesCatalog.search("Oncorhynchus").any { it.commonName == "Rainbow trout" })
        assertEquals("Rainbow trout", SpeciesCatalog.search("steelhead").first().commonName)
    }

    @Test fun includesCommonNorcalFreshwaterSpecies() {
        val ids = SpeciesCatalog.all.map { it.id }.toSet()
        assertTrue("white sturgeon should be present", "white_sturgeon" in ids)
        assertTrue("Sacramento perch should be present", "sacramento_perch" in ids)
        assertTrue("channel catfish should be present", "channel_catfish" in ids)
    }
}
