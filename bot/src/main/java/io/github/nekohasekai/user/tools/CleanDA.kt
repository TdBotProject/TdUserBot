package io.github.nekohasekai.user.tools

import io.github.nekohasekai.nekolib.core.client.TdHandler
import io.github.nekohasekai.nekolib.core.raw.getUser
import io.github.nekohasekai.nekolib.core.raw.setChatMemberStatus
import io.github.nekohasekai.nekolib.core.utils.*
import td.TdApi
import java.util.*

class CleanDA : TdHandler() {

    override fun onLoad() {

        initFunction("clean_da")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!isMyMessage(message)) return

        val status = sudo make "Fetching..." syncEditTo message

        val pool = mkFastPool()

        val toDelete = LinkedList<Int>()

        var count = 0

        fetchSupergroupUsers(chatId) {

            it.forEach { member ->

                count++

                if (member.status is TdApi.ChatMemberStatusAdministrator) return@forEach

                if (getUser(member.userId).type is TdApi.UserTypeDeleted) {

                    toDelete.add(member.userId)

                    pool.execute {

                        sudo make "Fetching... ${toDelete.size} / $count" editTo status

                    }

                }

            }

            true

        }

        pool.shutdown()

        toDelete.forEach {

            setChatMemberStatus(chatId, it, TdApi.ChatMemberStatusLeft())

        }

        sudo make "Finish, ${toDelete.size} deleted" sendTo chatId

    }

}