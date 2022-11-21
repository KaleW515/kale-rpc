package com.kalew515.exchange;

import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenterImpl;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_ID_GENERATOR_CENTER_NAME;

/**
 * @author kale
 * @date 2022/11/21 上午10:41
 */
public class IdGeneratorCenterImpl implements IdGeneratorCenter {

    private final IdGenerator idGenerator;

    public IdGeneratorCenterImpl () {
        ConfigCenterImpl configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String idGeneratorName = configCenter.getConfig(RPC_ID_GENERATOR_CENTER_NAME);
        this.idGenerator = ExtensionLoader.getExtensionLoader(IdGenerator.class)
                                          .getExtension(idGeneratorName);
    }

    @Override
    public Long generateId () {
        return idGenerator.generatorId();
    }
}
