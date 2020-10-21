package ppanda.commons.bundle.math.algo.probability

import ppanda.commons.bundle.math.constructs.Fraction
import ppanda.commons.bundle.math.constructs.Fraction.Companion.ONE
import ppanda.commons.bundle.math.constructs.Fraction.Companion.ZERO
import ppanda.commons.bundle.math.constructs.Fraction.Companion.of
import ppanda.commons.bundle.math.constructs.SquareMatrix
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkovChainsTest {
    val `0` = ZERO
    val `1` = ONE

    @Test
    fun shouldFindFRMatrix() {
        val transitionMatrix = SquareMatrix(
            listOf(
                listOf(`0`, 2 by 3, 1 by 3, `0`, `0`),
                listOf(`0`, `0`, `0`, 3 by 7, 4 by 7),
                listOf(`0`, `0`, `0`, `0`, `0`),
                listOf(`0`, `0`, `0`, `0`, `0`),
                listOf(`0`, `0`, `0`, `0`, `0`)
            )
        )

        val markovChains = MarkovChains(transitionMatrix)
        val FR = markovChains.findFR()
        assertEquals(
            FR.toString(), """
            1/3 2/7 8/21
            0/1 3/7 4/7
        """.trimIndent()
        )
    }

    //Based on https://math.stackexchange.com/a/1991133
    @Test
    fun shouldFindFRMatrixAndFirstRowWillBeFinalProbabilities() {
        val transitionMatrix = SquareMatrix(
            listOf(
                listOf(`0`, 1 by 2, `0`, `0`, `0`, 1 by 2),         //  S0
                listOf(4 by 9, `0`, `0`, 3 by 9, 2 by 9, `0`),      //  S1
                listOf(`0`, `0`, `1`, `0`, `0`, `0`),               //  S2
                listOf(`0`, `0`, `0`, `1`, `0`, `0`),               //  S3
                listOf(`0`, `0`, `0`, `0`, `1`, `0`),               //  S4
                listOf(`0`, `0`, `0`, `0`, `0`, `1`),               //  S5
            )
        )

        val markovChains = MarkovChains(transitionMatrix)
        val FR = markovChains.findFR()
        assertEquals(FR.rows[0], listOf(`0`, 3 by 14, 1 by 7, 9 by 14))
    }


    private infix fun Int.by(other: Int): Fraction = of(numerator = this, denominator = other)
}



