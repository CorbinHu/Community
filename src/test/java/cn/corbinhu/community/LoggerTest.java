package cn.corbinhu.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: CorbinHu
 * @date: 2021/1/19 10:09
 * @description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger(){
        logger.trace("trace.log");
        logger.debug("debug.log");
        logger.info("info.log");
        logger.warn("warn.log");
        logger.error("error.log");
    }

}
