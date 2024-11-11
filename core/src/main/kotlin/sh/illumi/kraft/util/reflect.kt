package sh.illumi.kraft.util

import kotlin.reflect.KParameter

fun argsMatchParams(args: Array<out Any>, params: Array<out KParameter>) =
    if (args.size != params.size) false
    else args.zip(params).all { (arg, param) -> param.type.classifier == arg::class }