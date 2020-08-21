package io.github.nekohasekai.user.tools

import io.github.nekohasekai.nekolib.core.client.TdClient
import io.github.nekohasekai.nekolib.core.client.TdException
import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.getChat
import io.github.nekohasekai.nekolib.core.raw.getUser
import io.github.nekohasekai.nekolib.core.raw.searchChatMessages
import io.github.nekohasekai.nekolib.core.raw.setChatMemberStatus
import io.github.nekohasekai.nekolib.core.utils.*
import io.github.nekohasekai.nekolib.i18n.LocaleController
import io.github.nekohasekai.nekolib.i18n.UNKNOWN_PARAMETER
import kotlinx.coroutines.runBlocking
import td.TdApi
import java.util.*

class FilterUsers : TdHandler() {

    override fun onLoad() {

        initFunction("filter_users")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!isMyMessage(message)) return

        doFilterUsers(sudo, chatId, message, params)

    }

}

suspend fun TdHandler.doFilterUsers(anchor: TdClient, chatId: Long, message: TdApi.Message, params: Array<String>) {

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

            sudo make LocaleController.UNKNOWN_PARAMETER.input(it) onSuccess deleteDelay(message) replyTo message

            return

        }

    }

    val title = getChat(chatId).title

    val status = if (!hide) {
        if (message.canBeEdited) {
            sudo make "Filtering..." syncEditTo message
        } else {
            sudo make "Filtering..." syncReplyTo message
        }
    } else {
        sudo delete message
        sudo make "Filtering from $title..." syncTo me.id
    }

    val toDelete = LinkedList<Int>()

    val pool = mkFastPool()

    var count = 0

    anchor.fetchSupergroupUsers(chatId) { users ->

        users.forEach { member ->

            count++

            if (member.status is TdApi.ChatMemberStatusAdministrator) return@forEach

            var hasMsg = false
            var isAd = false
            val user = anchor.getUser(member.userId)
            val isDeleted = user.isDeleted

            if (user.isBot) return@forEach

            if (!isDeleted && (noMsg || likeAd)) {

                val msgs = anchor.searchChatMessages(chatId, "", member.userId, 0, 0, 1, TdApi.SearchMessagesFilterEmpty())

                hasMsg = if (msgs.totalCount > 0) {
                    !msgs.messages[0].isServiceMessage
                } else false

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

                runBlocking {

                    try {

                        sudo make "Filtering... ${toDelete.size} / $count" syncEditTo status

                    } catch (e: TdException) {

                        e.waitForRateLimit()

                    }

                }

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

    pool.executeTimed {

        sudo make "Finish, ${toDelete.size} deleted." onSuccess withDelay {

            if (anchor == sudo && !hide) delete(it)

            pool.shutdown()

        } editTo status


    }

}