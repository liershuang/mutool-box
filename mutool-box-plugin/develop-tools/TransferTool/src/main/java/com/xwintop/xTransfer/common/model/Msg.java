package com.xwintop.xTransfer.common.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.xwintop.xTransfer.messaging.IMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: Msg
 * @Description: 消息日志封装
 * @author: xufeng
 * @date: 2018/7/3 16:29
 */

public class Msg {
    private Map<String, Object> fields = new HashMap<String, Object>();

    public Msg() {
        super();
    }

    public Msg(String eventType, String msgId, String eventTime) {
        this.fields.put("eventType", eventType);
        this.fields.put("msgId", msgId);
        if (StrUtil.isBlank(eventTime)) {
            this.fields.put("eventTime", DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
        } else {
            this.fields.put("eventTime", eventTime);
        }
    }

    public Msg(String id, String sender, String receiver, int length, String type, String createTimestamp) {
        super();
        this.fields.put("id", id);
        this.fields.put("sender", sender);
        this.fields.put("receiver", receiver);
        this.fields.put("length", length);
        this.fields.put("type", type);
        this.fields.put("createTimestamp", createTimestamp);
    }

    public Msg(IMessage msg) {
        super();
        this.fields.put("id", msg.getId());
        if (null != msg.getRawData()) {
            this.fields.put("length", msg.getRawData().length);
        } else if (null != msg.getMessage()) {
            this.fields.put("length", ArrayUtil.length(msg.getMessage()));
        }
    }

    public Object put(String key, Object value) {
        return this.fields.put(key, value);
    }

    public Object get(String key) {
        return this.fields.get(key);
    }

    public Map toMap() {
        return this.fields;
    }

}
