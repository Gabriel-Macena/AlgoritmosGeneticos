import java.io.File
import java.util.*

fun readFile(file: File): QAP {
    val sc = Scanner(file)
    val size = sc.nextInt()
    val qap = QAP(size)
    for (i in 0..(size-1)) {
        for (j in 0..(size-1)) {
            qap.distMatrix[i][j] = sc.nextLong()
        }
    }
    for (i in 0..(size-1)) {
        for (j in 0..(size-1)) {
            qap.flowMatrix[i][j] = sc.nextLong()
        }
    }
    sc.close()
    return qap
}

class QAP(val size: Int) {
    val flowMatrix = Array(size, { LongArray(size) })
    val distMatrix = Array(size, { LongArray(size) })
    fun print() {
        println("---- DISTANCE MATRIX ----")
        for (i in 0..(size-1)) {
            for (j in 0..(size-1)) {
                print("\t ${distMatrix[i][j]}")
            }
            println()
        }
        println()
        println("---- FLOW MATRIX ----")
        for (i in 0..(size-1)) {
            for (j in 0..(size-1)) {
                print("\t ${flowMatrix[i][j]}")
            }
            println()
        }
    }
}