package cn.corbinhu.community.event;

import cn.corbinhu.community.entity.Event;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: CorbinHu
 * @date: 2021/1/24 16:54
 * @description:
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件
    public void fireEvent(Event event){
        // 将事件发布到指定主体
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
