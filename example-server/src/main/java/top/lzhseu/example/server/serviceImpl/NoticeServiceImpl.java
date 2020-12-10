package top.lzhseu.example.server.serviceImpl;

import top.lzhseu.annotation.RpcService;
import top.lzhseu.api.Notice;
import top.lzhseu.api.NoticeService;

/**
 * @author lzh
 * @date 2020/12/9 21:45
 */
@RpcService(group = "impl1", version = "v1")
public class NoticeServiceImpl implements NoticeService {
    @Override
    public Notice getNotice() {
        return Notice.builder()
                .publisher("lzh")
                .title("Oops")
                .content("Struggle for life! GO! GO! GO!").build();
    }
}
