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

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.samples.petclinic.owner.dto.OwnerForm;
import org.springframework.samples.petclinic.owner.dto.OwnerView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Wick Dynex
 */
@Controller
@RequiredArgsConstructor
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerService ownerService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}

	@ModelAttribute("owner")
	public OwnerForm findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new OwnerForm() : ownerService.findFormById(ownerId);
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid @ModelAttribute("owner") OwnerForm form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		Integer newOwnerId = ownerService.create(form);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		return "redirect:/owners/" + newOwnerId;
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, @ModelAttribute("owner") OwnerForm form,
			BindingResult result, Model model) {
		String lastName = form.getLastName();
		if (lastName == null) {
			lastName = ""; // empty string signifies broadest possible search
		}

		Page<OwnerView> ownersResults = ownerService.findByLastName(lastName, page);
		if (ownersResults.isEmpty()) {
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			OwnerView only = ownersResults.iterator().next();
			return "redirect:/owners/" + only.getId();
		}

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", ownersResults.getTotalPages());
		model.addAttribute("totalItems", ownersResults.getTotalElements());
		model.addAttribute("listOwners", ownersResults.getContent());
		return "owners/ownersList";
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid @ModelAttribute("owner") OwnerForm form, BindingResult result,
			@PathVariable("ownerId") int ownerId, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (!Objects.equals(form.getId(), ownerId)) {
			result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}

		ownerService.update(ownerId, form);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelAndView with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		mav.addObject("owner", ownerService.findById(ownerId));
		return mav;
	}

	private final VisitInsightsService visitInsightsService;

	@GetMapping("/owners/{ownerId}/insights")
	@ResponseBody
	public OwnerVisitSummary ownerInsights(@PathVariable("ownerId") int ownerId) {
		return this.visitInsightsService.recentVisitsSummary(ownerId);
	}

	@PostMapping("/owners/{ownerId}/visit-notes/preview")
	@ResponseBody
	public VisitNotePreview previewVisitNote(@PathVariable("ownerId") int ownerId, @RequestBody VisitNoteRequest request) {
		return this.visitInsightsService.previewVisitNote(ownerId, request);
	}

}
