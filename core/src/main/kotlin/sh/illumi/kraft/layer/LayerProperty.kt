package sh.illumi.kraft.layer

import kotlin.reflect.KProperty

interface LayerProperty<TLayer : ApplicationLayer<*>, TProperty> {
    operator fun getValue(thisRef: TLayer, property: KProperty<*>?): TProperty
    operator fun setValue(thisRef: TLayer, property: KProperty<*>?, value: TProperty)
}

fun <TLayer : ApplicationLayer<TLayer>, TProperty> lazyProperty(initializer: TLayer.() -> TProperty) =
    object : LayerProperty<TLayer, TProperty> {
        private var value: TProperty? = null

        override fun getValue(thisRef: TLayer, property: KProperty<*>?): TProperty {
            if (value == null) value = thisRef.initializer()
            return value!!
        }

        override fun setValue(thisRef: TLayer, property: KProperty<*>?, value: TProperty) {
            this.value = value
        }
    }

fun <TLayer : RootLayer<TLayer>, TProperty> lazyProperty(initializer: TLayer.() -> TProperty) =
    // fixme: remove duplicate. there's some weird issue with the kotlin type
    //  system that makes this necessary if i pass a RootLayer type to something
    //  that expects an ApplicationLayer type, it doesn't compile
    object : LayerProperty<TLayer, TProperty> {
        private var value: TProperty? = null

        override fun getValue(thisRef: TLayer, property: KProperty<*>?): TProperty {
            if (value == null) value = thisRef.initializer()
            return value!!
        }

        override fun setValue(thisRef: TLayer, property: KProperty<*>?, value: TProperty) {
            this.value = value
        }
    }