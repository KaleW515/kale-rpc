package com.kalew515.serialize.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kalew515.serialize.Serializer;

import java.nio.charset.StandardCharsets;

public class JsonSerializer implements Serializer {

    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @Override
    public byte[] serialize (Object obj) {
        String s = JSON.toJSONString(obj, SerializerFeature.WriteClassName);
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize (byte[] bytes, Class<T> clazz) {
        T t = JSON.parseObject(bytes, clazz);
        return t;
    }
}
