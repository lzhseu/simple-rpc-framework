package top.lzhseu.remoting.transport.netty.client;

import top.lzhseu.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个存放未处理请求的容器。
 * 单例模式
 *
 * @author lzh
 * @date 2020/12/6 19:49
 */
public final class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_REQUESTS_MAP = new ConcurrentHashMap<>();

    private UnprocessedRequests() {}

    public static UnprocessedRequests getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static UnprocessedRequests INSTANCE = new UnprocessedRequests();
    }

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_REQUESTS_MAP.put(requestId, future);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQUESTS_MAP.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
