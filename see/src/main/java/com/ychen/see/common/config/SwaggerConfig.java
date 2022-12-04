package com.ychen.see.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@EnableSwagger2
@Configuration
@ConditionalOnProperty(name = "swagger.enable", havingValue = "true", matchIfMissing = false)
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        List<Parameter> pars = new ArrayList<Parameter>();

        ParameterBuilder langPar = new ParameterBuilder();
        langPar.name("hbp-user-agent").description("lang")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(langPar.build());

        ParameterBuilder ticketPar = new ParameterBuilder();
        ticketPar.name("HBP-ADMIN-TOKEN").description("user token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(ticketPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                // 当前包路径
//                .apis(RequestHandlerSelectors.basePackage("com.huobi.pool.mall.web"))
//                .paths(PathSelectors.any())
//                .build();
    }

    //构建api文档的详细信息函数
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("小币种web API文档")
                //版本号
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }
}
