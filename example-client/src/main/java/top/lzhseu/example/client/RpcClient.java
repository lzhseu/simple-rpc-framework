package top.lzhseu.example.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.lzhseu.annotation.RpcScan;

/**
 * @author lzh
 * @date 2020/12/9 21:55
 */
@RpcScan(basePackages = "top.lzhseu.example.client")
public class RpcClient {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcClient.class);
        NoticeController noticeController = (NoticeController) context.getBean("noticeController");
        noticeController.test();
    }
}
