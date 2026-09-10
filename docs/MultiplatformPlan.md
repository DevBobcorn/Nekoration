# Nekoration — NeoForge + Fabric Multi-Loader Plan

Target: Minecraft **1.21.1**, current loader **NeoForge 21.1.224** (ModDevGradle 2.0.141, Gradle 9.2.1, Java 21).
Goal: ship one codebase that produces **two jars** — `nekoration-neoforge` and `nekoration-fabric` — following the architecture proven by BiomesOPlenty (BOP) in `reference/BiomesOPlenty`.

---

## 1. Summary

| | Current | Target |
|---|---|---|
| Layout | single Gradle project | 3 Gradle subprojects: `common`, `neoforge`, `fabric` |
| Loader APIs in sources | 36 files import `net.neoforged.*` | `common` has **zero** loader imports; loaders live in thin per-platform entry modules |
| Mappings | Parchment over official | **official (mojmap) on every module** so one source tree compiles unchanged per loader; Parchment stays as neoforge-side javadoc layer only |
| Registration | 9 `DeferredRegister`s | loader-agnostic registration facade (`RegistrySupplier`), wired by each platform |
| Jars | 1 | 2 (per loader), sharing all content/resources |

We deliberately do **not** use Architectury or add a GlitchCore dependency — BOP's own approach is plain Gradle source injection plus a thin hand-written abstraction layer, which fits Nekoration with only ~15 split points.

---

## 2. How BOP does it (reference analysis)

BOP's 1.21.1 branch (`reference/BiomesOPlenty`, branch `1.21.1`) is the closest model. Findings relevant to us:

1. **Subprojects**: `common`, `fabric`, `forge`, `neoforge` (we skip `forge`).
2. **The core trick — source-level sharing, no rewriting tools**:
   ```groovy
   // in each platform module's build.gradle
   tasks.withType(JavaCompile).configureEach {
       source project(':Common').sourceSets.main.allSource   // recompile common per loader
   }
   processResources { from project(':Common').sourceSets.main.resources }
   compileOnly project(':Common')   // so IDEs resolve common classes
   ```
   Common code is *recompiled against each loader's own Minecraft jar*. Because **every module uses official mojmap names** (Loom `officialMojangMappings()`, ForgeGradle `channel:'official'`, ModDevGradle/NeoForm), the identical source compiles everywhere with no remapping of our own code.
