package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.LoadedDocument;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class DocumentLoaderService implements CommandLineRunner {

    @Autowired
    private LoadedDocumentRepo documentRepo;

    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private VectorStore vectorStore;


    @SneakyThrows
    public void loadDocuments() {
        List<Resource> resources = Arrays.stream(resolver.getResources("classpath:/knowledgebase/**/*.txt")).toList();

        resources.stream()
                .filter(resource -> !documentRepo.existsByFilenameAndContentHash(resource.getFilename(), calcContentHash(resource)))
                .forEach(resource -> {
                    List<Document> documents = new TextReader(resource).get();
                    TokenTextSplitter textSplitter = TokenTextSplitter.builder().withChunkSize(200).build();
                    List<Document> chunks = textSplitter.apply(documents);
                    vectorStore.accept(chunks);

                    LoadedDocument loadedDocument = LoadedDocument.builder()
                            .documentType("txt")
                            .chunkCount(chunks.size())
                            .filename(resource.getFilename())
                            .contentHash(calcContentHash(resource))
                            .build();
                    documentRepo.save(loadedDocument);

                });


    }

    @SneakyThrows
    private String calcContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }

    @Override
    public void run(String... args) throws Exception {
        loadDocuments();
    }
}
