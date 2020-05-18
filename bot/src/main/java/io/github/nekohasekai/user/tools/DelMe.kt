package io.github.nekohasekai.user.tools

import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.deleteChatMessagesFromUser
import io.github.nekohasekai.nekolib.core.raw.deleteMessages
import io.github.nekohasekai.nekolib.core.utils.checkChatAdmin
import io.github.nekohasekai.nekolib.core.utils.delete
import io.github.nekohasekai.nekolib.core.utils.fetchUserMessages
import io.github.nekohasekai.nekolib.core.utils.isMyMessage
import td.TdApi

class DelMe : TdHandler() {

    override fun onLoad() {

        initFunction("del_me")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!isMyMessage(message)) return

        if (checkChatAdmin(chatId, userId)) {

            deleteChatMessagesFromUser(chatId, userId)

        } else fetchUserMessages(chatId, userId) { messages ->

            delete(chatId, * messages.map { it.id }.toLongArray())

            true

        }

    }

}