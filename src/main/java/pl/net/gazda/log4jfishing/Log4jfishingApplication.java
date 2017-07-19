package pl.net.gazda.log4jfishing;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

import static org.apache.logging.log4j.util.Strings.isBlank;

@SpringBootApplication
@RestController
public class Log4jfishingApplication {
    private static final Logger LOG = LoggerFactory.getLogger(Log4jfishingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(Log4jfishingApplication.class, args);
	}

    /**
     * For request tracking set the following request headers
     * fishing-tag : 'my-fishing-tag'
     * fishing-level: ['debug', 'trace']
     */
	@RequestMapping("/fishing")
	public ResponseEntity<Object> handle(@RequestHeader Map<String, String> headers) {
        LOG.info("/fishing request");
        LOG.debug("/fishing with DEBUG");
        LOG.trace("/fishing with TRACE, request headers: {}", headers);
        return ResponseEntity.ok().build();
	}

    @Configuration
    public class WebConfig {
        @Bean
        public Filter loggingContextFilter() {
            return new AbstractRequestLoggingFilter() {
                @Override
                protected void beforeRequest(HttpServletRequest httpServletRequest, String s) {
                    String fishingLevel = httpServletRequest.getHeader(LoggingContext.FISHING_LEVEL_KEY);
                    String fishingTag = httpServletRequest.getHeader(LoggingContext.FISHING_TAG_KEY);
                    LoggingContext.start(getTag(fishingTag), getLevel(fishingLevel));
                }

                private String getTag(String fishingTag) {
                    return isBlank(fishingTag) ? UUID.randomUUID().toString() : fishingTag;
                }

                private String getLevel(String fishingLevel) {
                    return isBlank(fishingLevel) ? fishingLevel : fishingLevel.trim().toLowerCase();
                }

                @Override
                protected void afterRequest(HttpServletRequest httpServletRequest, String s) {
                    LoggingContext.clear();
                }
            };
        }
    }

	public static class LoggingContext {
        private static final Logger LOG = LoggerFactory.getLogger(LoggingContext.class);

		public static final String FISHING_TAG_KEY = "fishing-tag";
        public static final String FISHING_LEVEL_KEY = "fishing-level";

		private static void start(String uuid, String level) {
			setLevel(level);
			setFishingTag(uuid);

            LOG.debug("Started LoggingContext with tag: [{}] level: [{}]", uuid, level);
		}

		private static void setFishingTag(String tag) {
			ThreadContext.put(FISHING_TAG_KEY, tag);
		}

		private static void setLevel(String level) {
			ThreadContext.put(FISHING_LEVEL_KEY, level);
		}

		public static void clear() {
            LOG.debug("Clearing LoggingContext");
            LOG.trace("Clearing LoggingContext with context: {}", ThreadContext.getContext());
			ThreadContext.clearAll();
		}
	}
}
