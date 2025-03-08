package sh.illumi.kraft.service

import kotlin.reflect.KClass

class ServiceKey(vararg tags: Tag<*>) {
    val tags = tags.toList()

    override fun equals(other: Any?): Boolean {
        if (other !is ServiceKey) return false
        return tags == other.tags
    }
    
    sealed interface Tag<T : Any> {
        val tag: String
        val value: T
        
        class Accessor(override val value: ServiceAccessor) : Tag<ServiceAccessor> {
            override val tag = "accessor"
        }
        
        class ServiceClass(override val value: KClass<out Service>) : Tag<KClass<out Service>> {
            override val tag = "serviceClass"
        }
    }

    override fun hashCode(): Int {
        return tags.hashCode()
    }
}