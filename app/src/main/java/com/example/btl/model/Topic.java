package com.example.btl.model;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String name;
    private String des;
    private List<QS> listQuestion = new ArrayList<>();

    public Topic(String name, String des, List<QS> listQuestion) {
        this.name = name;
        this.des = des;
        this.listQuestion = listQuestion;
    }

    public Topic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public List<QS> getListQuestion() {
        return listQuestion;
    }

    public void setListQuestion(List<QS> listQuestion) {
        this.listQuestion = listQuestion;
    }
}
