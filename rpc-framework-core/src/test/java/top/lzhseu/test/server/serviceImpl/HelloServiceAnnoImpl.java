package top.lzhseu.test.server.serviceImpl;

import top.lzhseu.annotation.RpcService;
import top.lzhseu.test.service.Hello;
import top.lzhseu.test.service.HelloService;

/**
 * @author lzh
 * @date 2020/12/8 20:13
 */
@RpcService(group = "test2", version = "v_1.0")
public class HelloServiceAnnoImpl implements HelloService {

    static {
        System.out.println("HelloServiceAnnoImpl 被创建");
    }

    @Override
    public String sayHello(Hello hello) {
        return hello.getPerson() + " says: this is test2 with annotation, and the message is " + hello.getMessage();
    }
}
