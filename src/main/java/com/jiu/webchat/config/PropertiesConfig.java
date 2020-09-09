package com.jiu.webchat.config;

import com.jiu.webchat.utils.FileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;

/**
 * Package com.jiu.webchat.config
 * ClassName PropertiesConfig.java
 * Description 属性配置文件
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 14:47
 **/
@Configuration
@PropertySource("classpath:dbconfig.properties")
public class PropertiesConfig {

    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.driver}")
    private String driverClass;
    @Value("${jdbc.username}")
    private String userName;
    @Value("${jdbc.password}")
    private String password;
    @Value("${district}")
    private String district;
    @Value("${spring.web.view.prefix}")
    private String webViewPrefix;
    @Value("${spring.web.view.suffix}")
    private String webViewSuffix;
    @Value("${spring.web.static.handler}")
    private String webStaticHandler;
    @Value("${spring.web.static.resource}")
    private String webStaticResource;
    @Value("${spring.web.static.cache.period}")
    private Integer webStaticCachedPeriod;
    @Value("${mybatis.locations}")
    private String mapperLocations;
    @Value("${mybatis.type.alias.package}")
    private String mybatisTypeAliasPackages;

    @Value("${ftp.ip}")
    private String ftpIP;
    @Value("${ftp.post}")
    private int ftpPost;
    @Value("${ftp.user}")
    private String ftpUser;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.url}")
    private String ftpUrl;
    @Value("${ftp.start}")
    private String ftpStart;
    @Value("${ftp.browse}")
    private String ftpBrowse;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public String getWebViewPrefix() {
        return webViewPrefix;
    }

    public String getWebViewSuffix() {
        return webViewSuffix;
    }

    public String getWebStaticHandler() {
        return webStaticHandler;
    }

    public String getWebStaticResource() {
        return webStaticResource;
    }

    public Integer getWebStaticCachedPeriod() {
        return webStaticCachedPeriod;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public String getMybatisTypeAliasPackages() {
        return mybatisTypeAliasPackages;
    }

    public String getUrl() {
        return url;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDistrict() {
        return district;
    }

    public String getFtpIP() {
        return ftpIP;
    }

    public int getFtpPost() {
        return ftpPost;
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public String getFtpUrl() {
        return ftpUrl;
    }

    public String getFtpStart() {
        return ftpStart;
    }

    public String getFtpBrowse() {
        return ftpBrowse;
    }

    @PostConstruct
    public void init() {
        FileUpload.setFileUpload(this);
    }

}
