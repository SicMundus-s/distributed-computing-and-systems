package itmo.org.learningenglishmanagmentsystem.core.entity.key;


import lombok.Data;

import java.io.Serializable;

@Data
public class IntervalRepetitionKey implements Serializable {

    Long userId;
    Long wordId;
}
