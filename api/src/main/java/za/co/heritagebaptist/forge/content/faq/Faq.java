package za.co.heritagebaptist.forge.content.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.common.Publishable;

@Entity
@Table(name = "faq")
public class Faq extends BaseEntity  implements Publishable {
    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "text")
    private String answer;

    private int sortOrder;

    private boolean published;

    @Override
    public boolean isPublished() {
        return published;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
