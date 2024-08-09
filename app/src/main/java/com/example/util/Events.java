package com.example.util;

public class Events {
    public static class FullScreen {
        private boolean isFullScreen = false;

        public boolean isFullScreen() {
            return isFullScreen;
        }

        public void setFullScreen(boolean fullScreen) {
            isFullScreen = fullScreen;
        }
    }

    public static class WatchList {
        private String watchListId;

        public String getWatchListId() {
            return watchListId;
        }

        public void setWatchListId(String watchListId) {
            this.watchListId = watchListId;
        }
    }
}
