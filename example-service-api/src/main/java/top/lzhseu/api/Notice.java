package top.lzhseu.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzh
 * @date 2020/12/9 21:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    private String publisher;

    private String title;

    private String content;

}
