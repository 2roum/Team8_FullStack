package site.devroad.softeer.src.exam.dto.domain;

import site.devroad.softeer.src.exam.model.ExamMcq;

import java.util.List;

public class MultiChoiceQuestion {
    private final Long seq;
    private final String title;
    private final String content;
    private final List<String> choices;

    public MultiChoiceQuestion(Long seq, String title, String content, List<String> choices) {
        this.seq = seq;
        this.title = title;
        this.content = content;
        this.choices = choices;
    }

    public MultiChoiceQuestion(ExamMcq examMcq) {
        this.seq = examMcq.getSequence();
        this.title = examMcq.getTitle();
        this.content = examMcq.getContent();
        this.choices = examMcq.getChoices();
    }

    public Long getSeq() {
        return seq;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getChoices() {
        return choices;
    }
}
