package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = "ArticleDetailFragment";
    private static final String LOG_TAG = ArticleDetailFragment.class.getSimpleName();
    //@formatter:off
    @BindView(R.id.photo) public ImageView photoView;
    @BindView(R.id.article_title) public TextView titleView;
    @BindView(R.id.article_byline) public TextView bylineView;
    @BindView(R.id.article_body) public TextView bodyView;
    @Nullable @BindView(R.id.app_bar_layout) public AppBarLayout appBarLayout;
    @Nullable @BindView(R.id.toolbar) public Toolbar toolbar;
    @Nullable @BindView(R.id.detail_content) public RelativeLayout detailContent;
    @BindView(R.id.share_fab) public FloatingActionButton shareFab;
    @BindView(R.id.meta_bar) public LinearLayout metaBar;
    @BindColor(R.color.material_grey) public int materialGrey;
    public boolean isDark = false;
    private int bylineThreshold = 33;
    //@formatter:on
    private View rootView;
    private Cursor cursor;
    private long itemId;
    private int overLapTopMargin;
    private int extraSideMargin;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    //Lifecycle start
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            itemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, rootView);
        calculateMargins();
        bindViews();
        if (appBarLayout != null && toolbar != null) {
            CollapsingToolbarLayout.LayoutParams params =
                    new CollapsingToolbarLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, overLapTopMargin);
            toolbar.setLayoutParams(params);
            appBarLayout.setExpanded(false);
        }
        if (detailContent != null) {
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(extraSideMargin, overLapTopMargin, extraSideMargin, 0);
            detailContent.setLayoutParams(params);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }
    //Lifecycle end

    private void calculateMargins() {
        Pair<Integer, Integer> pair = Utils.getScreenWidthAndHeight(getContext());
        overLapTopMargin = (int) (pair.second * 0.4); // 40% of the height
        extraSideMargin = (int) (pair.first * 0.1);  // 10% of the width

        // The following uses dark magic
        bylineThreshold = (Utils.pxToDp(pair.first) -48)/9;
    }

    @OnClick(R.id.share_fab)
    public void onClick() {
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText("Some sample text")
                .getIntent(), getString(R.string.action_share)));
    }

    private void bindViews() {
        if (rootView == null) {
            return;
        }
        if (cursor != null) {
            rootView.setAlpha(0);
            rootView.setVisibility(View.VISIBLE);
            titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));
            bylineView.setText(formatByLine(Utils.getModifiedByline(cursor, getContext())));
            bodyView.setText(Html.fromHtml(cursor.getString(ArticleLoader.Query.BODY)));
            bodyView.setMovementMethod(LinkMovementMethod.getInstance());
            photoView.setMaxHeight((int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8));
            final String photoUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
            Picasso.with(getContext())
                    .load(photoUrl)
                    .placeholder(getContext().getResources().getDrawable(R.drawable.empty_detail))
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            final Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                            Palette.from(bitmap)
                                    .maximumColorCount(3)
                                    .clearFilters()
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                                isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                                            } else {
                                                isDark = lightness == ColorUtils.IS_DARK;
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onError() {
                            Log.e(LOG_TAG, "onError: Couldn't load photo - " + photoUrl);
                        }
                    });
            shareFab.setVisibility(View.VISIBLE);
            rootView.animate().alpha(1);

        } else {
            rootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A");
            bodyView.setText("N/A");
        }
    }

    private String formatByLine(String modifiedByline) {
        if (modifiedByline.length() > bylineThreshold) {
            return new StringBuilder(modifiedByline).insert(modifiedByline.indexOf("by"), "\n").toString();
        }
        return modifiedByline;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), itemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        this.cursor = cursor;
        if (this.cursor != null && !this.cursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            this.cursor.close();
            this.cursor = null;
        }
        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursor = null;
        bindViews();
    }
}
