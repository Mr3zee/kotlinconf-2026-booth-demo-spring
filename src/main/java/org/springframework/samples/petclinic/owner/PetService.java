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

import java.util.Collection;
import java.util.Objects;

import org.springframework.samples.petclinic.owner.dto.OwnerView;
import org.springframework.samples.petclinic.owner.dto.PetForm;
import org.springframework.samples.petclinic.util.OwnerMapper;
import org.springframework.samples.petclinic.util.PetMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

	private final OwnerRepository owners;

	private final PetTypeRepository petTypes;

	private final PetMapper petMapper;

	private final OwnerMapper ownerMapper;

	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() {
		return petTypes.findPetTypes();
	}

	@Transactional(readOnly = true)
	public OwnerView findOwnerView(int ownerId) {
		return ownerMapper.toView(loadOwner(ownerId));
	}

	@Transactional(readOnly = true)
	public PetForm findPetForm(int ownerId, Integer petId) {
		if (petId == null) {
			return new PetForm();
		}
		Pet existing = loadOwner(ownerId).getPet(petId);
		return existing == null ? new PetForm() : petMapper.toForm(existing);
	}

	@Transactional(readOnly = true)
	public boolean isDuplicateName(int ownerId, String name, Integer excludePetId) {
		Owner owner = loadOwner(ownerId);
		if (excludePetId == null) {
			return owner.getPet(name, true) != null;
		}
		Pet match = owner.getPet(name, false);
		return match != null && !Objects.equals(match.getId(), excludePetId);
	}

	@Transactional
	public void create(int ownerId, PetForm form) {
		Owner owner = loadOwner(ownerId);
		Pet pet = petMapper.toEntity(form);
		owner.addPet(pet);
		owners.save(owner);
	}

	@Transactional
	public void update(int ownerId, int petId, PetForm form) {
		Owner owner = loadOwner(ownerId);
		Pet existing = owner.getPet(petId);
		Assert.state(existing != null, "Pet not found on owner for id=" + petId);
		petMapper.updateEntity(existing, form);
		owners.save(owner);
	}

	private Owner loadOwner(int ownerId) {
		return owners.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException(
					"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
	}

}
