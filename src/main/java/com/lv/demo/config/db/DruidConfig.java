package com.lv.demo.config.db;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lv
 */
@Configuration
public class DruidConfig {

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatView() {
        ServletRegistrationBean<StatViewServlet> reg =
                new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        reg.addInitParameter("loginUsername", "admin");
        reg.addInitParameter("loginPassword", "admin");
        return reg;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> druidWebStat() {
        FilterRegistrationBean<WebStatFilter> reg = new FilterRegistrationBean<>(new WebStatFilter());
        reg.addUrlPatterns("/*");
        reg.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return reg;
    }

}
