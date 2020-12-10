package top.lzhseu.example.client;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import top.lzhseu.annotation.RpcReference;
import top.lzhseu.api.Notice;
import top.lzhseu.api.NoticeService;

/**
 * @author lzh
 * @date 2020/12/9 21:57
 */
@Controller
public class NoticeController {

    @RpcReference(group = "impl1", version = "v1")
    private NoticeService noticeService;

    public void test() {
        Notice notice = noticeService.getNotice();
        System.out.println("get the result: ");
        System.out.println(notice);
    }
}
