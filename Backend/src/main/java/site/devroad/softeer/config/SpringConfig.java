package site.devroad.softeer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class SpringConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    public SpringConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/api/user/signup"
                        , "/api/user/signin"
                        , "/api/purchase/exam/success"
                        , "/api/purchase/exam/fail");
    }
}
