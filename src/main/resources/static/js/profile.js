$(function () {
    $(".follow-btn").click(follow);
});

function follow() {
    var btn = this;
    if ($(btn).hasClass("btn-info")) {
        $.post(
            CONTEXT_PATH + "/follow",
            {"entityType": 3, "entityId": $(btn).prev().val()},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    // 关注TA
                    location.reload();
                    // $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
                } else {
                    alert(data.msg);
                }
            }
        )
    } else {
        $.post(
            CONTEXT_PATH + "/unfollow",
            {"entityType": 3, "entityId": $(btn).prev().val()},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    // 取消关注
                    location.reload();
                    // $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}