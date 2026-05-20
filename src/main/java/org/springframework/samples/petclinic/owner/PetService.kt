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
package org.springframework.samples.petclinic.owner

import org.springframework.samples.petclinic.owner.dto.OwnerView
import org.springframework.samples.petclinic.owner.dto.PetForm
import org.springframework.samples.petclinic.util.OwnerMapper
import org.springframework.samples.petclinic.util.PetMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PetService(
    private val owners: OwnerRepository,
    private val petTypes: PetTypeRepository,
    private val petMapper: PetMapper,
    private val ownerMapper: OwnerMapper,
) {

    @Transactional(readOnly = true)
    fun findPetTypes(): Collection<PetType> = petTypes.findPetTypes()

    @Transactional(readOnly = true)
    fun findOwnerView(ownerId: Int): OwnerView = ownerMapper.toView(loadOwner(ownerId))

    @Transactional(readOnly = true)
    fun findPetForm(ownerId: Int, petId: Int?): PetForm {
        if (petId == null) {
            return PetForm()
        }
        val existing = loadOwner(ownerId).getPet(petId)
        return if (existing == null) PetForm() else petMapper.toForm(existing)
    }

    @Transactional(readOnly = true)
    fun isDuplicateName(ownerId: Int, name: String, excludePetId: Int?): Boolean {
        val owner = loadOwner(ownerId)
        if (excludePetId == null) {
            return owner.getPet(name, true) != null
        }
        val match = owner.getPet(name, false)
        return match != null && match.id != excludePetId
    }

    @Transactional
    fun create(ownerId: Int, form: PetForm) {
        val owner = loadOwner(ownerId)
        val pet = petMapper.toEntity(form)
        owner.addPet(pet)
        owners.save(owner)
    }

    @Transactional
    fun update(ownerId: Int, petId: Int, form: PetForm) {
        val owner = loadOwner(ownerId)
        val existing = checkNotNull(owner.getPet(petId)) { "Pet not found on owner for id=$petId" }
        petMapper.updateEntity(existing, form)
        owners.save(owner)
    }

    private fun loadOwner(ownerId: Int): Owner =
        owners.findById(ownerId).orElseThrow {
            IllegalArgumentException(
                "Owner not found with id: $ownerId. Please ensure the ID is correct "
            )
        }
}
