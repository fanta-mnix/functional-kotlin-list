package collections

fun main(args: Array<String>) {
    val myList = List(1, 2, 3, 4, 5)
    val newList = myList.set(3, 99)
    val first = myList[0]
    val second = myList[1]
    val hugeList = List(1..100)
    val hugeSum = hugeList.sum()
    println("fin")
}