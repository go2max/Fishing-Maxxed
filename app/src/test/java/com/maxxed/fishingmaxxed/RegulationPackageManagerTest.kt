package com.maxxed.fishingmaxxed

import org.junit.Assert.*
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Signature

class RegulationPackageManagerTest {
    private class MemoryStorage : RulePackageStorage {
        var now: ByteArray? = null; var before: ByteArray? = null
        override fun current() = now
        override fun previous() = before
        override fun replace(current: ByteArray, previous: ByteArray?) { now = current; before = previous }
    }
    @Test fun verifiesSignatureRejectsCorruptionAndRollsBack() {
        val pair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val storage = MemoryStorage().apply { now = "old".toByteArray() }
        val manager = RegulationPackageManager(pair.public, storage) { listOf(BundledRules.engine).map { throwAway ->
            RegulationRule("z", "s", java.time.LocalDate.MIN, java.time.LocalDate.MAX, java.time.LocalDate.MIN, java.time.LocalDate.MAX,
                null, null, 1, setOf("rod and reel"), false, Origin.UNKNOWN, RuleSource("t", "u", java.time.LocalDate.MIN), true)
        } }
        val payload = "valid package".toByteArray()
        val signer = Signature.getInstance("SHA256withRSA").apply { initSign(pair.private); update(payload) }
        val hash = MessageDigest.getInstance("SHA-256").digest(payload).joinToString("") { "%02x".format(it) }
        assertTrue(manager.import(SignedRulePackage(payload, hash, signer.sign())) is PackageImportResult.Accepted)
        assertArrayEquals(payload, storage.current())
        assertTrue(manager.rollback())
        assertArrayEquals("old".toByteArray(), storage.current())
        assertTrue(manager.import(SignedRulePackage("bad".toByteArray(), hash, byteArrayOf(1))) is PackageImportResult.Rejected)
    }
}
