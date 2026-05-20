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

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.samples.petclinic.owner.dto.OwnerView;
import org.springframework.samples.petclinic.owner.dto.PetForm;
import org.springframework.samples.petclinic.util.PetValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Wick Dynex
 */
@Controller
@RequestMapping("/owners/{ownerId}")
@RequiredArgsConstructor
class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private final PetService petService;

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return petService.findPetTypes();
	}

	@ModelAttribute("owner")
	public OwnerView findOwner(@PathVariable("ownerId") int ownerId) {
		return petService.findOwnerView(ownerId);
	}

	@ModelAttribute("pet")
	public PetForm findPet(@PathVariable("ownerId") int ownerId,
			@PathVariable(name = "petId", required = false) Integer petId) {
		return petService.findPetForm(ownerId, petId);
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
		dataBinder.setDisallowedFields("id", "*.id");
	}

	@GetMapping("/pets/new")
	public String initCreationForm(@PathVariable("ownerId") int ownerId, ModelMap model) {
		model.put("pet", new PetForm());
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(@PathVariable("ownerId") int ownerId, @Valid @ModelAttribute("pet") PetForm form,
			BindingResult result, RedirectAttributes redirectAttributes) {

		if (StringUtils.hasText(form.getName()) && petService.isDuplicateName(ownerId, form.getName(), null)) {
			result.rejectValue("name", "duplicate", "already exists");
		}

		LocalDate currentDate = LocalDate.now();
		if (form.getBirthDate() != null && form.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		petService.create(ownerId, form);
		redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm() {
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			@Valid @ModelAttribute("pet") PetForm form, BindingResult result, RedirectAttributes redirectAttributes) {

		String petName = form.getName();

		if (StringUtils.hasText(petName) && petService.isDuplicateName(ownerId, petName, petId)) {
			result.rejectValue("name", "duplicate", "already exists");
		}

		LocalDate currentDate = LocalDate.now();
		if (form.getBirthDate() != null && form.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		petService.update(ownerId, petId, form);
		redirectAttributes.addFlashAttribute("message", "Pet details has been edited");
		return "redirect:/owners/{ownerId}";
	}

}
