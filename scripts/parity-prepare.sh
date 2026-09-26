#!/usr/bin/env bash
# Prepare a checkout for the `:catalog` design-parity run.
#
#   scripts/parity-prepare.sh
#
# Passed as `design-map-command` by the parity job. It is a FILE rather
# than the two inline commands it replaces, and that is load-bearing rather than tidiness.
#
# ## Why not inline
#
# The reusable workflow interpolates `design-map-command` into two different shells: the shard job
# runs it (`bash -eo pipefail -c "$DESIGN_MAP_COMMAND"`), and the GATE job splices its literal text
# into the script that computes the cache key:
#
#     parts+=(--part "map-command=${{ inputs.design-map-command }}")
#
# That second one is not a quoted string being passed around — it is this text becoming part of the
# gate's own program, which runs under `set -euo pipefail`. So a `$name` written here would be
# expanded by the GATE, against the gate's environment, where it is unbound: the first version of
# this step assigned `sdkmanager=...` and used `"$sdkmanager"`, and the gate died with
# `sdkmanager: unbound variable` on run 354 while the parity job itself rendered and published
# perfectly. A path contains no `$`, so it survives both shells intact and keeps the cache key
# stable across runner images.
#
# Keep it that way: no `$` in the `design-map-command` string, and any variable this needs lives
# here, in a file only the shard job executes.
set -euo pipefail

# The Android platform `:catalog` compiles against. This is the repo's only AGP module —
# `androidx.xr.glimmer:glimmer` ships as an AAR with no Compose Multiplatform port, so it renders
# through Robolectric and needs a real SDK where the other sheets do not.
#
# The package is `platforms;android-37.0`, NOT `android-37`: the module sets `compileSdk = 37` and
# the platform directory carries the minor. Same step `ci.yml` runs for the same module, and the
# same spelling that only ever fails in CI.
#
# This lives in `design-map-command` because it is the one hook the reusable workflow runs BEFORE
# the render.
sdkmanager="${ANDROID_HOME:-${ANDROID_SDK_ROOT:?no Android SDK on this runner}}/cmdline-tools/latest/bin/sdkmanager"
yes | "$sdkmanager" --licenses > /dev/null 2>&1 || true
"$sdkmanager" 'platforms;android-37.0'

# Regenerate rather than trusting a committed map whose source annotations may have changed in the
# same revision. The workflow scopes rendering from this exact discovery output.
./gradlew :catalog:composePreviewDiscover --stacktrace
scripts/design-map.sh
