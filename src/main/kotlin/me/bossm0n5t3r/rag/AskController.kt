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
    @PostMapping
    fun ask(
        @RequestBody question: Question,
    ): Answer = askService.ask(question)
}
