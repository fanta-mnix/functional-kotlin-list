package collections

import java.util.*

interface List<out T> : Iterable<T> {
    val isEmpty: Boolean
    val value: T
    val size: Int
    val left: List<T>
    val right: List<T>

    operator fun get(index: Int): T

    companion object {
        operator fun <T> invoke(vararg values: T): List<T> = List(values.asIterable())
        operator fun <T> invoke(values: Iterable<T>): List<T> = Empty.addAll(values)
    }
}

class Some<out T>(override val value: T,
                  override val left: List<T> = Empty,
                  override val right: List<T> = Empty) : List<T> {

    override val size: Int = 1 + left.size + right.size

    override val isEmpty: Boolean
        get() = false

    override fun get(index: Int): T {
        assertIndex(index)

        fun follow(directions: String): List<T> {
            var current: List<T> = this
            for (direction in directions) {
                current = if (direction == 'l') current.left else current.right
            }
            return current
        }
        return follow(getPath(index)).value
    }

    fun getPath(index: Int): String {
        assertIndex(index, Int::rangeTo)

        val directions = StringBuilder()
        fun iter(index: Int): StringBuilder {
            fun nextDirection(index: Int): Char = if (index % 2 == 0) 'r' else 'l'
            return when (index) {
                0 -> directions
                else -> iter((index - 1) / 2).append(nextDirection(index))
            }
        }
        return iter(index).toString()
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {

        private val queue: ArrayDeque<List<T>> = ArrayDeque(listOf(this@Some))

        override fun hasNext(): Boolean = queue.isNotEmpty()

        override fun next(): T = queue.poll().apply {
            if (left is Some) {
                queue.add(left)
                if (right is Some) queue.add(right)
            }
        }.value
    }
}

object Empty : List<Nothing> {

    override val isEmpty: Boolean
        get() = true

    override val size: Int
        get() = 0

    override fun iterator(): Iterator<Nothing> = object : Iterator<Nothing> {
        override fun hasNext(): Boolean = false
        override fun next(): Nothing = throw NoSuchElementException()
    }

    override val value: Nothing
        get() = throw NoSuchElementException()
    override val left: List<Nothing>
        get() = this
    override val right: List<Nothing>
        get() = this

    override fun get(index: Int): Nothing = throw NoSuchElementException()
}

internal fun <T> List<T>.assertIndex(index: Int, rangeOp: (Int, Int) -> IntRange = Int::until): Int {
    if (index !in rangeOp(0, size)) throw IndexOutOfBoundsException()
    return index
}

fun <T> List<T>.set(index: Int, newValue: T): List<T> {
    if (index == 0) return Some(newValue, left, right)

    assertIndex(index, Int::rangeTo)

    val path = (this as Some).getPath(index)
    fun replace(list: List<T>, current: Int): List<T> =
            if (current == path.length) Some(newValue, list.left, list.right)
            else when (path[current]) {
                'l' -> Some(list.value, replace(list.left, current + 1), list.right)
                'r' -> Some(list.value, list.left, replace(list.right, current + 1))
                else -> throw AssertionError()
            }

    return replace(this, 0)
}

fun <T> List<T>.add(newValue: T): List<T> = set(size, newValue)
fun <T> List<T>.addAll(elements: Iterable<T>): List<T> = elements.fold(this) { acc, x -> acc.add(x) }
fun <T> List<T>.prepend(newValue: T): List<T> =
    if (this is Empty) Some(newValue)
    else Some(newValue).addAll(this)