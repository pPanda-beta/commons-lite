package ppanda.commons.bundle.math.elements

import ppanda.commons.bundle.math.groups.BiGroup
import ppanda.commons.bundle.math.groups.BiGroups
import ppanda.commons.bundle.math.groups.Group
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class Primitive<T : Any>(val value: T, val kType: KType) : ArithmeticElement<Primitive<T>> {
    private val biGroupOfT = BiGroups.getGroupOf<T>(kType)

    override val additiveGroup: Group<Primitive<T>> by lazy {
        primitiveGroupUsing(biGroupOfT.additiveGroup, kType)
    }
    override val multiplicativeGroup: Group<Primitive<T>> by lazy {
        primitiveGroupUsing(biGroupOfT.multiplicativeGroup, kType)
    }

    override fun toString(): String = value.toString()

    @ExperimentalStdlibApi
    companion object {
        inline fun <reified E : Any> of(e: E) = Primitive(e, typeOf<E>())

        inline fun <reified E : Any> biGroup(): BiGroup<Primitive<E>> =
            primitiveGroupUsing(BiGroups.getGroupOfT(), typeOf<E>(), typeOf<Primitive<E>>())

        init {
            installAllPrimitiveGroups()
        }
    }
}

fun <E : Any> primitiveGroupUsing(groupOfE: Group<E>, kType: KType): Group<Primitive<E>> =
    object : Group<Primitive<E>> {
        override val identity = Primitive(groupOfE.identity, kType)
        override fun inverse(x: Primitive<E>) = Primitive(groupOfE.inverse(x.value), kType)
        override fun operation(x: Primitive<E>, y: Primitive<E>) =
            Primitive(groupOfE.operation(x.value, y.value), kType)
    }

fun <E : Any> primitiveGroupUsing(
    groupOfE: BiGroup<E>, kType: KType, primitiveKType: KType
): BiGroup<Primitive<E>> = BiGroups.getOrComputeGroupOf(primitiveKType) {
    BiGroup.using(
        primitiveGroupUsing(groupOfE.additiveGroup, kType),
        primitiveGroupUsing(groupOfE.multiplicativeGroup, kType)
    )
}


@ExperimentalStdlibApi
fun installAllPrimitiveGroups() {
    BiGroups.installGroupOf(BiGroup.create(0, { -it }, Int::plus, 1, { 1 / it }, Int::times))
    BiGroups.installGroupOf(BiGroup.create(0.0, { -it }, Double::plus, 1.0, { 1.0 / it }, Double::times))
    BiGroups.installGroupOf(BiGroup.create(0.0f, { -it }, Float::plus, 1.0f, { 1.0f / it }, Float::times))
    BiGroups.installGroupOf(BiGroup.create(0L, { -it }, Long::plus, 1L, { 1L / it }, Long::times))
}