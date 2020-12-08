package top.lzhseu.test.server.serviceImpl;

import top.lzhseu.test.service.Hello;
import top.lzhseu.test.service.HelloService;

/**
 * @author lzh
 * @date 2020/12/8 14:42
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(Hello hello) {
        return hello.getPerson() + " says: " + hello.getMessage();
    }
}
