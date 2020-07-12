package io.github.nekohasekai.user.tools

import cn.hutool.core.thread.ThreadUtil
import io.github.nekohasekai.nekolib.core.client.TdException
import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.getChat
import io.github.nekohasekai.nekolib.core.raw.getUser
import io.github.nekohasekai.nekolib.core.raw.searchChatMessages
import io.github.nekohasekai.nekolib.core.raw.setChatMemberStatus
import io.github.nekohasekai.nekolib.core.utils.*
import io.github.nekohasekai.nekolib.i18n.LocaleController
import io.github.nekohasekai.nekolib.i18n.UNKNOWN_PARAMETER
import td.TdApi
import java.util.*

class FilterUsers : TdHandler() {

    override fun onLoad() {

        initFunction("filter_users")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {


        if (!isMyMessage(message)) return

        var noMsg = false
        var noPhoto = false
        var likeAd = false

        var hide = false
        var keepDeleted = false

        params.forEach {

            if (it == "-m" || it == "--no-msg") {

                noMsg = true

            } else if (it == "-p" || it == "--no-photo") {

                noPhoto = true

            } else if (it == "-a" || it == "--like-ad") {

                likeAd = true

            } else if (it == "-k" || it == "--keep-deleted") {

                keepDeleted = true

            } else if (it == "-h" || it == "--hide") {

                hide = true

            } else {

                sudo make LocaleController.UNKNOWN_PARAMETER.input(it) replyTo message send deleteDelay(message)

                return

            }

        }

        val title = getChat(chatId).title

        val status = if (!hide) {
            sudo make "Filtering..." syncEditTo message
        } else {
            sudo delete message
            sudo make "Filtering from $title..." syncTo me.id
        }

        val toDelete = LinkedList<Int>()

        val pool = mkFastPool()

        var count = 0

        fetchSupergroupUsers(chatId) {

            it.forEach { member ->

                count++

                if (member.status is TdApi.ChatMemberStatusAdministrator) return@forEach

                var hasMsg = false
                var isAd = false
                val user = getUser(member.userId)
                val isDeleted = user.isDeleted

                if (!isDeleted && (noMsg || likeAd)) {

                    hasMsg = searchChatMessages(chatId, "", member.userId, 0, 0, 1, TdApi.SearchMessagesFilterEmpty()).totalCount > 0

                }

                if (!isDeleted && likeAd) {

                    isAd = !hasMsg && user.profilePhoto == null

                    if (isAd) {

                        val userName = user.displayName.replace(" ", "")

                        isAd = if (userName.length in 2..3) {

                            userName.all { Character.UnicodeScript.of(it.toInt()) == Character.UnicodeScript.HAN }

                        } else {

                            false

                        }

                    }

                }

                if (noMsg && hasMsg) {

                    return@forEach

                } else if (noPhoto && user.profilePhoto != null) {

                    return@forEach

                } else if (likeAd && !isAd) {

                    return@forEach

                } else if (isDeleted && keepDeleted) {

                    return@forEach

                }

                toDelete.add(member.userId)

                pool.executeTimed {

                    sudo make "Filtering... ${toDelete.size} / $count" editTo status

                }

            }

            true

        }

        toDelete.forEachIndexed { index, it ->

            do {

                try {

                    setChatMemberStatus(chatId, it, TdApi.ChatMemberStatusLeft())

                    break

                } catch (e: TdException) {

                    if (e.code == 429) {

                        runCatching {

                            sudo make "Deleting...  ${index + 1}/ ${toDelete.size}\n\nWaiting for rate limit: ${e.retryAfter.parseTime(true)}" editTo status

                            e.waitForRateLimit()

                        }

                    } else throw e

                }

            } while (true)

            pool.executeTimed {

                sudo make "Deleting...  ${index + 1}/ ${toDelete.size}" editTo status

            }

        }

        pool.shutdown()

        sudo make "Finish, ${toDelete.size} deleted" sendTo chatId

    }

}