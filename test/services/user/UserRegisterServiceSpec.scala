package services.user

import domains.error.{ AlreadyRegisteredError, RegisterConflictError }
import org.specs2.matcher.Scope
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification

import scala.concurrent.Future


class UserRegisterServiceSpec extends PlaySpecification with Mockito {

  trait CommonBefore {
    val mockUserName = "mockName"
    val mockSessionId = "mockSession"

    val mockLockComponent = mock[RegisterLockComponent]
    val mockUserRepository = mock[UserRepository]
    val mockPointRepository = mock[CoinRepository]

    val service = new UserRegisterService(
      mockUserRepository, mockLockComponent, mockPointRepository
    )

    mockLockComponent.lock(anyString) returns Right(())
    mockUserRepository.findByName(anyString) returns Future.successful(None)
    mockUserRepository.register(anyString, anyString) returns Future.successful(mockSessionId)
    mockPointRepository.append(anyInt) returns Future.successful(15)
  }

  class CommonContext extends Scope with CommonBefore

  "会員登録" should {

    "すべての事前条件が正常" should {

      class Context extends CommonContext

      "エラーは発生しない" in new Context {
        val result = service.register(mockUserName)
        await(result) must not(throwA[Throwable])
      }

      "外部モジュールの更新系メソッドを正常に呼び出す" in new Context {
        val result = service.register(mockUserName)
        await(result) must not(throwA[Throwable])

        there was
            one(mockLockComponent).lock(anyString) andThen
            one(mockUserRepository).register(anyString, anyString) andThen
            one(mockPointRepository).append(anyInt) andThen
            one(mockLockComponent).unlock(anyString)
      }
    }

    "ユーザがすでに登録されている" should {

      trait Before {
        self: CommonBefore =>
        mockUserRepository.findByName(anyString) returns Future.successful(Some("mockUserId"))
      }

      class Context extends CommonContext with Before

      "AlreadyRegisteredErrorが発生" in new Context {
        val result = service.register(mockUserName)
        await(result) must throwA[AlreadyRegisteredError]
      }

      "ロック処理も、登録系処理は呼び出されない" in new Context {
        val result = service.register(mockUserName)
        await(result) must throwA[Throwable]

        there was
            no(mockLockComponent).lock(anyString) andThen
            no(mockUserRepository).register(anyString, anyString) andThen
            no(mockPointRepository).append(anyInt) andThen
            no(mockLockComponent).unlock(anyString)
      }
    }


    "登録処理がロックされている" should {

      trait Before {
        self: CommonBefore =>
        mockLockComponent.lock(anyString) returns Left(new IllegalStateException())
      }

      class Context extends CommonContext with Before

      "RegisterConflictErrorが発生" in new Context {
        val result = service.register(mockUserName)
        await(result) must throwA[RegisterConflictError]
      }

      "登録系処理は呼び出されず、ロックも解除されない" in new Context {
        val result = service.register(mockUserName)
        await(result) must throwA[Throwable]

        there was
            one(mockLockComponent).lock(anyString) andThen
            no(mockUserRepository).register(anyString, anyString) andThen
            no(mockPointRepository).append(anyInt) andThen
            no(mockLockComponent).unlock(anyString)
      }
    }

    "ユーザー登録処理で想定しないエラーが発生" should {

      trait Before {
        self: CommonBefore =>
        mockUserRepository.register(anyString, anyString) returns Future.failed(new RuntimeException())
      }

      class Context extends CommonContext with Before

      "ポイント登録処理は呼び出されず、ロックが解除される" in new Context {
        val result = service.register(mockUserName)
        await(result) must throwA[Throwable]

        there was
            one(mockLockComponent).lock(anyString) andThen
            one(mockUserRepository).register(anyString, anyString) andThen
            no(mockPointRepository).append(anyInt) andThen
            one(mockLockComponent).unlock(anyString)
      }
    }

    "ポイント加算処理で想定しないエラーが発生" should {

      trait Before {
        self: CommonBefore =>
        mockPointRepository.append(anyInt) returns Future.failed(new RuntimeException())
      }

      class Context extends CommonContext with Before

      "ロックが解除される" in new Context {
        val result = service.register(mockUserName)
        await(result) must throwA[Throwable]

        there was
            one(mockLockComponent).lock(anyString) andThen
            one(mockUserRepository).register(anyString, anyString) andThen
            one(mockPointRepository).append(anyInt) andThen
            one(mockLockComponent).unlock(anyString)
      }
    }
  }
}
