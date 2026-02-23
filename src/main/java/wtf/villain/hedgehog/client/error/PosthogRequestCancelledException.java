package wtf.villain.hedgehog.client.error;

@SuppressWarnings("unused")
public class PosthogRequestCancelledException extends RuntimeException {

    public PosthogRequestCancelledException() {
        super("The request was cancelled");
        setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
