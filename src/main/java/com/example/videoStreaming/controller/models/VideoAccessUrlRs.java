package com.example.videoStreaming.controller.models;


import lombok.Data;

@Data
public class VideoAccessUrlRs {
    private String accessVideoToken;

    public VideoAccessUrlRs(String accessVideoToken) {
        this.accessVideoToken = accessVideoToken;
    }
}
