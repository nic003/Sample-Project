package com.exam.kotlin.sample.extension

/**
 * Created by msaycon on 01,Oct,2019
 */
sealed class Response<out L, out R> {
    /** * Represents the left side of [Response] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Response<L, Nothing>()
    /** * Represents the right side of [Response] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Response<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Response.Left(a)
    fun <R> right(b: R) = Response.Right(b)

    fun response(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

fun <T, L, R> Response<L, R>.flatMap(fn: (R) -> Response<L, T>): Response<L, T> =
    when (this) {
        is Response.Left -> Response.Left(a)
        is Response.Right -> fn(b)
    }

fun <T, L, R> Response<L, R>.map(fn: (R) -> (T)): Response<L, T> = this.flatMap(fn.c(::right))
