package me.bossm0n5t3r.rag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class AskService(
    private val aiClient: ChatClient,
    private val vectorStore: VectorStore,
    @Value("classpath:/rag-prompt-template.st")
    private val ragPromptTemplate: Resource,
) {
    fun ask(question: Question): Answer {
        val similarDocuments: List<Document> =
            vectorStore
                .similaritySearch(
                    SearchRequest
                        .query(question.question)
                        .withTopK(2),
                )
        val contentList = similarDocuments.map { it.content }

        val answer =
            aiClient
                .prompt()
                .user { userSpec ->
                    userSpec
                        .text(ragPromptTemplate)
                        .param("input", question.question)
                        .param("documents", contentList.joinToString("\n"))
                }.call()
                .content()

        return Answer(answer)
    }
}
