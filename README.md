# Spring + Kotlin Demo (KotlinConf '26)

A demo project showcasing **Spring + Kotlin** scenarios — how to take an existing Spring Boot application and evolve it to use Kotlin in different ways.

The codebase is a stripped-down Spring Petclinic used purely as a realistic carrier for the demo. 
The interesting part is *not* the app itself but the **build/setup migrations** demonstrated on different branches, 
driven by the prompts in [`prompts/`](prompts/).

## Demo scenarios (prompts)

Each prompt drives the demo from the `main` branch state to a target setup.

- [`prompts/migrate-to-maven-with-kotlin.md`](prompts/migrate-to-maven-with-kotlin.md) — migrate to a Maven + Kotlin setup.
- [`prompts/migrate-to-kotlin-toolchain.md`](prompts/migrate-to-kotlin-toolchain.md) — migrate to a [Kotlin Toolchain](https://kotlin-toolchain.org/latest/) setup.

## Branches

| Branch             | State                                                                                                |
|--------------------|------------------------------------------------------------------------------------------------------|
| `main`             | Baseline Spring Boot Petclinic on **Gradle (Kotlin DSL)**, pure Java. Starting point for every demo. |
| `kotlin-maven`     | Result of running `migrate-to-maven-with-kotlin.md` — **Maven + Kotlin** build with `Specialty.kt`.  |
| `kotlin-toolchain` | Result of running `migrate-to-kotlin-toolchain.md` — **Kotlin Toolchain** build with `Specialty.kt`. |

## Running the app (main)

Gradle:

```bash
./gradlew bootRun
```

Maven:

```bash
./mvnw spring-boot:run
```

Then open http://localhost:8080/.
