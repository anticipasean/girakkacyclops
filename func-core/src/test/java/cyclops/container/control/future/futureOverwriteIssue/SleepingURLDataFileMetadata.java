package cyclops.container.control.future.futureOverwriteIssue;

import cyclops.container.control.Either;
import java.io.IOException;
import java.net.URL;
import lombok.Getter;


@Getter
public class SleepingURLDataFileMetadata extends DataFileMetadata {

    private final URL url;

    public SleepingURLDataFileMetadata(long customerId,
                                       String type,
                                       URL url) {
        super(customerId,
              type);
        this.url = url;
    }

    @Override
    public Either<IOException, String> loadContents() {
        // System.out.println("Current thread " + Thread.currentThread().getId());
        try {
            Thread.sleep(501l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Either.right("success");
    }

}
