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
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.aot.DisabledInAotMode
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mysql.MySQLContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mysql")
@Testcontainers(disabledWithoutDocker = true)
@DisabledInNativeImage
@DisabledInAotMode
internal class MySqlIntegrationTests {

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

    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        val container: MySQLContainer = MySQLContainer(DockerImageName.parse("mysql:9.6"))
    }
}
