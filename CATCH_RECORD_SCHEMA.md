# Catch Record Schema v1

Records are stored only in Android app-private storage. Each record includes a UUID, capture time, user-confirmed species and optional variant, calibrated length and uncertainty, optional weight, notes, method, photo path, status, private exact coordinates, broad public region, and regulation result.

Statuses are `LOCAL_CATCH`, `KEEPER`, `RELEASED`, and `UNVERIFIED`. `KEEPER` is accepted only when the rule result is `POTENTIALLY_LEGAL`. Default CSV exports omit exact coordinates and the private photo path.
