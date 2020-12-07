package top.lzhseu.remoting.transport;

import top.lzhseu.remoting.dto.RpcRequest;

/**
 * RPC 请求传输接口
 * @author lzh
 * @date 2020/12/4 15:27
 */
public interface RpcRequestTransport {

    /**
     * 发送 RPC 请求
     * @param rpcRequest RPC 请求实例
     * @return RPC 请求结果
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
