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

/**
 * This is one of the most easiest bot - it will just print information about itself
 */
suspend fun main(vararg args: String) {
    val map = HashMap<ChatIdentifier, ChatIdentifier>()

    telegramBotWithBehaviourAndLongPolling(Config.botToken, CoroutineScope(Dispatchers.IO)) {
        onChatJoinRequest {
            val model = getModel(it.from?.asCommonUser()?.ietfLanguageCode?.code)
            bot.sendMessage(it.from.id, model.problem)
            map[it.from.id] = it.chat.id
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
                val group = map[user.id]!!
                if (args[0] == Config.password){
                    bot.approveChatJoinRequest(group, user.id)
                    bot.sendMessage(it.chat, model.correct)
                    println("user ${user.id} joined $group")
                }else{
                    bot.sendMessage(user.id, model.incorrect)
                    println("user ${user.id} join $group failed")
                }
            }else{
                println("user ${user.id} not found group")
                bot.sendMessage(user.id, model.notFound)
            }
        }
    }.second.join()
}