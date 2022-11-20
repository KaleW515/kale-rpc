package com.kalew515.remoting.handler;

import com.kalew515.common.enums.CompressEnum;
import com.kalew515.common.enums.SerializerEnum;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.compress.Compress;
import com.kalew515.exchange.Message;
import com.kalew515.exchange.constants.RpcMessageConstants;
import com.kalew515.exchange.messages.HeartBeatRequest;
import com.kalew515.exchange.messages.HeartBeatResponse;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static com.kalew515.exchange.constants.RpcMessageConstants.MAGIC_NUMBER;
import static com.kalew515.exchange.constants.RpcMessageConstants.VERSION;

@ChannelHandler.Sharable
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode (ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        try {
            ByteBuf byteBuf = ctx.channel().alloc().buffer();
            logger.debug("encode message: [{}]", msg);
            // 4 byte magic number
            byteBuf.writeBytes(MAGIC_NUMBER);
            // 1 byte version
            byteBuf.writeByte(VERSION);
            // make 4 byte for the length
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);
            // 1 byte message type
            int messageType = msg.getMessageType();
            byteBuf.writeByte(messageType);
            // 1 byte serializer type
            byteBuf.writeByte(msg.getSerializer());
            // 1 byte compress type
            byteBuf.writeByte(msg.getCompress());
            // 8 byte request id
            byteBuf.writeLong(msg.getRequestId());

            byte[] body = null;
            int fullLength = RpcMessageConstants.HEAD_LENGTH;
            if (messageType != Message.HEARTBEAT_TYPE_REQUEST && messageType != Message.HEARTBEAT_TYPE_RESPONSE) {
                String name = SerializerEnum.getName(msg.getSerializer());
                logger.info("serializer name: [{}] ", name);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                                                       .getExtension(name);
                body = serializer.serialize(msg);
                String compressName = CompressEnum.getName(msg.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                                                   .getExtension(compressName);
                body = compress.compress(body);
                fullLength += body.length;
            }
            if (body != null) {
                byteBuf.writeBytes(body);
            }
            int writeIndex = byteBuf.writerIndex();
            byteBuf.writerIndex(
                    writeIndex - fullLength + MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
            out.add(byteBuf);
        } catch (Exception e) {
            logger.error("Encode request error", e);
        }
    }

    @Override
    protected void decode (ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
                           List<Object> list) throws Exception {
        logger.info("byteBuf length: [{}]", byteBuf.readableBytes());
        if (byteBuf.readableBytes() >= RpcMessageConstants.TOTAL_LENGTH) {
            try {
                list.add(decodeFrame(byteBuf));
            } catch (Exception e) {
                logger.error("decode frame error", e);
                throw e;
            }
        }
    }

    private Object decodeFrame (ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);

        int fullLength = in.readInt();

        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        long requestId = in.readLong();
        Message message = null;
        if (messageType == Message.HEARTBEAT_TYPE_REQUEST) {
            message = new HeartBeatRequest();
            message.setSerializer(codecType);
            message.setMessageType(Message.HEARTBEAT_TYPE_REQUEST);
            message.setRequestId(requestId);
            message.setCompress(compressType);
            return message;
        } else if (messageType == Message.HEARTBEAT_TYPE_RESPONSE) {
            message = new HeartBeatResponse();
            message.setSerializer(codecType);
            message.setMessageType(Message.HEARTBEAT_TYPE_RESPONSE);
            message.setRequestId(requestId);
            message.setCompress(compressType);
            return message;
        }
        int bodyLength = fullLength - RpcMessageConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // decompress
            String compressName = CompressEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                                               .getExtension(compressName);
            bs = compress.decompress(bs);
            // deserializer
            String serializerName = SerializerEnum.getName(codecType);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                                                   .getExtension(serializerName);
            if (messageType == Message.RPC_MESSAGE_TYPE_REQUEST) {
                RpcRequest deserialize = serializer.deserialize(bs, RpcRequest.class);
                deserialize.setSerializer(codecType);
                deserialize.setMessageType(Message.RPC_MESSAGE_TYPE_REQUEST);
                deserialize.setRequestId(requestId);
                deserialize.setCompress(compressType);
                return deserialize;
            } else {
                RpcResponse<?> deserialize = serializer.deserialize(bs, RpcResponse.class);
                deserialize.setSerializer(codecType);
                deserialize.setMessageType(Message.RPC_MESSAGE_TYPE_RESPONSE);
                deserialize.setRequestId(requestId);
                deserialize.setCompress(compressType);
                return deserialize;
            }
        }
        return null;
    }

    private void checkMagicNumber (ByteBuf in) {
        int len = MAGIC_NUMBER.length;
        byte[] magicNumber = new byte[len];
        in.readBytes(magicNumber);
        for (int i = 0; i < len; i++) {
            if (magicNumber[i] != MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException(
                        "unknown magic code: " + Arrays.toString(magicNumber));
            }
        }
    }

    private void checkVersion (ByteBuf in) {
        byte version = in.readByte();
        if (version != VERSION) {
            throw new IllegalArgumentException("version isn't compatible " + version);
        }
    }
}
