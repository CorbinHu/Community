package cn.corbinhu.community;

import cn.corbinhu.community.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: CorbinHu
 * @date: 2021/1/21 12:53
 * @description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {

    @Autowired
    public SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive(){

        String text = "有个地方可以*嫖*娼*，也可以*赌*博*，真的太牛逼了，你不去你s妈d死d了，你个大d煞d笔！";
        String t = sensitiveFilter.filter(text);
        System.out.println(t);
    }
}
