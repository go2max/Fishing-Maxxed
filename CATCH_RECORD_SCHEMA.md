# Catch Record Schema v1

Records are stored only in Android app-private storage. Each record includes a UUID, capture time, user-confirmed species and optional variant, calibrated length and uncertainty, optional weight, notes, method, photo path, status, private exact coordinates, broad public region, and regulation result.

Statuses are `LOCAL_CATCH`, `KEEPER`, `RELEASED`, and `UNVERIFIED`. `KEEPER` is accepted only when the rule result is `POTENTIALLY_LEGAL`.

Default CSV exports omit exact coordinates and the private photo path. They include non-private review fields: species, species confirmation, length, uncertainty, confidence, optional weight, method, status, broad public region, origin classification, rule decision, rule summary, and notes. Exact coordinates are included only when `includePrivateCoordinates` is explicitly enabled by code.
