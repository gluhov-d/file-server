package com.github.gluhov.to;

import com.github.gluhov.model.FileStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@AllArgsConstructor
@Value
@EqualsAndHashCode(callSuper = true)
public class FileTo extends BaseTo {
    @NotBlank
    @Size(min=2)
    String name;

    @NotBlank
    String filePath;

    FileStatus fileStatus;

    public FileTo(Long id, String name, String filePath, FileStatus fileStatus) {
        super(id);
        this.name = name;
        this.filePath = filePath;
        this.fileStatus = fileStatus;
    }
}