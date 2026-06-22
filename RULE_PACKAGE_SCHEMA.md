# Regulation Package Contract v1

The engine consumes normalized rules containing zone, species ID, effective dates, season boundaries, minimum/maximum slot, bag limit, allowed methods, closure flag, location-scoped origin classification, source title/URL/check date, and an authoritative-support flag.

`RegulationPackageManager` verifies SHA-256 and an RSA/SHA-256 signature against an injected trusted public key, rejects empty or unparseable packages, preserves the prior package, and supports rollback. The production app intentionally exposes no import action until a production public key and independently maintained package source are configured. The bundled demonstration package is always non-authoritative and therefore cannot authorize Keeper.
