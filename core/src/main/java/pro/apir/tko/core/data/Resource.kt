package pro.apir.tko.core.data

import pro.apir.tko.core.data.Resource.Error
import pro.apir.tko.core.data.Resource.Success
import pro.apir.tko.core.exception.Failure

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Resource] are either an instance of [Error] or [Success].
 * FP Convention dictates that [Error] is used for "failure"
 * and [Success] is used for "success".
 *
 * @see Error
 * @see Success
 */
sealed class Resource<out R> {
    /** * Represents the left side of [Resource] class which by convention is a "Failure". */
    data class Error(val failure: Failure) : Resource<Nothing>()

    /** * Represents the right side of [Resource] class which by convention is a "Success". */
    data class Success<out R>(val data: R) : Resource<R>()

    /**
     * Returns true if this is a Right, false otherwise.
     * @see Success
     */
    val isSuccess get() = this is Success<R>

    /**
     * Returns true if this is a Left, false otherwise.
     * @see Error
     */
    val isError get() = this is Error

    /**
     * Creates a Left type.
     * @see Error
     */
    fun failure(failure: Failure) = Error(failure)

    /**
     * Creates a Left type.
     * @see Success
     */
    fun <R> success(data: R) = Success(data)

    /**
     * Applies fnL if this is a Left or fnR if this is a Right.
     * @see Error
     * @see Success
     */
    inline fun fold(
        onFailure: (Failure) -> Unit,
        onSuccess: (R) -> Unit
    ) {
        when (this) {
            is Error -> onFailure(failure)
            is Success -> onSuccess(data)
        }
    }

}

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

/**
 * Right-biased flatMap() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
fun <T, R> Resource<R>.flatMap(fn: (R) -> Resource<T>): Resource<T> =
    when (this) {
        is Error -> Error(failure)
        is Success -> fn(data)
    }

/**
 * Right-biased map() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
fun <T, R> Resource<R>.map(fn: (R) -> (T)): Resource<T> = this.flatMap(fn.c(::success))

fun <R> Resource<R>.mapFailure(fn: (Failure) -> Failure): Resource<R> {
    return when (this) {
        is Error -> Error(fn(failure))
        is Success -> this
    }
}

/** Returns the value from this `Right` or the given argument if this is a `Left`.
 *  Right(12).getOrElse(17) RETURNS 12 and Left(12).getOrElse(17) RETURNS 17
 */
fun <R> Resource<R>.getOrElse(value: R): R =
    when (this) {
        is Error -> value
        is Success -> data
    }

fun <R> Resource<R>.getOrNull(): R? =
    when (this) {
        is Error -> null
        is Success -> data
    }

fun <R> Resource<R>.onSuccess(fn: (R) -> Unit) {
    when (this) {
        is Success -> fn(data)
    }

}