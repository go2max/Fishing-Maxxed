package com.maxxed.fishingmaxxed

import java.time.LocalDate
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature
import java.util.UUID
import kotlin.math.hypot

enum class CatchStatus { LOCAL_CATCH, KEEPER, RELEASED, UNVERIFIED }
enum class Origin { NATIVE, INTRODUCED, INVASIVE, UNKNOWN }
enum class Confidence { HIGH, MEDIUM, LOW }
enum class RuleDecision(val label: String) {
    POTENTIALLY_LEGAL("Potentially legal to keep"),
    RELEASE_REQUIRED("Not within keep limits - release required"),
    UNABLE_TO_VERIFY("Unable to verify - check official regulations")
}

data class Point(val x: Float, val y: Float)
data class MeasurementInput(
    val fishStart: Point,
    val fishEnd: Point,
    val referenceStart: Point,
    val referenceEnd: Point,
    val referenceLength: Double,
    val referenceUncertainty: Double = 0.1
)
data class Measurement(val length: Double, val uncertainty: Double, val confidence: Confidence)

object MeasurementCalculator {
    fun calculate(input: MeasurementInput): Measurement? {
        val fishPixels = hypot(input.fishEnd.x - input.fishStart.x, input.fishEnd.y - input.fishStart.y).toDouble()
        val referencePixels = hypot(input.referenceEnd.x - input.referenceStart.x, input.referenceEnd.y - input.referenceStart.y).toDouble()
        if (fishPixels < 20 || referencePixels < 20 || input.referenceLength <= 0) return null
        val length = fishPixels / referencePixels * input.referenceLength
        val ratio = referencePixels / fishPixels
        val perspective = length * 0.02
        val pixelError = length * 4.0 / fishPixels
        val uncertainty = kotlin.math.sqrt(input.referenceUncertainty * input.referenceUncertainty + perspective * perspective + pixelError * pixelError)
        val confidence = when {
            fishPixels >= 500 && ratio >= .25 && input.referenceUncertainty / input.referenceLength <= .01 -> Confidence.HIGH
            fishPixels >= 180 && ratio >= .12 -> Confidence.MEDIUM
            else -> Confidence.LOW
        }
        return Measurement(length, uncertainty.coerceAtLeast(0.1), confidence)
    }
}

data class Species(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val variants: List<String> = emptyList()
)

object SpeciesCatalog {
    val all = listOf(
        Species("largemouth_bass", "Largemouth bass", "Micropterus salmoides"),
        Species("smallmouth_bass", "Smallmouth bass", "Micropterus dolomieu"),
        Species("spotted_bass", "Spotted bass", "Micropterus punctulatus"),
        Species("striped_bass", "Striped bass", "Morone saxatilis"),
        Species("rainbow_trout", "Rainbow trout", "Oncorhynchus mykiss", listOf("Steelhead")),
        Species("brown_trout", "Brown trout", "Salmo trutta"),
        Species("chinook_salmon", "Chinook salmon", "Oncorhynchus tshawytscha"),
        Species("channel_catfish", "Channel catfish", "Ictalurus punctatus"),
        Species("bluegill", "Bluegill", "Lepomis macrochirus"),
        Species("crappie", "Crappie", "Pomoxis spp."),
        Species("common_carp", "Common carp", "Cyprinus carpio"),
        Species("white_sturgeon", "White sturgeon", "Acipenser transmontanus")
    )
    fun search(query: String): List<Species> = all.filter {
        query.isBlank() || it.commonName.contains(query, true) || it.scientificName.contains(query, true)
    }
}

data class CatchRecord(
    val id: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis(),
    val speciesId: String?,
    val speciesName: String,
    val speciesConfirmed: Boolean,
    val variant: String = "",
    val lengthInches: Double?,
    val uncertaintyInches: Double?,
    val confidence: Confidence?,
    val weightPounds: Double?,
    val notes: String,
    val method: String,
    val status: CatchStatus,
    val photoPath: String?,
    val latitude: Double?,
    val longitude: Double?,
    val publicRegion: String,
    val origin: Origin,
    val ruleDecision: RuleDecision,
    val ruleSummary: String
)

data class RuleSource(val title: String, val url: String, val checked: LocalDate)
data class RegulationRule(
    val zone: String,
    val speciesId: String,
    val effectiveFrom: LocalDate,
    val effectiveTo: LocalDate,
    val seasonStart: LocalDate,
    val seasonEnd: LocalDate,
    val minLength: Double?,
    val maxLength: Double?,
    val bagLimit: Int,
    val allowedMethods: Set<String>,
    val closed: Boolean,
    val origin: Origin,
    val source: RuleSource,
    val authoritative: Boolean
)
data class RuleRequest(
    val zone: String?, val speciesId: String?, val date: LocalDate, val lengthInches: Double?,
    val method: String, val retainedToday: Int, val boundaryAmbiguous: Boolean = false
)
data class RuleResult(val decision: RuleDecision, val summary: String, val rule: RegulationRule? = null) {
    val maySetKeeper get() = decision == RuleDecision.POTENTIALLY_LEGAL
}

