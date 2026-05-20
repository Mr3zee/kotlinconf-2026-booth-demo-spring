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

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.samples.petclinic.util.VetMapper
import org.springframework.samples.petclinic.vet.dto.VetView
import org.springframework.samples.petclinic.vet.dto.VetsResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VetService(
    private val vetRepository: VetRepository,
    private val vetMapper: VetMapper,
) {

    @Transactional(readOnly = true)
    fun findPaginated(page: Int): Page<VetView> {
        val pageable = PageRequest.of(page - 1, PAGE_SIZE)
        return vetRepository.findAll(pageable).map { vetMapper.toView(it) }
    }

    @Transactional(readOnly = true)
    fun findAllAsResource(): VetsResource {
        val resource = VetsResource()
        vetRepository.findAll().forEach { resource.vetList.add(vetMapper.toView(it)) }
        return resource
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
