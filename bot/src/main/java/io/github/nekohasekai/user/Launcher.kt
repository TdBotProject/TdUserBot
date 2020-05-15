package io.github.nekohasekai.user

import io.github.nekohasekai.nekolib.cli.TdCli
import io.github.nekohasekai.nekolib.core.TdLoader
import io.github.nekohasekai.nekolib.core.raw.getChat
import io.github.nekohasekai.nekolib.core.raw.getUser
import io.github.nekohasekai.nekolib.core.utils.defaultLog
import io.github.nekohasekai.nekolib.core.utils.displayName
import io.github.nekohasekai.nekolib.core.utils.text
import io.github.nekohasekai.user.tnf.FormatTestNumber
import io.github.nekohasekai.user.tools.CleanDA
import io.github.nekohasekai.user.tools.DelAll
import io.github.nekohasekai.user.tools.UpgradeToSupergroup
import td.TdApi

object Launcher : TdCli() {

    init {

        options databaseDirectory "data/main"

    }

    @JvmStatic
    fun main(args: Array<String>) {

        TdLoader.tryLoad()

        start()

    }

    override fun onLoad() {

        super.onLoad()

        addHandler(FormatTestNumber())

        addHandler(CleanDA())

        addHandler(DelAll())

        addHandler(UpgradeToSupergroup())

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {
        super.onNewMessage(userId, chatId, message)

        if (message.content is TdApi.MessagePinMessage) {

            println("userId = $userId, chatId = $chatId, pinned = ${(message.content as TdApi.MessagePinMessage).messageId}")

        }

        if (userId == 0) return

        message.text?.also {

            defaultLog.debug("[${getChat(chatId).title}] ${getUser(userId).displayName}: ${message.text}")

        }

        message.content.takeIf { it is TdApi.MessageSticker }?.also {

            defaultLog.debug("[${getChat(chatId).title}] ${getUser(userId).displayName}: [Sticker ${(it as TdApi.MessageSticker).sticker.emoji}]")

        }

    }

}