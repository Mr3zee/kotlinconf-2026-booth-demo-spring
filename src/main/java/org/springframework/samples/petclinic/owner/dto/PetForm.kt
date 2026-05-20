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

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.samples.petclinic.owner.PetType
import java.time.LocalDate

/**
 * Form-backing DTO for creating or updating a
 * [org.springframework.samples.petclinic.owner.Pet].
 *
 * Note: keeps a [PetType] reference rather than a typeId so the existing
 * `PetTypeFormatter` continues to drive the dropdown without template churn.
 */
data class PetForm(
    var id: Int? = null,

    var name: String? = null,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    var birthDate: LocalDate? = null,

    var type: PetType? = null,
) {

    val isNew: Boolean
        get() = id == null
}
