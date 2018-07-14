package operators

import Individual
import RND
import validate

/* CROSSOVER */

enum class CrossoverType {
    UNIFORM,
    DIAGONAL,
    OB_SCAN,
    FB_SCAN,
    UNI_SCAN
}

/**
 * Perform a uniform crossover without a sectional point.
 */
fun uniform(parents: List<Individual>, generation: Int): Individual {
    val size = parents[0].size
    val child = Individual(size, generation)
    repeat(size) {
        val index = RND.nextInt(parents.size)
        child.solution[it] = parents[index].solution[it]
    }
    validate(child)
    return child
}

fun diagonal(
        parents: List<Individual>,
        generation: Int
): Individual {
    val size = parents[0].size
    val child = Individual(size, generation)
    val sectionSize = size / parents.size
    val availableParents = (0 until parents.size).shuffled(RND).toMutableList()
    var nextSection = sectionSize
    var i = 0
    var index = availableParents.removeAt(0)
    var nextParent = parents[index]
    while (i < size) {
        if (i < nextSection) {
            child.solution[i] = nextParent.solution[i]
        } else {
            index = availableParents.removeAt(0)
            nextParent = parents[index]
            nextSection = if (availableParents.isEmpty()) size else nextSection + sectionSize
            child.solution[i] = nextParent.solution[i]
        }
        i++
    }
    validate(child)
    return child
}

fun baseCrossover(parents: List<Individual>, generation: Int, type: CrossoverType): Individual {
    return when (type) {
        CrossoverType.UNIFORM -> uniform(parents, generation)
        CrossoverType.DIAGONAL -> diagonal(parents, generation)
        CrossoverType.FB_SCAN -> MultiCrossover.geneScanning(parents, generation, ScanningType.FB_SCAN)
        CrossoverType.OB_SCAN -> MultiCrossover.geneScanning(parents, generation, ScanningType.OB_SCAN)
        CrossoverType.UNI_SCAN -> MultiCrossover.geneScanning(parents, generation, ScanningType.UNIFORM)
    }
}