package ppanda.commons.bundle.math.groups


interface Group<T> {
    val identity: T
    fun operation(x: T, y: T): T
    fun inverse(x: T): T
}

interface BiGroup<T> {
    val additiveGroup: Group<T>
    val multiplicativeGroup: Group<T>
}


