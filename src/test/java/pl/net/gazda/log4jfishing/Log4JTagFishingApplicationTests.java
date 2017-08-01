package pl.net.gazda.log4jfishing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static pl.net.gazda.log4jfishing.Log4JTagFishingApplicationTests.FishingRequestBuilder.fishingGetRequest;
import static pl.net.gazda.log4jfishing.Log4jTagFishingApplication.LoggingContext.FISHING_LEVEL_KEY;
import static pl.net.gazda.log4jfishing.Log4jTagFishingApplication.LoggingContext.FISHING_TAG_KEY;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class Log4JTagFishingApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void should_tagFishing_when_fishingLevelIsSet() throws Exception {
        mockMvc.perform(fishingGetRequest().build());
        mockMvc.perform(fishingGetRequest().withFishingTag("test-tag-without-level").build());
        mockMvc.perform(fishingGetRequest().withFishingLevel("debug").build());
        mockMvc.perform(fishingGetRequest().withFishingTag("test-tag").withFishingLevel("trace").build());
	}

    public static class FishingRequestBuilder {
        private final MockHttpServletRequestBuilder requestBuilder;

        private FishingRequestBuilder(MockHttpServletRequestBuilder request) {
            this.requestBuilder = request;
        }

        static FishingRequestBuilder fishingGetRequest() {
            return new FishingRequestBuilder(get("/fishing"));
        }

        FishingRequestBuilder withFishingTag(String tag) {
            requestBuilder.header(FISHING_TAG_KEY, tag);
            return this;
        }

        FishingRequestBuilder withFishingLevel(String level) {
            requestBuilder.header(FISHING_LEVEL_KEY, level);
            return this;
        }

        MockHttpServletRequestBuilder build() {
            return requestBuilder;
        }
    }
}
