package com.android.materialsearchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.materialsearchview.databinding.ViewSearchBinding;

public class MaterialSearchView extends CardView {
    static final String LOG_TAG = "MaterialSearchView";
    private static final int ANIMATION_DURATION = 250;

    private boolean animateSearchView;
    private int searchMenuPosition;
    private String searchHint;
    private int searchTextColor;
    private Integer searchIconColor;
    private CharSequence mOldQueryText;
    private CharSequence mUserQuery;
    private boolean hasAdapter = false;
    private boolean hideSearch = false;

    private ViewSearchBinding b;
    private OnQueryTextListener listenerQuery;
    private OnVisibilityListener visibilityListener;

    public MaterialSearchView(@NonNull Context context) {
        super(context);
        init(context, null);
    }


    public MaterialSearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialSearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, 0, 0);

        final LayoutInflater inflater = LayoutInflater.from(context);
        b = DataBindingUtil.inflate(inflater, R.layout.view_search, this, true);
        animateSearchView = a.getBoolean(R.styleable.MaterialSearchView_search_animate, true);
        searchMenuPosition = a.getInteger(R.styleable.MaterialSearchView_search_menu_position, 0);
        searchHint = a.getString(R.styleable.MaterialSearchView_search_hint);
        searchTextColor = a.getColor(R.styleable.MaterialSearchView_search_text_color, getResources().getColor(android.R.color.black));
        searchIconColor = a.getColor(R.styleable.MaterialSearchView_search_icon_color, getResources().getColor(android.R.color.black));

        b.imgBack.setOnClickListener(mOnClickListener);
        b.imgClear.setOnClickListener(mOnClickListener);
        b.editText.addTextChangedListener(mTextWatcher);
        b.editText.setOnEditorActionListener(mOnEditorActionListener);

        final int imeOptions = a.getInt(R.styleable.MaterialSearchView_search_ime_options, -1);
        if (imeOptions != -1) {
            setImeOptions(imeOptions);
        }

        final int inputType = a.getInt(R.styleable.MaterialSearchView_search_input_type, -1);
        if (inputType != -1) {
            setInputType(inputType);
        }

        boolean focusable;
        focusable = a.getBoolean(R.styleable.MaterialSearchView_search_focusable, true);
        b.editText.setFocusable(focusable);

        a.recycle();

        b.editText.setHint(getSearchHint());
        b.editText.setTextColor(getTextColor());
        setDrawableTint(b.imgBack.getDrawable(), searchIconColor);
        setDrawableTint(b.imgClear.getDrawable(), searchIconColor);
        checkForAdapter();
    }


    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == b.imgClear) {
                setSearchText(null);
            } else if (v == b.imgBack) {
                hideSearch();
            }
        }
    };
    /**
     * Callback to watch the text field for empty/non-empty
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int before, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int after) {
            submitText(s);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private final TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            onSubmitQuery();
            return true;
        }
    };


    private void submitText(CharSequence s) {
        mUserQuery = b.editText.getText();
        updateClearButton();
        if (listenerQuery != null && !TextUtils.equals(s, mOldQueryText)) {
            listenerQuery.onQueryTextChange(String.valueOf(s));
        }
        mOldQueryText = s.toString();
    }

    void onSubmitQuery() {
        if (listenerQuery != null) {
            b.linearItemsHolder.setVisibility(GONE);
            listenerQuery.onQueryTextSubmit(b.editText.getText().toString());
        }
    }

    private void updateClearButton() {
        final boolean hasText = !TextUtils.isEmpty(b.editText.getText());
        b.imgClear.setVisibility(hasText ? VISIBLE : GONE);
    }


    public void setQuery(CharSequence query, boolean submit) {
        b.editText.setText(query);
        if (query != null) {
            mUserQuery = query;
        }
        // If the query is not empty and submit is requested, submit the query
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    /**
     * Sets the IME options on the query text field.
     */
    public void setImeOptions(int imeOptions) {
        b.editText.setImeOptions(imeOptions);
    }

    /**
     * Returns the IME options set on the query text field.
     */
    public int getImeOptions() {
        return b.editText.getImeOptions();
    }

    /**
     * Sets the input type on the query text field.
     */
    public void setInputType(int inputType) {
        b.editText.setInputType(inputType);
    }

    /**
     * Returns the input type set on the query text field.
     */
    public int getInputType() {
        return b.editText.getInputType();
    }


    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public void setSearchText(String queryText) {
        b.editText.setText(queryText);
    }

    public void setSearchRecyclerAdapter(RecyclerView.Adapter adapter) {
        b.recycler.setAdapter(adapter);
        checkForAdapter();
    }

    public void hideRecycler() {
        hideSearch = true;
        b.linearItemsHolder.setVisibility(GONE);
    }

    public void showRecycler() {
        hideSearch = false;
        b.linearItemsHolder.setVisibility(VISIBLE);
    }


    public void showSearch() {
        hideSearch = false;
        checkForAdapter();
        setVisibility(View.VISIBLE);
        if (animateSearchView)
            if (Build.VERSION.SDK_INT >= 21) {
                Animator animatorShow = ViewAnimationUtils.createCircularReveal(
                        this, // view
                        getCenterX(), // center x
                        (int) convertDpToPixel(23), // center y
                        0, // start radius
                        (float) Math.hypot(getWidth(), getHeight()) // end radius
                );
                animatorShow.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (visibilityListener != null) {
                            visibilityListener.onOpen();
                        }
                        if (hasAdapter) {
                            b.linearItemsHolder.setVisibility(View.VISIBLE);
                        }
                    }
                });
                animatorShow.start();
            } else {
                if (hasAdapter) {
                    b.linearItemsHolder.setVisibility(View.VISIBLE);
                }
            }
    }

    public void hideSearch() {
        checkForAdapter();
        if (hasAdapter) {
            b.linearItemsHolder.setVisibility(View.GONE);
        }
        if (animateSearchView) {
            if (Build.VERSION.SDK_INT >= 21) {
                Animator animatorHide = ViewAnimationUtils.createCircularReveal(
                        this, // View
                        getCenterX(), // center x
                        (int) convertDpToPixel(23), // center y
                        (float) Math.hypot(getWidth(), getHeight()), // start radius
                        0// end radius
                );
                animatorHide.setStartDelay(hasAdapter ? ANIMATION_DURATION : 0);
                animatorHide.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (visibilityListener != null) {
                            visibilityListener.onClose();
                        }
                        setVisibility(GONE);
                    }
                });
                animatorHide.start();
            } else {
                setVisibility(GONE);
            }
        }
    }

    public void setMenuPosition(int menuPosition) {
        this.searchMenuPosition = menuPosition;
        invalidate();
        requestFocus();
    }

    // search searchHint
    public String getSearchHint() {
        if (TextUtils.isEmpty(searchHint)) {
            return "Search";
        }
        return searchHint;
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
        invalidate();
        requestFocus();
    }

    // text color
    public void setTextColor(int textColor) {
        this.searchTextColor = textColor;
        invalidate();
        requestFocus();
    }

    public void setSearchIconColor(int searchIconColor) {
        this.searchIconColor = searchIconColor;
        invalidate();
        requestFocus();
    }

    public int getTextColor() {
        return searchTextColor;
    }

    /**
     * Get views
     */
    public EditText getEditText() {
        return b.editText;
    }

    public ImageView getImageBack() {
        return b.imgBack;
    }

    public ImageView getImageClear() {
        return b.imgClear;
    }

    public RecyclerView getRecyclerView() {
        return b.recycler;
    }

    /**
     * Interface
     */
    public void addQueryTextListener(OnQueryTextListener listenerQuery) {
        this.listenerQuery = listenerQuery;
    }

    public interface OnQueryTextListener {
        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);
    }

    public void setOnVisibilityListener(OnVisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }


    public interface OnVisibilityListener {
        boolean onOpen();

        boolean onClose();
    }

    /**
     * Helpers
     */
    public void setDrawableTint(Drawable resDrawable, int resColor) {
        resDrawable.setColorFilter(new PorterDuffColorFilter(resColor, PorterDuff.Mode.SRC_ATOP));
        resDrawable.mutate();
    }

    public float convertDpToPixel(float dp) {
        return dp * (getContext().getResources().getDisplayMetrics().densityDpi / 160f);
    }

    private void checkForAdapter() {
        hasAdapter = !hideSearch && b.recycler.getAdapter() != null && b.recycler.getAdapter().getItemCount() > 0;
    }

    /*
    TODO not correct but close
    Need to do correct measure
     */
    private int getCenterX() {
        int icons = (int) (getWidth() - convertDpToPixel(21 * (1 + searchMenuPosition)));
        int padding = (int) convertDpToPixel(searchMenuPosition * 21);
        return icons - padding;
    }
}
