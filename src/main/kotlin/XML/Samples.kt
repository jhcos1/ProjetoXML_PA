package XML

annotation class Sample


@Sample
fun docSample() {
    val myXML = Document("SampleTag")
    println(myXML.rootTagName)              //SampleTag

    myXML.changeRootTagName("TagNameChanged")

    println(myXML.rootTagName)              // TagNameChanged
    println(myXML.documentRoot.name)        // TagNameChanged
}

@Sample
fun attribSample() {
    val myXML = Document("SampleTag")

    myXML.addAttribute("newAttribute", 5)
    println(myXML.getAttributeValue("newAttribute"))          // 5

    myXML.changeAttributeValue("newAttribute", "changedValue")
    println(myXML.getAttributeValue("newAttribute"))          // changedValue
}

@Sample
fun tagSample() {
    val myXML = Document("SampleTag")
    val mainTag = myXML.documentRoot

    mainTag.addAttribute("content", "students")
    mainTag.addTag("student", "XPTO")

    println( myXML.prettyPrintXML() )              // XML Formated Document
}

@Sample
fun tagDSLSample() {
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

    println( myXML.prettyPrintXML() )              // XML Formated Document
}

@Sample
fun translateSample() {
    class ComponenteAvaliacao(@XmlAttribute val nome: String, @XmlAttribute val peso: Int)

    class FUC(
        @XmlAttribute
        val codigo: String,
        val nome: String,
        val ects: Double,
        @XmlExclude
        val observacoes: String,
        val avaliacao: List<ComponenteAvaliacao>
    )

    val myXML = Document("fucs")
    val fucs = myXML.documentRoot

    val f1 = FUC("M123", "Programação Avançada", 6.0, "Quase a acabar ...",
        listOf(
            ComponenteAvaliacao("Quizzes", 20),
            ComponenteAvaliacao("Projeto", 80)
        )
    )

    val f2 = FUC("N456", "Gestão de tempo", 6.0, "Nunca é de mais",
        listOf(
            ComponenteAvaliacao("Assiduidade", 20),
            ComponenteAvaliacao("Trabalho_Grupo", 30),
            ComponenteAvaliacao("Trabalho_Individual", 50)
        )
    )

    val f3 = FUC("O789", "Networking", 6.0, "Dá sempre jeito",
        listOf(
            ComponenteAvaliacao("Participacao", 25),
            ComponenteAvaliacao("Teste_1", 35),
            ComponenteAvaliacao("Teste_2", 40)
        )
    )

    val fucsList:List<FUC> = listOf(f1, f2, f3)

    fucs.translate(fucsList)
    //fucs.translate(f2)
    println(myXML.prettyPrintXML())

}