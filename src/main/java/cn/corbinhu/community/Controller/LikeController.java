package cn.corbinhu.community.Controller;

import cn.corbinhu.community.entity.User;
import cn.corbinhu.community.service.LikeService;
import cn.corbinhu.community.utils.CommunityUtil;
import cn.corbinhu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: CorbinHu
 * @date: 2021/1/23 15:22
 * @description:
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(1,"请先登录！");
        }

        // 点赞，保存到redis中
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);

        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0, null, map);
    }
}
