package site.devroad.softeer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.utility.JwtUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private JwtUtility jwtUtility;

    public AuthInterceptor(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws CustomException {
        //jwt validateToken에서 문제가 생기면 CustomException을 던짐
        logger.info("request handler : " + handler.getClass());
        logger.info(request.getMethod() + " " + request.getRequestURI());
        logger.info("jwt is " + request.getHeader("jwt"));
        String jwt = request.getHeader("jwt");
        jwtUtility.validateToken(jwt);
        Long accountId = jwtUtility.getAccountId(jwt);
        String userName = jwtUtility.getUserName(jwt);
        request.setAttribute("accountId", accountId);
        request.setAttribute("userName", userName);
        return true;
    }
}
