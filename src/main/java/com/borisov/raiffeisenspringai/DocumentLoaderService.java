package com.borisov.raiffeisenspringai;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentLoaderService {

    @Autowired
    private LoadedDocumentRepo documentRepo;

    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private VectorStore vectorStore;


    public void loadNewDocuments(){
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.split(List.of());

        //todo finish this
    }

}
