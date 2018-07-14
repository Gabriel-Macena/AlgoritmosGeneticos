class Individual(val size: Int, val generation: Int) {
    var evaluated: Long = -1
    var fitness: Double = -1.0
    var solution: IntArray = IntArray(size)

    operator fun set(index: Int, element: Int): Int {
        val old = solution[index]
        solution[index] = element
        return old
    }

    operator fun get(index: Int): Int {
        return solution[index]
    }

    fun evaluate(matrices: QAP): Long {
        if (evaluated >= 0) return evaluated
        evaluated = 0
        val maxSize = matrices.size - 1
        for (i in 0..maxSize) {
            for (j in 0..maxSize) {
                if (i != j) {
                    val di = solution[i]
                    val dj = solution[j]
                    evaluated += matrices.distMatrix[i][j] * matrices.flowMatrix[di][dj]
                }
            }
        }
        fitness = 1.0/evaluated
        return evaluated
    }

    fun print() {
        println("--- BEST SOLUTION ---")
        solution.forEach { print("\t ${it + 1}") }
        println()
        println("Generation: $generation")
        println("Value: $evaluated")
    }
}