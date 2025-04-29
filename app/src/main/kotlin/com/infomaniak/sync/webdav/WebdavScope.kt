/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.webdav

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class WebdavScoped

@WebdavScoped
@DefineComponent(parent = SingletonComponent::class)
interface WebdavComponent

@DefineComponent.Builder
interface WebdavComponentBuilder {
    fun build(): WebdavComponent
}
