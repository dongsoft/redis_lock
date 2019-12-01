package com.caacetc.lock.utils;

import java.util.UUID;

public class ToolsUtil {

    /**
     * 获取UUID
     * @return
     */
    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().replace("-", "");
        return uuidStr;
    }
}
