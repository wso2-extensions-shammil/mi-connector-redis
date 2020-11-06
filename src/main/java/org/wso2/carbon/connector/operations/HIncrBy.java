/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.connector.operations;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.util.RedisConstants;

public class HIncrBy extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        RedisServer serverObj = null;
        try {
            serverObj = new RedisServer(messageContext);
            String key = messageContext.getProperty(RedisConstants.KEY).toString();
            String field = messageContext.getProperty(RedisConstants.FIELD).toString();
            int value = Integer.parseInt(messageContext.getProperty(RedisConstants.VALUE).toString());
            Long response;

            if (serverObj.isClusterEnabled()) {
                response = serverObj.getJedisCluster().hincrBy(key, field, value);
            } else {
                response = serverObj.getJedis().hincrBy(key, field, value);
            }
            if (response != null) {
                messageContext.setProperty(RedisConstants.RESULT, response);
            } else {
                handleException("Redis server throw null response", messageContext);
            }
        } catch (Exception e) {
            handleException("Error while connecting the server or calling the redis method", e, messageContext);
        } finally {
            if (serverObj != null) {
                serverObj.close();
            }
        }
    }
}
