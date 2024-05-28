package XML

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class XmlAdapter(val adapterClass: KClass<*>)
//@Target(AnnotationTarget.PROPERTY)
//annotation class XmlUpdater(val value: String)

@Target(AnnotationTarget.PROPERTY)
annotation class XmlAttribute
@Target(AnnotationTarget.PROPERTY)
annotation class XmlExclude


internal const val errorTagName = "The Tag name can only contain letters, numbers, hyphens, underscores and periods"
internal const val errorTagType = "Invalid parameter. Must be Tag"
internal const val errorAttributeName = "The Attribute name can only contain letters, numbers, hyphens, underscores and periods"
internal const val errorTagValue = "A Tag cannot simultaneously have value and other Tags"

/*
Element names must start with a letter or underscore.
Element names cannot start with the letters xml (or XML, or Xml, etc.)
Element names can contain letters, digits, hyphens, underscores, and periods.
Element names cannot contain spaces.
*/
internal fun checkEntityName(name: String): Boolean {
    // array of valid characters
    val valid = charArrayOf('_', '-', '.')

    if ( name.uppercase().startsWith("XML") ) return false
    if ( !(name.first().isLetter() || name.first()=='_') ) return false

    for (char in name.uppercase().iterator())
        if ( !(char.isLetterOrDigit() || char in valid) && char.isWhitespace() ) return false

    return true
}
