# XML Package

## Programação Avançada
#### Joaquim H. Carvalho (Aluno 97044)

It aims to be a library for generating and manipulating XML in Kotlin, covering the essential elements:  
- **Document**: is the main container.  
- **Tags**: Tags are the structuring entities of an XML document.
- **Text nested in Tags**: Tags can contain text. In this case, the Tag cannot contain other Tags.
- **Nested Tags**: Tags can be nested in a tree structure. **There can only be one Tag at the root**.
- **Attributes**: Attributes can be part of the Document or Tags

### Usage

Using this library starts with creating an empty document.  
  
``val myXML = Document("example")``  
  
The "version" and "encoding" attributes are automatically assigned when the document is created.  
Since each document can only contain one Tag, the creation of a document also includes the creation of its root Tag.  
The Root Tag has the name of the input parameter, but can be changed if necessary.  
So, the first step to start manipulating the rest of the document's content is to assign the class that contains the root Tag to a variable.  
  
``val mainTag = myXML.documentRoot``  

Next, you can build your own document using the various methods provided by library, or, if you have a set of structured data to transform into XML, use automatic translation!...  

  ``mainTag.translate( <yourData> )``  

Finaly, to see the results:  

  ``println( myXML.prettyPrintXML() )``  

### Samples

There are some samples in the inline documentation.
These samples are available in the [Samples.kt](https://github.com/jhcos1/ProjetoXML_PA/blob/master/src/main/kotlin/XML/Samples.kt) file.
  
### DSL  

Sometimes we need to create a file from scratch.  
In this case, we can use the simplified way of creating the XML document.  
Here is an example:  

       
    val myXML = Document("SampleTag")  
    val mainTag = myXML.documentRoot  
    val doc = mainTag.addTag {
        addTag("curso", "Mestrado em Engenharia de Informática" )
        addTag("fuc") {
            addAttribute("codigo", "03782")
            addAttribute("teste", 33)
            addTag("nome","Dissertação")
            addTag("ects",42.0)
            addTag("avaliacao") {
                addTag("componente") {
                    addAttribute("nome", "Dissertação")
                    addAttribute("peso","60%")
                }
                addTag("componente") {
                    addAttribute("nome", "Apresentação")
                    addAttribute("peso","20%")
                }
                addTag("componente") {
                    addAttribute("nome", "Discussão")
                    addAttribute("peso","20%")
                }
            }
        }
    }
    
    println( myXML.prettyPrintXML() )  
    

## Reference
  
### Document properties and methods  

The Class Document creates a new document with the main Tag.  
If Tag name is not mentioned, assumes "documentRoot" as is name. Also adds the "version" and "encoding" attributes with default values.  
    
      fun docSample() {
          val myXML = Document("empty")
          val mainTag = myXML.documentRoot
       
          println( myXML.prettyPrintXML() )              // XML Formated Document
      }  
  
##### *General*  
**rootTagName**: name for the main Tag. If the name is not identified, it defaults to "documentRoot"  
**documentRoot**: name of the document's main Tag  
**size**: number of Tags in the document. Can also be obtained through the fullSize property of the main class  
**maxDepth**: maximal depth for the Tags in the document  
**search**: searches into the document for the given pattern  
**prettyPrintXML**: returns a string with the document in XML format  
**saveToFile**: Save the XML formated document to a file   
  
##### *Tag methods*  
**changeRootTagName**: Allows changing the name of the document's main Tag  
**changeAllTagsNamed**: Changes the name of all Tag's with a specific name in the document to a new name  
**deleteAllTagsNamed** Deletes all Tag's with a specific name   
##### *Attribute methods*  
**addAttribute**: Adds an attribute to the Document if it does not exist  
**addAttributeOnAllTagsNamed**: Adds an attribute in all Tag's with a specific name in the document  
**getAllAttributes**: Returns a list with the names of all attributes  
**getAttributeValue**: Returns the value of an attribute  
**changeAttributeValue**: Changes the value of an attribute  
**changeAttributeName**: Changes the name of an attribute  
**changeAllAttributesNamed**: Changes the name of all attributes in the document with a specific name to a new name  
**deleteAttribute**: Deletes an attribute identified by name if it exists  
**deleteAttributeOnAllTagsNamed**: Deletes an attribute identified by is name in all Tag's with a specific name  

### Tag properties and methods  
  
The Class Tag manages Tags and its attributes.  
To create a Document with main Tag class, please use Document class.  


    fun tagSample() {
        val myXML = Document("SampleTag")
        val mainTag = myXML.documentRoot
      
        mainTag.addAttribute("content", "students")
        mainTag.addTag("student", "XPTO")
      
        println( myXML.prettyPrintXML() )  
    }  

  ##### *General*  
    
 **parent**: the parent of the Tag. It will be Document if the Tag is the root Tag  
 **name**: name for the Tag  
 **value**: Optional. The value for the Tag, if it has one.  
    
 **children**: the children container  
 **size**: number of Tags in the children container  
 **fullSize**: number off all Tags in the container, including the children Tags  
 **depth**: the level the Tag is at, taking into account that the main Tag is at level 1  
 **maxDepth**: the maximal depth for the Tag  
 **path**: the full path from the Root Tag  
 **hasChildren**: returns true if current Tag has children Tags, otherwise false  
 **hasAttributes**: returns true if current Tag has attributes, otherwise false  
 **prettyPrintXML**: generates the XML formatted string  
  
##### *Tag methods*  
    
 **addTag**: add a Tag to the children Tag container  
 **changeTagName**: changes the Tag's name  
 **changeAllTagsNamed**: changes the name of all Tags that have the given name  
 **delete**: deletes itself. The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated  
 **deleteTag**: deletes a Tag in the child container. The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated  
 **deleteAllTagsNamed**: deletes all the Tags with the given name. The Tag will only be deleted if it does not contain other Tags, or if the force parameter is activated  
  
##### *Attribute methods*  
     
**addAttribute**: adds an attribute to the Tag only if it doesn't exist  
**addAttributeOnAllTagsNamed**: adds an attribute on all Tag's that have the given name  
**getAllAttributes**: returns the List with the name of all attributes  
**getAttributeValue**: return the value of an attribute  
**changeAttributeValue**: changes the attribute value  
**changeAttributeName**: changes the attribute name  
**changeAllAttributesNamed**: changes the name of all attributes that have the given name  
**deleteAttribute**: deletes an attribute by name, if it exists  
**deleteAttributeOnAllTagsNamed**: deletes an attribute by is name on all Tag's that have the given name  
  
