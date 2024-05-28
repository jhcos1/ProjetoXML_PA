package XML

import XML.Document
import com.sun.jdi.IntegerValue
import javax.print.Doc
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.javaType
import kotlin.reflect.KClass as KotlinReflectKClass
import kotlin.reflect.javaType
import kotlin.reflect.jvm.reflect
import kotlin.system.exitProcess


/**
 * Manages the CLASS Tag and its attributes.
 * To create a Document with main class, please use Document class
 *
 * @see Document
 * @see Attribute
 *
 * @sample [tagSample]
 * @sample [tagDSLSample]
 *
 * @constructor Creates a new Tag
 *              If current Tag has any value, it cannot contain another Tags
 *
 * @param parent the parent of the Tag. It will be Document if the Tag is the root Tag
 * @param name name for the Tag
 * @param value Optional. The value for the Tag, if it has one.
 *
 * @property children The children container
 * @property size Number of Tags in the children container
 * @property fullSize Number off all Tags in the container, including the children Tags
 * @property depth The level the Tag is at, taking into account that the main Tag is at level 1
 * @property maxDepth The maximal depth for the Tag
 * @property path The full path from the Root Tag
 * @property hasChildren Returns true if current Tag has children Tags, otherwise false
 * @property hasAttributes Returns true if current Tag has attributes, otherwise false
 *
 * @property prettyPrintXML Generates the XML formatted string
 * @property addTag Add a Tag to the children Tag container
 * @property changeTagName Changes the Tag's name
 * @property changeAllTagsNamed Changes the name of all Tags that have the given name
 * @property delete Deletes itself. The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated
 * @property deleteTag Deletes a Tag in the child container. The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated
 * @property deleteAllTagsNamed Deletes all the Tags with the given name. The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated
 *
 * @property [addAttribute] Adds an attribute to the Tag only if it doesn't exist
 * @property [addAttributeOnAllTagsNamed] Adds an attribute on all Tag's that have the given name
 * @property [getAllAttributes] Returns the List with the name of all attributes
 * @property [getAttributeValue] Return the value of an attribute
 * @property [changeAttributeValue] Changes the attribute value
 * @property [changeAttributeName] Changes the attribute name
 * @property [changeAllAttributesNamed] Changes the name of all attributes that have the given name
 * @property [deleteAttribute] Deletes an attribute by name, if it exists
 * @property [deleteAttributeOnAllTagsNamed] Deletes an attribute by is name on all Tag's that have the given name
 *
*/



