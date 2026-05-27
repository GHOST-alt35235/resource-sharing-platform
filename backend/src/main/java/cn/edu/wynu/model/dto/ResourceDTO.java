package cn.edu.wynu.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ResourceDTO {
    @NotBlank(message = "资源标题不能为空")
    private String title;

    private String description;

    private Long fileId;

    private Integer price;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
