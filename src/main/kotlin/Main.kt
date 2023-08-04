import dev.inmo.tgbotapi.extensions.api.chat.invite_links.approveChatJoinRequest
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onChatJoinRequest
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommandWithArgs
import dev.inmo.tgbotapi.extensions.utils.asCommonUser
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.ChatIdentifier
import i18n.getModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * This is one of the most easiest bot - it will just print information about itself
 */

data class JoinRequest(
    val identifier: ChatIdentifier,
    val password: String,
)

/**
 * XXTEA.kt comes from https://github.com/xJoeWoo/xxtea-kotlin
 */

object XXTEA {

    private const val DELTA = -0x61c88647

    @Suppress("NOTHING_TO_INLINE", "FunctionName")
    private inline fun MX(sum: Int, y: Int, z: Int, p: Int, e: Int, k: IntArray): Int {
        return (z.ushr(5) xor (y shl 2)) + (y.ushr(3) xor (z shl 4)) xor (sum xor y) + (k[p and 3 xor e] xor z)
    }

    fun encrypt(data: ByteArray, key: ByteArray): ByteArray =
        data.takeIf { it.count() != 0 }
            ?.let {
                encrypt(data.toIntArray(true), key.fixKey().toIntArray(false))
                    .toByteArray(false)
            }
            ?: data

    fun encrypt(data: String, key: ByteArray): ByteArray? =
        runCatching { encrypt(data.encodeToByteArray(throwOnInvalidSequence = true), key) }.getOrNull()

    fun encrypt(data: ByteArray, key: String): ByteArray? =
        runCatching { encrypt(data, key.encodeToByteArray(throwOnInvalidSequence = true)) }.getOrNull()

    fun encrypt(data: String, key: String): ByteArray? =
        runCatching {
            encrypt(
                data.encodeToByteArray(throwOnInvalidSequence = true),
                key.encodeToByteArray(throwOnInvalidSequence = true)
            )
        }.getOrNull()

    fun decrypt(data: ByteArray, key: ByteArray): ByteArray =
        data.takeIf { it.count() != 0 }
            ?.let {
                decrypt(data.toIntArray(false), key.fixKey().toIntArray(false))
                    .toByteArray(true)
            } ?: data

    fun decrypt(data: ByteArray, key: String): ByteArray? =
        kotlin.runCatching { decrypt(data, key.encodeToByteArray(throwOnInvalidSequence = true)) }.getOrNull()

    fun decryptToString(data: ByteArray, key: ByteArray): String? =
        kotlin.runCatching { decrypt(data, key).decodeToString(throwOnInvalidSequence = true) }.getOrNull()

    fun decryptToString(data: ByteArray, key: String): String? =
        kotlin.runCatching { decrypt(data, key)?.decodeToString(throwOnInvalidSequence = true) }.getOrNull()

    private fun encrypt(v: IntArray, k: IntArray): IntArray {
        val n = v.size - 1

        if (n < 1) {
            return v
        }
        var p: Int
        var q = 6 + 52 / (n + 1)
        var z = v[n]
        var y: Int
        var sum = 0
        var e: Int

        while (q-- > 0) {
            sum += DELTA
            e = sum.ushr(2) and 3
            p = 0
            while (p < n) {
                y = v[p + 1]
                v[p] += MX(sum, y, z, p, e, k)
                z = v[p]
                p++
            }
            y = v[0]
            v[n] += MX(sum, y, z, p, e, k)
            z = v[n]
        }
        return v
    }

    private fun decrypt(v: IntArray, k: IntArray): IntArray {
        val n = v.size - 1

        if (n < 1) {
            return v
        }
        var p: Int
        val q = 6 + 52 / (n + 1)
        var z: Int
        var y = v[0]
        var sum = q * DELTA
        var e: Int

        while (sum != 0) {
            e = sum.ushr(2) and 3
            p = n
            while (p > 0) {
                z = v[p - 1]
                v[p] -= MX(sum, y, z, p, e, k)
                y = v[p]
                p--
            }
            z = v[n]
            v[0] -= MX(sum, y, z, p, e, k)
            y = v[0]
            sum -= DELTA
        }
        return v
    }

    private fun ByteArray.fixKey(): ByteArray {
        if (size == 16) return this
        val fixedKey = ByteArray(16)

        if (size < 16) {
            copyInto(fixedKey)
        } else {
            copyInto(fixedKey, endIndex = 16)
        }
        return fixedKey
    }

    private fun ByteArray.toIntArray(includeLength: Boolean): IntArray {
        var n = if (size and 3 == 0)
            size.ushr(2)
        else
            size.ushr(2) + 1
        val result: IntArray

        if (includeLength) {
            result = IntArray(n + 1)
            result[n] = size
        } else {
            result = IntArray(n)
        }
        n = size
        for (i in 0 until n) {
            result[i.ushr(2)] = result[i.ushr(2)] or (0x000000ff and this[i].toInt() shl (i and 3 shl 3))
        }
        return result
    }

    private fun IntArray.toByteArray(includeLength: Boolean): ByteArray? {
        var n = size shl 2

        if (includeLength) {
            val m = this[size - 1]
            n -= 4
            if (m < n - 3 || m > n) {
                return null
            }
            n = m
        }
        val result = ByteArray(n)

        for (i in 0 until n) {
            result[i] = this[i.ushr(2)].ushr(i and 3 shl 3).toByte()
        }
        return result
    }
}

suspend fun main(vararg args: String) {
    val map = HashMap<ChatIdentifier, JoinRequest>()

    if (args.size != 1){
        println("Invalid BotToken")
        return
    }

    println("BotToken: ${args[0]}")

    telegramBotWithBehaviourAndLongPolling(args[0], CoroutineScope(Dispatchers.IO)) {
        onChatJoinRequest {
            val model = getModel(it.from?.asCommonUser()?.ietfLanguageCode?.code)
            val passwordLength = (it.chat.id.chatId).absoluteValue.toString().length
            var start: Long = 1;
            for (i in 1..passwordLength-1) {
                start *= 10;
            }
            val password = Random.nextLong(start, (start * 10) - 1).toString()
            val secret = "20221209"
            val fakepassword = XXTEA.encrypt(password, secret)
            val encodedPassword: String = Base64.getEncoder().encodeToString(fakepassword)
            bot.sendMessage(it.from.id, model.problem.replace("[PASSWORD]", encodedPassword))
            map[it.from.id] = JoinRequest(it.chat.id, password)
            println("user ${it.from.id} start joining ${it.chat.id}")
        }
        onCommandWithArgs("join") { it, args ->
            val user = it.chat.asPrivateChat()!!
            val model = getModel(it.from?.asCommonUser()?.ietfLanguageCode?.code)
            println(it)
            if (args.size != 1){
                bot.sendMessage(user.id, model.usage)
                return@onCommandWithArgs
            }
            if (map.containsKey(user.id)){
                val req = map[user.id]!!
                if (args[0] == req.password){
                    bot.approveChatJoinRequest(req.identifier, user.id)
                    bot.sendMessage(it.chat, model.correct)
                    println("user ${user.id} joined ${req.identifier}")
                    map.remove(user.id)
                }else{
                    bot.sendMessage(user.id, model.incorrect)
                    println("user ${user.id} join ${req.identifier} failed")
                }
            }else {
                println("user ${user.id} not found group")
                bot.sendMessage(user.id, model.notFound)
            }
        }
    }.second.join()
}
