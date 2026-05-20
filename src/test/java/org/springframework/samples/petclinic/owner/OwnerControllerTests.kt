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

import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.BDDMockito.given
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.samples.petclinic.owner.dto.OwnerForm
import org.springframework.samples.petclinic.util.OwnerMapper
import org.springframework.samples.petclinic.util.PetMapper
import org.springframework.samples.petclinic.util.VisitMapper
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import java.time.LocalDate
import java.util.Optional

/**
 * Test class for [OwnerController]
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(OwnerController::class)
@Import(OwnerService::class, OwnerMapper::class, PetMapper::class, VisitMapper::class)
@DisabledInNativeImage
@DisabledInAotMode
internal class OwnerControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var owners: OwnerRepository

    private fun george(): Owner {
        val george = Owner()
        george.id = TEST_OWNER_ID
        george.firstName = "George"
        george.lastName = "Franklin"
        george.address = "110 W. Liberty St."
        george.city = "Madison"
        george.telephone = "6085551023"
        val max = Pet()
        val dog = PetType()
        dog.name = "dog"
        max.type = dog
        max.name = "Max"
        max.birthDate = LocalDate.now()
        george.addPet(max)
        max.id = 1
        return george
    }

    @BeforeEach
    fun setup() {
        val george = george()
        given(owners.findByLastNameStartingWith(eq("Franklin"), any<Pageable>()))
            .willReturn(PageImpl(listOf(george)))

        given(owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george))
        val visit = Visit()
        visit.id = 1
        visit.date = LocalDate.now()
        val max = checkNotNull(george.getPet("Max")) { "Test setup: Max not found on George" }
        max.visits.add(visit)
    }

    @Test
    fun initCreationForm() {
        mockMvc.perform(get("/owners/new"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    @Test
    fun processCreationFormSuccess() {
        mockMvc.perform(
            post("/owners/new").param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "123 Caramel Street")
                .param("city", "London")
                .param("telephone", "1316761638")
        ).andExpect(status().is3xxRedirection)
    }

    @Test
    fun processCreationFormHasErrors() {
        mockMvc.perform(
            post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs").param("city", "London")
        )
            .andExpect(status().isOk)
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    @Test
    fun initFindForm() {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/findOwners"))
    }

    @Test
    fun processFindFormSuccess() {
        val other = Owner()
        other.id = TEST_OWNER_ID + 1
        val tasks = PageImpl(listOf(george(), other))
        `when`(owners.findByLastNameStartingWith(any<String>(), any<Pageable>())).thenReturn(tasks)
        mockMvc.perform(get("/owners?page=1")).andExpect(status().isOk).andExpect(view().name("owners/ownersList"))
    }

    @Test
    fun processFindFormByLastName() {
        val tasks = PageImpl(listOf(george()))
        `when`(owners.findByLastNameStartingWith(eq("Franklin"), any<Pageable>())).thenReturn(tasks)
        mockMvc.perform(get("/owners?page=1").param("lastName", "Franklin"))
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/$TEST_OWNER_ID"))
    }

    @Test
    fun processFindFormNoOwnersFound() {
        val tasks = PageImpl<Owner>(listOf())
        `when`(owners.findByLastNameStartingWith(eq("Unknown Surname"), any<Pageable>())).thenReturn(tasks)
        mockMvc.perform(get("/owners?page=1").param("lastName", "Unknown Surname"))
            .andExpect(status().isOk)
            .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
            .andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
            .andExpect(view().name("owners/findOwners"))
    }

    @Test
    fun initUpdateOwnerForm() {
        mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", hasProperty<Any>("lastName", `is`<String>("Franklin"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("firstName", `is`<String>("George"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("address", `is`<String>("110 W. Liberty St."))))
            .andExpect(model().attribute("owner", hasProperty<Any>("city", `is`<String>("Madison"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("telephone", `is`<String>("6085551023"))))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    @Test
    fun processUpdateOwnerFormSuccess() {
        mockMvc.perform(
            post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "123 Caramel Street")
                .param("city", "London")
                .param("telephone", "1616291589")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    @Test
    fun processUpdateOwnerFormUnchangedSuccess() {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID))
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    @Test
    fun processUpdateOwnerFormHasErrors() {
        mockMvc.perform(
            post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "")
                .param("telephone", "")
        )
            .andExpect(status().isOk)
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    @Test
    fun showOwner() {
        mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(model().attribute("owner", hasProperty<Any>("lastName", `is`<String>("Franklin"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("firstName", `is`<String>("George"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("address", `is`<String>("110 W. Liberty St."))))
            .andExpect(model().attribute("owner", hasProperty<Any>("city", `is`<String>("Madison"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("telephone", `is`<String>("6085551023"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("pets", not(empty<Any>()))))
            .andExpect(
                model().attribute(
                    "owner",
                    hasProperty<Any>("pets", hasItem(hasProperty<Any>("visits", hasSize<Any>(greaterThan(0)))))
                )
            )
            .andExpect(view().name("owners/ownerDetails"))
    }

    @Test
    fun processUpdateOwnerFormWithIdMismatch() {
        val pathOwnerId = 1

        val owner = Owner()
        owner.id = 2
        owner.firstName = "John"
        owner.lastName = "Doe"
        owner.address = "Center Street"
        owner.city = "New York"
        owner.telephone = "0123456789"

        `when`(owners.findById(pathOwnerId)).thenReturn(Optional.of(owner))

        val form = OwnerForm()
        form.id = 2
        form.firstName = "John"
        form.lastName = "Doe"
        form.address = "Center Street"
        form.city = "New York"
        form.telephone = "0123456789"

        mockMvc.perform(MockMvcRequestBuilders.post("/owners/{ownerId}/edit", pathOwnerId).flashAttr("owner", form))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/owners/$pathOwnerId/edit"))
            .andExpect(flash().attributeExists("error"))
    }

    companion object {
        private const val TEST_OWNER_ID = 1
    }
}
