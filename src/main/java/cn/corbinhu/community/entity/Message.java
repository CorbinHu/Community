package cn.corbinhu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: CorbinHu
 * @date: 2021/1/22 13:12
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
