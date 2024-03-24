package love.ytlsnb.common.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 实体类
 */
@Data
@ToString
public class User implements Serializable {
    private int id;
    private String name;
}
