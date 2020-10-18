package ppanda.commons.bundle.math.constructs

import ppanda.commons.bundle.math.elements.ArithmeticElement
import ppanda.commons.bundle.math.groups.BiGroup
import ppanda.commons.bundle.math.groups.Group

//TODO: should be AdditiveElement only
open class Matrix<T : ArithmeticElement<T>>(
    val rows: List<List<T>>
) : ArithmeticElement<Matrix<T>> {

    val noOfRows: Int = rows.size
    val noOfCols: Int = rows.get(0).size

    override val additiveGroup: Group<Matrix<T>> by lazy {
        Group.using(zeroOfSameSize(), { it.scalarMultiply(minusOneOfT) }, Matrix<T>::addWith)
    }
    override val multiplicativeGroup: Group<Matrix<T>> by lazy {
        Group.using(identityOfSameColSize(), { TODO() }, Matrix<T>::multiplyBy)
    }

    protected val biGroupOfT: BiGroup<T> by lazy { this[0][0].biGroup() }
    protected val plusOneOfT: T by lazy { biGroupOfT.multiplicativeGroup.identity }
    protected val minusOneOfT: T by lazy { plusOneOfT.additiveInverse() }
    protected val zeroOfT: T by lazy { biGroupOfT.additiveGroup.identity }

    fun addWith(other: Matrix<T>): Matrix<T> = generateOfSize(noOfRows, noOfCols) { i, j -> this[i][j] + other[i][j] }

    fun multiplyBy(other: Matrix<T>): Matrix<T> = generateOfSize(noOfRows, other.noOfCols) { i, j ->
        (0 until noOfCols)
            .map { k -> this[i][k] * other[k][j] }
            .reduce(ArithmeticElement<T>::add)
    }

    fun scalarMultiply(scalar: T): Matrix<T> = map { t -> t * scalar }

    fun identityOfSameRowSize() = SquareMatrix.identity(noOfRows, biGroupOfT)
    fun identityOfSameColSize() = SquareMatrix.identity(noOfCols, biGroupOfT)
    fun zeroOfSameSize() = zero<T>(noOfRows, noOfCols, biGroupOfT)


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


    open fun transpose(): Matrix<T> = generateOfSize(noOfCols, noOfRows) { i, j -> this[j][i] }

    // TODO: Needs optimization, should be under O(m*n)
    open fun getCofactor(i: Int, j: Int): Matrix<T> =
        filterRows { row, rowIndex -> rowIndex != i }
            .filterCols { col, colIndex -> colIndex != j }


    open fun determinant(): T {
        if (noOfRows == 1 && noOfCols == 1) {
            return this[0][0]
        }
        var determinant: T = zeroOfT // Ideally zero
        var sign: T = plusOneOfT // Ideally equivalent of +1
        val firstRow = getRow(0)
        for (columnIndex in firstRow.indices) {
            val cell = firstRow[columnIndex]
            val determinantOfCoFactor = getCofactor(0, columnIndex)
                .determinant()
            determinant += sign * cell * determinantOfCoFactor
            sign = sign.additiveInverse()
        }
        return determinant
    }

    protected open fun <U : ArithmeticElement<U>> map(mapper: (T) -> U): Matrix<U> = map { t, _, _ -> mapper(t) }

    protected open fun <U : ArithmeticElement<U>> map(mapper: (T, Int, Int) -> U): Matrix<U> =
        generateOfSize(noOfRows, noOfCols) { i, j -> mapper(this[i][j], i, j) }

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

//TODO: All ArithmeticElement functions should return SquareMatrix<T> instead of Matrix<T>
open class SquareMatrix<T : ArithmeticElement<T>>(rows: List<List<T>>) : Matrix<T>(rows) {
    val size = noOfRows

    override val multiplicativeGroup: Group<Matrix<T>> by lazy {
        Group.using(identityOfSameColSize(), { (it as SquareMatrix<T>).inverse() }, Matrix<T>::multiplyBy)
    }

    fun identityOfSameDimension() = identity(size, biGroupOfT)


    open fun adjoint(): SquareMatrix<T> {
        if (noOfRows == 1 && noOfCols == 1) {
            return SquareMatrix(listOf(listOf(this[0][0])))
        }
        return map { _, rowIndex: Int, columnIndex: Int ->
            val sign: T = if ((rowIndex + columnIndex) % 2 == 0) plusOneOfT else minusOneOfT
            val cofactor = getCofactor(rowIndex, columnIndex)
            sign * cofactor.determinant()
        }   // TODO:  Refactor to improve performance, use functions to generate transposed matrix
            .transpose()
    }

    open fun inverse(): SquareMatrix<T> {
        val determinant = determinant()
        if (determinant == zeroOfT) {
            throw ArithmeticException("Matrix \n$this \n has  a determinant equivalent to zero, i.e. $zeroOfT")
        }
        return adjoint().map { eachCell: T -> eachCell / determinant }
    }

    // TODO: Apply DRY for next 3
    override fun transpose(): SquareMatrix<T> = generateOfSize(size) { i, j -> this[j][i] }

    override fun <U : ArithmeticElement<U>> map(mapper: (T) -> U): SquareMatrix<U> =
        map { t, _, _ -> mapper(t) }

    override fun <U : ArithmeticElement<U>> map(mapper: (T, Int, Int) -> U): SquareMatrix<U> =
        generateOfSize(size) { i, j -> mapper(this[i][j], i, j) }

    companion object {
        fun <E : ArithmeticElement<E>> generateOfSize(size: Int, generator: ((Int, Int) -> E)) =
            SquareMatrix(generateRows(size, size, generator))

        fun <E : ArithmeticElement<E>> identity(size: Int, biGroup: BiGroup<E>) = generateOfSize(size) { i, j ->
            if (i == j) biGroup.multiplicativeGroup.identity else biGroup.additiveGroup.identity
        }
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
