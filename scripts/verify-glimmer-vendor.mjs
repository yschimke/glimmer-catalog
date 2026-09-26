#!/usr/bin/env node

import { execFileSync } from "node:child_process";
import { createHash } from "node:crypto";
import { mkdtemp, readFile, readdir, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { dirname, join, relative } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const pin = JSON.parse(await readFile(join(root, "vendor/glimmer-upstream.json"), "utf8"));
const vendored = join(root, "vendor/glimmer/src/commonMain/kotlin");
const changed = new Set([
  "androidx/xr/glimmer/Button.kt",
  "androidx/xr/glimmer/ButtonGroup.kt",
  "androidx/xr/glimmer/IndirectPointerGesture.kt",
  "androidx/xr/glimmer/Surface.kt",
  "androidx/xr/glimmer/internal/color/HctUtils.kt",
  "androidx/xr/glimmer/stack/DefaultStackItemKey.kt",
  "androidx/xr/glimmer/stack/StackScrimModifier.kt",
]);

async function filesBelow(directory) {
  const result = [];
  for (const entry of await readdir(directory, { withFileTypes: true })) {
    const path = join(directory, entry.name);
    if (entry.isDirectory()) result.push(...(await filesBelow(path)));
    else if (entry.name.endsWith(".kt")) result.push(path);
  }
  return result;
}

const scratch = await mkdtemp(join(tmpdir(), "glimmer-vendor-"));
try {
  const archive = join(scratch, "sources.jar");
  const response = await fetch(pin.source);
  if (!response.ok) throw new Error(`download failed: ${response.status} ${response.statusText}`);
  const bytes = Buffer.from(await response.arrayBuffer());
  const actualSha = createHash("sha256").update(bytes).digest("hex");
  if (actualSha !== pin.sha256) {
    throw new Error(`sources jar sha256 is ${actualSha}, expected ${pin.sha256}`);
  }
  await writeFile(archive, bytes);
  execFileSync("unzip", ["-q", archive, "-d", scratch]);

  const upstream = join(scratch, "androidx");
  const upstreamFiles = (await filesBelow(upstream)).map((path) => relative(scratch, path)).sort();
  const vendoredFiles = (await filesBelow(vendored)).map((path) => relative(vendored, path)).sort();
  if (JSON.stringify(upstreamFiles) !== JSON.stringify(vendoredFiles)) {
    throw new Error("vendored Kotlin file set differs from the pinned sources jar");
  }

  const unexpected = [];
  const unchangedPorts = [];
  for (const path of upstreamFiles) {
    const before = await readFile(join(scratch, path));
    const after = await readFile(join(vendored, path));
    const differs = !before.equals(after);
    if (differs && !changed.has(path)) unexpected.push(path);
    if (!differs && changed.has(path)) unchangedPorts.push(path);
  }
  if (unexpected.length) {
    throw new Error(`unlisted edits to pinned sources:\n${unexpected.join("\n")}`);
  }
  if (unchangedPorts.length) {
    throw new Error(`declared port edits no longer differ:\n${unchangedPorts.join("\n")}`);
  }
  console.log(
    `Verified ${upstreamFiles.length} Kotlin files against Glimmer ${pin.release}; ` +
      `${changed.size} declared port edits.`,
  );
} finally {
  await rm(scratch, { recursive: true, force: true });
}
