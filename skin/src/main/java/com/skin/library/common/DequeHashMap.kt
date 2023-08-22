package com.skin.library.common

class DequeHashMap<K, V> {
    private val keys = ArrayDeque<K>()
    private val map = hashMapOf<K, V>()

    fun putFirst(key: K, value: V) {
        keys.addFirst(key)
        map[key] = value
    }

    fun putLast(key: K, value: V) {
        keys.addLast(key)
        map[key] = value
    }

    fun removeFirst(): V? {
        return map.remove(keys.removeFirstOrNull())
    }

    fun removeLast(): V? {
        return map.remove(keys.removeLastOrNull())
    }

    fun first(): V? {
        return map[keys.firstOrNull()]
    }

    fun last(): V? {
        return map[keys.lastOrNull()]
    }

    fun get(key: K): V? {
        return map[key]
    }

    fun isEmpty(): Boolean {
        return keys.isEmpty()
    }

    fun forEach(action: (Entry<K, V>) -> Unit): Unit {
        for (element in keys) action(Entry(element, map[element]!!))
    }

    companion object {
        class Entry<K, V>(var key: K, var value: V) {

        }
    }
}