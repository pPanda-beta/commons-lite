package ppanda.commons.bundle.math.elements

import ppanda.commons.bundle.math.groups.BiGroup
import ppanda.commons.bundle.math.groups.BiGroups
import ppanda.commons.bundle.math.groups.Group
import kotlin.reflect.KClass

data class Primitive<T : Any>(val value: T, val clazz: KClass<T>) : ArithmeticElement<Primitive<T>> {
    private val biGroup = BiGroups.getGroupOf(clazz)

    override val additiveGroup: Group<Primitive<T>>
        get() = primitiveGroupUsing(biGroup.additiveGroup, clazz)
    override val multiplicativeGroup: Group<Primitive<T>>
        get() = primitiveGroupUsing(biGroup.multiplicativeGroup, clazz)

    override fun toString(): String = value.toString()

    companion object {
        inline fun <reified E : Number> of(e: E) = Primitive(e, E::class)

        inline fun <reified E : Number> biGroup() =
            primitiveGroupUsing(BiGroups.getGroupOfT(), E::class)

        init {
            installAllPrimitiveGroups()
        }
    }
}

fun <E : Any> primitiveGroupUsing(groupOfE: Group<E>, clazz: KClass<E>): Group<Primitive<E>> =
    object : Group<Primitive<E>> {
        override val identity = Primitive(groupOfE.identity, clazz)
        override fun inverse(x: Primitive<E>) = Primitive(groupOfE.inverse(x.value), clazz)
        override fun operation(x: Primitive<E>, y: Primitive<E>) =
            Primitive(groupOfE.operation(x.value, y.value), clazz)
    }

fun <E : Any> primitiveGroupUsing(groupOfE: BiGroup<E>, clazz: KClass<E>): BiGroup<Primitive<E>> = BiGroup.using(
    primitiveGroupUsing(groupOfE.additiveGroup, clazz),
    primitiveGroupUsing(groupOfE.multiplicativeGroup, clazz)
)

fun installAllPrimitiveGroups() {
    BiGroups.installGroupOf(BiGroup.create(0, { -it }, Int::plus, 1, { 1 / it }, Int::times))
    BiGroups.installGroupOf(BiGroup.create(0.0, { -it }, Double::plus, 1.0, { 1.0 / it }, Double::times))
    BiGroups.installGroupOf(BiGroup.create(0.0f, { -it }, Float::plus, 1.0f, { 1.0f / it }, Float::times))
    BiGroups.installGroupOf(BiGroup.create(0L, { -it }, Long::plus, 1L, { 1L / it }, Long::times))
}