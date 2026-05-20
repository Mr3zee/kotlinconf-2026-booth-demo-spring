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
import org.springframework.samples.petclinic.owner.dto.PetView
import org.springframework.samples.petclinic.owner.dto.VisitForm
import org.springframework.samples.petclinic.util.OwnerMapper
import org.springframework.samples.petclinic.util.PetMapper
import org.springframework.samples.petclinic.util.VisitMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VisitService(
    private val owners: OwnerRepository,
    private val ownerMapper: OwnerMapper,
    private val petMapper: PetMapper,
    private val visitMapper: VisitMapper,
) {

    @Transactional(readOnly = true)
    fun findOwnerView(ownerId: Int): OwnerView = ownerMapper.toView(loadOwner(ownerId))

    @Transactional(readOnly = true)
    fun findPetView(ownerId: Int, petId: Int): PetView {
        val pet = loadOwner(ownerId).getPet(petId)
            ?: throw IllegalArgumentException(
                "Pet with id $petId not found for owner with id $ownerId."
            )
        return petMapper.toView(pet)
    }

    @Transactional
    fun create(ownerId: Int, petId: Int, form: VisitForm) {
        val owner = loadOwner(ownerId)
        if (owner.getPet(petId) == null) {
            throw IllegalArgumentException(
                "Pet with id $petId not found for owner with id $ownerId."
            )
        }
        val visit = visitMapper.toEntity(form)
        owner.addVisit(petId, visit)
        owners.save(owner)
    }

    private fun loadOwner(ownerId: Int): Owner =
        owners.findById(ownerId).orElseThrow {
            IllegalArgumentException(
                "Owner not found with id: $ownerId. Please ensure the ID is correct "
            )
        }
}
