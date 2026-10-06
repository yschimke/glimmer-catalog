#!/usr/bin/env node

import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";

const path = "catalog/build/compose-previews/ui-builder.json";
const catalog = JSON.parse(await readFile(path, "utf8"));
assert.equal(catalog.schema, "compose-ui-builder-catalog/v1");
assert.equal(catalog.catalog.id, "glimmer-catalog");
assert.equal(catalog.catalog.platform, "glasses");
assert.equal(catalog.statusSemantics.frame.seedDevice, "spec:width=960dp,height=720dp,dpi=160");
assert.equal(catalog.statusSemantics.previewSurfaces.wasm.fidelity, "authoritative");

const expected = [
  "action-card",
  "button",
  "button-group",
  "card",
  "contained-voice-input-indicator",
  "glimmer-lazy-column",
  "icon",
  "icon-button",
  "icon-toggle-button",
  "image-card",
  "list-item",
  "text",
  "title-chip",
  "toggle-button",
  "vertical-stack",
  "voice-input-indicator",
].map((name) => `glimmer/${name}`);
const actual = Object.keys(catalog.statusSemantics.components).filter(
  (id) => id !== "glimmer/sticker",
);
assert.deepEqual(actual.sort(), expected.sort(), "the builder palette must expose every Glimmer API");
for (const id of expected) {
  assert.equal(
    catalog.statusSemantics.components[id].canvas,
    id,
    `${id} must be claimed by the catalog-owned Glimmer renderer`,
  );
  assert.equal(catalog.statusSemantics.components[id].nativeOnly, false);
}
assert.match(catalog.statusSemantics.components["glimmer/sticker"].excluded, /preview frame/);
assert.equal(
  catalog.diagnostics.some((diagnostic) => diagnostic.code === "component.policy.orphaned"),
  false,
  "every @BuilderComponent policy must bind to a discovered callsite",
);
assert.equal(
  catalog.diagnostics.some((diagnostic) => diagnostic.code === "component.canvas.unclaimed"),
  false,
  "the Wasm shelf must not fall back to placeholders",
);
console.log(`Verified ${expected.length} Glimmer UI Builder components.`);
