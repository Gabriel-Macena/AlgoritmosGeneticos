import java.util.*

/* VALIDATION */
fun validate(individual: Individual) {
    val size = individual.size
    val repeatedIndices = mutableListOf<Int>()
    val nonOccurringValues = mutableListOf<Int>()
    nonOccurringValues.addAll(0..(size-1))
    nonOccurringValues.shuffle(RND)
    for((index, value) in individual.solution.withIndex()) {
        if (!nonOccurringValues.remove(value)) {
            //If it wasn't removed, it was repeated
            repeatedIndices.add(index)
        }
    }
    for(i: Int in repeatedIndices.shuffled(RND)) {
        individual.solution[i] = nonOccurringValues.removeAt(0)
    }
}