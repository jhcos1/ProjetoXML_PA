package XML

import java.io.File


/**
 * Manages the CLASS Document and its attributes.
 * The management of its content is done using the CLASS Tag.
 *
 * @see Tag
 * @see Attribute
 *
 * @sample [docSample]
 *
 * @constructor Creates a new document with the main Tag.
 *              If Tag name is not mentioned, assumes "documentRoot" as is name.
 *              Also adds the "version" and "encoding" attributes with default values.
 *
 * @param rootTagName name for the main Tag. If the name is not identified, it defaults to "documentRoot"
 *
 * @property [documentRoot] name of the document's main Tag
 * @property [size] number of Tags in the document. Can also be obtained through the fullSize property of the main class
 * @property [maxDepth] Maximal depth for the Tags in the document
 *
 * @property [search] Searches into the document for the given pattern
 * @property [prettyPrintXML] Returns a string with the document in XML format
 * @property [saveToFile] Save the XML formated document to a file
 * @property [changeRootTagName] Allows changing the name of the document's main Tag
 * @property [changeAllTagsNamed] Changes the name of all Tag's with a specific name in the document to a new name
 * @property [deleteAllTagsNamed] Deletes all Tag's with a specific name
 * @property [addAttribute] Adds an attribute to the Document if it does not exist
 * @property [addAttributeOnAllTagsNamed] Adds an attribute in all Tag's with a specific name in the document
 * @property [getAllAttributes] Returns a list with the names of all attributes
 * @property [getAttributeValue] Returns the value of an attribute
 * @property [changeAttributeValue] Changes the value of an attribute
 * @property [changeAttributeName] Changes the name of an attribute
 * @property [changeAllAttributesNamed] Changes the name of all attributes in the document with a specific name to a new name
 * @property [deleteAttribute] Deletes an attribute identified by name if it exists
 * @property [deleteAttributeOnAllTagsNamed] Deletes an attribute identified by is name in all Tag's with a specific name
 *
 * @author Joaquim Henriques Carvalho (N. 97044)
 * @since 19 May 2024
 */


