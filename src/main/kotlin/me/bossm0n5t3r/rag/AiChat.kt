package me.bossm0n5t3r.rag

import org.springframework.core.io.Resource

interface AiChat {
    fun chatWithClient(
        prompt: Resource,
        params: Map<String, Any>,
    ): Answer

    fun chatWithModel(
        prompt: Resource,
        params: Map<String, Any>,
    ): Answer
}
