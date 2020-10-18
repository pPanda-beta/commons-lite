package ppanda.commons.bundle.math.constructs

import ppanda.commons.bundle.math.elements.Primitive
import ppanda.commons.bundle.math.elements.Primitive.Companion.of
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
class MatrixTest {
    @Test
    fun shouldBeAbleToCreateIdentityMatrices() {
        val I3 = SquareMatrix.identity(3, Primitive.biGroup<Double>())

        assertEquals(
            I3.toString(), """
            1.0 0.0 0.0
            0.0 1.0 0.0
            0.0 0.0 1.0
        """.trimIndent()
        )
    }

    @Test
    fun shouldHaveIdentityProperty() {
        val I3 = SquareMatrix.identity(3, Primitive.biGroup<Double>())
        val matrix = primitiveMatrix(
            listOf(1.2, 0.0, 3.4),
            listOf(0.0, 0.0, 8.9),
            listOf(7.0, 6.5, 0.0),
        )

        val expected = matrix.multiplyBy(I3)
        assertEquals(expected, matrix)
    }

    @Test
    fun shouldSupportMatrixOfMatrix() {
        val I2 = SquareMatrix.identity(2, Primitive.biGroup<Int>())
        val _I3I2 = SquareMatrix.identity(3, I2.biGroup())

        assertEquals(_I3I2[0][0], _I3I2[1][1])
        assertEquals(_I3I2[0][0], _I3I2[2][2])
        assertEquals(_I3I2[1][0][0][0].value, 0)
    }


    @Test
    fun shouldSupportMatrixMultiplication() {
        val a = primitiveMatrix(
            listOf(8, 1, 2),
            listOf(-5, 6, 7),
        )
        val b = primitiveMatrix(
            listOf(-5, 1),
            listOf(0, 2),
            listOf(-11, 7),
        )
        val expectedResult = primitiveMatrix(
            listOf(-62, 24),
            listOf(-52, 56),
        )

        val actualResult = a * b

        assertEquals(actualResult, expectedResult)
    }

    private inline fun <reified T : Any> primitiveMatrix(vararg rows: List<T>) =
        Matrix(rows.map { row -> row.map { of(it) } })
}