data class Document(
    var rootTagName: String
) {

    constructor() : this("documentRoot")

    private val attributes: MutableList<Attribute> = mutableListOf()

    val documentRoot: Tag =
        if (checkEntityName(rootTagName)) Tag(this, rootTagName)
        else throw RuntimeException(errorTagName)

    val size: Int get() = documentRoot.fullSize()

    val maxDepth: Int get() = documentRoot.maxDepth()

    init {
        // Define atributos por defeito
        this.attributes.add( Attribute( "version", "1.0") )
        this.attributes.add( Attribute( "encoding", "UTF-8") )
    }

    /**
     * Returns a string with the document in XML format
     *
     * @return [String]: String with the document in XML format
     */
    fun prettyPrintXML(): String {
        var str = "<?xml "
        this.attributes.forEach { str += it.prettyPrintXML + " " }
        str += "?>\n" + documentRoot.prettyPrintXML() + "\n"
        return str
    }

    /**
     * Save the XML formated document to a file
     *
     * @param fileName String with the name of the file without the extension, which will be xml
     *
     */
    fun saveToFile(fileName: String) {
        File("${fileName}.xml").writeText( this.prettyPrintXML() )
    }

    /**
     * Save the XML formated document to a file
     *
     * @param path String with the full path of the location where the file will be saved, without the slash in the end
     * @param fileName String with the name of the file without the extension, which will be xml
     */
    fun saveToFile(path: String, fileName: String) {
        saveToFile("${path}/${fileName}")
    }

    /**
     * Searches into the document for the given pattern
     *
     * @return [List] Returns the list of founded objects
     */
    fun search(pattern: String): List<Tag> {
        return documentRoot.search(pattern)
    }

    /******************************************************************************************/
    /*                                                                                        */
    /*                                                                                        */
    /*                                                                                        */
    /*                                         TAGS                                           */
    /*                                                                                        */
    /*                                                                                        */
    /*                                                                                        */
    /******************************************************************************************/

    /**
     * Allows changing the name of the document's main Tag
     *
     * @param name String with the new name for the main Tag
     *
     * @return [Boolean] true if the name was changed, otherwise false
     * Throws an exception if the new specified name is invalid
     */
    fun changeRootTagName(name: String): Boolean {

        if (checkEntityName(name)) {
            this.rootTagName = name
            this.documentRoot.changeTagName(name)
        }
        else throw RuntimeException(errorTagName)

        return true
    }

    /**
     * Changes the name of all Tag's with a specific name in the document to a new name
     *
     * @param oldName The name of the Tag's to be changed
     * @param newName The new Tag's name
     *
     * @return [Int] the total number of Tag's changed in the document
     * Throws an exception if the new specified name is invalid
     */
    fun changeAllTagsNamed(oldName: String, newName: String): Int {

        if (checkEntityName(newName))
            return documentRoot.changeAllTagsNamed(oldName, newName)

        return 0
    }

    /**
     * Deletes all Tag's with a specific name
     *
     * @param tagName The name of the Tag's to be deleted
     *
     * @return [Int] the total number of Tag's deleted in the document
     */
    fun deleteAllTagsNamed(tagName: String, force: Boolean = false): Int {
            return documentRoot.deleteAllTagsNamed(tagName)
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
     * Changes the name of all attributes in the document with a specific name to a new name
     *
     * @param tagName The name of the Tag's to be changed
     * @param oldName The name of the attribute to be changed
     * @param newName The new name for the attribute
     *
     * @return [Int] the total number of attributes changed in the document
     * Throws an exception if the new specified name is invalid
     */
    fun changeAllAttributesNamed(tagName: String, oldName: String, newName: String): Int {

        if (checkEntityName(newName))
            return documentRoot.changeAllAttributesNamed(tagName, oldName, newName)

        return 0
    }

    /**
     * Adds an attribute in all Tag's with a specific name in the document
     *
     * @param tagName The name of the Tag's to be changed
     * @param attributeName The name of the attribute to be added
     * @param value The value of attribute. It cannot be null
     *
     * @return [Int] the total number of attributes added to the document
     * Throws an exception if the new specified name is invalid
     */
    fun addAttributeOnAllTagsNamed(tagName: String, attributeName: String, value: Any): Int {

        if (checkEntityName(attributeName))
            return documentRoot.addAttributeOnAllTagsNamed(tagName, attributeName, value)

        return 0

    }

    /**
     * Adds an attribute to the Document if it does not exist
     *
     * @param name The name of the attribute to be added
     * @param value The value of attribute. It cannot be null
     *
     * @return [Boolean] true if the value of the attribute was added, otherwise false
     * Throws an exception if the new specified name is invalid
     */
    fun addAttribute(name: String, value: Any): Boolean {
        if (checkEntityName(name))
            if( this.getAllAttributes().contains(name) )
                return false
            else {
                this.attributes.add(Attribute(name, value))
                return true
            }
        else throw RuntimeException(errorAttributeName)
    }

    /**
     * Returns a list with the names of all attributes
     *
     * @return [List] List of Strings with the attribute names relative to Document
     *
     */
    fun getAllAttributes(): List<String> {
        val atribs: MutableList<String> = mutableListOf()
        this.attributes.forEach {
            atribs.add(it.name)
        }
        return atribs
    }

    /**
     * Returns the value of an attribute
     *
     * @param name String containing the name of the attribute with the value to be obtained
     *
     * @return [String] Attribute value in String format. Null if attribute does not exist
     *
     */
    fun getAttributeValue(name: String): String? {
        this.attributes.forEach {
            if (it.name == name)
                return it.getValueAsString
        }
        return null
    }

    /**
     * Changes the value of an attribute
     *
     * @param name String containing the name of the attribute to be changed
     * @param newValue Value of the attribute. It can be a string, a number, or a date
     *
     * @return [Boolean] true if the value of the attribute was changed, otherwise false
     *
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
     * Changes the name of an attribute
     *
     * @param name String containing the name of the attribute to be changed
     * @param newName String containing the new name for the attribute
     *
     * @return [Boolean] true if the attribute was changed, otherwise false
     * Throws an exception if the new specified name is invalid
     *
     */
    fun changeAttributeName(name: String, newName: String): Boolean {
        if (checkEntityName(newName))
            this.attributes.forEach {
                if (it.name == name) {
                    it.changeAttributeName( newName )
                    return true
                }
            }
        else throw RuntimeException(errorAttributeName)

        return false
    }

    /**
     * Deletes an attribute identified by name if it exists
     *
     * @param name String containing the name of the attribute to be removed
     *
     * @return [Boolean] true if the attribute was deleted, otherwise false
     */
    fun deleteAttribute(name: String): Boolean {
        this.attributes.forEachIndexed { idx, x ->
            if (attributes[idx].name == name) {
                attributes.removeAt(idx)
                return true
            }
        }
        return false
    }

    /**
     * Deletes an attribute identified by is name in all Tag's with a specific name
     *
     * @param tagName String containing the name of the Tag to be changed
     * @param attributeName String containing the name of the attribute to be removed
     *
     * @return [Int] the total number of attributes removed from document
     *
     */
    fun deleteAttributeOnAllTagsNamed(tagName: String, attributeName: String): Int {

        if (checkEntityName(attributeName) && checkEntityName(tagName))
            return documentRoot.deleteAttributeOnAllTagsNamed(tagName, attributeName)

        return 0

    }

}