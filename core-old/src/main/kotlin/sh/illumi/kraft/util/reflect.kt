package sh.illumi.kraft.util

import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

fun argsMatchParams(args: Array<out Any>, params: Array<out KParameter>) =
    if (args.size != params.size) false
    else args.zip(params).all { (arg, param) ->
        arg::class.createType().isSubtypeOf(param.type)
    }