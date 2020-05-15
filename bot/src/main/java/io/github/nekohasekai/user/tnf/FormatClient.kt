package io.github.nekohasekai.user.tnf

import io.github.nekohasekai.nekolib.core.client.TdClient
import io.github.nekohasekai.nekolib.core.raw.checkAuthenticationCode
import io.github.nekohasekai.nekolib.core.raw.deleteAccountOrNull
import io.github.nekohasekai.nekolib.core.raw.getMe
import io.github.nekohasekai.nekolib.core.raw.setAuthenticationPhoneNumber
import io.github.nekohasekai.nekolib.core.utils.displayName
import io.github.nekohasekai.nekolib.core.utils.mkLog
import td.TdApi.*
import java.io.File

class FormatClient(val dcId: String, val number: String) : TdClient() {

    val log = mkLog("$dcId$number")

    init {

        options.apply {
            useTestDc(true)
            databaseDirectory("data/test/$dcId$number")
        }

    }

    override suspend fun onAuthorizationState(authorizationState: AuthorizationState) {

        if (authorizationState is AuthorizationStateWaitPhoneNumber) {

            setAuthenticationPhoneNumber("99966$dcId$number")

        } else if (authorizationState is AuthorizationStateWaitCode) {

            checkAuthenticationCode("$dcId$dcId$dcId$dcId$dcId")

        } else if (authorizationState is AuthorizationStateWaitPassword) {

            log.debug("发起注销")

            deleteAccountOrNull("Delete Test Account")

            stop()

        } else if (authorizationState is AuthorizationStateWaitRegistration) {

            if (number.toInt() % 100 == 0) {

                log.debug("跳过")

            }

            stop()

            /*

            registerUser("User#$dcId$number")

            log.debug("注册用户")

            isNew = true

            processed = false


             */
        } else if (authorizationState is AuthorizationStateReady) {

            _me = getMe()

            log.debug("发起注销: ${me.displayName}")

            deleteAccountOrNull("Delete Test Account")

        } else {

            super.onAuthorizationState(authorizationState)

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        File(options.databaseDirectory).deleteRecursively()

    }


}
