package top.lzhseu.remoting.dto;

import lombok.*;

/**
 * RPC 协议消息
 * 自定义的 RPC 协议如下：
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B codec（序列化类型）    1B compress（压缩类型）    4B  requestId（请求的Id）
 * </pre>
 *
 * @author lzh
 * @date 2020/12/5 17:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RpcMessage {

    /**
     * 消息类型：request. response
     */
    private byte messageType;

    /**
     * 序列化类型：jdk, kryo, protoBuf ...
     */
    private byte codec;

    /**
     * 压缩类型
     */
    private byte compress;

    /**
     * 请求的id
     */
    private int requestId;

    /**
     * 消息负载，即实际的数据
     */
    private Object data;
}
