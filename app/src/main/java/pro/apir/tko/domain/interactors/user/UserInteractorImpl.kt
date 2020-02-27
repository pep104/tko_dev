package pro.apir.tko.domain.interactors.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.UserModel
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(private val userRepository: UserRepository) : UserInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getCurrentUser(): Either<Failure, UserModel> = withContext(dispatcher) {
        userRepository.getUser()
    }

    override suspend fun getCurrentUserId(): Either<Failure, Int> = withContext(dispatcher) {
        userRepository.getUserId()
    }
}