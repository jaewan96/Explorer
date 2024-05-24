package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.sessionhandling.ingame.event.*;
import com.explorer.realtime.sessionhandling.waitingroom.WaitingRoomSessionHandler;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Component
@RequiredArgsConstructor
public class InGameSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(WaitingRoomSessionHandler.class);

    private final StartGame startGame;
    private final RestartGame restartGame;
    private final IngameBroadcastPosition ingameBroadcastPosition;
    private final FindUserData findUserData;
    private final UserInfoData userInfoData;

    public Mono<Void> inGameHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");
        String channelId = json.optString("channelId");

        switch (eventName) {
            case "startGame" :
                log.info("start game");
                String teamCode = json.getString("teamCode");
                String channelName = json.getString("channelName");
                startGame.process(teamCode, channelName);
                break;

            case "restartGame":
                log.info("restart game");
                return restartGame.process(channelId, UserInfo.ofJson(json), connection);

            case "broadcastPosition":
                log.info("broadcastPosition");
                ingameBroadcastPosition.process(json).subscribe();
                return Mono.empty();

            case "findUserData":
                log.info("findUserData");
                return findUserData.process(json);

            case "userInfo":
                log.info("userInfo");
                return userInfoData.process(json);
        }

        return Mono.empty();
    }

}
