package sh.illumi.oss.lib.kraft.resource

interface Resource<
    TResource : Resource<TResource, ResourceProvider<TResource>>,
    TProvider : ResourceProvider<TResource>
> {
    var provider: TProvider
}
