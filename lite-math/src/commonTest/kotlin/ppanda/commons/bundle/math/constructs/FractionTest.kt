package ppanda.commons.bundle.math.constructs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FractionTest {
    @Test
    fun shouldNotNullGroups() { // TODO: bug in kotlin

        val x = Fraction.of(1, 2)
        assertNotNull(x.multiplicativeGroup)
        assertEquals(x.multiplicativeGroup.identity, Fraction.ONE)

        assertNotNull(Fraction.ZERO.multiplicativeGroup.identity.multiplicativeGroup)
        assertNotNull(Fraction.ZERO.multiplicativeGroup.identity.additiveGroup)
        assertNotNull(Fraction.ZERO.additiveGroup.identity.multiplicativeGroup)
        assertNotNull(Fraction.ZERO.additiveGroup.identity.additiveGroup)
    }

    @Test
    fun shouldHaveGroupIdentityFeatures() {
        val _3by4 = Fraction.of(3, 4)
        assertEquals(_3by4 + Fraction.ZERO, _3by4)
        assertEquals(_3by4 * Fraction.ONE, _3by4)
    }

    @Test
    fun shouldHaveGroupInverseFeatures() {
        val _5by4 = Fraction.of(5, 4)
        val _4by5 = Fraction.of(4, 5)
        val minus5by4 = Fraction.of(-5, 4)

        assertEquals(_5by4.additiveInverse(), minus5by4)
        assertEquals(_5by4.multiplicativeInverse(), _4by5)
    }

    @Test
    fun shouldAddTwoFractions() {
        val _3by4 = Fraction.of(3, 4)
        val _7by4 = Fraction.of(7, 4)
        val _1by4 = Fraction.of(1, 4)

        assertEquals(_3by4 + Fraction.ONE, _7by4)
        assertEquals(Fraction.ONE - _3by4, _1by4)
    }
}

