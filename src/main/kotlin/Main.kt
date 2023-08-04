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
import kotlin.math.abs

/**
 * This is one of the most easiest bot - it will just print information about itself
 */

data class JoinRequest(
    val identifier: ChatIdentifier,
    val password: String,
)
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
            val password = abs(it.chat.id.chatId).toString()
            val encodedPassword: String = Base64.getEncoder().encodeToString(password.toByteArray())
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