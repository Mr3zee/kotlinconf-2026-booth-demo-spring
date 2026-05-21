# Spring + Kotlin Demo (KotlinConf '26)

A demo project showcasing **Spring + Kotlin** scenarios — how to take an existing Spring Boot application and evolve it to use Kotlin in different ways.

The codebase is a stripped-down Spring Petclinic used purely as a realistic carrier for the demo.
The interesting part is *not* the app itself but the **build/setup and code migrations** demonstrated on different branches,
driven by the prompts in [`prompts/`](prompts/).

## Demo scenarios (prompts)

Each prompt drives the demo from the `main` branch state to a target setup. See [`prompts/README.md`](prompts/README.md) for the full list and the branch each one produces.

## Branches

| Branch                                                                                                                                                  | Prompt                                                                            | State                                                                                                            |
|---------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| [`main`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/main)                                                                         | —                                                                                 | Baseline Spring Boot Petclinic on **Gradle (Kotlin DSL)**, pure Java. Starting point for every demo.             |
| [`add-kotlin-file-to-existing-java-project`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/add-kotlin-file-to-existing-java-project) | —                                                                                 | Adds Kotlin support to the Gradle build and introduces a single Kotlin service (`VisitInsights.kt`).             |
| [`rewrite-owner-mapper`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/rewrite-owner-mapper)                                         | [`rewrite-owner-mapper.md`](prompts/rewrite-owner-mapper.md)                      | `OwnerMapper` rewritten in Kotlin, leaving the rest of the project untouched.                                    |
| [`migrate-ver-service-and-entity`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/migrate-ver-service-and-entity)                     | [`migrate-vet-service-and-entity.md`](prompts/migrate-vet-service-and-entity.md)  | `VetService`, `Vet`, and `VetView` migrated to idiomatic Kotlin.                                                 |
| [`full-migration`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/full-migration)                                                     | [`full-migration-in-steps.md`](prompts/full-migration-in-steps.md)                | Full Java → Kotlin migration done in 6 reviewable steps (tests → utils → DTOs → services → entities → controllers). |
| [`kotlin-maven`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/kotlin-maven)                                                         | [`migrate-to-maven-with-kotlin.md`](prompts/migrate-to-maven-with-kotlin.md)      | Build migrated to **Maven + Kotlin**, with `Specialty.kt` as a sample Kotlin file.                               |
| [`kotlin-toolchain`](https://github.com/Mr3zee/kotlinconf-2026-booth-demo-spring/tree/kotlin-toolchain)                                                 | [`migrate-to-kotlin-toolchain.md`](prompts/migrate-to-kotlin-toolchain.md)        | Build migrated to a [Kotlin Toolchain](https://kotlin-toolchain.org/latest/) setup, with `Specialty.kt`.         |

## This branch — `migrate-ver-service-and-entity`

Result of running [`migrate-vet-service-and-entity.md`](prompts/migrate-vet-service-and-entity.md):

- `Vet.java`, `VetService.java`, and `VetView.java` converted to Kotlin under the same packages; Java originals removed.
- `build.gradle.kts` gets Kotlin (`jvm`, `plugin.spring`, `plugin.jpa`) at version 2.3.21 alongside Java.
- Maven build (`pom.xml`, `mvnw`, `.mvn/`) removed — Gradle (Kotlin DSL) is the only build.
- All other files untouched.

## Running the app

```bash
./gradlew bootRun
```

Then open http://localhost:8080/.
