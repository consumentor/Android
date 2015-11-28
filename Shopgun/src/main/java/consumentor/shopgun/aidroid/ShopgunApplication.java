package consumentor.shopgun.aidroid;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Simon on 28.08.13.
 */
public class ShopgunApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                //.showStubImage(R.drawable.ic_stub)
                //.showImageForEmptyUri(R.drawable.ic_empty)
                //.showImageOnFail(R.drawable.ic_error)
                //.resetViewBeforeLoading(false)  // default
                //.delayBeforeLoading(1000)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                        //.preProcessor(...)
                        //.postProcessor(...)
                        //.extraForDownloader(...)
                        //.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                        //.bitmapConfig(Bitmap.Config.ARGB_8888) // default
                        //.decodingOptions(...)
                        //.displayer(new SimpleBitmapDisplayer()) // default
                        //.handler(new Handler()) // default
                .build();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                //.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                //.discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
                //.taskExecutor(...)
                //.taskExecutorForCachedImages(...)
                //.threadPoolSize(3) // default
                //.threadPriority(Thread.NORM_PRIORITY - 1) // default
                //.tasksProcessingOrder(QueueProcessingType.FIFO) // default
                //.denyCacheImageMultipleSizesInMemory()
                //.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                //.memoryCacheSize(2 * 1024 * 1024)
                //.memoryCacheSizePercentage(13) // default
                //.discCache(new UnlimitedDiscCache(cacheDir)) // default
                .discCacheSize(50 * 1024 * 1024)
                        //.discCacheFileCount(100)
                        //.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                        //.imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                        //.imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(defaultOptions)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
}
