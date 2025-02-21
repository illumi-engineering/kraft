package sh.illumi.kraft.service

import sh.illumi.kraft.layer.ApplicationLayer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

data class ServiceKey(
    val serviceClass: KClass<Service>,
    val layers: List<KClass<ApplicationLayer>>,
)