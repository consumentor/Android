package consumentor.shopgun.aidroid.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Simon on 2013-11-05.
 */
public class ShopgunAuthenticationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new ShopgunAccountAuthenticator(this).getIBinder();
    }
}
