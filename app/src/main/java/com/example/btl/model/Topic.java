package com.example.btl.model;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String name;
    private String id;
    private String descriptions;
    private String author;
    private List<QS> Question = new ArrayList<>();



    public Topic(String name, String id, String descriptions, String author, List<QS> question) {
        this.name = name;
        this.id = id;
        this.descriptions = descriptions;
        this.author = author;
        Question = question;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Topic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<QS> getQuestion() {
        return Question;
    }

    public void setQuestion(List<QS> question) {
        Question = question;
    }
}
