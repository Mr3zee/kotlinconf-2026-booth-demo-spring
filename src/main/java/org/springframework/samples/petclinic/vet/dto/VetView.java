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
package org.springframework.samples.petclinic.vet.dto;

import java.util.List;

import org.springframework.samples.petclinic.vet.Specialty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-only view DTO for rendering a
 * {@link org.springframework.samples.petclinic.vet.Vet}. Keeps {@link Specialty}
 * references so the existing template can render {@code ${specialty.name}} unchanged.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VetView {

	private Integer id;

	private String firstName;

	private String lastName;

	private List<Specialty> specialties;

	public int getNrOfSpecialties() {
		return specialties == null ? 0 : specialties.size();
	}

}
