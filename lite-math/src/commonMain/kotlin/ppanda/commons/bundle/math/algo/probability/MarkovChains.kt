package ppanda.commons.bundle.math.algo.probability

import ppanda.commons.bundle.math.constructs.Fraction
import ppanda.commons.bundle.math.constructs.Fraction.Companion.ONE
import ppanda.commons.bundle.math.constructs.Fraction.Companion.ZERO
import ppanda.commons.bundle.math.constructs.SquareMatrix

class MarkovChains(val transitionMat: SquareMatrix<Fraction>) {
    //Based on https://youtu.be/BsOkOaB8SFk?t=472

    fun findFR(): SquareMatrix<Fraction> {
        val isAbsorbing = absorbingStates::contains
        val nonAbsorbingRows = transitionMat.filterRows { _, rowIndex -> !isAbsorbing(rowIndex) }
        val R = nonAbsorbingRows.filterCols { _, columnIndex -> isAbsorbing(columnIndex) }
        val Q = nonAbsorbingRows.filterCols { _, columnIndex -> !isAbsorbing(columnIndex) }
        val I = Q.identityOfSameRowSize()
        val IminusQ = (I - Q).asSquareMatrix()
        val F = IminusQ.inverse()
        val FR = F * R

        return FR.asSquareMatrix()
    }

    val absorbingStates: Set<Int> by lazy {
        (0 until transitionMat.noOfRows)
            .filter { isAbsorbing(transitionMat[it], it) }
            .toSet()
    }


    private fun isAbsorbing(row: List<Fraction>, rowIndex: Int): Boolean {
        if (row.all { it == ZERO }) {
            return true
        }
//TODO: Google's foobar challenge doomsday_fuel fails if we dont stop here
//        return false

// Ideally states pointing only to self is also a type absorbing / terminal state
        val exactlySingleCellIsOne = row.count(ONE::equals) == 1
        return exactlySingleCellIsOne && row[rowIndex] == ONE
    }
}