3. **`common` compiles against vanilla only**: BOP 1.21.1 uses VanillaGradle 0.2.1; BOP 26.2 uses ModDevGradle with only `neoFormVersion` set (NeoForm = vanilla + official mappings, no NeoForge API). Either way, `net.neoforged.*` / `net.minecraftforge.*` imports are impossible in common — which forces clean abstraction.
4. **Platform modules are thin shims**: a `@Mod` class / entrypoint calling common `init()` (BOP: 248 common files vs 5 fabric / 18 neoforge files).
5. **Datagen runs once on the NeoForge module but writes into `:Common/src/generated/resources`**, which every module packages.
6. **Mixins live in common** in BOP (all target vanilla classes); config declared per loader: `[[mixins]]` in `neoforge.mods.toml`, `mixins` array in `fabric.mod.json`. Refmap generated only where needed (Loom for Fabric). *(We diverge: Nekoration's mixins ship in the neoforge module only — see §5.7.)*
7. **Access control triplicated** in BOP (AT for Forge/NeoForge, accesswidener for Fabric) — Nekoration currently uses reflection (`CreativeInventoryReflection`) and no AT, so we skip this entirely.
8. BOP's platform split is enabled by an external library (GlitchCore: RegistryHelper / EventManager / Config). We write our own minimal version instead (see §5) — GlitchCore doesn't cover our hardest parts anyway (networking, extended menus, item renderers, connected textures).

### What we do differently
- **`common` via ModDevGradle with `neoFormVersion` only** (modern replacement for VanillaGradle; same toolchain we already use). The only 1.21.1 NeoForm build is **`1.21.1-20240808.144430`**.
- No capabilities/loader-attribute machinery — with only 3 modules and `compileOnly project(':Common')` (like BOP's 1.21.1 branch), it's unnecessary.

---

## 3. Target project layout

```
nekoration/
├─ build.gradle            # root: shared subproject config, source injection
├─ settings.gradle         # include common, neoforge, fabric
├─ gradle.properties       # + fabric_loader_version, fabric_version, neo_form_version
├─ common/
│  ├─ build.gradle         # ModDevGradle, neoFormVersion only (vanilla, mojmap)
│  └─ src/
│     ├─ main/java/io/devbobcorn/nekoration/...   # ~95% of current sources
│     ├─ main/resources/                          # assets/, data/, pack.mcmeta
│     └─ generated/resources/                     # datagen output (shared)
├─ neoforge/
│  ├─ build.gradle         # ModDevGradle, neoforge 21.1.224, parchment, runs, JUnit
│  └─ src/
│     ├─ main/java/io/devbobcorn/nekoration/neoforge/  # entry class, client entry, platform impls, datagen
│     ├─ main/java/io/devbobcorn/nekoration/{mixin,world/upgrade}/  # world upgrader — NeoForge-only (§5.7)
│     ├─ main/resources/nekoration.mixins.json, META-INF/neoforge.mods.toml
│     └─ test/java/... # LegacyWorldUpgraderTest
├─ fabric/
   ├─ build.gradle         # fabric-loom, officialMojangMappings, runs
   └─ src/main/
      ├─ java/io/devbobcorn/nekoration/fabric/     # entrypoints, platform impls
      └─ resources/fabric.mod.json
```

---

## 4. Build setup

### 4.1 `gradle.properties` additions
```properties
neo_form_version=1.21.1-20240808.144430
fabric_loader_version=0.16.9          # or newer
fabric_version=0.116.17+1.21.1        # latest 1.21.1 fabric-api build
```

### 4.2 `settings.gradle`
```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository { maven { name = 'Fabric'; url = uri('https://maven.fabricmc.net') } }
            filter { includeGroupAndSubgroups('net.fabricmc') }
        }
    }
}
plugins { id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0' }
rootProject.name = 'nekoration'
include('common', 'neoforge', 'fabric')
```

### 4.3 Root `build.gradle` — the sharing mechanism (BOP-style)
```groovy
subprojects {
    apply plugin: 'java-library'
    java.toolchain.languageVersion = JavaLanguageVersion.of(21)
    tasks.withType(JavaCompile).configureEach { options.encoding = 'UTF-8'; options.release.set(21) }

    if (it.name != 'Common') {
        dependencies { compileOnly project(':Common') }
        tasks.withType(JavaCompile).configureEach {
            source project(':Common').sourceSets.main.allJava
        }
        processResources { from project(':Common').sourceSets.main.resources }
    }
}
```
Notes:
- Safe with MDG 2.x (it compiles against prebuilt artifacts, unlike old NeoGradle which needed a task-name filter).
- Keep `org.gradle.configuration-cache=true` — recent Loom supports it; verify in Phase 0 (fall back to Loom 1.13+ or disable it for the fabric module if it fights back).
- Gradle wrapper stays 9.2.1 (Tencent mirror); **pick a Loom release compatible with Gradle 9** (1.11+; BOP 26.2 uses Loom 1.15 on Gradle 9.3). If no release cooperates, drop the wrapper to 8.10.x — MDG 2.0.141 supports Gradle 8.4+ too.

### 4.4 `common/build.gradle`
```groovy
plugins { id 'net.neoforged.moddev' version '2.0.141' }
base { archivesName = 'nekoration-common' }
sourceSets.main.resources.srcDirs = ['src/generated/resources', 'src/main/resources']

neoForge {
    neoFormVersion = project.neo_form_version     // vanilla + mojmap ONLY — no NeoForge API
}
dependencies {
    testImplementation /* existing JUnit setup (moved from root; see below) */
}
```
The world-upgrader test (`LegacyWorldUpgraderTest`) moves to the **neoforge** module's test source set together with its subject (§5.7); wire the existing JUnit 5 deps there.

### 4.5 `neoforge/build.gradle`
Same MDG plugin but with the full loader: `neoForge { version = project.neo_version; parchment { ... }; runs { client/server/data/gameTestServer } ; mods { ... } }`. **The `data` run's `--output` points at `common/src/generated/resources`** (BOP trick — datagen once, shared by all loaders). Jade/BOP/Modrinth deps stay here as `compileOnly`/`localRuntime`.

### 4.6 `fabric/build.gradle`
```groovy
plugins { id 'fabric-loom' version '<1.11+, Gradle-9-compatible>' }
dependencies {
    minecraft "com.mojang:minecraft:${minecraft_version}"
    mappings loom.officialMojangMappings()        // REQUIRED — keeps common source compiling unchanged
    modImplementation "net.fabricmc:fabric-loader:${fabric_loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${fabric_version}"
}
loom {
    runs { client { client() } server { server() } }
}
```
No mixin configuration needed — the fabric module contains no mixins (§5.7).
Parchment can be added later via Loom's parchment addon (same param metadata, no name drift).

---

## 5. Platform abstraction layer ("split classes", BOP-style)

No ServiceLoader registry, no reflection — just small interfaces in `common` under `io.devbobcorn.nekoration.xplat`, with implementations living in each platform module. Common entry points accept platform objects (constructor/di-style), and a tiny static facade covers the few call-time needs.

### 5.1 Static facade
```java
// common
public final class NekoPlatform {
    private static NekoPlatformImpl impl;
    public static void init(NekoPlatformImpl i) { impl = i; }
    public static boolean isClient() { return impl.isClient(); }
    public static NekoConfigData config() { return impl.config(); }
    public static void sendToServer(CustomPacketPayload p) { impl.sendToServer(p); }
    public static void sendToClient(ServerPlayer p, CustomPacketPayload payload) { impl.sendToClient(p, payload); }
    public static void openEaselMenu(ServerPlayer p, EaselMenuBlockEntity be) { impl.openEaselMenu(p, be); }
    public static void enqueue(Runnable r) { impl.enqueue(r); }   // main-thread work
}
```
`NekoPlatform.init(...)` is the first call in each entry class. Replaces `FMLEnvironment.dist` in `ComponentCompat`.

### 5.2 Registration facade (biggest mechanical change)
Current: 9 `DeferredRegister`s exposing `DeferredHolder`/`DeferredItem`/`DeferredBlock` static fields, consumed everywhere as `.get()` (and `.getKey()`, `Holder`).

Target:
```java
// common — xplat/RegistrySupplier.java
public interface RegistrySupplier<T> extends Supplier<T> {
    ResourceLocation getId();
    ResourceKey<T> getKey();
    Holder<T> holder();
}
// common — xplat/NekoRegistrar.java
public interface NekoRegistrar {
    <T, R extends T> RegistrySupplier<R> register(Registry<T> reg, String name, Supplier<R> factory);
    default RegistrySupplier<Block> block(String name, Supplier<Block> f, Function<Block, BlockItem> item) { ... }
    // helpers for item, blockentity, entity (vanilla EntityType.Builder.of(...).build(...)),
    // menu, recipeserializer, creative tab
}
```
- **NeoForge impl**: wraps the existing `DeferredRegister`s; `RegistrySupplier` wraps `DeferredHolder` (which already implements `Holder`).
- **Fabric impl**: `Registry.register(...)` (+ `registerForHolder`), returns a small immutable `RegistrySupplier`.
- **Creative tabs**: register as ordinary registry objects on both loaders; switch `CreativeModeTab.builder()` (NeoForge overload) to the **vanilla** `builder(CreativeModeTab.Row, int)` with explicit row/column. `displayItems`/`withTabsBefore` code stays as-is.
- All ~140 call sites keep working via the same `get()`/`getId()` API; recipes/tags/loot datagen untouched.

### 5.3 Event mapping
No unified event bus in common; instead, common logic classes expose static handlers invoked by per-platform listeners.

| Current (NeoForge) | Common handler | Fabric wiring |
|---|---|---|
| `FMLCommonSetupEvent` + `enqueueWork` (flammability) | `NekoInit.commonSetup()` | entrypoint → `FlammableBlockRegistry.getDefaultInstance().add(...)` |
| `ServerStartingEvent` | drop (log-only) or `NekoLifecycle.serverStarting(server)` | `ServerLifecycleEvents.SERVER_STARTING` |
| `RegisterColorHandlersEvent.Block/Item` (`NekorationColorHandlers`) | `NekoColorHandlers.register(registrar)` (own functional interfaces) | `ColorProviderRegistry.BLOCK/ITEM.register` |
| `EntityRenderersEvent.RegisterRenderers` | `NekoClientSetup.registerRenderers()` — vanilla `EntityRenderers.register` / `BlockEntityRenderers.register` (loader-agnostic) | call from `onInitializeClient` |
| `RegisterLayerDefinitions` | `NekoClientSetup.registerLayerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>>)` | `EntityModelLayerRegistry.registerModelLayer` |
| `RegisterMenuScreensEvent` | `NekoClientSetup.registerScreens(BiConsumer<MenuType<?>, ScreenFactory>)` | `HandledScreens` / screen API |
| `RegisterClientExtensionsEvent` + `IClientItemExtensions` (wallpaper BEWLR) | renderer class stays common | `BuiltinItemRendererRegistry.INSTANCE.register(...)` |
| `RenderHighlightEvent.Block` (2 placement-hint renderers) | `NekoOutlineRenderer.render(...)` | `WorldRenderEvents.BEFORE_BLOCK_OUTLINE` |
| `ScreenEvent.Init.Post` / `Render.Pre` / `ClientPlayerNetworkEvent.LoggingOut` (`NekoCreativeTabFilterClient`) | `CreativeScreenFilterHandler` static methods | `ScreenEvents.AFTER_INIT` / `BEFORE_RENDER`, `ClientPlayConnectionEvents.DISCONNECT` |
| `ModelEvent.ModifyBakingResult` (`NekoModelSwapper`) | `NekoModelSwapper.modifyBakedModel(id, model)` | `ModelLoadingPlugin.modifyModelAfterBake` (exists for 1.21.1) |
| `GatherDataEvent` (datagen) | — datagen moves to neoforge module (§7.7) | follow-up only |

`ClientHelper`, `PaintingRenderTypes`, renderers themselves: vanilla APIs → common unchanged.

### 5.4 Networking
- Payload records (`CustomPacketPayload` + `StreamCodec`) are **vanilla** → all 5 stay in common untouched (including `handle(payload, context)` logic, adapted to a small `PayloadContext` wrapper: `player()`, `enqueue()`).
- Per platform: channel/codec/receiver registration
  - NeoForge: keep `RegisterPayloadHandlersEvent` registrar (`playToServer`/`playToClient`).
  - Fabric: `PayloadTypeRegistry.playC2S()/playS2C().register(TYPE, CODEC)` + `ServerPlayNetworking` / `ClientPlayNetworking.registerGlobalReceiver`.
  - Verify Fabric threading semantics; `PayloadContext.enqueue()` (→ `server.execute`/`client.execute` on Fabric) guarantees main-thread handling on both.
- Sending: `PacketDistributor` vs `ServerPlayNetworking.send` behind `NekoPlatform.sendToServer/sendToClient`.

### 5.5 Config
`NekoConfig` currently exposes two `ModConfigSpec`s (SERVER spec is actually **empty** — simplification opportunity) and `Client` fields used by painting code.
- Common: `interface NekoConfigData { boolean useImageRendering(); boolean simplifyRendering(); boolean debugMode(); int maxUndoLimit(); }` with defaults; all consumers call `NekoPlatform.config()...`.
- NeoForge module: keep `ModConfigSpec` + `registerConfig` + NeoForge's `ConfigurationScreen` (with `IConfigScreenFactory`) implementing `NekoConfigData`.
- Fabric module: small TOML/JSON loader (no external config library — values only); loaded at client entrypoint.
- NeoForge config file-sync/reload UI stays a neoforge nicety.

### 5.6 Remaining platform-coupled bits
| Item | Plan |
|---|---|
| `ModMenuTypes` (`IMenuTypeExtension.create`) | menu class common; type creation per platform. Fabric: `ExtendedScreenHandlerType<T,D>` (+ data codec). Opening call `player.openMenu` behind `NekoPlatform.openEaselMenu` (Fabric: `ExtendedScreenHandlerFactory#getScreenOpeningData`). |
| `PaintingEntity implements IEntityWithComplexSpawn` | remove; use vanilla NBT path: item spawn calls a common `initializeFromItem(stack)` and save data rides `addAdditionalSaveData` — portable. (Alternatively keep the neoforge interface via a thin neoforge-only subclass — not worth it.) |
| `PotBlock.canSustainPlant` (`TriState`) | drop the neoforge extension override; rely on vanilla `canSurvive`/plant-on-top logic. Only affects planting vanilla plants inside modded pots. |
| Flammability registration | `NekoPlatform.enqueue` on neoforge (`FireBlock` map) vs `FlammableBlockRegistry` on Fabric — via the common-setup registrar (§5.3). |
| `CreativeInventoryReflection` (private field via reflection) | works on both loaders (mojmap names at runtime on both). Optionally convert to an access widener later. |
| Jade compat (`@WailaPlugin`, 2 classes) | duplicate per platform module (Jade artifacts are loader-specific); jade-fabric exists on Modrinth. Or keep neoforge-only first, fabric later. |
| BOP compat | **zero BOP API imports** today (texture fallback to oak) → stays in common, no work. |
| `NekorationClient` second `@Mod(dist=CLIENT)` | split into `NekorationNeoForgeClient` / `NekorationFabricClient(ClientModInitializer)`. |

### 5.7 World upgrader & mixins — NeoForge-only
The v1→v2 world-data upgrader (`LegacyWorldUpgrader` + the 4 storage mixins: `ChunkStorageMixin`, `LevelStorageSourceMixin`, `PlayerDataStorageMixin`, `SimpleRegionStorageMixin`) is **only needed on NeoForge**; the Fabric build ships without it.
- Move the whole bundle into the **neoforge module**: `io.devbobcorn.nekoration.mixin.*` (keep the package name — zero churn), `io.devbobcorn.nekoration.world.upgrade.*`, the JUnit test, and `nekoration.mixins.json` (declared via the existing `[[mixins]]` block in `neoforge.mods.toml`).
- Keep the config as-is: **no refmap entry** — the neoforge module compiles in mojmap and NeoForge runs mojmap at runtime, so no refmap is ever generated or needed.
- The bundle is self-contained (verified: nothing else references `LegacyWorldUpgrader` or the mixin classes), so this is a straight move.
- Fabric module: **zero mixins** — no mixin config in `fabric.mod.json`, no Mixin AP, no refmap, and the fabric jar stays free of upgrader classes.

### 5.8 Datagen
Move the `datagen` package (providers + `NekorationDataGenerators` with `GatherDataEvent`) into the **neoforge module** (BOP pattern); it writes into `common/src/generated/resources`, which both loaders package. Providers reference common registry classes — fine, neoforge compiles common sources. Fabric datagen (fabric-data-generation-api) is a possible follow-up, not required.

---

## 6. Code migration map

| Bucket | Files | Action |
|---|---|---|
| Pure vanilla → common, no edits | most of `blocks/`, `blocks/furniture/`, `blocks/stone/`, `blocks/states/`, `entities/{PaintingData,SeatEntity,WallpaperEntity}`, `items/*`, `recipes/*` (classes), `client/rendering/*` renderer classes, `client/gui/*`, `NekoColors`, `utils/URLHelper`, `common/VanillaCompat` | move to `common/` |
| NeoForge-only → neoforge module | `world/upgrade/*`, `mixin/*`, `nekoration.mixins.json`, `LegacyWorldUpgraderTest` (upgrader is NeoForge-only, §5.7), `datagen/*` | straight move |
| Vanilla after small edit | `blocks/containers/EaselMenuMenu` (ctor ok), blocks referencing registration fields (switch to `RegistrySupplier`), `Nekoration.java` creative tabs (vanilla `builder(Row,int)`), `entities/PaintingEntity` (drop `IEntityWithComplexSpawn`), `blocks/cement/DyeablePotBlock` (drop `TriState`), `common/ComponentCompat` (`NekoPlatform.isClient()`) | move + de-neoforge |
| Registration layer | `Nekoration`, `registry/*` (9 registers) | rewrite on `NekoRegistrar` facade (§5.2) |
| Split per platform | `Nekoration`→entry classes, `NekorationClient`, `NekoConfig`, `NekorationNetwork`, `registry/ModMenuTypes` (type creation), `client/ct/NekoModelSwapper`+`NekoBakedModelWrapperWithData`, `NekorationColorHandlers`, `NekoCreativeTabFilterClient`, hint renderers, `compat/jade/*` | logic common, wiring per platform (§5.3–5.6) |
| Platform-only | `datagen/*` | → `neoforge/` module |
| Metadata | `src/main/templates/META-INF/neoforge.mods.toml` | → `neoforge/`; add `fabric/fabric.mod.json` |

**CT system caveat (`client/ct/*`)**: `BakedModelWrapper`, `ModelData`, `ModelProperty` are NeoForge client APIs with no Fabric equivalent. Plan: de-neoforge the wrapper (implement vanilla `BakedModel` directly) and source the per-position CT data from a **client-side store** (`Map<BlockPos, data>` managed by a common client class, invalidated on block updates) instead of bake-time model data. This works identically on both loaders and also removes the neoforge post-bake event dependency for *reading* data (the wrap step still hooks `ModelEvent.ModifyBakingResult` / Fabric's `modifyModelAfterBake`).

---

## 7. Resources & metadata

- `neoforge.mods.toml`: unchanged content, moves to `neoforge/src/main/resources` (or keep the template + `generateModMetadata` mechanism in the neoforge module).
- New `fabric/src/main/resources/fabric.mod.json`:
  - `entrypoints`: `main` → `NekorationFabric`, `client` → `NekorationFabricClient`
  - `mixins`: none (the world upgrader is NeoForge-only, §5.7)
  - `depends`: `fabricloader >= 0.16`, `minecraft ~1.21.1`, `java >= 21`, `fabric-api`
  - `suggests`: `biomesoplenty`, `jade`
  - same icon/name/version placeholders, expanded by `processResources`
- Add a minimal `pack.mcmeta` to `common` resources (harmless on NeoForge, guards Fabric resource-pack handling).
- No AT / accesswidener needed today.

---

## 8. Versioning, publishing, CI

- Keep `mod_version = 2.0.0`. Archives: `nekoration-common` (not published), `nekoration-neoforge`, `nekoration-fabric`.
- Publishing: Minotaur per loader (uploadFile: neoforge `jar`, fabric `remapJar`); CurseForge via cursegradle with loader-specific `relations` (fabric-api required on fabric). Optional: BOP-style maven publishing from root.
- CI: GitHub Actions building both modules on tag push (BOP has reference workflows in `reference/BiomesOPlenty/.github/workflows`).

---

## 9. Phased workplan

**Phase 0 — scaffolding (verify build first!)**
1. Create `common/`, `neoforge/`, `fabric/` modules; move nothing yet.
2. Root `settings.gradle`/`build.gradle` per §4; move `neoforge.mods.toml` to neoforge module; empty `NekorationNeoForge` entry calling `Nekoration.init()`; hello-world fabric module (empty entrypoint).
3. Verify: `gradlew :neoforge:build`, `gradlew :fabric:build`, and a `runClient` on each. Resolve Loom/Gradle-9/config-cache issues **here**, before touching sources.

**Phase 1 — common-ize (neoforge keeps working throughout)**
4. Move all bucket-1 files to common; move the NeoForge-only bundle (`mixin/*`, `world/upgrade/*`, its test, `nekoration.mixins.json`, `datagen/*`) into the neoforge module (§6); delete every `net.neoforged` import that has a vanilla substitute (§6); keep neoforge entry classes green (`gradlew :neoforge:build` after each package).
5. Introduce `NekoRegistrar`/`RegistrySupplier`; rewrite `registry/*` and ~140 references; switch tabs to vanilla builder API.
6. Networking payloads → common + `PayloadContext`; config → `NekoConfigData` interface with neoforge impl; `NekoPlatform` facade.
7. Datagen now lives in the neoforge module (moved in step 4); re-run the `data` run → verify `common/src/generated/resources` diff-empty.

**Phase 2 — client abstraction**
8. `NekoClientSetup` + event split per §5.3; CT rework per §6 caveat (client-side data store).
9. Verify neoforge `runClient`: tabs/filters, easel menu, painting, wallpaper, CT visuals, hint renderers.

**Phase 3 — Fabric implementation**
10. `FabricRegistrar`, `FabricNetwork`, `FabricConfig`, `NekorationFabric(Client)` entrypoints; client hooks (`ColorProviderRegistry`, `BuiltinItemRendererRegistry`, `EntityModelLayerRegistry`, screens, `WorldRenderEvents`, `ScreenEvents`, `ModelLoadingPlugin.modifyModelAfterBake`, `ExtendedScreenHandlerType`).
11. `fabric.mod.json` (no mixins — upgrader is NeoForge-only, §5.7).
12. Fabric `runClient` full smoke test: same checklist as 9.

**Phase 4 — polish**
13. Jade fabric artifact + plugin; publishing matrix; CI; update README/docs and `.vscode` launch configs (per-module devlaunch files).

Sizing: Phase 1 is the bulk (registration facade + 36-file de-neoforging, but ~100 files move verbatim). Phases 2–3 are incremental, each split point independently testable.

---

## 10. Risks & open questions

| Risk | Mitigation |
|---|---|
| Loom ↔ Gradle 9.2.1 / configuration-cache friction | Phase 0 spike; pin newest Loom or drop wrapper to 8.10.x (MDG fine either way) |
| Fabric post-bake model hook behavior differs from `ModelEvent.ModifyBakingResult` | Both confirmed present (`ModelModifier.AfterBake` since 1.21); verify wrap order vs phase callbacks in Phase 2/3 |
| Fabric networking threading | Route all handlers through `PayloadContext.enqueue()`; test easel-menu round-trips on dedicated server |
| Creative-tab row/column differences change tab ordering vs current layout | `withTabsBefore` keys preserved; visually check creative screen |
| CT visuals drift after removing NeoForge model data | Compare screenshots neoforge-before vs both-after (windows/cement panes under world edits) |
| `RegistrySupplier` API misses a consumer pattern (`Holder` in tags, `getKey` in recipes) | expose `holder()`/`getKey()` up front; compile errors will enumerate gaps quickly |
| Empty SERVER config spec disappears on Fabric | no behavioral change (spec has no values); keep neoforge file for familiarity |

## 11. Dev environment notes

- Project lives at `D:\Projects\nekoration-1.21.1` (Windows) / WSL `/mnt/d/...`. Available JDKs: `C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot` (Windows) — matches the toolchain logs.
- In WSL, let foojay resolve a Linux JDK 21 for toolchains, or run Gradle from the Windows side (`gradlew.bat`) to reuse the Microsoft JDK and existing caches; either is fine — builds must just stay consistent.
- `.vscode/launch.json` currently points at MDG devlaunch files (`build/moddev/*Run*.txt`, `-Dfml.modFolders`) → neoforge-only; add per-module entries in Phase 4 (Loom generates its own dev-launch config too).
- Keep the Tencent Gradle mirror in the wrapper.

---

## 12. Implementation status (updated 2026-09-10)

Phases 0–3 are implemented and verified; Phase 4 polish items remain.

**Done**

- Phase 0: three-module build (`common`, `neoforge`, `fabric`); Loom 1.11.8 on Gradle 9.2.1 with configuration cache; BOP-style source injection in the root `build.gradle` (main compile only); datagen writes to `common/src/generated/resources`.
- Phase 1: `xplat` layer (`NekoPlatform`/`NekoPlatformImpl`, `NekoRegistrar`, `RegistrySupplier`, `PayloadContext`, `NekoConfigData`); all 9+ registry classes rewritten on the facade and moved to common; every `net.neoforged` import removed from common (common compiles against NeoForm vanilla + mojmap); entries split into `NekorationNeoForge(Client)`; payloads live in common; `NekoConfig` (NeoForge `ModConfigSpec`) implements `NekoConfigData`.
- Phase 2 (folded into Phase 1): `NekoClientSetup`, `NekoColorHandlers`, `NekoCreativeTabFilterClient`, placement-hint renderers all split into common logic + `neoforge.client.*` wiring; CT system split into a common core (`NekoCTModel`, `NekoCTRegistry`) plus per-platform shells (`NeoForgeCTModel` via model data; `FabricCTModel` via the Fabric renderer API — note: the plan §6 client-side-store idea was not viable since vanilla `getQuads` has no position, so the shells differ per loader instead).
- Phase 3: `FabricRegistrar` (Registry.registerForHolder), `FabricNetwork`, `FabricConfig` (JSON), `NekorationFabric(Client)`, full client hooks (renderers, colors, layers, item properties, BEWLR, tooltip component, block outline, creative filter, CT, screens via reflection because vanilla `MenuScreens.register` is private and fabric removed `HandledScreens`); `nekoration.accesswidener` mirrors the common AT.
- Verified: `:common:build` (vanilla-only), `:neoforge:build` + test, `:fabric:build`; `runServer` on both loaders reaches full startup; `runClient` on both reaches the title screen with zero missing models; `runData` output is diff-empty.

**Divergences from the plan worth knowing**

- `PaintingEntity` no longer uses `IEntityWithComplexSpawn`; the server sends a common `PaintingInitPayload` from `startSeenByPlayer`.
- `PotBlock.canSustainPlant` (NeoForge `TriState`) was dropped; `FrameSideBlock` break-to-reconnect now goes through `playerWillDestroy`/`playerDestroy` (vanilla has no cancellable destroy hook); `CreativeModeTab` ordering uses registration order (vanilla has no `withTabsBefore`); `FireBlock`-based flammability registration moved to the platform modules.
- Client-class references were purged from all server-loaded common classes (fabric's env check refuses to load them); the client messaging/handling logic lives in `ClientHelper`.
- Hand-written assets (`assets/`, `data/`) moved from the neoforge module to `common/src/main/resources` so both loaders package them; the neoforge module keeps `nekoration.mixins.json` and the mods.toml template.
- JEI is now a runtime-testing dependency on both loaders (`localRuntime` on neoforge, `modLocalRuntime` on fabric) and is declared as an optional dependency in `neoforge.mods.toml` / `fabric.mod.json` `suggests`. Enabling it required bumping NeoForge to 21.1.250 (JEI 19.53.0.426 requires 21.1.238+) and Loom to 1.13.6 (older Loom refuses to consume mod jars built by newer Loom). JEI also exposed a compatibility bug: `ColorInheritStonecuttingRecipe` extended `SingleItemRecipe` instead of vanilla `StonecutterRecipe`, so JEI's vanilla stonecutting plugin threw a `ClassCastException` on the client recipe sync (hard crash on neoforge, logged disconnect on fabric); it now extends `StonecutterRecipe` and overrides `getSerializer()` like `ColorInheritShapedRecipe`. Hand-written recipe JSONs (`painting`/`palette`/`wallpaper`) were fixed to the 1.21.1 ingredient format.
- Texture datagen no longer hardcodes a source-tree output path: `NekoTextureAssetProvider` derives its output root from the data run's `--output` (`PackOutput#getOutputFolder`), and both texture providers read their hand-written inputs from `common/src/main/resources` after the asset move. (A stale repo-root `src/generated/...` path briefly left orphaned BOP plank PNGs from a failed pre-fix datagen run — cleaned up.)

**Remaining (Phase 4)**

- Jade fabric artifact + plugin (kept neoforge-only for now, as the plan allows).
- Publishing matrix (Minotaur/cursegradle per loader) and CI workflows.
- Fabric CT visuals: compare against neoforge screenshots (the quad emission path uses `fromVanilla` + `spriteBake`; verify in-game and adjust if atlas mapping differs).
- Fabric dev launch entries in `.vscode/launch.json` (Loom generates them on IDE sync; stale root-project entries were removed).
