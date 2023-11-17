package orz.springboot.feign;

import feign.Capability;
import org.springframework.context.annotation.Bean;
import orz.springboot.web.OrzWebProps;

public class OrzFeignConfiguration {
    private final OrzWebProps props;

    public OrzFeignConfiguration(OrzWebProps props) {
        this.props = props;
    }

    @Bean
    public Capability capability() {
        return new OrzFeignCapability(props);
    }
}
