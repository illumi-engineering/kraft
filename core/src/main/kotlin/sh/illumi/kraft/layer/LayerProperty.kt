package sh.illumi.kraft.layer

import kotlin.reflect.KProperty

interface LayerProperty<TLayer : ApplicationLayer, TProperty> {
    operator fun getValue(thisRef: TLayer, property: KProperty<*>?): TProperty
    operator fun setValue(thisRef: TLayer, property: KProperty<*>?, value: TProperty)
}

fun <TProperty, TLayer : ApplicationLayer> lazyProperty(initializer: TLayer.() -> TProperty) =
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