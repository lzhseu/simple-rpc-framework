package top.lzhseu.test.registry.zk.utils;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import top.lzhseu.registry.zk.utils.CuratorUtil;

/**
 * @author lzh
 * @date 2020/12/5 12:41
 */
public class CuratorUtilTest {

    private CuratorFramework zkClient = CuratorUtil.getZkClient();

    @Test
    public void testCreatePersistentNode() {
        CuratorUtil.createPersistentNode(zkClient, "/node/testNode");
    }
}
