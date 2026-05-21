# Prompts

Each prompt is a self-contained instruction set that takes the project from the `main` branch state to a target setup or refactor. Running a prompt should produce (roughly) the corresponding branch.

| Prompt                                                                       | Target branch                              | What it does                                                                                       |
|------------------------------------------------------------------------------|--------------------------------------------|----------------------------------------------------------------------------------------------------|
| [`rewrite-owner-mapper.md`](rewrite-owner-mapper.md)                         | `rewrite-owner-mapper`                     | Rewrite `OwnerMapper` in Kotlin, ignore project setup.                                             |
| [`migrate-vet-service-and-entity.md`](migrate-vet-service-and-entity.md)     | `migrate-ver-service-and-entity`           | Migrate `VetService`, `Vet`, and `VetView` to idiomatic Kotlin (Gradle setup updated).             |
| [`full-migration-in-steps.md`](full-migration-in-steps.md)                   | `full-migration`                           | Migrate the whole Java codebase to Kotlin in 6 reviewable steps with pauses between them.          |
| [`migrate-to-maven-with-kotlin.md`](migrate-to-maven-with-kotlin.md)         | `kotlin-maven`                             | Switch the build to Maven + Kotlin; only `Specialty.java` is converted.                            |
| [`migrate-to-kotlin-toolchain.md`](migrate-to-kotlin-toolchain.md)           | `kotlin-toolchain`                         | Switch the build to [Kotlin Toolchain](https://kotlin-toolchain.org/latest/); only `Specialty.java` is converted. |

The `add-kotlin-file-to-existing-java-project` branch is a hand-prepared starting point (Kotlin enabled in Gradle + one Kotlin service) and has no associated prompt.
