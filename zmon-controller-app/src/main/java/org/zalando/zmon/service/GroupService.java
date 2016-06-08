package org.zalando.zmon.service;

import java.util.List;

import org.zalando.zmon.api.ZmonGroup;
import org.zalando.zmon.api.ZmonGroupMember;

/**
 * Created by jmussler on 11/11/14.
 */
public interface GroupService {
    List<ZmonGroup> getAllGroups();
    long addMember(String groupId, String memberId);
    long removeMember(String groupId, String memberId);
    long clearActive(String groupId);
    long addToActive(String groupId, String memberId);
    long removeFromActive(String groupId, String memberId);

    long addPhone(String memberId, String phone);
    long removePhone(String memberId, String phone);
    ZmonGroupMember getMember(String memberId);
    void setName(String memberId, String name);
}
