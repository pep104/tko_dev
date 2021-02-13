package pro.apir.tko.domain.interactors.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.UserModel
import pro.apir.tko.domain.repository.user.UserRepository
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(private val userRepository: UserRepository) : UserInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getCurrentUser(): Resource<UserModel> = withContext(dispatcher) {
        userRepository.getUser()
    }

    override suspend fun getCurrentUserId(): Int = withContext(dispatcher) {
        userRepository.getUserId()
    }
}