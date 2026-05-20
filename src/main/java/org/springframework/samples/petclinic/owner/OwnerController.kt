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

import jakarta.validation.Valid
import org.springframework.samples.petclinic.owner.dto.OwnerForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
internal class OwnerController(private val ownerService: OwnerService) {

    @InitBinder
    fun setAllowedFields(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id", "*.id")
    }

    @ModelAttribute("owner")
    fun findOwner(@PathVariable(name = "ownerId", required = false) ownerId: Int?): OwnerForm =
        if (ownerId == null) OwnerForm() else ownerService.findFormById(ownerId)

    @GetMapping("/owners/new")
    fun initCreationForm(): String = VIEWS_OWNER_CREATE_OR_UPDATE_FORM

    @PostMapping("/owners/new")
    fun processCreationForm(
        @Valid @ModelAttribute("owner") form: OwnerForm,
        result: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.")
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
        }

        val newOwnerId = ownerService.create(form)
        redirectAttributes.addFlashAttribute("message", "New Owner Created")
        return "redirect:/owners/$newOwnerId"
    }

    @GetMapping("/owners/find")
    fun initFindForm(): String = "owners/findOwners"

    @GetMapping("/owners")
    fun processFindForm(
        @RequestParam(defaultValue = "1") page: Int,
        @ModelAttribute("owner") form: OwnerForm,
        result: BindingResult,
        model: Model,
    ): String {
        // empty string signifies broadest possible search
        val lastName = form.lastName ?: ""

        val ownersResults = ownerService.findByLastName(lastName, page)
        if (ownersResults.isEmpty) {
            result.rejectValue("lastName", "notFound", "not found")
            return "owners/findOwners"
        }

        if (ownersResults.totalElements == 1L) {
            val only = ownersResults.iterator().next()
            return "redirect:/owners/${only.id}"
        }

        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", ownersResults.totalPages)
        model.addAttribute("totalItems", ownersResults.totalElements)
        model.addAttribute("listOwners", ownersResults.content)
        return "owners/ownersList"
    }

    @GetMapping("/owners/{ownerId}/edit")
    fun initUpdateOwnerForm(): String = VIEWS_OWNER_CREATE_OR_UPDATE_FORM

    @PostMapping("/owners/{ownerId}/edit")
    fun processUpdateOwnerForm(
        @Valid @ModelAttribute("owner") form: OwnerForm,
        result: BindingResult,
        @PathVariable("ownerId") ownerId: Int,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.")
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
        }

        if (form.id != ownerId) {
            result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.")
            redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.")
            return "redirect:/owners/{ownerId}/edit"
        }

        ownerService.update(ownerId, form)
        redirectAttributes.addFlashAttribute("message", "Owner Values Updated")
        return "redirect:/owners/{ownerId}"
    }

    /**
     * Custom handler for displaying an owner.
     */
    @GetMapping("/owners/{ownerId}")
    fun showOwner(@PathVariable("ownerId") ownerId: Int): ModelAndView {
        val mav = ModelAndView("owners/ownerDetails")
        mav.addObject("owner", ownerService.findById(ownerId))
        return mav
    }

    companion object {
        private const val VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm"
    }
}
