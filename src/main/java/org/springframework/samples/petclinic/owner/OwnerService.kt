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

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.samples.petclinic.owner.dto.OwnerForm
import org.springframework.samples.petclinic.owner.dto.OwnerView
import org.springframework.samples.petclinic.util.OwnerMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerService(
    private val owners: OwnerRepository,
    private val ownerMapper: OwnerMapper,
) {

    @Transactional(readOnly = true)
    fun findById(ownerId: Int): OwnerView = ownerMapper.toView(loadOwner(ownerId))

    @Transactional(readOnly = true)
    fun findFormById(ownerId: Int): OwnerForm = ownerMapper.toForm(loadOwner(ownerId))

    @Transactional(readOnly = true)
    fun findByLastName(lastName: String, page: Int): Page<OwnerView> {
        val pageable = PageRequest.of(page - 1, PAGE_SIZE)
        return owners.findByLastNameStartingWith(lastName, pageable).map { ownerMapper.toView(it) }
    }

    @Transactional
    fun create(form: OwnerForm): Int? {
        val entity = ownerMapper.toEntity(form)
        owners.save(entity)
        return entity.id
    }

    @Transactional
    fun update(ownerId: Int, form: OwnerForm) {
        val existing = loadOwner(ownerId)
        ownerMapper.updateEntity(existing, form)
        owners.save(existing)
    }

    private fun loadOwner(ownerId: Int): Owner =
        owners.findById(ownerId).orElseThrow {
            IllegalArgumentException(
                "Owner not found with id: $ownerId. " +
                    "Please ensure the ID is correct and the owner exists in the database."
            )
        }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
