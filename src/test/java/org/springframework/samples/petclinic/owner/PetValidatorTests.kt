/*
 * Copyright 2012-2024 the original author or authors.
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

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.samples.petclinic.owner.dto.PetForm
import org.springframework.samples.petclinic.util.PetValidator
import org.springframework.validation.Errors
import org.springframework.validation.MapBindingResult
import java.time.LocalDate

/**
 * Test class for [PetValidator]
 *
 * @author Wick Dynex
 */
@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
internal class PetValidatorTests {

    private lateinit var petValidator: PetValidator
    private lateinit var pet: PetForm
    private lateinit var petType: PetType
    private lateinit var errors: Errors

    @BeforeEach
    fun setUp() {
        petValidator = PetValidator()
        pet = PetForm()
        petType = PetType()
        errors = MapBindingResult(HashMap<Any, Any>(), "pet")
    }

    @Test
    fun validate() {
        petType.name = PET_TYPE_NAME
        pet.name = PET_NAME
        pet.type = petType
        pet.birthDate = PET_BIRTH_DATE

        petValidator.validate(pet, errors)

        assertFalse(errors.hasErrors())
    }

    @Nested
    inner class ValidateHasErrors {

        @Test
        fun validateWithInvalidPetName() {
            petType.name = PET_TYPE_NAME
            pet.name = ""
            pet.type = petType
            pet.birthDate = PET_BIRTH_DATE

            petValidator.validate(pet, errors)

            assertTrue(errors.hasFieldErrors("name"))
        }

        @Test
        fun validateWithInvalidPetType() {
            pet.name = PET_NAME
            pet.type = null
            pet.birthDate = PET_BIRTH_DATE

            petValidator.validate(pet, errors)

            assertTrue(errors.hasFieldErrors("type"))
        }

        @Test
        fun validateWithInvalidBirthDate() {
            petType.name = PET_TYPE_NAME
            pet.name = PET_NAME
            pet.type = petType
            pet.birthDate = null

            petValidator.validate(pet, errors)

            assertTrue(errors.hasFieldErrors("birthDate"))
        }
    }

    companion object {
        private const val PET_NAME = "Buddy"
        private const val PET_TYPE_NAME = "Dog"
        private val PET_BIRTH_DATE: LocalDate = LocalDate.of(1990, 1, 1)
    }
}
