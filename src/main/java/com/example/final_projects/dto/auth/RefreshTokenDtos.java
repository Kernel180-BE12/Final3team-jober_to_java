package com.example.final_projects.dto.auth;

public class RefreshTokenDtos {
    public static class RefreshRequest{
        private String refreshToken;
        public RefreshRequest(){}
        public RefreshRequest(String refreshToken){
            this.refreshToken = refreshToken;
        }
        public String getRefreshToken() {return refreshToken;}
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class RefreshResponse{
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpiresInMs;
        private long refreshTokenExpiresInMs;
        public RefreshResponse(){}
        public RefreshResponse(String at, String rt, long atExp, long rtExp){
            this.accessToken = at; this.refreshToken = rt;
            this.accessTokenExpiresInMs = atExp; this.refreshTokenExpiresInMs = rtExp;
        }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public long getAccessTokenExpiresInMs() { return accessTokenExpiresInMs; }
        public long getRefreshTokenExpiresInMs() { return refreshTokenExpiresInMs; }

    }
}
