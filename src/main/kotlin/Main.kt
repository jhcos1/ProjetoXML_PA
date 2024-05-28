import XML.*


class ComponenteAvaliacao(@XmlAttribute val nome: String, @XmlAttribute val peso: Int)

@XmlAdapter(FUCAdapter::class)
class FUC(
    @XmlAttribute
    val codigo: String,
    var nome: String,
    val ects: Double,
    @XmlExclude
    val observacoes: String,
    val avaliacao: List<ComponenteAvaliacao>)


class FUCAdapter() {

    //@XmlUpdater("nome")
    val updateNome: (FUC) -> Unit = { it.nome = "...$it!!!" }
}




fun main() {
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