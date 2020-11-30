package com.mutool.box.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020-09-21 15:31<br>
 */
@Component
public class PageConfig {

    //错误页面设置
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        return (container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/errorPage/401.page");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/errorPage/404.page");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/errorPage/500.page");

            container.addErrorPages(error401Page, error404Page, error500Page);
        });
    }
}
