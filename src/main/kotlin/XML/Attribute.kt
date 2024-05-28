package XML

/**
 * Define an attribute.
 *
 * The attribute can be used in both a Tag and a Document.
 *
 * @see Document
 * @see Tag
 *
 * @sample [attribSample]
 *
 * @param name attribute name
 * @param value attribute value, which can be a string, a number or a date
 *
 * @constructor Creates a new attribute in the dependence of a Document or a Tag.
 *
 * @property getValueAsString returns the attribute value as a String
 * @property displayValue returns the attribute value as a String with included delimiters
 * @property changeAttributeName changes the attribute name
 * @property changeAttributeValue changes the attribute value
 * @property prettyPrintXML returns a string with the attribute name and value in XML format
 *
 * @author Joaquim Henriques Carvalho (N. 97044)
 * @since 5 May 2024
 */
internal data class Attribute(
    var name: String,
    var value: Any?
) {

    /**
     * Returns the attribute value as a String
     */
    val getValueAsString: String get() =
        when(value) {
            is String -> value as String
            is Boolean -> value.toString().uppercase()
            else -> value.toString()
        }

    /**
     * Returns the attribute value as a String with included delimiters
     */
    val displayValue: String get() =
        when(value) {
            is String -> "\"" + value + "\""
            is Int -> value.toString()
            is Double -> value.toString()
            is Float -> value.toString()
            is Boolean -> value.toString().uppercase()
            else -> "\"" + value.toString() + "\""
        }

    /**
     * Returns a string with the attribute name and value in XML format
     */
    val prettyPrintXML get() = """${this.name}=${this.displayValue}"""

    /**
     * Changes the attribute name
     */
    fun changeAttributeName(newName: String) { this.name = newName }

    /**
     * Changes the attribute value
     */
    fun changeAttributeValue(newValue: Any) { this.value = newValue }

}
