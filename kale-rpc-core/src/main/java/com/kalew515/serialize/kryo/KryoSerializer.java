package com.kalew515.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kalew515.common.exception.SerializeException;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize (Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            // obj --> byte
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("serialize failed: " + e.getMessage());
        }
    }

    @Override
    public <T> T deserialize (byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return t;
        } catch (Exception e) {
            throw new SerializeException("deserialize failed: " + e.getMessage());
        }
    }
}
