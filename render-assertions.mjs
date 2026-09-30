// Properties every `:catalog` render must hold, checked by the design-artifacts workflow against the
// data products the render already wrote (`previews/<id>.fonts.json`, `<id>.semantics.json`) before
// anything publishes. Format and failure semantics:
// https://github.com/yschimke/compose-ai-tools/blob/main/docs/RENDER_ASSERTIONS.md
//
// Why this file exists: this sheet rendered all seven Glimmer type roles at weight 400 for its whole
// life. `createGoogleSansFlexTypography()` carries the kit's weights on the `wght` axis (520 body,
// 650 caption, 750 title) and the renderer's static Google Fonts instance had no `fvar` table, so
// `Paint.setFontVariationSettings` dropped every axis without a word. The render passed,
// `failOnFallback` passed (the family resolved), and the visual diff passed (it was wrong from the
// first render). Visual diff asks "did this change?"; these ask "is it still true?".
//
// The typography rules read `compose-semantics`, not `fonts-used`: on the Robolectric lane a
// `fonts-used` record's `resolvedFamily` is an `android.graphics.Typeface@…` identity, so the family
// name is only observable on the text node. What `fonts-used` does carry reliably is whether the
// variation axes were applied, which is the one fact semantics cannot see.

/** The kit's axis weights. Every text layer in the Glimmer UI kit sits on one of these. */
const KIT_WGHT = new Set([520, 650, 750]);

/** Every laid-out text node in one preview's semantics tree. */
function textNodes(data) {
  const out = [];
  const walk = (node) => {
    if (!node || typeof node !== "object") return;
    // A laid-out run carries `typography`; a merged accessibility label carries only `text`.
    if (node.text != null && node.typography) out.push(node);
    for (const child of node.children ?? []) walk(child);
  };
  walk(data.root ?? data);
  return out;
}

export const assertions = [
  {
    id: "glimmer-types-in-google-sans-flex",
    product: "compose-semantics",
    because:
      "every text layer in the Glimmer UI kit is Google Sans Flex; any other family on a text node " +
      "means the downloadable face silently fell back and the sheet no longer shows the design system",
    require: { "everyTextNode.typography.fontFamily": "Google Sans Flex" },
  },
  {
    id: "glimmer-text-sits-on-a-kit-axis-weight",
    product: "compose-semantics",
    because:
      "the kit's type roles are axis weights 520/650/750, not named styles; a text node without one " +
      "of them is typing in a weight the design system does not have",
    // Code because set membership over a parsed axis value is not something `contains` can state.
    // A glyph-only sticker has no text node, so it holds vacuously.
    check: (data) => {
      const bad = textNodes(data).filter((n) => {
        const m = /\bwght ([\d.]+)/.exec(n.typography?.fontVariationSettings ?? "");
        return !m || !KIT_WGHT.has(Number(m[1]));
      });
      return bad.length === 0
        ? null
        : bad
            .map((n) => `${n.typography?.fontVariationSettings || "no axes"} ("${n.text}")`)
            .join(", ");
    },
  },
  {
    id: "glimmer-variation-axes-are-applied",
    product: "fonts-used",
    because:
      "the rule above only proves the axes were REQUESTED; a static font instance drops them and " +
      "every role renders at 400 — the exact bug this file was written for",
    require: { "noFont.droppedVariationSettings": null },
  },
];
