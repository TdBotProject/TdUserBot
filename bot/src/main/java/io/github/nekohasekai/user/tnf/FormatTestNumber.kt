package io.github.nekohasekai.user.tnf

import cn.hutool.core.io.FileUtil
import io.github.nekohasekai.nekolib.core.client.TdHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import td.TdApi
import java.io.File
import java.util.concurrent.Executors

class FormatTestNumber : TdHandler() {

    override fun onLoad() {

        initFunction("ftn", "ftn_dc", "ftn_all")

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        File("data/test").mkdirs()

        if (function == "ftn") {

            val dcId = param.substring(0, 1)
            val number = param.substring(1)

            FormatClient(dcId, number).start()

        } else if (function == "ftn_dc") {

            val pool = Executors.newFixedThreadPool(16)

            for (index in 0 until 10000) {

                var str = "$index"

                while (str.length < 4) {

                    str = "0$str"

                }

                pool.execute {

                    runBlocking {

                        val client = FormatClient(param, str)

                        client.start()

                        client.waitForClose()

                        launch(Dispatchers.IO) {

                            FileUtil.del("data/test/$param$str")

                        }

                    }

                }

            }

        } else {

            val pool = Executors.newFixedThreadPool(16)

            for (dcId in 1..3) {

                for (index in 0 until 10000) {

                    var str = "$index"

                    while (str.length < 4) {

                        str = "0$str"

                    }

                    pool.execute {

                        runBlocking {

                            val client = FormatClient("$dcId", str)

                            client.start()

                            client.waitForClose()

                            launch(Dispatchers.IO) {

                                FileUtil.del("data/test/$dcId$str")

                            }

                        }

                    }

                }

            }

        }

    }

}
