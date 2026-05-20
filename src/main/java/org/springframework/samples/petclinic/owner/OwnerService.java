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
package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.dto.OwnerForm;
import org.springframework.samples.petclinic.owner.dto.OwnerView;
import org.springframework.samples.petclinic.util.OwnerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

	private static final int PAGE_SIZE = 5;

	private final OwnerRepository owners;

	private final OwnerMapper ownerMapper;

	@Transactional(readOnly = true)
	public OwnerView findById(int ownerId) {
		return ownerMapper.toView(loadOwner(ownerId));
	}

	@Transactional(readOnly = true)
	public OwnerForm findFormById(int ownerId) {
		return ownerMapper.toForm(loadOwner(ownerId));
	}

	@Transactional(readOnly = true)
	public Page<OwnerView> findByLastName(String lastName, int page) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		return owners.findByLastNameStartingWith(lastName, pageable).map(ownerMapper::toView);
	}

	@Transactional
	public Integer create(OwnerForm form) {
		Owner entity = ownerMapper.toEntity(form);
		owners.save(entity);
		return entity.getId();
	}

	@Transactional
	public void update(int ownerId, OwnerForm form) {
		Owner existing = loadOwner(ownerId);
		ownerMapper.updateEntity(existing, form);
		owners.save(existing);
	}

	private Owner loadOwner(int ownerId) {
		return owners.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId
					+ ". Please ensure the ID is correct and the owner exists in the database."));
	}

}
