package services

import javax.inject.Inject

import infrastructures.user.RegisterLockComponent

class UserRegisterService @Inject()(
    lockComponent: RegisterLockComponent,
    userRepository: UserRepository,
    pointRepository: PointRepository
) {

}
