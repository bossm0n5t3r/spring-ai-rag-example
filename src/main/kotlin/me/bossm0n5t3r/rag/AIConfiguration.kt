package me.bossm0n5t3r.rag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TextSplitter
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.io.File

@Configuration
class AIConfiguration(
    @Value("\${app.vectorstore.path:./vectorstore.json}")
    private val vectorStorePath: String,
    @Value("\${app.resource}")
    private val pdfResource: Resource,
) {
    @Bean
    fun chatClient(chatClientBuilder: ChatClient.Builder): ChatClient = chatClientBuilder.build()

    @Bean
    fun simpleVectorStore(embeddingModel: EmbeddingModel): SimpleVectorStore {
        val simpleVectorStore = SimpleVectorStore(embeddingModel)
        val vectorStoreFile = File(vectorStorePath)
        if (vectorStoreFile.exists()) { // load existing vector store if exists
            simpleVectorStore.load(vectorStoreFile)
        } else { // otherwise load the documents and save the vector store
            val documentReader = TikaDocumentReader(pdfResource)
            val documents: List<Document> = documentReader.get()
            val textSplitter: TextSplitter = TokenTextSplitter()
            val splitDocuments: List<Document> = textSplitter.apply(documents)
            simpleVectorStore.add(splitDocuments)
            simpleVectorStore.save(vectorStoreFile)
        }
        return simpleVectorStore
    }
}
