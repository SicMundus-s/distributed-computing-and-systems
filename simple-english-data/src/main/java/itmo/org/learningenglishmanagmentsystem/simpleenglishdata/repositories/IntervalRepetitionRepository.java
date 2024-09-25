package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories;


import itmo.org.learningenglishmanagmentsystem.core.entity.IntervalRepetition;
import itmo.org.learningenglishmanagmentsystem.core.entity.key.IntervalRepetitionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IntervalRepetitionRepository extends JpaRepository<IntervalRepetition, IntervalRepetitionKey> {

    @Query(value = "select * from interval_repetition " +
                   "where user_id = ?1 " +
                   "and (last_translation + interval '1 day' < current_timestamp " +
                   "or count_correct_translation = 0) " +
                   "limit 1", nativeQuery = true)
    Optional<IntervalRepetition> findWordForListIntervalRepetition(Long userId);

    @Query(value = "select * from interval_repetition " +
                   "where user_id = ?1 " +
                   "and word_id = ?2", nativeQuery = true)
    Optional<IntervalRepetition> findIntervalRepetitionByUserIdAndWordId(Long userId, Long wordId);
}

