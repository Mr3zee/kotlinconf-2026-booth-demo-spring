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

import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository class for `Owner` domain objects. All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data.
 */
interface OwnerRepository : JpaRepository<Owner, Int> {

    /**
     * Retrieve [Owner]s from the data store by last name, returning all owners
     * whose last name *starts* with the given name.
     */
    fun findByLastNameStartingWith(lastName: String, pageable: Pageable): Page<Owner>

    /**
     * Retrieve an [Owner] from the data store by id.
     */
    override fun findById(id: Int): Optional<Owner>
}
