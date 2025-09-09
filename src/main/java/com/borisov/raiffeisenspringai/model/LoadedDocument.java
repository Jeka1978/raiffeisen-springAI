package com.borisov.raiffeisenspringai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LoadedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType;

    @Column(name = "chunk_count")
    private Integer chunkCount;

    @Column(name = "loaded_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime loadedAt;

}
