package sh.illumi.oss.lib.kraft

import java.io.File

import kotlin.reflect.KClass

annotation class Provide<TResource : Any>(val key: String, val resourceClass: KClass<TResource>)

interface ResourceProvider<TResource> {
    val key: String
}

interface Resource<TResource : Resource<TResource>> {
    var provider: ResourceProvider<TResource>
}