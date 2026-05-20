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

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Repository class for `Vet` domain objects. All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data.
 */
interface VetRepository : Repository<Vet, Int> {

    /**
     * Retrieve all `Vet`s from the data store.
     */
    @Transactional(readOnly = true)
    @Cacheable("vets")
    fun findAll(): Collection<Vet>

    /**
     * Retrieve all `Vet`s from the data store in pages.
     */
    @Transactional(readOnly = true)
    @Cacheable("vets")
    fun findAll(pageable: Pageable): Page<Vet>
}
