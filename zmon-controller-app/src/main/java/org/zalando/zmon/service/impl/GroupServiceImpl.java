package org.zalando.zmon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.zmon.api.ZmonGroup;
import org.zalando.zmon.api.ZmonGroupMember;
import org.zalando.zmon.event.ZMonEventType;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.GroupService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jmussler on 11/11/14.
 */
@Service
public class GroupServiceImpl implements GroupService {

    private final JedisPool redisPool;
    protected final DefaultZMonPermissionService authorityService;

    @Autowired
    private NoOpEventLog eventLog;

//    private static final EventLogger EVENT_LOG = EventLogger.getLogger(AlertServiceImpl.class);

    public void createGroupIfNotExists(Jedis jedis, String groupId) {
        jedis.hsetnx("zmon:groups", groupId, groupId);
    }

    @Override
    public long addMember(String groupId, String memberId) {
        try (Jedis jedis = redisPool.getResource()) {
            createGroupIfNotExists(jedis, groupId);
            return jedis.sadd(getMembersKey(groupId), memberId);
        }
    }

    @Override
    public long removeMember(String groupId, String memberId) {
        try (Jedis jedis = redisPool.getResource()) {
            return jedis.srem(getMembersKey(groupId), memberId);
        }
    }


    @Override
    public long clearActive(String groupId) {
        try (Jedis jedis = redisPool.getResource()) {
            eventLog.log(ZMonEventType.GROUP_MODIFIED, authorityService.getUserName(), "clear-active", groupId);
            jedis.del(getActiveKey(groupId));
            return 0;
        }
    }

    @Override
    public long addToActive(String groupId, String memberId) {
        try (Jedis jedis = redisPool.getResource()) {
            ZmonGroup g = getGroup(groupId);

            if (g.members.contains(memberId)) {
                eventLog.log(ZMonEventType.GROUP_MODIFIED, authorityService.getUserName(), "activate", groupId, memberId);
                return jedis.sadd(getActiveKey(groupId), memberId);
            }
            return -1;
        }
    }

    @Override
    public long removeFromActive(String groupId, String memberId) {
        try (Jedis jedis = redisPool.getResource()) {
            eventLog.log(ZMonEventType.GROUP_MODIFIED, authorityService.getUserName(), "remove-active", groupId, memberId);
            return jedis.srem(getActiveKey(groupId), memberId);
        }

    }

    @Override
    public long addPhone(String memberId, String phone) {
        try (Jedis jedis = redisPool.getResource()) {
            eventLog.log(ZMonEventType.GROUP_MODIFIED, authorityService.getUserName(), "add-phone", null, memberId, phone);
            return jedis.sadd(getMemberPhoneKey(memberId), phone);
        }
    }

    protected String getMemberPhoneKey(String memberId) {
        return "zmon:member:" + memberId + ":phone";
    }

    @Override
    public long removePhone(String memberId, String phone) {
        try (Jedis jedis = redisPool.getResource()) {
            eventLog.log(ZMonEventType.GROUP_MODIFIED, authorityService.getUserName(), "remove-phone", null, memberId, phone);
            return jedis.srem(getMemberPhoneKey(memberId), phone);
        }
    }

    @Override
    public void setName(String memberId, String name) {
        try (Jedis jedis = redisPool.getResource()) {
            jedis.set("zmon:member:" + memberId + ":name", name);
        }
    }

    @Override
    public ZmonGroupMember getMember(String memberId) {
        ZmonGroupMember m = new ZmonGroupMember();
        try (Jedis jedis = redisPool.getResource()) {
            Set<String> phones = jedis.smembers(getMemberPhoneKey(memberId));
            String name = jedis.get("zmon:member:" + memberId + ":name");
            if (name == null) {
                name = "";
            }
            m.name = name;
            m.phones.addAll(phones);
            m.id = memberId;
            m.email = memberId;
        }
        return m;
    }

    @Autowired
    public GroupServiceImpl(JedisPool pool, DefaultZMonPermissionService authorityService) {
        this.redisPool = pool;
        this.authorityService = authorityService;
    }

    protected String getMembersKey(String id) {
        return "zmon:group:" + id + ":members";
    }

    protected String getActiveKey(String id) {
        return "zmon:group:" + id + ":active";
    }

    public ZmonGroup getGroup(String id) {

        try (Jedis jedis = redisPool.getResource()) {
            Set<String> members = jedis.smembers(getMembersKey(id));
            Set<String> active = jedis.smembers(getActiveKey(id));

            ZmonGroup g = new ZmonGroup();
            g.id = id;
            g.name = id;

            g.members.addAll(members);
            g.active.addAll(active);

            return g;
        }

    }

    @Override
    public List<ZmonGroup> getAllGroups() {
        List<ZmonGroup> groups = new ArrayList<>();

        try (Jedis jedis = redisPool.getResource()) {
            Map<String, String> gs = jedis.hgetAll("zmon:groups");
            for (String id : gs.keySet()) {
                Set<String> members = jedis.smembers(getMembersKey(id));
                Set<String> active = jedis.smembers(getActiveKey(id));

                ZmonGroup g = new ZmonGroup();
                g.id = id;
                g.name = gs.get(id);

                g.members.addAll(members);
                g.active.addAll(active);

                groups.add(g);
            }
        }

        return groups;
    }
}
