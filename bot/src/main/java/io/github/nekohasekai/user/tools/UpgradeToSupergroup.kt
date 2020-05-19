package io.github.nekohasekai.user.tools

import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.upgradeBasicGroupChatToSupergroupChatWith
import io.github.nekohasekai.nekolib.core.utils.*
import io.github.nekohasekai.nekolib.i18n.FN_BASIC_GROUP_ONLY
import io.github.nekohasekai.nekolib.i18n.LocaleController
import td.TdApi

class UpgradeToSupergroup : TdHandler() {

    override fun onLoad() {

        initFunction("upgrade")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!isMyMessage(message)) return

        if (!message.fromBasicGroup) {

            sudo make LocaleController.FN_BASIC_GROUP_ONLY to chatId at message.id edit deleteDelay()

            return

        }

        sudo delete message

        upgradeBasicGroupChatToSupergroupChatWith(chatId) onError {

            sudo make it editTo message

        }

    }

}