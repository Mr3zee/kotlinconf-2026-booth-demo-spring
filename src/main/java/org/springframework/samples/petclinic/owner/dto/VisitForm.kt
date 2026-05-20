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

import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

/**
 * Form-backing DTO for booking a new
 * [org.springframework.samples.petclinic.owner.Visit].
 */
data class VisitForm(
    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    var date: LocalDate? = LocalDate.now(),

    @field:NotBlank
    var description: String? = null,
) {

    val isNew: Boolean
        get() = true
}
