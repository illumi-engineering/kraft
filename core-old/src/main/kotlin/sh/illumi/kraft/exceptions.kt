package sh.illumi.kraft

import sh.illumi.kraft.service.ServiceContainer

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
    : KraftException(message)