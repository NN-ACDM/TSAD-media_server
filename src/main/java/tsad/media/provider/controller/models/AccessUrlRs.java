package tsad.media.provider.controller.models;


import lombok.Data;

@Data
public class AccessUrlRs {
    private String mediaAccessToken;

    public AccessUrlRs(String mediaAccessToken) {
        this.mediaAccessToken = mediaAccessToken;
    }
}
