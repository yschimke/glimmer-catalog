# Glimmer sample patches

Small fixes to the vendored AndroidX Glimmer samples, applied by `scripts/import-samples.mjs` after
the copy. The contract is `samples/patches/README.md`'s, verbatim: a fix is **a patch with a stated
reason, never an edit to a vendored file**, and a patch that stops applying fails the import rather
than disappearing.

Cut one against a fresh import:

```
node scripts/import-samples.mjs --manifest samples-catalog/import.json \
  --quarantine samples-catalog/quarantine.json --patches samples-catalog/patches \
  --out /tmp/glimmer-fresh
```

One patch so far, `0001-samples-type-in-google-sans-flex.patch`: it swaps upstream's `@Preview` for
this module's `@GlimmerSamplePreview` so the samples render in the kit's typeface. It is mechanical,
so re-cut it after an upstream bump by importing with an empty `--patches` directory and applying
the same two substitutions (`@Preview` and its import) to every file.
