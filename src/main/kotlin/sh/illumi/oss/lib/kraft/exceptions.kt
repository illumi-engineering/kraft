package sh.illumi.oss.lib.kraft

import kotlin.reflect.KClass

import sh.illumi.oss.lib.kraft.service.Service
import sh.illumi.oss.lib.kraft.service.ServiceContainer

/**
 * Base exception for Kraft exceptions
 *
 * @param message The exception message
 */
open class KraftException(message: String) : Exception(message)

/**
 * Base exception for service container exceptions
 *
 * @param serviceContainer The service container that the exception occurred in
 * @param message The exception message
 */
open class ServiceContainerException(open val serviceContainer: ServiceContainer, message: String)
    : KraftException("ServiceContainer: $message")

/**
 * Exception thrown when a service has no suitable constructor
 * TODO: provide expected constructor signature in exception message
 *
 * @param serviceContainer The service container that the service is missing from
 */
class ServiceHasNoSuitableConstructorException(override val serviceContainer: ServiceContainer)
    : ServiceContainerException(serviceContainer, "Service has no suitable constructor. TODO: provide expected constructor signature in exception message")

/**
 * Exception thrown when a service is missing the [ServiceMetadata][sh.illumi.oss.lib.kraft.service.ServiceMetadata] annotation
 *
 * @param serviceClass The service class that is missing the annotation
 * @param serviceContainer The service container that the service is missing from
 */
class ServiceMissingMetadataException(serviceClass: KClass<out Service<*>>, override val serviceContainer: ServiceContainer)
    : ServiceContainerException(serviceContainer, "Service missing @RegisterService annotation: ${serviceClass.simpleName}")