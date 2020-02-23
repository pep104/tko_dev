package pro.apir.tko.domain.interactors.user

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.UserModel
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(private val userRepository: UserRepository) : UserInteractor {
    override suspend fun getCurrentUser(): Either<Failure, UserModel> {
        return userRepository.getUser()
    }

    override suspend fun getCurrentUserId(): Either<Failure, Int> {
        return userRepository.getUserId()
    }
}