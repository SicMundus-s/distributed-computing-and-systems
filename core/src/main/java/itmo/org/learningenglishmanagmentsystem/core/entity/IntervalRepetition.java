package itmo.org.learningenglishmanagmentsystem.core.entity;


import itmo.org.learningenglishmanagmentsystem.core.entity.key.IntervalRepetitionKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity(name = "interval_repetition")
@NoArgsConstructor
@Getter
@Setter
@IdClass(IntervalRepetitionKey.class)
public class IntervalRepetition {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Id
    private User userId;
    @ManyToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    @Id
    private Word wordId;
    @Column(name = "count_correct_translation")
    private int countCorrectTranslation;
    @Column(name = "last_translation")
    private Date lastTranslation;

    public IntervalRepetition(User userId, Word wordId) {
        this.userId = userId;
        this.wordId = wordId;
        this.countCorrectTranslation = 0;
        this.lastTranslation = new Date();
    }
}
