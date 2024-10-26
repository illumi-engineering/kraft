package sh.illumi.oss.lib.kraft

import kotlin.reflect.KClass

open class KraftException(message: String) : Exception(message)

open class ServiceContainerException(open val serviceContainer: ServiceScope<*>, message: String)
    : KraftException("ServiceContainer[handle=${serviceContainer.depth}]: $message")

class ServiceHasNoSuitableConstructorException(override val serviceContainer: ServiceScope<*>)
    : ServiceContainerException(serviceContainer, "Service has no suitable constructor. TODO: provide expected constructor signature in exception message")

class ServiceMissingRegisterAnnotationException(serviceKlass: KClass<out Service<*>>, override val serviceContainer: ServiceScope<*>)
    : ServiceContainerException(serviceContainer, "Service missing @RegisterService annotation: ${serviceKlass.simpleName}")