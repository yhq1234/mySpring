package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@MapperScan("com.example.mapper") //扫描的mapper
@SpringBootApplication
@EnableScheduling   //要加上开启定时任务的注解
@EnableCaching  //开启缓存
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Configuration
	@EnableSwagger2
	//是否开启swagger，正式环境一般是需要关闭的（避免不必要的漏洞暴露！），可根据springboot的多环境配置进行设置
	//@ConditionalOnProperty(name = "swagger.enable",havingValue = "true")
	public static class Swagger2 {
		// swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
		@Bean
		public Docket createRestApi() {
			return new Docket(DocumentationType.SWAGGER_2)
					.apiInfo(apiInfo())
					.select()
					// 为当前包路径
					.apis(RequestHandlerSelectors.basePackage("com.example.controller")).paths(PathSelectors.any())
					.build();
		}
		// 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
		private ApiInfo apiInfo() {
			return new ApiInfoBuilder()
					// 页面标题
					.title("Spring Boot 测试使用 Swagger2 构建RESTful API")
					// 创建人信息
					.contact(new Contact("yaohuanqing",  "https://www.cnblogs.com/zs-notes/category/1258467.html",  "1729497919@qq.com"))
					// 版本号
					.version("1.0")
					// 描述
					.description("API 描述")
					.build();
		}
		@Bean
		public Queue helloQueue() {
			return new Queue("helloQueue");
		}
	}
}
