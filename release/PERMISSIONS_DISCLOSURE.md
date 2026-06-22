# Permissions Disclosure

Current blocked build:

- No Android runtime permissions are requested.

Production brief mismatch:

- The intended product scope requires camera access and likely location and local file/export handling.
- Those features are not implemented in the current artifact, so no permission prompt, education screen, or denial recovery flow exists yet.

Result:

- This build must remain blocked until the final permission model is implemented and explained in-app.
