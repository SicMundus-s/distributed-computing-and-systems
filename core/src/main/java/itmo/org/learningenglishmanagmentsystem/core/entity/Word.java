package itmo.org.learningenglishmanagmentsystem.core.entity;


import itmo.org.learningenglishmanagmentsystem.core.dto.PopularWordDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "words")
@NamedNativeQuery(
        name = "FindPopularWordsDTO",
        query = "select w.id, w.word, w.translate, count(d.word_id) as countRepetitionsOfWordsFromUsers " +
                "from dictionary d left join words w on d.word_id = w.id " +
                "group by w.id, w.word, w.translate " +
                "order by countRepetitionsOfWordsFromUsers desc " +
                "LIMIT 1000",
        resultSetMapping = "PopularWordsDTO")
@SqlResultSetMapping(
        name = "PopularWordsDTO",
        classes = {
                @ConstructorResult(
                        targetClass = PopularWordDto.class,
                        columns = {
                                @ColumnResult(name = "word", type = String.class),
                                @ColumnResult(name = "translate", type = String.class),
                        }
                )
        }
)
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String word;
    private String translate;

    @ManyToMany(mappedBy = "words",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> users;

    @OneToMany(mappedBy = "wordId")
    private Set<IntervalRepetition> intervalRepetitionWords;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "categories_words",
            joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id"))
    private Set<Categories> categories;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (!Objects.equals(id, word1.id)) return false;
        if (!Objects.equals(word, word1.word)) return false;
        return Objects.equals(translate, word1.translate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (word != null ? word.hashCode() : 0);
        result = 31 * result + (translate != null ? translate.hashCode() : 0);
        return result;
    }
}
