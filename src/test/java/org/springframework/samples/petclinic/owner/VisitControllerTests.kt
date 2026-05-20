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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.samples.petclinic.util.OwnerMapper
import org.springframework.samples.petclinic.util.PetMapper
import org.springframework.samples.petclinic.util.VisitMapper
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import java.util.Optional

/**
 * Test class for [VisitController]
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(VisitController::class)
@Import(VisitService::class, OwnerMapper::class, PetMapper::class, VisitMapper::class)
@DisabledInNativeImage
@DisabledInAotMode
internal class VisitControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @BeforeEach
    fun init() {
        val owner = Owner()
        val pet = Pet()
        owner.addPet(pet)
        pet.id = TEST_PET_ID
        given(owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner))
    }

    @Test
    fun initNewVisitForm() {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk)
            .andExpect(view().name("pets/createOrUpdateVisitForm"))
    }

    @Test
    fun processNewVisitFormSuccess() {
        mockMvc.perform(
            post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "George")
                .param("description", "Visit Description")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    @Test
    fun processNewVisitFormHasErrors() {
        mockMvc.perform(
            post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "George")
        )
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(status().isOk)
            .andExpect(view().name("pets/createOrUpdateVisitForm"))
    }

    companion object {
        private const val TEST_OWNER_ID = 1
        private const val TEST_PET_ID = 1
    }
}
