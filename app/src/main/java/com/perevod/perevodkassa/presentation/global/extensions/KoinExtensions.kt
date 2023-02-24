package com.perevod.perevodkassa.presentation.global.extensions

import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
inline fun <reified T : Any> Koin.createClassScope(): Scope {
    return createScope(
        scopeId = T::class.getFullName(),
        qualifier = named(name = T::class.getFullName())
    )
}