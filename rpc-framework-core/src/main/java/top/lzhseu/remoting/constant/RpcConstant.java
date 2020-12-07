package top.lzhseu.remoting.constant;

/**
 * 定义一些常量
 *
 * @author lzh
 * @date 2020/12/5 18:13
 */
public class RpcConstant {

    /**
     * 魔数
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'S', (byte) 'R', (byte) 'P', (byte) 'C'};

    /**
     * 版本号
     */
    public static final byte VERSION = 1;

    /**
     * 头部长度
     */
    public static final byte RPC_MESSAGE_HEADER_LENGTH = 16;

    /**
     * 帧最长长度 8MB
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    /**
     * RPC REQUEST 请求类型
     */
    public static final byte RPC_REQUEST_TYPE = 1;

    /**
     * RPC RESPONSE 请求类型
     */
    public static final byte RPC_RESPONSE_TYPE = 2;

    /**
     * HEARTBEAT REQUEST 请求类型
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;

    /**
     * HEARTBEAT RESPONSE 请求类型
     */
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    /**
     * PING 消息，一般用于心跳信息的发送
     */
    public static final String PING = "ping";

    /**
     * PONG 消息，一般用于心跳信息的回应
     */
    public static final String PONG = "pong";
}
