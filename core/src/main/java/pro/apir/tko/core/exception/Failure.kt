package pro.apir.tko.core.exception

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    object NetworkConnection : Failure()
    data class ServerError(
            val errorMessage: String = "Unknown Error",
            val statusCode: Int = 0
    ) : Failure()

    object RefreshTokenExpired : Failure()

    object Ignore : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}