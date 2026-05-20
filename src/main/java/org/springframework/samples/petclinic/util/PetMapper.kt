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
package org.springframework.samples.petclinic.util

import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.dto.PetForm
import org.springframework.samples.petclinic.owner.dto.PetView
import org.springframework.stereotype.Component

@Component
class PetMapper(private val visitMapper: VisitMapper) {

    fun toForm(pet: Pet): PetForm {
        return PetForm(pet.id, pet.name, pet.birthDate, pet.type)
    }

    fun updateEntity(pet: Pet, form: PetForm) {
        pet.name = form.name ?: ""
        pet.birthDate = form.birthDate
        pet.type = form.type
    }

    fun toEntity(form: PetForm): Pet {
        val pet = Pet()
        updateEntity(pet, form)
        return pet
    }

    fun toView(pet: Pet): PetView {
        val visits = pet.visits.map { visitMapper.toView(it) }
        val id = checkNotNull(pet.id) { "Cannot build PetView from a transient Pet (id is null)" }
        return PetView(id, pet.name, pet.birthDate, pet.type, visits)
    }
}
