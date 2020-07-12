package io.github.nekohasekai.user

import io.github.nekohasekai.nekolib.cli.TdCli
import io.github.nekohasekai.nekolib.cli.TdLoader
import io.github.nekohasekai.nekolib.cli.bl.ExportBinlog
import io.github.nekohasekai.nekolib.core.raw.deleteChatMessagesFromUser
import io.github.nekohasekai.nekolib.core.raw.getChat
import io.github.nekohasekai.nekolib.core.raw.getUser
import io.github.nekohasekai.nekolib.core.utils.*
import io.github.nekohasekai.user.tools.DelAll
import io.github.nekohasekai.user.tools.DelMe
import io.github.nekohasekai.user.tools.FilterUsers
import io.github.nekohasekai.user.tools.UpgradeToSupergroup
import kotlinx.coroutines.delay
import td.TdApi

object Launcher : TdCli() {

    var parameters: Array<String> = arrayOf()

    var isSubBot = false
    var loaded = false

    override val loginType get() = getLoginType()

    @JvmName("_getLoginType")
    private fun getLoginType(): LoginType {

//        if (isSubBot) return LoginType.USER
//
//        return LoginType.ALL

        return LoginType.USER

    }

    @JvmStatic
    fun main(args: Array<String>) {

        parameters = args

        TdLoader.tryLoad()

        start()

    }

    override fun onLoad() {

        if (isSubBot) options databaseDirectory "data/user"

        if (parameters.isNotEmpty()) {

            when (parameters.getOrNull(0)) {

                "export" -> {

                    addHandler(ExportBinlog(parameters.shift()))

                    return

                }

            }

        }

        addHandler(FilterUsers())

        addHandler(DelMe())

        addHandler(DelAll())

        addHandler(UpgradeToSupergroup())

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        while (isSubBot && !loaded) delay(1000L)

        super.onNewMessage(userId, chatId, message)

        if (userId == 0) return

        if (chatId == -1001432997913L) {

            if (message.content is TdApi.MessagePinMessage) {

                val user = getUser(userId)

                if (user.isBot) {

                    deleteChatMessagesFromUser(chatId, userId)

                    return

                }

                sudo delete message

                return

            }

        }

        message.text?.also {

            defaultLog.debug("[${getChat(chatId).title}] ${getUser(userId).displayName}: ${message.text}")

        }

        message.content.takeIf { it is TdApi.MessageSticker }?.also {

            defaultLog.debug("[${getChat(chatId).title}] ${getUser(userId).displayName}: [Sticker ${(it as TdApi.MessageSticker).sticker.emoji}]")

        }

    }

}