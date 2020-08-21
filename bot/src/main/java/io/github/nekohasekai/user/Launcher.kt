package io.github.nekohasekai.user

import cn.hutool.log.level.Level
import io.github.nekohasekai.nekolib.cli.TdCli
import io.github.nekohasekai.nekolib.cli.TdLoader
import io.github.nekohasekai.nekolib.cli.bl.ExportBinlog
import io.github.nekohasekai.nekolib.core.raw.deleteChatMessagesFromUser
import io.github.nekohasekai.nekolib.core.raw.getChat
import io.github.nekohasekai.nekolib.core.raw.getUser
import io.github.nekohasekai.nekolib.core.utils.*
import io.github.nekohasekai.user.tools.*
import kotlinx.coroutines.delay
import td.TdApi
import kotlin.system.exitProcess

object Launcher : TdCli() {

    var parameters: Array<String> = arrayOf()

    override val loginType get() = LoginType.USER

    @JvmStatic
    fun main(args: Array<String>) {

        readSettings("user.conf")?.insertProperties()

        val logLevel = stringEnv("LOG_LEVEL").takeIf { !it.isNullOrBlank() }?.toUpperCase() ?: "INFO"

        runCatching {

            LOG_LEVEL = Level.valueOf(logLevel)

        }.onFailure {

            LOG_LEVEL = Level.INFO

            defaultLog.error("Invalid log level $logLevel, fallback to INFO.")

        }

        TdLoader.tryLoad()

        if (args.any { it == "--download-library" }) exitProcess(0)

        parameters = args

        start()

    }

    override fun onLoad() {

        if (parameters.isNotEmpty()) {

            when (parameters.getOrNull(0)) {

                "export" -> {

                    addHandler(ExportBinlog(parameters.shift()))

                    return

                }

            }

        }

        addHandler(ChatInCommon())

        addHandler(FilterUsers())

        addHandler(DelMe())

        addHandler(DelAll())

        addHandler(UpgradeToSupergroup())

    }

}