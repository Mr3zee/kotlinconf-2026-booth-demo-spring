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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.samples.petclinic.util.OwnerMapper
import org.springframework.samples.petclinic.util.PetMapper
import org.springframework.samples.petclinic.util.PetTypeFormatter
import org.springframework.samples.petclinic.util.VisitMapper
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import java.time.LocalDate
import java.util.Optional

/**
 * Test class for the [PetController]
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(
    value = [PetController::class],
    includeFilters = [ComponentScan.Filter(value = [PetTypeFormatter::class], type = FilterType.ASSIGNABLE_TYPE)]
)
@Import(PetService::class, PetMapper::class, OwnerMapper::class, VisitMapper::class)
@DisabledInNativeImage
@DisabledInAotMode
internal class PetControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @MockitoBean
    private lateinit var types: PetTypeRepository

    @BeforeEach
    fun setup() {
        val cat = PetType()
        cat.id = 3
        cat.name = "hamster"
        given(types.findPetTypes()).willReturn(listOf(cat))

        val owner = Owner()
        val pet = Pet()
        val dog = Pet()
        owner.addPet(pet)
        owner.addPet(dog)
        pet.id = TEST_PET_ID
        dog.id = TEST_PET_ID + 1
        pet.name = "petty"
        dog.name = "doggy"
        given(owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner))
    }

    @Test
    fun initCreationForm() {
        mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(view().name("pets/createOrUpdatePetForm"))
            .andExpect(model().attributeExists("pet"))
    }

    @Test
    fun processCreationFormSuccess() {
        mockMvc.perform(
            post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
                .param("type", "hamster")
                .param("birthDate", "2015-02-12")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    @Nested
    inner class ProcessCreationFormHasErrors {

        @Test
        fun processCreationFormWithBlankName() {
            mockMvc.perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "\t \n")
                    .param("birthDate", "2015-02-12")
            )
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"))
                .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
                .andExpect(status().isOk)
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }

        @Test
        fun processCreationFormWithDuplicateName() {
            mockMvc.perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "petty")
                    .param("birthDate", "2015-02-12")
            )
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"))
                .andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
                .andExpect(status().isOk)
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }

        @Test
        fun processCreationFormWithMissingPetType() {
            mockMvc.perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
                    .param("birthDate", "2015-02-12")
            )
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "type"))
                .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
                .andExpect(status().isOk)
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }

        @Test
        fun processCreationFormWithInvalidBirthDate() {
            val currentDate = LocalDate.now()
            val futureBirthDate = currentDate.plusMonths(1).toString()

            mockMvc.perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
                    .param("birthDate", futureBirthDate)
            )
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
                .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch.birthDate"))
                .andExpect(status().isOk)
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }

        @Test
        fun initUpdateForm() {
            mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
                .andExpect(status().isOk)
                .andExpect(model().attributeExists("pet"))
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }
    }

    @Test
    fun processUpdateFormSuccess() {
        mockMvc.perform(
            post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
                .param("type", "hamster")
                .param("birthDate", "2015-02-12")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    @Nested
    inner class ProcessUpdateFormHasErrors {

        @Test
        fun processUpdateFormWithInvalidBirthDate() {
            mockMvc.perform(
                post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", " ")
                    .param("birthDate", "2015/02/12")
            )
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
                .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch"))
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }

        @Test
        fun processUpdateFormWithBlankName() {
            mockMvc.perform(
                post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "  ")
                    .param("birthDate", "2015-02-12")
            )
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"))
                .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
                .andExpect(view().name("pets/createOrUpdatePetForm"))
        }
    }

    companion object {
        private const val TEST_OWNER_ID = 1
        private const val TEST_PET_ID = 1
    }
}
