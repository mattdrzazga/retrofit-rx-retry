package com.mattdrzazga.retrofitrxretry

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class used to indicate that something went wrong on the server side.
 * This means that network request ended with 5xx code.
 */
open class ServerException : Exception {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}

/**
 * Indicates that request ended with non-critical failure and can be retried.
 */
class RecoverableServerException : ServerException {
    constructor() : super()
    constructor(exception: HttpException) : super(exception)
}

/**
 * Wraps [HttpException] in [ServerException].
 */
fun <T> Single<T>.wrapServerErrors(wrapper: (HttpException) -> Exception): Single<T> {
    return onErrorResumeNext {
        if (it is HttpException) {
            return@onErrorResumeNext Single.error<T>(wrapper(it))
        }
        return@onErrorResumeNext Single.error(it)
    }
}

/**
 * Resubscribes to the current Single if request ended with [RecoverableServerException] error.
 * This method will attempt recovery twice. First time after 1 second, and the second time after 2 seconds.
 */
fun <T> Single<T>.attemptRecoveryFromServerError(): Single<T> {
    return retryWhen { flowable ->
        // Don't combine with any Flowable here because
        // if the Publisher signals an onComplete, the resulting Single will signal a NoSuchElementException.
        // We want an actual error propagated downstream not this one.
        val maxAllowedRetries = 2
        val counter = AtomicInteger()
        flowable.flatMap {
            if (it is RecoverableServerException) {
                // Retry after waiting x seconds
                if (counter.getAndIncrement() != maxAllowedRetries) {
                    return@flatMap Flowable.timer(counter.toLong(), TimeUnit.SECONDS)
                }
            }
            // Don't retry
            return@flatMap Flowable.error<T>(it)
        }
    }
}

/**
 * Simulate ServerError. This exists only for debug purposes.
 * @param recoverable if true, this method will throw [RecoverableServerException], if false it will throw [ServerException]
 */
fun <T : Any> Single<T>.simulateServerError(recoverable: Boolean = true): Single<T> {
    return map {
        if (recoverable) {
            throw RecoverableServerException()
        } else {
            throw ServerException()
        }
    }
}