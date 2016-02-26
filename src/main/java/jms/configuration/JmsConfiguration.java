package jms.configuration;

public class JmsConfiguration {

    private String name;
    private String accessKey;
    private String secretKey;
    private String queueName;
    private Integer numberOfMessagesToPrefetch;
    private Integer delayUnitInMiliseconds;// = 60000;//default 1 minute
    private Integer maxAttempts = 10;//default
    private String url;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getNumberOfMessagesToPrefetch() {
        return numberOfMessagesToPrefetch;
    }

    public void setNumberOfMessagesToPrefetch(Integer numberOfMessagesToPrefetch) {
        this.numberOfMessagesToPrefetch = numberOfMessagesToPrefetch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDelayUnitInMiliseconds() {
        return delayUnitInMiliseconds;
    }

    public void setDelayUnitInMiliseconds(Integer delayUnitInMiliseconds) {
        this.delayUnitInMiliseconds = delayUnitInMiliseconds;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "{" + this.getClass().getName() + ": [name:\"" + name + "\", accessKey:\"" + accessKey
                + "\", secretKey:\"" + secretKey + "\", queueName:\"" + queueName + "\", numberOfMessagesToPrefetch:\""
                + numberOfMessagesToPrefetch + "\", delayUnitInMiliseconds:\"" + delayUnitInMiliseconds
                + "\", maxAttempts:\"" + maxAttempts + "\", url:\"" + url + "\" ] }";
    }

}
