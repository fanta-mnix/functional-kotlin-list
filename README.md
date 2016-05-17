# Immutable List in Kotlin
Immutable list in Kotlin implemented as a binary tree.
The goals is to reduce complexity of random access reads and updates when compared to the traditional linked list.

### Advantages
Read: O(log n)
Update: O(log n)
Insert: O(n)

Object allocation is also reduced, as the nodes replaced in the update operation are only the ones in the path of change.
