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

/**
 * Read-only view DTO for rendering an
 * [org.springframework.samples.petclinic.owner.Owner]. Always materialised from a
 * persisted entity, so every field the schema declares non-null is non-null here too.
 */
data class OwnerView(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val address: String,
    val city: String,
    val telephone: String,
    val pets: List<PetView>,
)
