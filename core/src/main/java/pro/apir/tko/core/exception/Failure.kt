package pro.apir.tko.core.exception

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    object NetworkConnection : Failure()
    data class ServerError(val message: String? = null) : Failure()
    object RefreshTokenExpired: Failure()

    object Ignore: Failure()
    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure: Failure()
}