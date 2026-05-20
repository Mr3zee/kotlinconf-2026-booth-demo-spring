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
import java.time.LocalDate
import org.springframework.samples.petclinic.owner.dto.OwnerView
import org.springframework.samples.petclinic.owner.dto.PetForm
import org.springframework.samples.petclinic.util.PetValidator
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/owners/{ownerId}")
internal class PetController(private val petService: PetService) {

    @ModelAttribute("types")
    fun populatePetTypes(): Collection<PetType> = petService.findPetTypes()

    @ModelAttribute("owner")
    fun findOwner(@PathVariable("ownerId") ownerId: Int): OwnerView = petService.findOwnerView(ownerId)

    @ModelAttribute("pet")
    fun findPet(
        @PathVariable("ownerId") ownerId: Int,
        @PathVariable(name = "petId", required = false) petId: Int?,
    ): PetForm = petService.findPetForm(ownerId, petId)

    @InitBinder("owner")
    fun initOwnerBinder(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id", "*.id")
    }

    @InitBinder("pet")
    fun initPetBinder(dataBinder: WebDataBinder) {
        dataBinder.setValidator(PetValidator())
        dataBinder.setDisallowedFields("id", "*.id")
    }

    @GetMapping("/pets/new")
    fun initCreationForm(@PathVariable("ownerId") ownerId: Int, model: ModelMap): String {
        model["pet"] = PetForm()
        return VIEWS_PETS_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/pets/new")
    fun processCreationForm(
        @PathVariable("ownerId") ownerId: Int,
        @Valid @ModelAttribute("pet") form: PetForm,
        result: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {
        val petName = form.name
        if (!petName.isNullOrBlank() && petService.isDuplicateName(ownerId, petName, null)) {
            result.rejectValue("name", "duplicate", "already exists")
        }

        val currentDate = LocalDate.now()
        if (form.birthDate?.isAfter(currentDate) == true) {
            result.rejectValue("birthDate", "typeMismatch.birthDate")
        }

        if (result.hasErrors()) {
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM
        }

        petService.create(ownerId, form)
        redirectAttributes.addFlashAttribute("message", "New Pet has been Added")
        return "redirect:/owners/{ownerId}"
    }

    @GetMapping("/pets/{petId}/edit")
    fun initUpdateForm(): String = VIEWS_PETS_CREATE_OR_UPDATE_FORM

    @PostMapping("/pets/{petId}/edit")
    fun processUpdateForm(
        @PathVariable("ownerId") ownerId: Int,
        @PathVariable("petId") petId: Int,
        @Valid @ModelAttribute("pet") form: PetForm,
        result: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {
        val petName = form.name
        if (!petName.isNullOrBlank() && petService.isDuplicateName(ownerId, petName, petId)) {
            result.rejectValue("name", "duplicate", "already exists")
        }

        val currentDate = LocalDate.now()
        if (form.birthDate?.isAfter(currentDate) == true) {
            result.rejectValue("birthDate", "typeMismatch.birthDate")
        }

        if (result.hasErrors()) {
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM
        }

        petService.update(ownerId, petId, form)
        redirectAttributes.addFlashAttribute("message", "Pet details has been edited")
        return "redirect:/owners/{ownerId}"
    }

    companion object {
        private const val VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm"
    }
}
