package net.azeti.recipesharing.core.extensions

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
