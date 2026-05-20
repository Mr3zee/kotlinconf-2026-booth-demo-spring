/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace
import org.springframework.data.domain.Pageable
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.owner.PetTypeRepository
import org.springframework.samples.petclinic.owner.Visit
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Integration test of the Service and the Repository layer.
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */
@DataJpaTest
// Ensure that if the mysql profile is active we connect to the real database:
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @TestPropertySource("/application-postgres.properties")
internal open class ClinicServiceTests {

    @Autowired
    protected lateinit var owners: OwnerRepository

    @Autowired
    protected lateinit var types: PetTypeRepository

    @Autowired
    protected lateinit var vets: VetRepository

    private val pageable: Pageable = Pageable.unpaged()

    @Test
    fun shouldFindOwnersByLastName() {
        var page = owners.findByLastNameStartingWith("Davis", pageable)
        assertThat(page).hasSize(2)

        page = owners.findByLastNameStartingWith("Daviss", pageable)
        assertThat(page).isEmpty()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        assertThat(owner.lastName).startsWith("Franklin")
        assertThat(owner.pets).hasSize(1)
        val type = checkNotNull(owner.pets[0].type)
        assertThat(type.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    open fun shouldInsertOwner() {
        var page = owners.findByLastNameStartingWith("Schultz", pageable)
        val found = page.totalElements.toInt()

        val owner = Owner()
        owner.firstName = "Sam"
        owner.lastName = "Schultz"
        owner.address = "4, Evans Street"
        owner.city = "Wollongong"
        owner.telephone = "4444444444"
        owners.save(owner)
        assertThat(owner.id).isNotZero

        page = owners.findByLastNameStartingWith("Schultz", pageable)
        assertThat(page.totalElements).isEqualTo((found + 1).toLong())
    }

    @Test
    @Transactional
    open fun shouldUpdateOwner() {
        var optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        var owner = optionalOwner.get()
        val oldLastName = owner.lastName
        val newLastName = oldLastName + "X"

        owner.lastName = newLastName
        owners.save(owner)

        // retrieving new name from database
        optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        owner = optionalOwner.get()
        assertThat(owner.lastName).isEqualTo(newLastName)
    }

    @Test
    fun shouldFindAllPetTypes() {
        val petTypes = types.findPetTypes()

        val petType1 = EntityUtils.getById(petTypes, PetType::class.java, 1)
        assertThat(petType1.name).isEqualTo("cat")
        val petType4 = EntityUtils.getById(petTypes, PetType::class.java, 4)
        assertThat(petType4.name).isEqualTo("snake")
    }

    @Test
    @Transactional
    open fun shouldInsertPetIntoDatabaseAndGenerateId() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        var owner6 = optionalOwner.get()

        val found = owner6.pets.size

        var pet = Pet()
        pet.name = "bowser"
        val petTypes = types.findPetTypes()
        pet.type = EntityUtils.getById(petTypes, PetType::class.java, 2)
        pet.birthDate = LocalDate.now()
        owner6.addPet(pet)
        assertThat(owner6.pets).hasSize(found + 1)

        owners.save(owner6)

        optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        owner6 = optionalOwner.get()
        assertThat(owner6.pets).hasSize(found + 1)
        // checks that id has been generated
        pet = checkNotNull(owner6.getPet("bowser"))
        assertThat(pet.id).isNotNull
    }

    @Test
    @Transactional
    open fun shouldUpdatePetName() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        var owner6 = optionalOwner.get()

        var pet7 = checkNotNull(owner6.getPet(7))
        val oldName = pet7.name

        val newName = oldName + "X"
        pet7.name = newName
        owners.save(owner6)

        optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        owner6 = optionalOwner.get()
        pet7 = checkNotNull(owner6.getPet(7))
        assertThat(pet7.name).isEqualTo(newName)
    }

    @Test
    fun shouldFindVets() {
        val vetList = vets.findAll()

        val vet = EntityUtils.getById(vetList, Vet::class.java, 3)
        assertThat(vet.lastName).isEqualTo("Douglas")
        assertThat(vet.nrOfSpecialties).isEqualTo(2)
        assertThat(vet.specialties[0].name).isEqualTo("dentistry")
        assertThat(vet.specialties[1].name).isEqualTo("surgery")
    }

    @Test
    @Transactional
    open fun shouldAddNewVisitForPet() {
        val optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner6 = optionalOwner.get()

        val pet7 = checkNotNull(owner6.getPet(7))
        val found = pet7.visits.size
        val visit = Visit()
        visit.description = "test"

        owner6.addVisit(pet7.id, visit)
        owners.save(owner6)

        assertThat(pet7.visits)
            .hasSize(found + 1)
            .allMatch { it.id != null }
    }

    @Test
    fun shouldFindVisitsByPetId() {
        val optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner6 = optionalOwner.get()

        val pet7 = checkNotNull(owner6.getPet(7))
        val visits = pet7.visits

        assertThat(visits)
            .hasSize(2)
            .element(0)
            .extracting(Visit::date)
            .isNotNull
    }
}
