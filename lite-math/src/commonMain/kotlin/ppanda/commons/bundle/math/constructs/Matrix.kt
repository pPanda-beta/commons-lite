package ppanda.commons.bundle.math.constructs

import ppanda.commons.bundle.math.elements.ArithmeticElement
import ppanda.commons.bundle.math.groups.BiGroup
import ppanda.commons.bundle.math.groups.Group

open class Matrix<T : ArithmeticElement<T>>(
    val rows: List<List<T>>
) : ArithmeticElement<Matrix<T>> {

    val noOfRows: Int = rows.size
    val noOfCols: Int = rows.get(0).size

    override val additiveGroup: Group<Matrix<T>> by lazy {
        Group.using(zeroOfSameSize(), { TODO() }, Matrix<T>::addWith)
    }
    override val multiplicativeGroup: Group<Matrix<T>> by lazy {
        Group.using(identityOfSameColSize(), { TODO() }, Matrix<T>::multiplyBy)
    }

    fun addWith(other: Matrix<T>): Matrix<T> = generateOfSize(noOfRows, noOfCols) { i, j -> this[i][j] + other[i][j] }

    fun multiplyBy(other: Matrix<T>): Matrix<T> = generateOfSize(noOfRows, other.noOfCols) { i, j ->
        (0 until noOfCols)
            .map { k -> this[i][k] * other[k][j] }
            .reduce(ArithmeticElement<T>::add)
    }

    fun identityOfSameRowSize() = SquareMatrix.identity(noOfRows, this[0][0].biGroup())
    fun identityOfSameColSize() = SquareMatrix.identity(noOfCols, this[0][0].biGroup())
    fun zeroOfSameSize() = zero<T>(noOfRows, noOfCols, this[0][0].biGroup())

    fun getRow(i: Int) = rows[i]
    fun getColumn(j: Int) = rows.map { it[j] }
    operator fun get(i: Int) = getRow(i)

    fun filterRows(predicate: ((Iterable<T>, Int) -> Boolean)) = Matrix(
        rows.withIndex()
            .filter { predicate(it.value, it.index) }
            .map { it.value }
    )

    //TODO: Need to refactor
    fun filterCols(predicate: ((Sequence<T>, Int) -> Boolean)) = Matrix(
        (0 until noOfCols)
            .map(::getColumnSeq)
            .withIndex()
            .filter { predicate(it.value, it.index) }
            .map { it.index }
            .toSet()
            .let { filteredColIndices ->
                rows.map { eachRow ->
                    eachRow
                        .withIndex()
                        .filter { it.index in filteredColIndices }
                        .map { it.value }
                }
            }
    )


    private fun getColumnSeq(j: Int) = rows.asSequence().map { it[j] }


    override fun toString(): String = rows.joinToString(separator = "\n") { row -> row.joinToString(separator = " ") }

    override fun hashCode(): Int = rows.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Matrix<*>

        if (rows != other.rows) return false

        return true
    }

    companion object {
        fun <E : ArithmeticElement<E>> generateOfSize(noOfRows: Int, noOfCols: Int, generator: ((Int, Int) -> E)) =
            Matrix(generateRows(noOfRows, noOfCols, generator))

        fun <E : ArithmeticElement<E>> zero(noOfRows: Int, noOfCols: Int, biGroup: BiGroup<E>) =
            generateOfSize(noOfRows, noOfCols) { _, _ -> biGroup.additiveGroup.identity }
    }
}


open class SquareMatrix<T : ArithmeticElement<T>>(rows: List<List<T>>) : Matrix<T>(rows) {
    val size = noOfRows

    fun identityOfSameDimension() = identity(size, this[0][0].biGroup())

    companion object {
        fun <E : ArithmeticElement<E>> identity(size: Int, biGroup: BiGroup<E>) = SquareMatrix(
            generateRows(size, size) { i, j ->
                if (i == j) biGroup.multiplicativeGroup.identity else biGroup.additiveGroup.identity
            })
    }
}


private fun <E : ArithmeticElement<E>> generateRows(
    noOfRows: Int,
    noOfCols: Int,
    generator: (Int, Int) -> E
): List<List<E>> =
    (0 until noOfRows)
        .map { rowIndex ->
            (0 until noOfCols)
                .map { colIndex -> generator(rowIndex, colIndex) }
        }
