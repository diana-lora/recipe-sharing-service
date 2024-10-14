package net.azeti.recipe.extensions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun expectTrueOr(
    value: Boolean,
    function: () -> Any,
) {
    contract {
        returns() implies value
    }
    if (!value) {
        function()
    }
}

fun <E : Any, T : List<E>> T?.ifNotEmpty(func: (List<E>) -> E): E? {
    if (!this.isNullOrEmpty()) {
        return func(this)
    }
    return null
}
