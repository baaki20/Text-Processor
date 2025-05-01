package com.dataflow.model;

import java.util.Objects;

public class TextRecord {
    private String id;
    private String label;
    private String content;

    public TextRecord(String id, String label, String content) {
        this.id = id;
        this.label = label;
        this.content = content;
    }

    // Getters
    public String getId() { return id; }
    public String getLabel() { return label; }
    public String getContent() { return content; }

    // Setters
    public void setLabel(String label) { this.label = label; }
    public void setContent(String content) { this.content = content; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextRecord)) return false;
        TextRecord that = (TextRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TextRecord{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
