package orz.springboot.feign;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import orz.springboot.feign.exception.OrzFeignException;
import orz.springboot.web.OrzWebApiHandler;
import orz.springboot.web.OrzWebUtils;
import orz.springboot.web.annotation.OrzWebApi;

@Slf4j
@RestControllerAdvice(annotations = {OrzWebApi.class})
public class OrzFeignApiAdvice {
    private final OrzWebApiHandler handler;

    public OrzFeignApiAdvice(OrzWebApiHandler handler) {
        this.handler = handler;
    }

    @ExceptionHandler({OrzFeignException.class})
    public Object handleException(Exception topException, HandlerMethod method, HttpServletRequest request) throws Exception {
        var exception = OrzWebUtils.getException(OrzFeignException.class, topException).orElseThrow(() -> topException);
        handler.reportError(exception.isAlarm(), exception.isLogging(), exception.getReason(), topException, method, log, Level.DEBUG);
        return handler.buildErrorResponse(exception.getProtocol(), exception.getReason(), exception.getTraces(), topException, request);
    }
}
