package com.kalew515.exchange;

import com.kalew515.common.extension.SPI;

/**
 * @author kale
 * @date 2022/11/21 上午10:42
 */
@SPI
public interface IdGenerator {

    public Long generatorId ();

}
