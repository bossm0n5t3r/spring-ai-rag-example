package me.bossm0n5t3r.rag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class OpenAiChat(
    private val aiClient: ChatClient,
    private val chatModel: OpenAiChatModel,
) : AiChat {
    override fun chatWithClient(
        prompt: Resource,
        params: Map<String, Any>,
    ): Answer {
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
                    userSpec.text(prompt)
                    for ((k, v) in params) {
                        userSpec.param(k, v)
                    }
                }.call()
                .content()

        return Answer(answer)
    }

    override fun chatWithModel(
        prompt: Resource,
        params: Map<String, Any>,
    ): Answer {
        val answer =
            chatModel
                .call(
                    Prompt(
                        SystemPromptTemplate(prompt)
                            .createMessage(params)
                            .content +
                            "Please respond in the format {data: [..., ...]}",
                    ),
                ).result
                .output
                .content

        return Answer(answer)
    }
}
