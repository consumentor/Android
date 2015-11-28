package consumentor.shopgun.aidroid.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.model.CertificationMark;
import consumentor.shopgun.aidroid.util.Util;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.customview.CircleDiagram;

import java.util.List;

/**
 * Created by Simon on 20.09.13.
 */
public class ProductComparisonActivity extends BaseActivity {

    public static final String EXTRA_PRODUCTS = "org.consumentor.shopgun.extra.EXTRA_PRODUCTS";

    List<Product> mProducts;

    ImageLoader mImageLoader;

    Context mContext;
    private float mScale;
    private int mCertificationMarkPadding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mScale = getResources().getDisplayMetrics().density;
        mCertificationMarkPadding = (int) (8 * mScale + 0.5f);

        mImageLoader = ImageLoader.getInstance();
        if (getIntent().getExtras().containsKey(EXTRA_PRODUCTS)) {
            mProducts = (List<Product>) getIntent().getExtras().getSerializable(EXTRA_PRODUCTS);

            setContentView(R.layout.fragment_compare_products);
            LinearLayout view = (LinearLayout) findViewById(R.id.columnHolder);

            for (final Product product : mProducts) {
                View productColumn = getLayoutInflater().inflate(R.layout.product_main_info_item, null);
                View button = productColumn.findViewById(R.id.theButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProductActivity.class);
                        intent.putExtra(ProductActivity.EXTRA_PRODUCT, product);
                        startActivity(intent);
                    }
                });

                TextView productNameTV = (TextView) productColumn.findViewById(R.id.productName);
                TextView brandNameTV = (TextView) productColumn.findViewById(R.id.brandName);
                TextView originTV = (TextView) productColumn.findViewById(R.id.productOrigin);
                if (!Util.isNullOrEmpty(product.name)) {


                    productNameTV.setText(product.toString());

                    if (product.brand != null) {
                        if (!Util.isNullOrEmpty(product.quantity) && !Util.isNullOrEmpty(product.quantityUnit)) {
                            brandNameTV.setText(String.format("%s, %s %s", product.brand.name, product.quantity, product.quantityUnit));
                        } else {
                            brandNameTV.setText(product.brand.name);
                        }
                    }

                    if(!Util.isNullOrEmpty(product.origin)){
                        originTV.setText(product.origin);
                    }

                    final ImageView productImageView = (ImageView) productColumn.findViewById(R.id.productImage);
                    mImageLoader.loadImage(product.imageMedium, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            productImageView.setImageBitmap(loadedImage);
                        }
                    });

                    CircleDiagram circleDiagram = (CircleDiagram) productColumn.findViewById(R.id.circleDiagram);
                    circleDiagram.init(product.getAdvicesRecursively());

                    initCertificationMarks(productColumn, product);
                } else {
                    productNameTV.setText(getString(R.string.product_info_missing));
                    brandNameTV.setText(product.gtin);
                }

                view.addView(productColumn);
            }
        }
    }

    private void initCertificationMarks(View view, Product product) {
        View lacksCertificationInfo = view.findViewById(R.id.lacksCertificationInfo);
        final LinearLayout certificationMarkContainer = (LinearLayout) view.findViewById(R.id.certificationMarkContainer);
        // clear any existing certification marks
        certificationMarkContainer.removeAllViews();
        if (product.certificationMarks.size() > 0) {
            for (final CertificationMark certificationMark : product.certificationMarks) {
                final ImageView certificationMarkButton = new ImageView(this);

                certificationMarkButton.setBackgroundResource(R.drawable.selector_button);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                certificationMarkButton.setLayoutParams(layoutParams);
                certificationMarkButton.setPadding(0, mCertificationMarkPadding, mCertificationMarkPadding, mCertificationMarkPadding);
                certificationMarkButton.setAdjustViewBounds(true);
                certificationMarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, CertificationMarkActivity.class);
                        intent.putExtra(CertificationMarkActivity.EXTRA_CERTIFICATIONMARK_ID, certificationMark.id);
                        startActivity(intent);
                    }
                });
                mImageLoader.loadImage(certificationMark.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        certificationMarkButton.setImageBitmap(loadedImage);
                        certificationMarkContainer.addView(certificationMarkButton, 0);
                    }
                });

                lacksCertificationInfo.setVisibility(View.GONE);
            }
        } else {
            View certificationMarkScrollView = view.findViewById(R.id.certificationMarkScrollView);
            certificationMarkScrollView.setVisibility(View.GONE);
            lacksCertificationInfo.setVisibility(View.VISIBLE);
        }
    }
}
