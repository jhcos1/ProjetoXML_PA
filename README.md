# XML Package

## Programação Avançada
### Joaquim H. Carvalho (Aluno 97044)

It aims to be a library for generating and manipulating XML in Kotlin, covering the essential elements:  
- **Document**: is the main container.  
- **Tags**: Tags are the structuring entities of an XML document.
- **Text nested in Tags**: Tags can contain text. In this case, the Tag cannot contain other Tags.
- **Nested Tags**: Tags can be nested in a tree structure. **There can only be one Tag at the root**.
- **Attributes**: Attributes can be part of the Document or Tags

#### Usage

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

#### Samples

There are some samples in the inline documentation.
These samples are available in the [Samples.kt](https://github.com/jhcos1/ProjetoXML_PA/blob/master/src/main/kotlin/XML/Samples.kt) file.
  
    
