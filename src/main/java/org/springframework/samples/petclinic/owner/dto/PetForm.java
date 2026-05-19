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

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.owner.PetType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form-backing DTO for creating or updating a
 * {@link org.springframework.samples.petclinic.owner.Pet}.
 * <p>
 * Note: keeps a {@link PetType} reference rather than a typeId so the existing
 * {@code PetTypeFormatter} continues to drive the dropdown without template churn.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetForm {

	private Integer id;

	private String name;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	private PetType type;

	public boolean isNew() {
		return this.id == null;
	}

}
