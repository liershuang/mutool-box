package com.mutool.box.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 描述：spring系统设置<br>
 * 作者：les<br>
 * 日期：2020/11/28 14:14<br>
 */
@Component
public class SystemConfig {

    /** 服务域名 */
    @Value("${mutool.server.domain}")
    public String serverDoamin;

    /** 服务端口 */
    @Value("${server.port}")
    public String serverPort;

    public String getServerUrl(){
        return serverDoamin+":"+serverPort;
    }

}
