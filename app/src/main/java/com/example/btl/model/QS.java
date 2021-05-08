package com.example.btl.model;

public class QS {
    private String txtQuestion;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private int answerTrue;
    private String time;

    public QS() {
    }

    public QS(String txtQuestion, String answerA, String answerB, String answerC, String answerD, int answerTrue, String time) {
        this.txtQuestion = txtQuestion;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.answerTrue = answerTrue;
        this.time = time;
    }

    public String getAnswerD() {
        return answerD;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getTxtQuestion() {
        return txtQuestion;
    }

    public void setTxtQuestion(String txtQuestion) {
        this.txtQuestion = txtQuestion;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public int getAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(int answerTrue) {
        this.answerTrue = answerTrue;
    }
}
