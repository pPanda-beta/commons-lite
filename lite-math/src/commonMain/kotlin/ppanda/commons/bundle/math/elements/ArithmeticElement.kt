package ppanda.commons.bundle.math.elements

import ppanda.commons.bundle.math.groups.BiGroup
import ppanda.commons.bundle.math.groups.Group

//TODO: should be done directly, unnecessary interface. This is equivalent to java Comparable<T>
interface SelfRecursive<T : SelfRecursive<T>> {
    fun self() = this as T
}

interface AdditiveElement<T : AdditiveElement<T>> : SelfRecursive<T> {
    val additiveGroup: Group<T>
    fun add(other: T) = additiveGroup.operation(self(), other)
    fun subtract(other: T) = add(additiveGroup.inverse(other))

    fun additiveInverse() = additiveGroup.inverse(self())

    operator fun plus(other: T) = add(other)
    operator fun minus(other: T) = subtract(other)
}

interface MultiplicativeElement<T : MultiplicativeElement<T>> : SelfRecursive<T> {
    val multiplicativeGroup: Group<T>
    fun multiply(other: T) = multiplicativeGroup.operation(self(), other)
    fun divide(other: T) = multiply(multiplicativeGroup.inverse(other))

    fun multiplicativeInverse() = multiplicativeGroup.inverse(self())

    operator fun times(other: T) = multiply(other)
    operator fun div(other: T) = divide(other)
}

interface ArithmeticElement<T : ArithmeticElement<T>> : AdditiveElement<T>, MultiplicativeElement<T> {
    fun biGroup() = BiGroup.using(additiveGroup, multiplicativeGroup)
}
