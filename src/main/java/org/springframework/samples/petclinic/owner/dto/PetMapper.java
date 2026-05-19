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
package org.springframework.samples.petclinic.owner.dto;

import java.util.List;

import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PetMapper {

	private final VisitMapper visitMapper;

	public PetForm toForm(Pet pet) {
		return new PetForm(pet.getId(), pet.getName(), pet.getBirthDate(), pet.getType());
	}

	public void updateEntity(Pet pet, PetForm form) {
		pet.setName(form.getName());
		pet.setBirthDate(form.getBirthDate());
		pet.setType(form.getType());
	}

	public Pet toEntity(PetForm form) {
		Pet pet = new Pet();
		updateEntity(pet, form);
		return pet;
	}

	public PetView toView(Pet pet) {
		List<VisitView> visits = pet.getVisits().stream().map(visitMapper::toView).toList();
		return new PetView(pet.getId(), pet.getName(), pet.getBirthDate(), pet.getType(), visits);
	}

}
