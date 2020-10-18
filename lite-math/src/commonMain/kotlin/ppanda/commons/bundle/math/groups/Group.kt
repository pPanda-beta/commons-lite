package ppanda.commons.bundle.math.groups

import kotlin.reflect.KClass


interface Group<T> {
    val identity: T
    fun operation(x: T, y: T): T
    fun inverse(x: T): T

    companion object {
        fun <E> using(identity: E, inverseOp: (E) -> E, operation: (E, E) -> E) = object : Group<E> {
            override val identity = identity
            override fun operation(x: E, y: E): E = operation(x, y)
            override fun inverse(x: E): E = inverseOp(x)
        }
    }
}

interface BiGroup<T> {
    val additiveGroup: Group<T>
    val multiplicativeGroup: Group<T>

    companion object {
        fun <E> using(additiveGroup: Group<E>, multiplicativeGroup: Group<E>) = object : BiGroup<E> {
            override val additiveGroup: Group<E> = additiveGroup
            override val multiplicativeGroup: Group<E> = multiplicativeGroup
        }

        fun <E> create(
            additiveIdentity: E, additiveInverseOp: (E) -> E, additionOp: (E, E) -> E,
            multiplicativeIdentity: E, multiplicativeInverseOp: (E) -> E, multiplicationOp: (E, E) -> E
        ): BiGroup<E> = using(
            Group.using(additiveIdentity, additiveInverseOp, additionOp),
            Group.using(multiplicativeIdentity, multiplicativeInverseOp, multiplicationOp)
        )
    }
}


object BiGroups {
    val map: MutableMap<KClass<Any>, BiGroup<Any>> = mutableMapOf()
    inline fun <reified T : Any> getGroupOfT(): BiGroup<T> = getGroupOf(T::class)

    inline fun <reified T : Any> installGroupOf(group: BiGroup<T>) = setGroupOfClazz(T::class, group)

    fun <T : Any> getGroupOf(kClass: KClass<T>) = map[kClass as KClass<Any>]!! as BiGroup<T>

    fun <T : Any> setGroupOfClazz(kClass: KClass<T>, group: BiGroup<T>) =
        map.put(kClass as KClass<Any>, group as BiGroup<Any>)
}

