Do a Java to Kotlin migration for this project in steps.

After each step, output exactly `STEP N COMPLETE — awaiting review` on its own line and call no further tools until I confirm. Do not start the next step on your own.

1. Tests first
2. Utilities and leaf classes
3. DTOs and sealed hierarchies
4. Service layer (business logic)
5. JPA entities
6. Controllers

Guidelines:
- Use the 2.3.21 Kotlin version.
- Update the project setup (gradle only, ignore maven)
- Put Kotlin files in the `src/main/java` directory, alongside Java files.
- No !! operators
- Don’t stick to Java 1-1 mapping, use Kotlin benefits and features to make code more concise
- Rely on Kotlin compiler plugins like allopen for smaller code footprint
- Entities and DTOs should use non-nullable types where implied by schema 
- Run tests after the migration
- Run the server and check that related endpoints work
- Explain your choices briefly after all is done
