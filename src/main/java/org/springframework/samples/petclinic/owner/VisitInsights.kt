package org.springframework.samples.petclinic.owner

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class VisitInsightsService(private val owners: OwnerRepository) {

    fun recentVisitsSummary(ownerId: Int): OwnerVisitSummary {
        val owner = owners.findById(ownerId).orElse(null)
            ?: error("Owner not found with id: $ownerId")

        val pets = owner.pets.map { pet ->
            val mostRecent = pet.visits.maxByOrNull { it.date }
            PetVisitInfo(
                petName = pet.name,
                lastVisit = mostRecent?.date,
                daysSinceLastVisit = mostRecent?.date?.daysAgo(),
                totalVisits = pet.visits.size,
            )
        }

        return OwnerVisitSummary(
            ownerId = ownerId,
            ownerName = "${owner.firstName} ${owner.lastName}",
            pets = pets,
        )
    }

    fun previewVisitNote(ownerId: Int, request: VisitNoteRequest): VisitNotePreview {
        val owner = owners.findById(ownerId).orElse(null)
            ?: error("Owner not found with id: $ownerId")

        val pet = owner.pets.firstOrNull { it.name.equals(request.petName, ignoreCase = true) }
        val today = LocalDate.now()

        return VisitNotePreview(
            ownerId = ownerId,
            ownerName = "${owner.firstName} ${owner.lastName}",
            petName = request.petName,
            petKnown = pet != null,
            visitedOn = request.visitedOn,
            daysAgo = ChronoUnit.DAYS.between(request.visitedOn, today),
            inFuture = request.visitedOn.isAfter(today),
            note = request.note,
            noteLength = request.note.length,
        )
    }
}

data class OwnerVisitSummary(
    val ownerId: Int,
    val ownerName: String,
    val pets: List<PetVisitInfo>,
)

data class PetVisitInfo(
    val petName: String?,
    val lastVisit: LocalDate?,
    val daysSinceLastVisit: Long?,
    val totalVisits: Int,
)

data class VisitNoteRequest(
    val petName: String,
    val note: String,
    val visitedOn: LocalDate,
)

data class VisitNotePreview(
    val ownerId: Int,
    val ownerName: String,
    val petName: String,
    val petKnown: Boolean,
    val visitedOn: LocalDate,
    val daysAgo: Long,
    val inFuture: Boolean,
    val note: String,
    val noteLength: Int,
)

private fun LocalDate.daysAgo(): Long = ChronoUnit.DAYS.between(this, LocalDate.now())
