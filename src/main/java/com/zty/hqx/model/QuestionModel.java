package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zty.hqx.classify.EQuestionClassify;

import java.io.Serializable;

/**
 * 每一道试题
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionModel implements Serializable {
    private int num;//题号
    private EQuestionClassify type;//类型：选择1 填空0
    private String question;//题干
    private String answer;//答案
    private String analysis;//解析
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    public QuestionModel(int num, int type, String question, String answer, String analysis, String optionA, String optionB, String optionC, String optionD) {
        this.num = num;
        this.type = EQuestionClassify.values()[type];
        this.question = question;
        this.answer = answer;
        this.analysis = analysis;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }

    public QuestionModel() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public EQuestionClassify getType() {
        return type;
    }

    public void setType(int type) {
        this.type = EQuestionClassify.values()[type];
    }

    public String dealType(int type) {
        switch (type) {
            case 0:
                return "填空题";
            case 1:
                return "单选题";
            case 2:
                return "多选题";
            case 4:
                return "简答题";
        }
        return null;
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
        return "QuestionModel{" +
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
