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
package org.springframework.samples.petclinic.util;

import java.util.List;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.dto.OwnerForm;
import org.springframework.samples.petclinic.owner.dto.OwnerView;
import org.springframework.samples.petclinic.owner.dto.PetView;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerMapper {

	private final PetMapper petMapper;

	public OwnerForm toForm(Owner owner) {
		return new OwnerForm(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getAddress(),
				owner.getCity(), owner.getTelephone());
	}

	public void updateEntity(Owner owner, OwnerForm form) {
		owner.setFirstName(form.getFirstName());
		owner.setLastName(form.getLastName());
		owner.setAddress(form.getAddress());
		owner.setCity(form.getCity());
		owner.setTelephone(form.getTelephone());
	}

	public Owner toEntity(OwnerForm form) {
		Owner owner = new Owner();
		updateEntity(owner, form);
		return owner;
	}

	public OwnerView toView(Owner owner) {
		List<PetView> pets = owner.getPets().stream().map(petMapper::toView).toList();
		return new OwnerView(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getAddress(),
				owner.getCity(), owner.getTelephone(), pets);
	}

}