data class Tag private constructor(
    val parent: Any,
    var name: String,
    var value: Any? = null
) {

    internal constructor(parent: Document, name: String) : this( parent, name, null) {
        if (!checkEntityName(name)) throw RuntimeException(errorTagName)
    }

    init {
        if (checkEntityName(name))
            when(parent) {
                is Tag -> parent.children.add(this)
                is Document -> parent
                else -> throw RuntimeException(errorTagType)
            }
        else throw RuntimeException(errorTagName)
    }

    private val attributes: MutableList<Attribute> = mutableListOf()

    /**
     * The children container
     */
    val children: MutableList<Tag> = mutableListOf()

    /**
     * Number of Tags in the children container
     */
    val size get(): Int = children.size

    /**
     * Number off all Tags in the container, including the children Tags
     */
    fun fullSize(): Int {
        var c = 1
        if(this.hasChildren)
            children.forEach() {
                c += it.fullSize()
            }

        return c
    }

    /**
     * The level the Tag is at, taking into account that the main Tag is at level 1
     */
    val depth: Int get() =
        when(parent) {
            is Document -> 1
            is Tag -> 1 + parent.depth
            else -> 0
        }

    /**
     * Maximal depth for the Tag
     */
    fun maxDepth(max:Int = 1): Int {
        var m=max
        if(this.hasChildren)
            children.forEach() {
                val d = it.maxDepth(max)
                if(d>m) m=d
            }
        else {
            if(this.depth>m) m=this.depth
        }

        return m
    }

    /**
     * The full path from the Root Tag
     *
     */
    val path: String get() =
        when(parent) {
            is Document -> "/" + this.name
            is Tag -> parent.path + "/" + this.name
            else -> "ERRO!!!"
        }

    /**
     * Returns true if current Tag has children Tags, otherwise false
     */
    val hasChildren: Boolean get() =
        if(this.size>0) true
        else false

    /**
     * Returns true if current Tag has attributes, otherwise false
     */
    val hasAttributes: Boolean get() =
        if(this.attributes.size>0) true
        else false



    fun accept(visitor: (Tag) -> Boolean) {
        if (visitor(this))
            children.forEach {
                it.accept(visitor)
            }
    }

    // obtem lista de atributos pela ordem do construtor primario
    private val KotlinReflectKClass<*>.classFields: List<KProperty<*>>
        get() {
            //require(isData) { "instance must be data class" }
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }



    fun translate( x: Any ): Tag {

        when (x) {
            is List<*> -> {
                x.forEach() { this.translate(it!!) }
                return this
            }
        }

        val clazz: KotlinReflectKClass<*> = x::class
        val hasAdapter = clazz.hasAnnotation<XmlAdapter>()
        val doc = this.addTag( clazz.simpleName!! )

        if(hasAdapter) {
            val f: Collection<KFunction<*>> = clazz.declaredMemberFunctions
        }

        clazz.classFields.forEach {
            if(!it.hasAnnotation<XmlExclude>())
                if(it.returnType.classifier == List::class) {
                    doc.addTag(it.name).translate(it.call(x) as List<*>)
                }
                else {
                    if(it.hasAnnotation<XmlAttribute>())
                        doc.addAttribute(it.name, it.call(x)!!)
                    else
                        doc.addTag(it.name, it.call(x)!!)
                }
        }

        return doc
    }



/**
 * Generates the XML formatted string
 *
 * @return [String] Returns a String with the content formatted in XML format
 */
fun prettyPrintXML(): String {

    var xmlString = ""
    var indentation = ""

    fun printAttributes(): String {
        var str = ""
        this.attributes.forEach {
                str += " " + it.prettyPrintXML
        }
        return str
    }

    for(i in 2..this.depth) indentation += "\t"

    if (this.value == null) {
        if (this.hasChildren) {
            xmlString = indentation + "<${this.name}${printAttributes()}>\n"
            children.forEach {
                xmlString += it.prettyPrintXML()
            }
            xmlString += indentation + "</${this.name}>\n"
        } else
            if (this.hasAttributes)
                xmlString = indentation + "<${this.name}${printAttributes()} />\n"
            else
                xmlString = indentation + "<${this.name}></${this.name}>\n"
    } else
        xmlString = indentation + "<${this.name}${printAttributes()}>${this.value}</${this.name}>\n"

    return xmlString
}

/**
 * Add a Tag to the children Tag container
 *
 * @param name String with the new Tag name
 * @param value value to put in container. If Tag has value, it cannot contain another Tags
 *
 * @return [Tag] The created Tag
 * Throws an exception if the new specified name is invalid
 * Throws an exception if the parent Tag has value
 */
fun addTag(name: String, value: Any? = null ): Tag {

    // Uma Tag só pode ter filhos se não tiver valor
    if (this.value != null) throw RuntimeException(errorTagValue)

    return Tag(this, name, value)
}

/**
 *  Facilitates the nested addition of Tags, allows code simplification and makes it easier to read
 */
fun addTag( name: String, value: Any? = null, build: Tag.() -> Unit) =
    if (value==null)
        Tag( this, name).apply {
            build(this) }
    else Tag( this, name, value )

/**
 *  Facilitates the nested addition of Tags, allows code simplification and makes it easier to read
 */
fun addTag( build: Tag.() -> Unit ) =
    if (value==null)
        this.apply {
            build(this) }
    else Tag( this, name, value )


/**
 * Changes the Tag's name
 *
 * @param newName String with the new Tag name
 *
 * @return [Boolean] true if name was changed, otherwise false
 * Throws an exception if the new specified name is invalid
 */
fun changeTagName(newName: String): Boolean {
    if (checkEntityName(newName)) {
        this.name = newName
        return true
    }
    return false
}

/**
 * Changes the name of all Tags that have the given name
 *
 * @param oldName String with the name of the Tags to be changed
 * @param newName String with the new Tag name
 *
 * @return [Int] Number of changes made
 * Throws an exception if the new specified name is invalid
 */
internal fun changeAllTagsNamed(oldName: String, newName: String): Int {
    var countSubstitutions = 0

    if (checkEntityName(newName))
        children.forEach {
            if (it.name==oldName) {
                it.changeTagName(newName)
                countSubstitutions += 1
            }

            if (it.hasChildren)
                countSubstitutions += it.changeAllTagsNamed(oldName, newName)
        }
    else throw RuntimeException(errorAttributeName)

    return countSubstitutions
}

/**
 * Deletes itself
 * The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated
 *
 * @param force Must be given if intended to force the deleted
 *
 * @return [Boolean] true if the Tag was deleted, otherwise false
 */
fun delete( force: Boolean = false ): Boolean {

    // Don't remove if Tag is the root Tag, because in that case parent is the Document
    if(this.parent is Tag)
        if (force || !this.hasChildren) return this.parent.deleteTag(this, force)

    return false
}

/**
 * Deletes a Tag in the child container
 * The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated
 *
 * @param force Must be given if intended to force the deleted
 *
 * @return [Boolean] true if the Tag was deleted, otherwise false
 */
fun deleteTag( child: Tag, force: Boolean = false ): Boolean {

    // Remove only if Tag is empty or if forced is activated
    if( (!child.hasChildren) || force )
        if(children.remove(child))
            return true

    return false
}

/**
 * Deletes all the Tags with the given name
 * The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated
 *
 * @param force Must be given if intended to force the deleted
 *
 * @return [Int] the number of deleted Tags
 */
internal fun deleteAllTagsNamed(tagName: String, force: Boolean = false, drops: Int = 0): Int {
    var countDrops = drops
    val toDrop: MutableList<Tag> = mutableListOf()

    this.children.forEach {
        // Speed delete if forced
        if(force && it.name==tagName) {
            toDrop.add(it)
        } else {
            // If has children, enter to verify if children has Tags to remove
            if (it.hasChildren)
                countDrops = it.deleteAllTagsNamed(tagName, force, countDrops)

            // After cleaning, verify if Tag has condition to be removed
            if(it.name==tagName && !it.hasChildren) toDrop.add(it)
        }
    }

    // In the end, remove all Tags marked to be removed
    if(toDrop.size>0)
        toDrop.forEach {
            if(children.remove(it))
                countDrops += 1
        }

    return countDrops
}

/** Searches into the Tag for the given pattern
 *
 * @return [List] Returns a List of matched Tags
 */
internal fun search(pattern: String): List<Tag> {
    val searchResults: MutableList<Tag> = mutableListOf()
    if(path.endsWith(pattern)) searchResults.addLast(this)
    if(this.hasChildren)
        this.children.forEach {
            it.search(pattern).forEach { searchResults.addLast(it) }
        }

    return searchResults.toList()
}


/******************************************************************************************/
/*                                                                                        */
/*                                                                                        */
/*                                                                                        */
/*                                     ATTRIBUTES                                         */
/*                                                                                        */
/*                                                                                        */
/*                                                                                        */
/******************************************************************************************/


/**
 * Adds an attribute to the Tag only if it doesn't exist
 *
 * @param name String with the name of the attribute to be added
 * @param value value for the attribute, which can be a string, a number or a date
 *
 * @return [Boolean] true if attribute was added, otherwise false
 * Throws an exception if the new specified name is invalid
 */
fun addAttribute(name: String, value: Any): Boolean {
    if( this.getAllAttributes().contains(name) )
        return false
    else
        if (checkEntityName(name)) {
            attributes.add(Attribute(name, value))
            return true
        }
        else throw RuntimeException(errorAttributeName)
}

/**
 * Adds an attribute on all Tag's that have the given name
 *
 * @param tagName String with the name of the Tags to be changed
 * @param attributeName String with the name of the attribute to be added
 * @param value value for the attribute, which can be a string, a number or a date
 *
 * @return [Int] Number of added attributes
 */
internal fun addAttributeOnAllTagsNamed(tagName:String, attributeName: String, value: Any): Int {
    var countAdds = 0

    children.forEach {
        if (it.name==tagName) {
            if(it.addAttribute(attributeName, value))
                countAdds += 1
        }

        if (it.hasChildren)
            countAdds += it.addAttributeOnAllTagsNamed(tagName, attributeName, value)
    }

    return countAdds
}

/**
 * Deletes an attribute by is name on all Tag's that have the given name
 *
 * @param tagName String with the name of the Tags to be changed
 * @param attributeName String with the name of the attribute to be deleted
 *
 * @return [Int] Number of deleted attributes
 */
internal fun deleteAttributeOnAllTagsNamed(tagName:String, attributeName: String): Int {
    var countDrops = 0

    children.forEach {
        if (it.name==tagName) {
            if(it.deleteAttribute(attributeName))
                countDrops += 1
        }

        if (it.hasChildren)
            countDrops += it.deleteAttributeOnAllTagsNamed(tagName, attributeName)
    }

    return countDrops
}

/**
 * Returns the List with the name of all attributes
 *
 * @return [List] List of Strings with the names off all attributes
 */
fun getAllAttributes(): List<String> {
    val atribs: MutableList<String> = mutableListOf()
    attributes.forEach {
            atribs.add(it.name)
    }
    return atribs
}

/**
 * Return the value of an attribute
 *
 * @param name Name of the attribute whose value is to be obtained
 *
 * @return String with the attribute value. Null if attribute doesn't exist
 */
fun getAttributeValue(name: String): String? {
    attributes.forEach {
        if (it.name == name)
            return it.getValueAsString
    }
    return null
}

/**
 * Changes the attribute value
 *
 * @param name String with the name of the attribute to be changed
 * @param newValue New value for the attribute
 *
 * @return [Boolean] true if the attribute was changes, otherwise false
 */
fun changeAttributeValue(name: String, newValue: Any): Boolean {
    this.attributes.forEach {
        if (it.name == name) {
            it.changeAttributeValue( newValue )
            return true
        }
    }

    return false
}

/**
 * Changes the attribute name
 *
 * @param name String with the name of the attribute to be changed
 * @param new  String with the new attribute name
 *
 * @return [Boolean] true if the attribute was changes, otherwise false
 * Throws an exception if the new specified name is invalid
 */
fun changeAttributeName(name: String, new: String): Boolean {
    if (checkEntityName(new))
        this.attributes.forEach {
            if (it.name == name) {
                it.changeAttributeName( new )
                return true
            }
        }
    else throw RuntimeException(errorAttributeName)

    return false
}

/**
 * Changes the name of all attributes that have the given name
 *
 * @param tagName String with the name of the Tags to be changed
 * @param oldName String with the name of the attribute to be changed
 * @param newName String with the new attribute name
 *
 * @return [Int] Number of changes made
 * Throws an exception if the new specified name is invalid
 */
internal fun changeAllAttributesNamed(tagName:String, oldName: String, newName: String): Int {
    var countSubstitutions = 0

    if (checkEntityName(newName))
        children.forEach {
            if (it.name==tagName) {
                it.changeAttributeName(oldName, newName)
                countSubstitutions += 1
            }

            if (it.hasChildren)
                countSubstitutions += it.changeAllAttributesNamed(tagName, oldName, newName)
        }
    else throw RuntimeException(errorAttributeName)

    return countSubstitutions
}

/**
 * Deletes an attribute by name, if it exists
 *
 * @param name String with the name of the attribute to be deleted
 *
 * @return [Boolean] true if attribute is deleted, otherwise false
 */
fun deleteAttribute(name: String): Boolean {
    attributes.forEachIndexed { idx, x ->
        if (attributes[idx].name == name) {
            attributes.removeAt(idx)
            return true
        }
    }
    return false
}

}