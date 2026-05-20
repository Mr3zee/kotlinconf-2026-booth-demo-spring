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
package org.springframework.samples.petclinic.model

import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.util.Locale

/**
 * @author Michael Isvy Simple test to make sure that Bean Validation is working (useful
 * when upgrading to a new version of Hibernate Validator/ Bean Validation)
 */
internal class ValidatorTests {

    private fun createValidator(): Validator {
        val localValidatorFactoryBean = LocalValidatorFactoryBean()
        localValidatorFactoryBean.afterPropertiesSet()
        return localValidatorFactoryBean
    }

    @Test
    fun shouldNotValidateWhenFirstNameEmpty() {
        LocaleContextHolder.setLocale(Locale.ENGLISH)
        val person = Person()
        person.firstName = ""
        person.lastName = "smith"

        val validator = createValidator()
        val constraintViolations = validator.validate(person)

        assertThat(constraintViolations).hasSize(1)
        val violation = constraintViolations.iterator().next()
        assertThat(violation.propertyPath).hasToString("firstName")
        assertThat(violation.message).isEqualTo("must not be blank")
    }
}