class RegulationEngine(private val rules: List<RegulationRule>) {
    fun evaluate(request: RuleRequest): RuleResult {
        if (request.zone == null || request.speciesId == null || request.boundaryAmbiguous) return unable("Location, species, or boundary is not verified.")
        val matches = rules.filter { it.zone == request.zone && it.speciesId == request.speciesId }
        if (matches.size != 1) return unable("No single supported rule applies.")
        val rule = matches.single()
        if (!rule.authoritative || request.date !in rule.effectiveFrom..rule.effectiveTo) return unable("The regulation source is unsupported or stale.", rule)
        if (rule.closed || request.date !in rule.seasonStart..rule.seasonEnd) return release("Season or area is closed.", rule)
        val length = request.lengthInches ?: return unable("A verified length is required.", rule)
        if (rule.minLength != null && length < rule.minLength) return release("Fish is below the minimum size.", rule)
        if (rule.maxLength != null && length > rule.maxLength) return release("Fish is above the slot maximum.", rule)
        if (request.retainedToday >= rule.bagLimit) return release("Local journal indicates the bag limit may be reached.", rule)
        if (request.method.lowercase() !in rule.allowedMethods) return release("Selected method is not allowed by this rule.", rule)
        return RuleResult(RuleDecision.POTENTIALLY_LEGAL, "All fields in this limited rule matched. Verify the official source before acting.", rule)
    }
    private fun unable(reason: String, rule: RegulationRule? = null) = RuleResult(RuleDecision.UNABLE_TO_VERIFY, reason, rule)
    private fun release(reason: String, rule: RegulationRule) = RuleResult(RuleDecision.RELEASE_REQUIRED, reason, rule)
}

object BundledRules {
    // The bundled catalog is intentionally non-authoritative. It exercises location-aware
    // evaluation but can never authorize Keeper without an independently validated package.
    val engine = RegulationEngine(listOf(
        RegulationRule("Sacramento Valley", "largemouth_bass", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 12.0, null, 5, setOf("rod and reel"), false,
            Origin.INTRODUCED, RuleSource("California Freshwater Sport Fishing Regulations", "https://wildlife.ca.gov/Regulations", LocalDate.of(2026, 6, 21)), false)
    ))
}

data class RegionMatch(val region: String?, val ambiguous: Boolean)
object RegionResolver {
    fun resolve(latitude: Double, longitude: Double): RegionMatch {
        val matches = buildList {
            if (latitude in 38.0..40.5 && longitude in -122.5..-120.5) add("Sacramento Valley")
            if (latitude in 37.5..38.3 && longitude in -122.6..-121.3) add("Bay and Delta")
        }
        return RegionMatch(matches.singleOrNull(), matches.size > 1)
    }
}

data class SignedRulePackage(val payload: ByteArray, val sha256: String, val signature: ByteArray)
interface RulePackageStorage {
    fun current(): ByteArray?
    fun previous(): ByteArray?
    fun replace(current: ByteArray, previous: ByteArray?)
}
sealed class PackageImportResult {
    data class Accepted(val ruleCount: Int) : PackageImportResult()
    data class Rejected(val reason: String) : PackageImportResult()
}

class RegulationPackageManager(
    private val trustedKey: PublicKey,
    private val storage: RulePackageStorage,
    private val parser: (ByteArray) -> List<RegulationRule>
) {
    fun import(candidate: SignedRulePackage): PackageImportResult = runCatching {
        val actualHash = MessageDigest.getInstance("SHA-256").digest(candidate.payload).joinToString("") { "%02x".format(it) }
        if (!actualHash.equals(candidate.sha256, true)) return PackageImportResult.Rejected("Checksum mismatch.")
        val verifier = Signature.getInstance("SHA256withRSA").apply { initVerify(trustedKey); update(candidate.payload) }
        if (!verifier.verify(candidate.signature)) return PackageImportResult.Rejected("Signature is not trusted.")
        val rules = parser(candidate.payload)
        if (rules.isEmpty()) return PackageImportResult.Rejected("Package contains no supported rules.")
        storage.replace(candidate.payload, storage.current())
        PackageImportResult.Accepted(rules.size)
    }.getOrElse { PackageImportResult.Rejected("Invalid package: ${it.message ?: "unknown error"}") }

    fun rollback(): Boolean {
        val prior = storage.previous() ?: return false
        storage.replace(prior, storage.current())
        return true
    }
}
