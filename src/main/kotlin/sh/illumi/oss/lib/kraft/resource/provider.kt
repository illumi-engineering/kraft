package sh.illumi.oss.lib.kraft.resource


interface ResourceProvider<TResource : Resource<TResource, ResourceProvider<TResource>>>

interface SingletonResourceProvider<
    TResource : Resource<TResource, ResourceProvider<TResource>>
> : ResourceProvider<TResource> {
    fun get(): TResource
}
