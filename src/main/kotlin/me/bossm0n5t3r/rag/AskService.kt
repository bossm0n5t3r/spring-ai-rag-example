package me.bossm0n5t3r.rag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class AskService(
    private val aiClient: ChatClient,
    private val chatModel: OpenAiChatModel,
    private val vectorStore: VectorStore,
    @Value("classpath:/rag-prompt-template.st")
    private val ragPromptTemplate: Resource,
) {
    private fun getContentList(question: Question): List<String> =
        vectorStore
            .similaritySearch(
                SearchRequest
                    .query(question.question)
                    .withTopK(2),
            ).map { it.content }

    fun askWithChatClient(question: Question): Answer {
        val contentList = getContentList(question)
        val answer =
            aiClient
                .prompt()
                .options(
                    OpenAiChatOptions
                        .builder()
                        .withModel(OpenAiApi.ChatModel.GPT_3_5_TURBO)
                        .withResponseFormat(
                            OpenAiApi.ChatCompletionRequest.ResponseFormat(OpenAiApi.ChatCompletionRequest.ResponseFormat.Type.TEXT),
                        ).build(),
                ).user { userSpec ->
                    userSpec
                        .text(ragPromptTemplate)
                        .param("input", question.question)
                        .param("documents", contentList.joinToString("\n"))
                }.call()
                .content()

        return Answer(answer)
    }

    fun askWithChatModel(question: Question): Answer {
        val contentList = getContentList(question)
        val answer =
            chatModel
                .call(
                    Prompt(
                        SystemPromptTemplate(ragPromptTemplate)
                            .createMessage(mapOf("input" to question.question, "documents" to contentList.joinToString("\n")))
                            .content +
                            "Please respond in the format {data: [..., ...]}",
                    ),
                ).result
                .output
                .content

        return Answer(answer)
    }
}
