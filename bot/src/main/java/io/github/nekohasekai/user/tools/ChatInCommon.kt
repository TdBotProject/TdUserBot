package io.github.nekohasekai.user.tools

import cn.hutool.core.util.NumberUtil
import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.getChatMember
import io.github.nekohasekai.nekolib.core.raw.getMessageOrNull
import io.github.nekohasekai.nekolib.core.raw.searchPublicChatOrNull
import io.github.nekohasekai.nekolib.core.utils.*
import td.TdApi

class ChatInCommon : TdHandler() {

    override fun onLoad() {

        initFunction("chat_in_common")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!isMyMessage(message)) return

        var target = 0

        if (message.replyToMessageId != 0L) {

            val replied = getMessageOrNull(chatId, message.replyToMessageId)

            if (replied != null && replied.senderUserId != me.id) {

                target = replied.senderUserId

            }

        }

        if (target == 0) {

            for (entity in message.entities!!) {

                if (entity.type is TdApi.TextEntityTypeMention) {

                    val username = message.text!!.substring(entity.offset, entity.offset + entity.length).substringAfter("@")

                    val user = searchPublicChatOrNull(username) ?: continue

                    target = user.id.toInt()

                    break

                } else if (entity.type is TdApi.TextEntityTypeMentionName) {

                    target = (entity.type as TdApi.TextEntityTypeMentionName).userId

                    break

                }

            }

        }

        if (target == 0 && NumberUtil.isInteger(param)) {

            target = param.toInt()

        }

        val groupsInCommon = getGroupsInCommon(target)

        var commons = "Chat in commons: \n\n" + groupsInCommon.joinToString("\n") { ">> " + it.title }

        sudo make "Fetching...\n\n$commons" editTo message

        val pool = mkFastPool()

        fetchChats {

            it.forEach { chat ->

                println(">> ${chat.title}")

                if (chat.type is TdApi.ChatTypeSupergroup && (chat.type as TdApi.ChatTypeSupergroup).isChannel) {

                    if (!getChatMember(chatId, me.id).isAdmin) return@forEach

                    if (!getChatMember(chatId, target).isMember) return@forEach

                    commons += "\n\n>> ${chat.title}"

                    pool.executeTimed {

                        sudo make "Fetching...\n\n$commons" editTo message

                    }

                }

            }

            true

        }

        pool.executeTimed {

            sudo make commons editTo message

        }

    }

}