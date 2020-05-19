package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 每一道试题
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamContentModel implements Serializable {
    private int num = 0;//题号
    private ClassifyModel type;//类型：选择1 填空0
    private String question;//题干
    private String answer;//答案
    private String analysis;//解析
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    public ExamContentModel(int num, ClassifyModel type, String question, String answer, String analysis, String optionA, String optionB, String optionC, String optionD) {
        this.num = num;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.analysis = analysis;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }

    public ExamContentModel() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ClassifyModel getType() {
        return type;
    }

    public void setType(ClassifyModel type) {
        this.type = type;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    @Override
    public String toString() {
        return "ExamContentModel{" +
                "num=" + num +
                ", type='" + type + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", analysis='" + analysis + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                '}';
    }
}
