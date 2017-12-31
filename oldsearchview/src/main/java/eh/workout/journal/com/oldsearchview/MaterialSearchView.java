package eh.workout.journal.com.oldsearchview;

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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import eh.workout.journal.com.oldsearchview.databinding.ViewSearchBinding;

public class MaterialSearchView extends CardView implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {
    private static final int ANIMATION_DURATION = 250;
    private int menuPosition;
    private String searchHint;
    private int textColor;
    private Integer iconColor;
    private boolean hasAdapter = false;
    private boolean hideSearch = false;

    private ViewSearchBinding binding;
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
        try {
            menuPosition = a.getInteger(R.styleable.MaterialSearchView_menu_position, 0);
            searchHint = a.getString(R.styleable.MaterialSearchView_search_hint);
            textColor = a.getColor(R.styleable.MaterialSearchView_text_color, getResources().getColor(android.R.color.black));
            iconColor = a.getColor(R.styleable.MaterialSearchView_icon_color, getResources().getColor(android.R.color.black));
        } finally {
            a.recycle();
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.view_search, this, true);
            binding.imgBack.setOnClickListener(this);
            binding.imgClear.setOnClickListener(this);
            binding.editText.addTextChangedListener(this);
            binding.editText.setOnEditorActionListener(this);
        }
        binding.editText.setHint(getSearchHint());
        binding.editText.setTextColor(getTextColor());
        setDrawableTint(binding.imgBack.getDrawable(), iconColor);
        setDrawableTint(binding.imgClear.getDrawable(), iconColor);
        checkForAdapter();
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public void setSearchText(String queryText) {
        binding.editText.setText(queryText);
    }

    public void setSearchRecyclerAdapter(RecyclerView.Adapter adapter) {
        binding.recycler.setAdapter(adapter);
        checkForAdapter();
    }

    public void hideRecycler() {
        hideSearch = true;
        binding.linearItemsHolder.setVisibility(GONE);
    }

    public void showRecycler() {
        hideSearch = false;
        binding.linearItemsHolder.setVisibility(VISIBLE);
    }


    public void showSearch() {
        hideSearch = false;
        checkForAdapter();
        setVisibility(View.VISIBLE);
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
                    if (hasAdapter) {
                        binding.linearItemsHolder.setVisibility(View.VISIBLE);
                    }
                }
            });
            animatorShow.start();
        } else {
            if (hasAdapter) {
                binding.linearItemsHolder.setVisibility(View.VISIBLE);
            }
        }
    }

    public void hideSearch() {
        checkForAdapter();
        if (hasAdapter) {
            binding.linearItemsHolder.setVisibility(View.GONE);
        }
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
                    setVisibility(GONE);
                }
            });
            animatorHide.start();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imgClear) {
            setSearchText(null);
        } else if (view == binding.imgBack) {
            hideSearch();
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        binding.imgClear.setVisibility(i2 == 0 ? GONE : VISIBLE);
        if (listenerQuery != null) {
            listenerQuery.onQueryTextChange(String.valueOf(charSequence));
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (listenerQuery != null) {
                binding.linearItemsHolder.setVisibility(GONE);
                listenerQuery.onQueryTextSubmit(textView.getText().toString().trim());
            }
            return false;
        }
        return false;
    }

    public void setMenuPosition(int menuPosition) {
        this.menuPosition = menuPosition;
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
        this.textColor = textColor;
        invalidate();
        requestFocus();
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
        invalidate();
        requestFocus();
    }

    public int getTextColor() {
        return textColor;
    }

    /**
     * Get views
     */
    public EditText getEditText() {
        return binding.editText;
    }

    public ImageView getImageBack() {
        return binding.imgBack;
    }

    public ImageView getImageClear() {
        return binding.imgClear;
    }

    public RecyclerView getRecyclerView() {
        return binding.recycler;
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
        hasAdapter = !hideSearch && binding.recycler.getAdapter() != null && binding.recycler.getAdapter().getItemCount() > 0;
    }

    /*
    TODO not correct but close
    Need to do correct measure
     */
    private int getCenterX() {
        int icons = (int) (getWidth() - convertDpToPixel(21 * (1 + menuPosition)));
        int padding = (int) convertDpToPixel(menuPosition * 21);
        return icons - padding;
    }

    /**
     * Not used
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
