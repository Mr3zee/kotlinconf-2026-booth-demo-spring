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
package org.springframework.samples.petclinic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.samples.petclinic.vet.VetRepository

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = ["logging.level.sql=DEBUG"])
class PetClinicIntegrationTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var vets: VetRepository

    @Autowired
    private lateinit var builder: RestTemplateBuilder

    @Test
    fun findAll() {
        vets.findAll()
        vets.findAll() // served from cache
    }

    @Test
    fun ownerDetails() {
        val template = builder.rootUri("http://localhost:$port").build()
        val result = template.exchange(RequestEntity.get("/owners/1").build(), String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun ownerList() {
        val template = builder.rootUri("http://localhost:$port").build()
        val result = template.exchange(RequestEntity.get("/owners?lastName=").build(), String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(PetClinicApplication::class.java, "--spring.docker.compose.lifecycle-management=NONE")
        }
    }
}
