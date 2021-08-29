package pro.apir.tko.core.exception

import java.io.IOException

/**
 * Created by antonsarmatin
 * Date: 15/02/2021
 * Project: tko
 */
class RefreshTokenNotValidException : IOException("Current refresh token was expired or blacklisted")
class TokenUncaughtException(): Exception("Uncaught exception for token")
