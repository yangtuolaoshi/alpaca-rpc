package love.ytlsnb.rpc.server.tcp.decorator;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import static love.ytlsnb.rpc.protocol.constants.ProtocolConstant.HEADER_LENGTH;

/**
 * 请求处理器装饰
 * 实现抽象构建Handler
 * 聚合具体构建：通过函数参数
 */
public class TCPBufferHandlerDecorator implements Handler<Buffer> {
    private final RecordParser recordParser;

    /**
     * 构造函数的参数就是具体构件
     * @param handler 具体构件，对它做增强
     */
    public TCPBufferHandlerDecorator(Handler<Buffer> handler) {
        RecordParser recordParser = RecordParser.newFixed(HEADER_LENGTH);
        // 指定一个处理流程
        recordParser.setOutput(new Handler<Buffer>() {
            // 初始化
            private int size = -1;
            // 一次完整的读取
            private Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {// 先拿首部
                    // 拿到数据段长度
                    size = buffer.getInt(13);
                    // 下次读取数据段
                    recordParser.fixedSizeMode(size);
                    // 把数据重新写回去（因为之前全读完了）
                    resultBuffer.appendBuffer(buffer);
                } else {// 再拿数据段
                    // 把数据写回去
                    resultBuffer.appendBuffer(buffer);
                    // 具体构件执行
                    handler.handle(resultBuffer);
                    // 重置状态
                    size = -1;
                    recordParser.fixedSizeMode(HEADER_LENGTH);
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        this.recordParser = recordParser;
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
