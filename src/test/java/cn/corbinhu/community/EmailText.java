package cn.corbinhu.community;

import cn.corbinhu.community.utils.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: CorbinHu
 * @date: 2021/1/19 14:48
 * @description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class EmailText {

    @Autowired
    private MailClient client;

    @Test
    public void setClient(){
        client.sentMail("597995293@qq.com", "text", "welcome");
    }

}
