package me.bossm0n5t3r.rag

import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class AskService(
    private val vectorStore: VectorStore,
    @Value("classpath:/rag-prompt-template.st")
    private val ragPromptTemplate: Resource,
    private val aiChat: AiChat,
) {
    private fun getContentList(question: Question): List<String> =
        vectorStore
            .similaritySearch(
                SearchRequest
                    .query(question.question)
                    .withTopK(2),
            ).map { it.content }

    private fun getParams(question: Question): Map<String, Any> =
        mapOf("input" to question.question, "documents" to getContentList(question).joinToString("\n"))

    fun askWithChatClient(question: Question): Answer = aiChat.chatWithClient(ragPromptTemplate, getParams(question))

    fun askWithChatModel(question: Question): Answer = aiChat.chatWithModel(ragPromptTemplate, getParams(question))
}
