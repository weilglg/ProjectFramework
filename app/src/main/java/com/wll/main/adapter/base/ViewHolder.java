package com.wll.main.adapter.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewHolder {

    /**
     * Views indexed with their IDs
     */
    private SparseArray<View> views;

    private View convertView;

    private Context context;

    private int itemLayoutId;

    private int position;

    private Object associatedObject;

    private ViewHolder(Context context, ViewGroup parent, int itemLayoutId,
                       int position) {
        this.context = context;
        this.position = position;
        this.itemLayoutId = itemLayoutId;
        this.views = new SparseArray<View>();
        convertView = LayoutInflater.from(context).inflate(itemLayoutId,
                parent, false);
        convertView.setTag(this);

    }

    /**
     * This method is the only entry point to get a ViewHolder.
     *
     * @param convertView  This convertView is getView() method is passed
     * @param context      The current context
     * @param itemLayoutId The itemLayoutId is line layout file ID
     * @param position     The position of the item within the adapter's data set of the
     *                     item whose view we want.
     * @return A ViewHolder instance
     */
    static ViewHolder getViewHolder(Context context, View convertView,
                                    ViewGroup parent, int itemLayoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, itemLayoutId, position);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder.itemLayoutId != itemLayoutId) {
            return new ViewHolder(context, parent, itemLayoutId, position);
        }
        holder.position = position;
        return holder;
    }

    /**
     * This method allows you to retrieve a view and perform custom operations
     * on it, not covered by the ViewHolder
     *
     * @param viewId The id of the view you want to retrieve
     * @return A view
     */
    public <T extends View> T getChildView(int viewId) {
        return retrieveView(viewId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T retrieveView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * Retrieve the convertView
     */
    public View getConvertView() {
        return convertView;
    }

    /**
     * Retrieve the current layout resource ID
     */
    public int getItemLayoutResId() {
        return itemLayoutId;
    }

    /**
     * Retrieve the overall position of the data in the list
     *
     * @throws IllegalArgumentException If the position hasn't been set at the construction of the
     *                                  this helper.
     */
    public int getPosition() {
        if (position == -1) {
            throw new IllegalStateException("Use ViewHolder constructor "
                    + "with position if you need to retrieve the position.");
        }
        return position;
    }

    /**
     * Will set the Spannable String Builder of a TextView
     *
     * @param viewId The view id
     * @param value  The Spannable String Builder to put in the TextView
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextView(int viewId, SpannableStringBuilder value) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setText(value);
        return this;
    }

    /**
     * Will set the text of a TextView
     *
     * @param viewId The view id
     * @param value  The text to put in the TextView
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextView(int viewId, String value) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setText(value);
        return this;
    }

    /**
     * Will set the text of a TextView
     *
     * @param viewId      The view id
     * @param stringResId The text resource id to put in the TextView
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextView(int viewId, int stringResId) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setText(context.getResources().getString(stringResId));
        return this;
    }

    /**
     * Will set the string builder of a TextView
     *
     * @param viewId The view id
     * @param sb     The string builder to put in the TextView
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextView(int viewId, StringBuilder sb) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setText(sb);
        return this;
    }

    /**
     * Will set the color of a TextView
     *
     * @param viewId       The view id
     * @param textColorRes The text color resource id
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextColorResource(int viewId, int textColorRes) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setTextColor(context.getResources().getColor(textColorRes));
        return this;
    }

    /**
     * Will set the ColorStateList of a TextView
     *
     * @param viewId         The view id
     * @param colorStateList The text color state list
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextColorResource(int viewId,
                                           ColorStateList colorStateList) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setTextColor(colorStateList);
        return this;
    }

    /**
     * Will set the text size of a TextView
     *
     * @param viewId   The view id
     * @param textSize The text size
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextSize(int viewId, float textSize) {
        TextView textView = retrieveView(viewId);
        if (textView != null)
            textView.setTextSize(textSize);
        return this;
    }

    /**
     * Apply the typeface to the given viewId, and enable subpixel rendering.
     */
    public ViewHolder setTypeface(int viewId, Typeface typeface) {
        TextView view = retrieveView(viewId);
        if (view != null) {
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    /**
     * Apply the typeface to all the given viewIds, and enable subpixel
     * rendering.
     */
    public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = retrieveView(viewId);
            if (view != null) {
                view.setTypeface(typeface);
                view.setPaintFlags(view.getPaintFlags()
                        | Paint.SUBPIXEL_TEXT_FLAG);
            }
        }
        return this;
    }

    /**
     * Will set background color of a view
     *
     * @param viewId The view id
     * @param color  A color, not a resource id
     * @return The ViewHolder for chaining
     */
    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setBackgroundColor(color);
        return this;
    }

    /**
     * Will set background of a view
     *
     * @param viewId The view id
     * @param resId  A resource id to use as a background
     * @return The ViewHolder for chaining
     */
    public ViewHolder setBackgroundResource(int viewId, int resId) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setBackgroundResource(resId);
        return this;
    }

    /**
     * Will set setFocusable of a view
     *
     * @param viewId The view Id
     * @param flag
     * @returnThe ViewHolder for chaining
     */
    public ViewHolder setFocusable(int viewId, boolean flag) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setFocusable(flag);
        ;
        return this;
    }

    /**
     * Will set clickAble of a view
     *
     * @param viewId The view Id
     * @param flag
     * @returnThe ViewHolder for chaining
     */
    public ViewHolder setClickable(int viewId, boolean flag) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setClickable(flag);
        ;
        return this;
    }

    /**
     * Will set the image of an ImageView from a resource id
     *
     * @param viewId   The view id
     * @param imageRes The image resource id
     * @return The ViewHolder for chaining
     */
    public ViewHolder setImageResource(int viewId, int imageRes) {
        ImageView imageView = retrieveView(viewId);
        if (imageView != null)
            imageView.setImageResource(imageRes);
        return this;
    }

    /**
     * Will set the image of an ImageView from a drawable
     *
     * @param viewId   The view id
     * @param drawable The image drawable
     * @return The ViewHolder for chaining
     */
    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView imageView = retrieveView(viewId);
        if (imageView != null)
            imageView.setImageDrawable(drawable);
        return this;
    }

    /**
     * Will set the image of an ImageView from a bitmap
     *
     * @param viewId The view id
     * @param bitmap The image bitmap
     * @return The ViewHolder for chaining
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = retrieveView(viewId);
        if (imageView != null)
            imageView.setImageBitmap(bitmap);
        return this;
    }

    /**
     * Will set the image url of an ImageView from a bitmap
     *
     * @param viewId The view id
     * @param imgUrl The image url
     * @return The ViewHolder for chaining
     */
    public ViewHolder setImageUrl(int viewId, String imgUrl) {
        ImageView imageView = retrieveView(viewId);
        if (imageView != null)
            ImageLoader.getInstance().displayImage(imgUrl, imageView, DisplayImageOptions.createSimple());
        return this;
    }

    /**
     * Will set the image url of an ImageView from a bitmap
     *
     * @param viewId  The view id
     * @param imgUrl  The image url
     * @param options Display picture configuration
     * @return The ViewHolder for chaining
     */
    public ViewHolder setImageUrl(int viewId, String imgUrl, DisplayImageOptions options) {
        ImageView imageView = retrieveView(viewId);
        if (imageView != null)
            ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
        return this;
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times.
     * Alpha between 0-1.
     */
    @SuppressLint("NewApi")
    public ViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            retrieveView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            retrieveView(viewId).startAnimation(alpha);
        }
        return this;
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * Add links into a TextView
     *
     * @param viewId The view id
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTextLinks(int viewId) {
        TextView tv = retrieveView(viewId);
        if (tv != null)
            Linkify.addLinks(tv, Linkify.ALL);
        return this;
    }

    /**
     * Add links into a TextView
     *
     * @param viewId The view id
     * @return The ViewHolder for chaining
     */
    public ViewHolder setViewAdapter(int viewId, BaseAdapter adapter) {
        AdapterView<BaseAdapter> view = retrieveView(viewId);
        if (view != null)
            view.setAdapter(adapter);
        return this;
    }

    /**
     * Set the progress of a ProgressBar
     *
     * @param viewId   The view id
     * @param progress The new progress, between 0 and max
     * @return The ViewHolder for chaining
     */
    public ViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = retrieveView(viewId);
        if (view != null)
            view.setProgress(progress);
        return this;
    }

    /**
     * Set the progress and max of a ProgressBar
     *
     * @param viewId   The view id
     * @param progress The new progress, between 0 and max
     * @param max      The max value of a ProgressBar
     * @return The ViewHolder for chaining
     */
    public ViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = retrieveView(viewId);
        if (view != null) {
            view.setMax(max);
            view.setProgress(progress);
        }
        return this;
    }

    /**
     * Sets the range of a ProgressBar to 0...max
     *
     * @param viewId The view id
     * @param max    The max value of a ProgressBar
     * @return The ViewHolder for chaining
     */
    public ViewHolder setProgressMax(int viewId, int max) {
        ProgressBar view = retrieveView(viewId);
        if (view != null)
            view.setMax(max);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar
     *
     * @param viewId The view id
     * @param rating The rating
     * @return The ViewHolder for chaining
     */
    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = retrieveView(viewId);
        if (view != null)
            view.setRating(rating);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar
     *
     * @param viewId The view id
     * @param max    The max value of a RatingBar
     * @return The ViewHolder for chaining
     */
    public ViewHolder setRating(int viewId, int rating, int max) {
        RatingBar view = retrieveView(viewId);
        if (view != null) {
            view.setMax(max);
            view.setRating(rating);
        }
        return this;
    }

    /**
     * Sets the tag of the view
     *
     * @param viewId The view id
     * @param tag    An Object to tag the view with
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTag(int viewId, Object tag) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setTag(tag);
        return this;
    }

    /**
     * Sets the tag of the view
     *
     * @param viewId The view id
     * @param key    The key identifying the tag
     * @param tag    An Object to tag the view with
     * @return The ViewHolder for chaining
     */
    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setTag(key, tag);
        return this;
    }

    /**
     * Sets the enabled of the view
     *
     * @param viewId  The view id
     * @param enabled enabled True if this view is enabled, false otherwise
     * @return The ViewHolder for chaining
     */
    public ViewHolder setEnabled(int viewId, boolean enabled) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setEnabled(enabled);
        return this;
    }

    /**
     * Sets the checked status of a checkable
     *
     * @param viewId  The view id
     * @param checked The checked status
     * @return The ViewHolder for chaining
     */
    public ViewHolder setChecked(int viewId, boolean checked) {
        CheckBox view = retrieveView(viewId);
        if (view != null)
            view.setChecked(checked);
        return this;
    }

    /**
     * Sets the on click listener of the view
     *
     * @param viewId          The view id
     * @param onClickListener The on click listener
     * @return The ViewHolder for chaining
     */
    public ViewHolder setOnClickListener(int viewId,
                                         OnClickListener onClickListener) {

        View view = retrieveView(viewId);
        if (view != null)
            view.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * Sets the on touch listener of the view
     *
     * @param viewId   The view id
     * @param listener The on touch listener
     * @return The ViewHolder for chaining
     */
    public ViewHolder setOnTounchListener(int viewId, OnTouchListener listener) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setOnTouchListener(listener);
        return this;
    }

    /**
     * Sets the on long click listener of the view
     *
     * @param viewId   The view id
     * @param listener The on long click listener
     * @return The ViewHolder for chaining
     */
    public ViewHolder setOnLongClickListener(int viewId,
                                             OnLongClickListener listener) {
        View view = retrieveView(viewId);
        if (view != null)
            view.setOnLongClickListener(listener);
        return this;
    }

    /**
     * Retrieves the last converted object on this view.
     */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    /**
     * Should be called during convert
     */
    public void setAssociatedObject(Object associatedObject) {
        this.associatedObject = associatedObject;
    }

}
