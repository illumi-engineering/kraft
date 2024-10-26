package sh.illumi.oss.lib.kraft

import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

abstract class ServiceScope<TScope : ServiceScope<TScope>> {
    abstract val coroutineScope: CoroutineScope
    abstract val depth: Int
    abstract val handle: Int

    private val services = mutableMapOf<String, Service<*>>()

    val expectedParamsLength: Int
        get() = depth + 2

    inline fun <reified TService : Service<*>> instantiateService(): TService =
        getConstructor<TService>()
            .call(
                coroutineScope,
                *getScopeLayerStack().toTypedArray()
            )

    inline fun <reified TService : Service<*>> getConstructor(): KFunction<TService> =
        TService::class.constructors.firstOrNull {
            if (it.parameters.size != expectedParamsLength) return@firstOrNull false

            for ((index, parameter) in it.parameters.withIndex()) {
                when (index) {
                    0 -> if (parameter.type.classifier != CoroutineScope::class) return@firstOrNull false
                    else -> if (parameter.type.classifier != getClassForIndex(index)) return@firstOrNull false
                }
            }

            true
        } ?: throw ServiceHasNoSuitableConstructorException(this)

    fun getClassForIndex(index: Int): KClass<out ServiceScope<*>> =
        if (index == 0) this.javaClass.kotlin
        else if (this is ScopeWithParent<*>) this.parentScope.getClassForIndex(index - 1)
        else throw KraftException("ServiceScope does not have a parent") // todo: better error reporting & handling

    fun getScopeLayerStack(): List<ServiceScope<*>> {
        val scopeStack = mutableListOf<ServiceScope<*>>()
        var currentScope: ServiceScope<*> = this

        while (currentScope is ScopeWithParent<*>) {
            scopeStack += currentScope
            currentScope = currentScope.parentScope
        }

        // todo: maybe there's a better way to do this?
        if (currentScope is RootLayerScope<*>) scopeStack += currentScope
        else throw KraftException("ServiceScope does not have a root layer!")

        return scopeStack.reversed() // the root layer should be at the start of the list
    }


    fun registerService(service: Service<*>) {
        val annotation = ServiceMetadata.resolveAnnotation(service.javaClass.kotlin, this)
        services[annotation.key] = service
    }

    fun getService(serviceKey: String): Service<*>? = services[serviceKey]

    inline fun <reified TService : Service<*>> service(): TService {
        val annotation = ServiceMetadata.resolveAnnotation(TService::class, this)

        return getService(annotation.key) as? TService
            ?: instantiateService<TService>().also { registerService(it) }
    }

    companion object {
        const val ROOT_DEPTH = 0
        const val ROOT_HANDLE = 0
        private const val MAX_DEPTH = 16 // a depth of 16 layers should be more than enough for any practical use case
        private const val MAX_HANDLES: Int = Int.MAX_VALUE
        private var nextHandle: Int = ROOT_HANDLE + 1

        fun nextHandle(): Int {
            if (nextHandle - 1 == MAX_HANDLES) {
                throw KraftException("ServiceScope handles exhausted")
            }

            val handle = nextHandle
            nextHandle++

            return handle
        }
    }
}

interface ScopeWithParent<TParentScope : ServiceScope<TParentScope>> {
    val parentScope: TParentScope
}

interface ScopeWithChildren<TChildScope : ServiceScope<TChildScope>> {
    var activeChildScope: TChildScope
}

abstract class RootLayerScope<TChildScope : ServiceScope<TChildScope>>(
    override val coroutineScope: CoroutineScope
) : ServiceScope<RootLayerScope<TChildScope>>(),
    ScopeWithChildren<TChildScope>
{
    override val depth: Int = ROOT_DEPTH
    override val handle: Int = ROOT_HANDLE
}

abstract class MidLayerScope<
    TParentScope : ServiceScope<TParentScope>,
    TChildScope : ServiceScope<TChildScope>
> (
    final override val parentScope: TParentScope,
    override val coroutineScope: CoroutineScope
) : ServiceScope<MidLayerScope<TParentScope, TChildScope>>(),
    ScopeWithParent<TParentScope>,
    ScopeWithChildren<TChildScope>
{
    override val depth: Int = parentScope.depth + 1
    override val handle: Int = nextHandle()
}

abstract class LeafLayerScope<TParentScope : ServiceScope<TParentScope>>(
    final override val parentScope: TParentScope,
    override val coroutineScope: CoroutineScope
) : ServiceScope<LeafLayerScope<TParentScope>>(),
    ScopeWithParent<TParentScope>
{
    override val depth: Int = parentScope.depth + 1
    override val handle: Int = nextHandle()
}