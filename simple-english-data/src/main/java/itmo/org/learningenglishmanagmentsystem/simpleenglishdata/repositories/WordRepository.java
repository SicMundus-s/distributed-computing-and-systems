package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories;


import itmo.org.learningenglishmanagmentsystem.core.dto.PopularWordDto;
import itmo.org.learningenglishmanagmentsystem.core.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;


public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByWordStartingWith(String word);
    @Query(nativeQuery = true, name = "FindPopularWordsDTO")
    List<PopularWordDto> getPopularWords();

    @Query(nativeQuery = true,
            value = "select words.id, word, translate " +
            "from categories_words join words on words.id = categories_words.word_id " +
            "join categories on categories.id = categories_words.category_id " +
            "where categories.name = ?1")
    Set<Word> findAllByCategories(String name);
    @Query(nativeQuery = true,
             value = "select w.id, w.word, w.translate " +
            "from dictionary d " +
            "join words w on d.word_id=w.id " +
            "where d.user_id = :userId order by w.word \n-- #pageable\n",
            countQuery = "SELECT count(*) FROM dictionary d " +
                    "JOIN words w ON d.word_id = w.id " +
                    "WHERE d.user_id = :userId")
    Page<Word> findAllByUserIdFromDictionary(Long userId, Pageable pageable);
}
