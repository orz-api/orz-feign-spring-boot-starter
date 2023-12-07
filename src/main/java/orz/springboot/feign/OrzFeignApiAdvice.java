package orz.springboot.feign;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import orz.springboot.base.OrzBaseUtils;
import orz.springboot.web.OrzWebApiHandler;
import orz.springboot.web.annotation.OrzWebApi;

import static orz.springboot.alarm.OrzAlarmUtils.alarm;
import static orz.springboot.base.OrzBaseUtils.hashMap;
import static orz.springboot.base.description.OrzDescriptionUtils.desc;

@RestControllerAdvice(annotations = {OrzWebApi.class})
public class OrzFeignApiAdvice {
    private static final Logger logger = LoggerFactory.getLogger("orz-feign-api");

    private final OrzWebApiHandler handler;

    public OrzFeignApiAdvice(OrzWebApiHandler handler) {
        this.handler = handler;
    }

    @ExceptionHandler({OrzFeignException.class})
    public Object handleException(Exception topException, HandlerMethod method, HttpServletRequest request) throws Exception {
        var exception = OrzBaseUtils.getException(OrzFeignException.class, topException).orElseThrow(() -> topException);
        if (exception.isAlarm()) {
            alarm("@ORZ_FEIGN_ERROR_ALARM", exception.getReason(), topException, hashMap(
                    "method", exception.getMethod(),
                    "url", exception.getUrl(),
                    "protocol", exception.getProtocol(),
                    "handler", method.toString()
            ));
        }
        if (exception.isLogging() && logger.isDebugEnabled()) {
            logger.debug(desc(null, "handler", handler), topException);
        }
        return handler.buildErrorResponse(exception.getProtocol(), exception.getReason(), exception.getTraces(), topException, request);
    }
}
