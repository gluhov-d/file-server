package com.github.gluhov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class FileEntity extends BaseEntity {
    @Column(name = "name")
    @NotBlank
    @Size(min = 2)
    @JsonIgnore
    private String name;

    @Column(name = "file_path")
    @NotBlank
    @JsonIgnore
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FileStatus fileStatus;

    public FileEntity(Long id, String name, String filePath, FileStatus fileStatus) {
        super(id);
        this.name = name;
        this.filePath = filePath;
        this.fileStatus = fileStatus;
    }
}