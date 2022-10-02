package de.hglabor.common.automation

class UpdatingProperty<T>(var callback: () -> T) {
    constructor(value: T) : this({ value }) {
        shouldUpdate = false
        forceUpdate = false
    }

    var value: T = callback()

    var shouldUpdate = true
    var forceUpdate = false

    fun get(): T {
        if (shouldUpdate || forceUpdate) {
            value = callback()
            forceUpdate = false
        }
        return value
    }

    fun set(newValue: () -> T) {
        callback = newValue
        shouldUpdate = true
        forceUpdate = false
    }

    fun set(newValue: T) {
        callback = { newValue }
        shouldUpdate = false
        forceUpdate = true
    }
}