package zombie;

public class MapInfoRequest {
    private String replyToTopic = "";

    public String getReplyToTopic() {
        return replyToTopic;
    }

    public MapInfoRequest replyToTopic(String replyToTopic) {
        this.replyToTopic = replyToTopic;
        return this;
    }
}
