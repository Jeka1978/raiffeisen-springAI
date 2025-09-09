package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.LoadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadedDocumentRepo extends JpaRepository<LoadedDocument, Long> {
    boolean existsByFilenameAndContentHash(String filename, String s);
}
