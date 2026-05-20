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
package org.springframework.samples.petclinic.vet

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.util.VetMapper
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

/**
 * Test class for the [VetController]
 */
@WebMvcTest(VetController::class)
@Import(VetService::class, VetMapper::class)
@DisabledInNativeImage
@DisabledInAotMode
internal class VetControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var vets: VetRepository

    private fun james(): Vet {
        val james = Vet()
        james.firstName = "James"
        james.lastName = "Carter"
        james.id = 1
        return james
    }

    private fun helen(): Vet {
        val helen = Vet()
        helen.firstName = "Helen"
        helen.lastName = "Leary"
        helen.id = 2
        val radiology = Specialty()
        radiology.id = 1
        radiology.name = "radiology"
        helen.addSpecialty(radiology)
        return helen
    }

    @BeforeEach
    fun setup() {
        given(vets.findAll()).willReturn(arrayListOf(james(), helen()))
        given(vets.findAll(any<Pageable>()))
            .willReturn(PageImpl(arrayListOf(james(), helen())))
    }

    @Test
    fun showVetListHtml() {
        mockMvc.perform(MockMvcRequestBuilders.get("/vets.html?page=1"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("listVets"))
            .andExpect(view().name("vets/vetList"))
    }

    @Test
    fun showResourcesVetList() {
        val actions = mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.vetList[0].id").value(1))
    }
}
