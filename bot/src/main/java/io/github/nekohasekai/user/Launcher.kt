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
import io.github.nekohasekai.user.tools.DelMe
import io.github.nekohasekai.user.tools.UpgradeToSupergroup
import td.TdApi

object Launcher : TdCli() {

    override val loginType = LoginType.USER

    init {

        options databaseDirectory "data/main"

    }

    @JvmStatic
    fun main(args: Array<String>) {

        TdLoader.tryLoad()

        start()

    }

    override fun onLoad() {

        addHandler(FormatTestNumber())

        addHandler(CleanDA())

        addHandler(DelMe())

        addHandler(DelAll())

        addHandler(UpgradeToSupergroup())

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        super.onNewMessage(userId, chatId, message)

        if (userId == 0) return

        message.text?.also {

            defaultLog.debug("[${getChat(chatId).title}] ${getUser(userId).displayName}: ${message.text}")

        }

        message.content.takeIf { it is TdApi.MessageSticker }?.also {

            defaultLog.debug("[${getChat(chatId).title}] ${getUser(userId).displayName}: [Sticker ${(it as TdApi.MessageSticker).sticker.emoji}]")

        }

    }

}