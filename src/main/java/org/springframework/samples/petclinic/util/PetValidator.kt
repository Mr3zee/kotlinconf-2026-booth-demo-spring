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
package org.springframework.samples.petclinic.util

import org.springframework.samples.petclinic.owner.dto.PetForm
import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * `Validator` for [PetForm] submissions.
 *
 * We're not using Bean Validation annotations here because it is easier to define such
 * validation rule in Java.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
class PetValidator : Validator {

    override fun validate(obj: Any, errors: Errors) {
        val form = obj as PetForm
        val name = form.name
        if (!StringUtils.hasText(name)) {
            errors.rejectValue("name", REQUIRED, REQUIRED)
        }

        if (form.id == null && form.type == null) {
            errors.rejectValue("type", REQUIRED, REQUIRED)
        }

        if (form.birthDate == null) {
            errors.rejectValue("birthDate", REQUIRED, REQUIRED)
        }
    }

    /**
     * This Validator validates *just* PetForm instances
     */
    override fun supports(clazz: Class<*>): Boolean {
        return PetForm::class.java.isAssignableFrom(clazz)
    }

    companion object {
        private const val REQUIRED = "required"
    }
}
