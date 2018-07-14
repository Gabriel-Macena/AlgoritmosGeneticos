package operators

import Individual
import RND
import java.util.*

enum class ScanningType {
    OB_SCAN,
    FB_SCAN,
    UNIFORM
}

class MultiCrossover {
    companion object {
        private fun selectMethod(parents: List<Individual>, markers: IntArray, type: ScanningType): Int {
            if (type == ScanningType.UNIFORM) {
                val r = RND
                val index = r.nextInt(parents.size)
                return parents[index][markers[index]]
            } else if (type == ScanningType.OB_SCAN) {
                val occurrencesMap = mutableMapOf<Int, Int>()
                parents.forEachIndexed { i, p ->
                    val key = p[markers[i]]
                    val value = occurrencesMap.getOrDefault(key, 0)
                    occurrencesMap[key] = value + 1
                }
                val highestCount = occurrencesMap.maxBy { (_, v) -> v }
                val hits = occurrencesMap.filter { (_, v) -> v == highestCount?.value }.keys
                if (hits.size > 1) {
                    //Tie-breaker
                    var value: Int = parents[0][markers[0]]
                    parents.forEachIndexed { i, p ->
                        value = p[markers[i]]
                        if(hits.contains(value)) {
                            return@forEachIndexed
                        }
                    }
                    return value
                }
                else return hits.first()
            } else {
                val chosen = rouletteWheel(parents, 1).first()
                val index = parents.indexOf(chosen)
                return chosen[markers[index]]
            }
        }

        fun geneScanning(parents: List<Individual>, generation: Int, selection: ScanningType): Individual {
            val size = parents[0].size
            val child = Individual(size, generation)
            val chosen = mutableListOf<Int>()
            val markers = IntArray(parents.size) { 0 }
            for (i in 0 until size) {
                parents.forEachIndexed { index, individual ->
                    while (chosen.contains(individual[markers[index]])) {
                        markers[index] += 1
                    }
                }
                child[i] = selectMethod(parents, markers, selection)
                chosen.add(child[i])

            }
            return child
        }
    }

}