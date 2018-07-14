class Population() {
    val size: Int
        get() = individuals.size
    var individuals = mutableListOf<Individual>()
    fun evaluateAll(matrices: QAP) {
        individuals.forEach { it.evaluate(matrices) }
    }
    fun best(): Individual {
        return individuals.sortedBy { it.evaluated }[0]
    }
    fun print() {
        print("(")
        individuals.forEachIndexed { index, individual ->
            if (index < (size - 1)) print("${individual.generation}, ")
            else print("${individual.generation}")
        }
        println(")")
    }
}