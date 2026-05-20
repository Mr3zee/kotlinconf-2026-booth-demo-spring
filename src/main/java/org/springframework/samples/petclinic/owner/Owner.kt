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

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.core.style.ToStringCreator
import org.springframework.samples.petclinic.model.Person

/**
 * Simple JavaBean domain object representing an owner.
 */
@Entity
@Table(name = "owners")
class Owner(
    @Column
    @field:NotBlank
    var address: String = "",
    @Column
    @field:NotBlank
    var city: String = "",
    @Column
    @field:NotBlank
    @field:Pattern(regexp = "\\d{10}", message = "{telephone.invalid}")
    var telephone: String = "",
) : Person() {

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @OrderBy("name")
    val pets: MutableList<Pet> = ArrayList()

    fun addPet(pet: Pet) {
        if (pet.isNew()) {
            pets.add(pet)
        }
    }

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     */
    fun getPet(name: String): Pet? = getPet(name, false)

    /**
     * Return the Pet with the given id, or null if none found for this Owner.
     */
    fun getPet(id: Int?): Pet? =
        pets.firstOrNull { !it.isNew() && it.id == id }

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     * @param ignoreNew whether to ignore new pets (pets that are not saved yet)
     */
    fun getPet(name: String, ignoreNew: Boolean): Pet? =
        pets.firstOrNull { pet ->
            pet.name.equals(name, ignoreCase = true) && (!ignoreNew || !pet.isNew())
        }

    override fun toString(): String =
        ToStringCreator(this)
            .append("id", id)
            .append("new", isNew())
            .append("lastName", lastName)
            .append("firstName", firstName)
            .append("address", address)
            .append("city", city)
            .append("telephone", telephone)
            .toString()

    /**
     * Adds the given [Visit] to the [Pet] with the given identifier.
     */
    fun addVisit(petId: Int?, visit: Visit) {
        requireNotNull(petId) { "Pet identifier must not be null!" }
        val pet = requireNotNull(getPet(petId)) { "Invalid Pet identifier!" }
        pet.addVisit(visit)
    }
}
