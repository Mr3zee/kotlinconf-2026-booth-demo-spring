package org.springframework.samples.petclinic.system

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import java.util.TreeSet
import java.util.regex.Pattern

/**
 * This test ensures that there are no hard-coded strings without internationalization in
 * any HTML files. Also ensures that a string is translated in every language to avoid
 * partial translations.
 *
 * @author Anuj Ashok Potdar
 */
class I18nPropertiesSyncTest {

    @Test
    fun checkNonInternationalizedStrings() {
        val root = Path.of("src/main")
        val files: List<Path> = Files.walk(root).use { stream ->
            stream.filter { p -> p.toString().endsWith(".java") || p.toString().endsWith(".html") }
                .filter { p -> !p.toString().contains("/test/") }
                .filter { p -> !p.fileName.toString().endsWith("Test.java") }
                .toList()
        }

        val report = StringBuilder()

        for (file in files) {
            val lines = Files.readAllLines(file)
            for (i in lines.indices) {
                val line = lines[i].trim()

                if (line.startsWith("//") || line.startsWith("@") || line.contains("log.")
                    || line.contains("System.out")
                ) {
                    continue
                }

                if (file.toString().endsWith(".html")) {
                    val hasLiteralText = HTML_TEXT_LITERAL.matcher(line).find()
                    val hasThTextAttribute = HAS_TH_TEXT_ATTRIBUTE.matcher(line).find()
                    val isBracketOnly = BRACKET_ONLY.matcher(line).find()

                    if (hasLiteralText && !line.contains("#{") && !hasThTextAttribute && !isBracketOnly) {
                        report.append("HTML: ")
                            .append(file)
                            .append(" Line ")
                            .append(i + 1)
                            .append(": ")
                            .append(line)
                            .append("\n")
                    }
                }
            }
        }

        if (report.isNotEmpty()) {
            fail<Unit>("Hardcoded (non-internationalized) strings found:\n$report")
        }
    }

    @Test
    fun checkI18nPropertyFilesAreInSync() {
        val propertyFiles: List<Path> = Files.walk(Path.of(I18N_DIR)).use { stream ->
            stream.filter { p -> p.fileName.toString().startsWith(BASE_NAME) }
                .filter { p -> p.fileName.toString().endsWith(PROPERTIES) }
                .toList()
        }

        val localeToProps: MutableMap<String, Properties> = HashMap()

        for (path in propertyFiles) {
            val props = Properties()
            Files.newBufferedReader(path).use { reader ->
                props.load(reader)
                localeToProps[path.fileName.toString()] = props
            }
        }

        val baseFile = BASE_NAME + PROPERTIES
        val baseProps = localeToProps[baseFile]
        if (baseProps == null) {
            fail<Unit>("Base properties file '$baseFile' not found.")
            return
        }

        val baseKeys = baseProps.stringPropertyNames()
        val report = StringBuilder()

        for ((fileName, props) in localeToProps) {
            // We use fallback logic to include english strings, hence messages_en is not
            // populated.
            if (fileName == baseFile || "messages_en.properties" == fileName) {
                continue
            }

            val missingKeys: MutableSet<String> = TreeSet(baseKeys)
            missingKeys.removeAll(props.stringPropertyNames())

            if (missingKeys.isNotEmpty()) {
                report.append("Missing keys in ").append(fileName).append(":\n")
                missingKeys.forEach { k -> report.append("  ").append(k).append("\n") }
            }
        }

        if (report.isNotEmpty()) {
            fail<Unit>("Translation files are not in sync:\n$report")
        }
    }

    companion object {
        private const val I18N_DIR = "src/main/resources"
        private const val BASE_NAME = "messages"
        const val PROPERTIES = ".properties"

        private val HTML_TEXT_LITERAL: Pattern = Pattern.compile(">([^<>{}]+)<")
        private val BRACKET_ONLY: Pattern = Pattern.compile("<[^>]*>\\s*[\\[\\]](?:&nbsp;)?\\s*</[^>]*>")
        private val HAS_TH_TEXT_ATTRIBUTE: Pattern = Pattern.compile("th:(u)?text\\s*=\\s*\"[^\"]+\"")
    }
}
