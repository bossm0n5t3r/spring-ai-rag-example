package me.bossm0n5t3r.rag

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ask")
class AskController(
    private val askService: AskService,
) {
    @PostMapping("/chat-client")
    fun askWithChatClient(
        @RequestBody question: Question,
    ): Answer = askService.askWithChatClient(question)

    @PostMapping("/chat-model")
    fun askWithChatModel(
        @RequestBody question: Question,
    ): Answer = askService.askWithChatModel(question)
}
