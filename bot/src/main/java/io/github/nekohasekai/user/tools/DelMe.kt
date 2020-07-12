package io.github.nekohasekai.user.tools

import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.deleteChatMessagesFromUser
import io.github.nekohasekai.nekolib.core.utils.*
import td.TdApi

class DelMe : TdHandler() {

    override fun onLoad() {

        initFunction("td_del_me")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!isMyMessage(message)) return

        if (message.fromSuperGroup && message.replyToMessageId == 0L && isChatAdmin(chatId, userId)) {

            deleteChatMessagesFromUser(chatId, userId)

        } else fetchUserMessages(chatId, userId,startsAt = message.replyToMessageId) { messages ->

            delete(chatId, * messages.filter { it.canBeDeletedForAllUsers }.map { it.id }.toLongArray())

            true

        }

    }

}