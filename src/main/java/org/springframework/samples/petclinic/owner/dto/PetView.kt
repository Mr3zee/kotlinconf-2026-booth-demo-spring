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
package org.springframework.samples.petclinic.owner.dto

import org.springframework.samples.petclinic.owner.PetType
import java.time.LocalDate

/**
 * Read-only view DTO for rendering a
 * [org.springframework.samples.petclinic.owner.Pet]. `toString()` returns
 * only the name so Thymeleaf's `#strings.listJoin(pets, ', ')` keeps producing the
 * same comma-separated names as before.
 *
 * `birthDate` and `type` remain nullable because the schema allows them to be null
 * (a pet can be created without a birth date, and `type` is briefly unset during
 * form binding before the formatter resolves it).
 */
data class PetView(
    val id: Int,
    val name: String,
    val birthDate: LocalDate?,
    val type: PetType?,
    val visits: List<VisitView>,
) {

    override fun toString(): String = name
}
