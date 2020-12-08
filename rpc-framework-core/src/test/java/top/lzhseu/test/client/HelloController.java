package top.lzhseu.test.client;

import org.springframework.stereotype.Component;
import top.lzhseu.annotation.RpcReference;
import top.lzhseu.test.service.Hello;
import top.lzhseu.test.service.HelloService;

/**
 * @author lzh
 * @date 2020/12/8 20:59
 */
@Component
public class HelloController {

    @RpcReference(group = "test2", version = "v_1.0")
    private HelloService helloService;

    public void test() {
        String s = helloService.sayHello(new Hello("lzh", "why always me"));
        System.out.println(s);

        for (int i = 0; i < 5; i++) {
            System.out.println(helloService.sayHello(new Hello("lzh", "circle test-" + i)));
        }
    }
}
