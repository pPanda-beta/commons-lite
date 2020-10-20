package ppanda.commons.bundle.math.constructs

import ppanda.commons.bundle.math.elements.ArithmeticElement
import ppanda.commons.bundle.math.groups.Group

data class Fraction private constructor(
        val numerator: Int,
        val denominator: Int,
) : ArithmeticElement<Fraction> {
    // TODO: Allow initialization logic to constructor

    override val additiveGroup: Group<Fraction> by lazy { additionFracGroup }
    override val multiplicativeGroup: Group<Fraction> by lazy { multFracGroup }

    override fun toString(): String = "$numerator/$denominator"

    companion object {
        val ZERO = Fraction(0, 1)
        val ONE = Fraction(1, 1)
        val INFINITY = Fraction(1, 0)

        fun of(numerator: Int = 0, denominator: Int = 1): Fraction {
            if (denominator == 0) {
                return INFINITY
            }
            val gcd = gcd(numerator, denominator)
            return Fraction(numerator / gcd, denominator / gcd)
        }
    }
}

private val additionFracGroup = object : Group<Fraction> {
    override val identity: Fraction = Fraction.of(0, 1)
    override fun inverse(x: Fraction) = Fraction.of(0 - (x.numerator), x.denominator)
    override fun operation(x: Fraction, y: Fraction): Fraction {
        val lcm = lcm(x.denominator, y.denominator)
        val part1 = x.numerator * (lcm / x.denominator)
        val part2 = y.numerator * (lcm / y.denominator)
        return Fraction.of(part1 + part2, lcm)
    }
}

private val multFracGroup = object : Group<Fraction> {
    override val identity: Fraction = Fraction.of(1, 1)
    override fun inverse(x: Fraction) = Fraction.of(x.denominator, x.numerator)
    override fun operation(x: Fraction, y: Fraction) = Fraction.of(
            x.numerator * y.numerator,
            x.denominator * y.denominator
    )
}

private fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

private fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b

