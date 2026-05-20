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

import org.springframework.samples.petclinic.owner.dto.OwnerView;
import org.springframework.samples.petclinic.owner.dto.PetView;
import org.springframework.samples.petclinic.owner.dto.VisitForm;
import org.springframework.samples.petclinic.util.OwnerMapper;
import org.springframework.samples.petclinic.util.PetMapper;
import org.springframework.samples.petclinic.util.VisitMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitService {

	private final OwnerRepository owners;

	private final OwnerMapper ownerMapper;

	private final PetMapper petMapper;

	private final VisitMapper visitMapper;

	@Transactional(readOnly = true)
	public OwnerView findOwnerView(int ownerId) {
		return ownerMapper.toView(loadOwner(ownerId));
	}

	@Transactional(readOnly = true)
	public PetView findPetView(int ownerId, int petId) {
		Pet pet = loadOwner(ownerId).getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		return petMapper.toView(pet);
	}

	@Transactional
	public void create(int ownerId, int petId, VisitForm form) {
		Owner owner = loadOwner(ownerId);
		if (owner.getPet(petId) == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		Visit visit = visitMapper.toEntity(form);
		owner.addVisit(petId, visit);
		owners.save(owner);
	}

	private Owner loadOwner(int ownerId) {
		return owners.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException(
					"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
	}

}
