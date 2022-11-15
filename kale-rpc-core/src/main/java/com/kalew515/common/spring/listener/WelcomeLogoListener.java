package com.kalew515.common.spring.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE + 20 + 1)
public class WelcomeLogoListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent (ApplicationEnvironmentPreparedEvent event) {
        final Logger logger = LoggerFactory.getLogger(getClass());
        String bannerText = buildBannerText();
        if (logger.isInfoEnabled()) {
            logger.info(bannerText);
        } else {
            System.out.print(bannerText);
        }
    }

    String buildBannerText () {

        StringBuilder bannerTextBuilder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        bannerTextBuilder
                .append(lineSeparator)
                .append(" _            _\n" +
                                "| | __  __ _ | |  ___   _ __  _ __    ___\n" +
                                "| |/ / / _` || | / _ \\ | '__|| '_ \\  / __|\n" +
                                "|   < | (_| || ||  __/ | |   | |_) || (__\n" +
                                "|_|\\_\\ \\__,_||_| \\___| |_|   | .__/  \\___|\n" +
                                "                             |_|\n")
                .append("  :: Kale Rpc ::");
        return bannerTextBuilder.toString();
    }

}
