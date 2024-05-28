import XML.Attribute
import XML.Document
import XML.Tag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertFalse

class TestXMLClass {

    fun Any.getType(): String =
        when (this) {
            is Int -> "INT"
            is String -> "STRING"
            is Tag -> "TAG"
            is Document -> "DOCUMENT"
            is Attribute -> "ATTRIBUTE"
            is Boolean -> "BOOLEAN"
            else -> "UNKNOWN"
        }

    val myXML = Document("plano")
    val docRoot = myXML.documentRoot

    val doc = docRoot.addTag {
        addTag("curso", "Mestrado em Engenharia de Informática" )
        addTag("fuc") {
            addTag("nome", "Programação Avançada")
            addTag("ects", 6.0)
            addTag("avaliacao") {
                addTag("componente"){
                    addAttribute("nome", "Quizzes")
                    addAttribute("peso","80%")
                }
                addTag("componente") {
                    addAttribute("nome", "Projeto")
                    addAttribute("peso","80%")
                }
            }
        }
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

    /*
     *
     *    Document tests
     *
     */
    @Test
    fun testDoc_size() {
        assertEquals(15, myXML.size )
    }

    @Test
    fun testDoc_maxDepth() {
        assertEquals(4, myXML.maxDepth )
    }

    @Test
    fun testNewDocument() {
        val testeDoc1 = Document()
        val testeDoc2 = Document("teste")
        assertEquals( "documentRoot", testeDoc1.rootTagName )
        assertEquals( "teste", testeDoc2.rootTagName )
    }

    @Test
    fun testDoc_changeRootTagName() {
        assertTrue( myXML.changeRootTagName("plano") )
        assertEquals( "plano", myXML.rootTagName )
    }

    @Test
    fun testDoc_addAttribute() {
        assertTrue( myXML.addAttribute("novoAtributo", 5) )
        assertEquals( "5", myXML.getAttributeValue("novoAtributo") )
        assertFalse( myXML.addAttribute("novoAtributo", 5) )
    }

    @Test
    fun testDoc_getAllAttributes() {
        assertEquals( listOf("version", "encoding"), myXML.getAllAttributes())
    }

    @Test
    fun testDoc_getAttributeValue() {
        assertEquals( "1.0", myXML.getAttributeValue("version"))
        assertEquals( "UTF-8", myXML.getAttributeValue("encoding"))
    }

    @Test
    fun testDoc_changeAttributeValue() {
        assertFalse( myXML.changeAttributeValue("teste", "teste" ) )
        assertTrue( myXML.changeAttributeValue("version", 222) )
        assertEquals( "222", myXML.getAttributeValue("version" ) )
    }

    @Test
    fun testDoc_changeAttributeName() {
        assertFalse( myXML.changeAttributeName("teste", "teste" ) )
        assertTrue( myXML.changeAttributeName("version", "versao") )
        assertEquals( listOf("versao", "encoding"), myXML.getAllAttributes() )
    }

    @Test
    fun testDoc_deleteAttribute() {
        assertFalse( myXML.deleteAttribute("teste" ) )
        assertTrue( myXML.deleteAttribute("version" ) )
        assertEquals( listOf("encoding"), myXML.getAllAttributes() )
    }

    @Test
    fun testDoc_checkEntityName() {
        assertEquals( true, myXML.changeRootTagName("teste123"))

        //assertEquals( true, myXML.changeRootTagName("teste_1.2-3"))
        //assertEquals( true, myXML.changeRootTagName("123teste"))
        //assertEquals( false, myXML.changeRootTagName("XMLteste"))
    }

    @Test
    fun testDoc_deleteAttributeOnAllTagsNamed() {
        assertEquals(5, myXML.deleteAttributeOnAllTagsNamed("componente","peso"))
    }

    @Test
    fun testDoc_changeAllAttributesNamed() {
        assertEquals(5, myXML.changeAllAttributesNamed("componente" ,"peso", "percent"))
    }

    @Test
    fun testDoc_addAttributeOnAllTagsNamed() {
        assertEquals(5, myXML.addAttributeOnAllTagsNamed("componente" ,"final", "100%"))
    }

    @Test
    fun testDoc_deleteAllTagsNamed() {
        assertEquals(5, myXML.deleteAllTagsNamed("componente" ))
    }

    @Test
    fun testDoc_changeAllTagsNamed() {
        assertEquals(5, myXML.changeAllTagsNamed("componente", "newName" ))
    }

    @Test
    fun testDoc_prettyPrintXML() {
        assertEquals( 557, myXML.prettyPrintXML().length )
    }



    /*
     *
     *    Tag tests
     *
     */
    @Test
    fun testTag_size() {
        assertEquals( 3, doc.size)
    }

    @Test
    fun testTag_fullSize() {
        assertEquals( 15, doc.fullSize() )
    }

    @Test
    fun testTag_path() {
        val tagToTest: Tag = doc.search("componente")[0]

        assertEquals("/plano/fuc/avaliacao/componente", tagToTest.path)
    }

    @Test
    fun testTag_depth() {
        val tagToTest: Tag = doc.search("componente")[0]

        assertEquals( 4, tagToTest.depth )
    }


    @Test
    fun testTag_hasAttributes() {
        val tagToTestFalse: Tag = docRoot.search("curso")[0]
        val tagToTestTrue: Tag = docRoot.search("componente")[0]

        assertTrue( tagToTestTrue.hasAttributes )
        assertFalse( tagToTestFalse.hasAttributes )
    }

    @Test
    fun testTag_hasChildren() {
        val tagToTestFalse: Tag = docRoot.search("curso")[0]
        val tagToTestTrue: Tag = docRoot.search("avaliacao")[0]

        assertTrue( tagToTestTrue.hasChildren )
        assertFalse( tagToTestFalse.hasChildren )
    }

    @Test
    fun testTag_addTag() {
        val newTag: Tag = docRoot.addTag("thisIsNew")
        assertTrue( newTag.getType() == "TAG" )
        assertEquals( "thisIsNew", newTag.name )
        assertEquals( 1, myXML.search("thisIsNew").size )
    }

    @Test
    fun testTag_changeTagName() {
        val newTag: Tag = docRoot.addTag("qualquerNome")
        assertEquals(true, newTag.changeTagName("novoNome") )
        assertEquals("novoNome", newTag.name )
    }

    @Test
    fun testTag_changeAllTagsNamed() {
        assertEquals( 5, docRoot.changeAllTagsNamed("componente", "novaTag"))
        assertEquals( 5, myXML.search("novaTag").size )
    }

    @Test
    fun testTag_delete() {
        val newTag: Tag = docRoot.addTag("thisIsNew")
        assertEquals( 1, myXML.search("thisIsNew").size )

        newTag.delete()
        assertEquals( 0, myXML.search("thisIsNew").size )

        assertEquals( false, docRoot.delete() )
        assertEquals( false, docRoot.delete(true) )
    }

    @Test
    fun testTag_deleteTag() {
        val tagToDel: Tag = docRoot.search("avaliacao")[0]
        val tagParent: Tag = tagToDel.parent as Tag

        assertEquals( false, tagParent.deleteTag( tagToDel ) )
        assertEquals( true, tagParent.deleteTag( tagToDel, true ) )
        assertEquals( 3, docRoot.search("componente").size )
    }

    @Test
    fun testTag_addAttribute() {
        docRoot.addAttribute("size", 23.5)
        docRoot.addAttribute("hora", Instant.now())
        assertEquals(listOf("size", "hora"), docRoot.getAllAttributes())

        docRoot.addAttribute("cor", "yellow")
        docRoot.addAttribute("id", 1001)

        assertEquals(listOf("size", "hora", "cor", "id"), docRoot.getAllAttributes())

        // atributo repetido. Suposto nao adicionar
        docRoot.addAttribute("id", 1005)
        assertEquals(listOf("size", "hora", "cor", "id"), docRoot.getAllAttributes())
    }

    @Test
    fun testTag_getAttributeValue() {
        docRoot.addAttribute("cor", "yellow")
        assertEquals("yellow", docRoot.getAttributeValue("cor") )
    }

    @Test
    fun testTag_getAllAttributes() {
        docRoot.addAttribute("size", 23.5)
        docRoot.addAttribute("cor", "yellow")
        docRoot.addAttribute("id", 1001)
        assertEquals(listOf("size", "cor", "id"), docRoot.getAllAttributes())
    }

    @Test
    fun testTag_changeAttributeValue() {
        docRoot.addAttribute("cor", "yellow")
        docRoot.addAttribute("id", 1001)

        // valor do atributo antes da alteracao
        assertEquals( "yellow", docRoot.getAttributeValue("cor"))
        docRoot.changeAttributeValue("cor", "green")

        // valor do atributo depois da alteracao
        assertEquals( "green", docRoot.getAttributeValue("cor"))
    }

    @Test
    fun testTag_changeAttributeName() {
        docRoot.addAttribute("cor", "yellow")
        docRoot.addAttribute("id", 1001)

        // nome e valor do atributo antes da alteracao
        assertEquals(listOf("cor", "id"), docRoot.getAllAttributes())
        assertEquals( "1001", docRoot.getAttributeValue("id"))
        docRoot.changeAttributeName("id", "identification")

        // nome e valor do atributo depois da alteracao
        assertEquals(listOf("cor", "identification"), docRoot.getAllAttributes())
        assertEquals( "1001", docRoot.getAttributeValue("identification"))
    }

    @Test
    fun testTag_deleteAttribute() {
        docRoot.addAttribute("size", 23.5)
        docRoot.addAttribute("hora", Instant.now())
        // lista de atributos antes do drop
        assertEquals(listOf("size", "hora"), docRoot.getAllAttributes())
        docRoot.deleteAttribute("size")

        // lista de atributos antes do drop
        assertEquals(listOf("hora"), docRoot.getAllAttributes())
    }

    @Test
    fun testTag_changeAllAttributesNamed() {
        // Tested with testDoc_changeAllAttributesNamed()
        assertEquals(5, myXML.changeAllAttributesNamed("componente" ,"peso", "percent"))
    }

    @Test
    fun testTag_addAttributeOnAllTagsNamed() {
        // Tested with testDoc_addAttributeOnAllTagsNamed()
        assertEquals(5, myXML.addAttributeOnAllTagsNamed("componente" ,"final", "100%"))
    }

    @Test
    fun testTag_deleteAttributeOnAllTagsNamed() {
        // Tested with testDoc_addAttributeOnAllTagsNamed()
        assertEquals(5, myXML.deleteAttributeOnAllTagsNamed("componente","peso"))
    }




    /*
     *
     *    Attribute tests
     *
     *    The Attribute class is Internal, so, its functions are tested using the methods of the classes that call them
     */

}


