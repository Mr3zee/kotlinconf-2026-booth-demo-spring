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
import org.springframework.samples.petclinic.owner.dto.VisitForm
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
internal class VisitController(private val visitService: VisitService) {

    @InitBinder
    fun setAllowedFields(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id", "*.id")
    }

    /**
     * Called before each and every `@RequestMapping` annotated method. Two goals:
     * make sure we always have fresh data, and since we do not use the session
     * scope, make sure that the `Pet` object always has an id (even though id is
     * not part of the form fields).
     */
    @ModelAttribute("visit")
    fun loadPetWithVisit(
        @PathVariable("ownerId") ownerId: Int,
        @PathVariable("petId") petId: Int,
        model: MutableMap<String, Any>,
    ): VisitForm {
        model["owner"] = visitService.findOwnerView(ownerId)
        model["pet"] = visitService.findPetView(ownerId, petId)
        return VisitForm()
    }

    // Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called.
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    fun initNewVisitForm(): String = "pets/createOrUpdateVisitForm"

    // Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called.
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    fun processNewVisitForm(
        @PathVariable("ownerId") ownerId: Int,
        @PathVariable("petId") petId: Int,
        @Valid @ModelAttribute("visit") form: VisitForm,
        result: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (result.hasErrors()) {
            return "pets/createOrUpdateVisitForm"
        }

        visitService.create(ownerId, petId, form)
        redirectAttributes.addFlashAttribute("message", "Your visit has been booked")
        return "redirect:/owners/{ownerId}"
    }
}
