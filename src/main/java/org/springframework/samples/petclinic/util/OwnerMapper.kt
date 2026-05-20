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

import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.dto.OwnerForm
import org.springframework.samples.petclinic.owner.dto.OwnerView
import org.springframework.stereotype.Component

@Component
class OwnerMapper(private val petMapper: PetMapper) {

    fun toForm(owner: Owner): OwnerForm {
        return OwnerForm(owner.id, owner.firstName, owner.lastName, owner.address, owner.city, owner.telephone)
    }

    fun updateEntity(owner: Owner, form: OwnerForm) {
        owner.firstName = form.firstName
        owner.lastName = form.lastName
        owner.address = form.address
        owner.city = form.city
        owner.telephone = form.telephone
    }

    fun toEntity(form: OwnerForm): Owner {
        val owner = Owner()
        updateEntity(owner, form)
        return owner
    }

    fun toView(owner: Owner): OwnerView {
        val pets = owner.pets.map { petMapper.toView(it) }
        val id = checkNotNull(owner.id) { "Cannot build OwnerView from a transient Owner (id is null)" }
        return OwnerView(id, owner.firstName, owner.lastName, owner.address, owner.city, owner.telephone, pets)
    }
}
