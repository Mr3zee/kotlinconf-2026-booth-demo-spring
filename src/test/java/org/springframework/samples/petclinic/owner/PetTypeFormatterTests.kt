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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.samples.petclinic.util.PetTypeFormatter
import java.text.ParseException
import java.util.Locale

/**
 * Test class for [PetTypeFormatter]
 *
 * @author Colin But
 */
@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
internal class PetTypeFormatterTests {

    @Mock
    private lateinit var types: PetTypeRepository

    private lateinit var petTypeFormatter: PetTypeFormatter

    @BeforeEach
    fun setup() {
        petTypeFormatter = PetTypeFormatter(types)
    }

    @Test
    fun testPrint() {
        val petType = PetType()
        petType.name = "Hamster"
        val petTypeName = petTypeFormatter.print(petType, Locale.ENGLISH)
        assertThat(petTypeName).isEqualTo("Hamster")
    }

    @Test
    @Throws(ParseException::class)
    fun shouldParse() {
        given(types.findPetTypes()).willReturn(makePetTypes())
        val petType = petTypeFormatter.parse("Bird", Locale.ENGLISH)
        assertThat(petType.name).isEqualTo("Bird")
    }

    @Test
    fun shouldThrowParseException() {
        given(types.findPetTypes()).willReturn(makePetTypes())
        Assertions.assertThrows(ParseException::class.java) {
            petTypeFormatter.parse("Fish", Locale.ENGLISH)
        }
    }

    /**
     * Helper method to produce some sample pet types just for test purpose
     */
    private fun makePetTypes(): List<PetType> {
        val petTypes: MutableList<PetType> = mutableListOf()
        petTypes.add(PetType().apply { name = "Dog" })
        petTypes.add(PetType().apply { name = "Bird" })
        return petTypes
    }
}
