package animal.forest.chat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import animal.forest.chat.R;

public class EmptyView extends RelativeLayout {

    private TextView mTitle;
    private TextView mMessage;

    private int mTitleResId;
    private int mMessageResId;

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EmptyView, 0, 0);

        mTitleResId = array.getResourceId(R.styleable.EmptyView_evTitle, R.string.ops);
        mMessageResId = array.getResourceId(R.styleable.EmptyView_evMessage, 0);

        array.recycle();

        init();
    }

    private void init() {
        View root = inflate(getContext(), R.layout.view_empty, this);
        mTitle = root.findViewById(R.id.empty_title);
        mMessage = root.findViewById(R.id.empty_message);

        mTitle.setText(mTitleResId);
        if (mMessageResId != 0) {
            mMessage.setText(mMessageResId);
        }
    }

    public void setTitle(@StringRes int resId) {
        mTitleResId = resId;
        mTitle.setText(mTitleResId);
    }

    public void setMessage(@StringRes int resId) {
        mMessageResId = resId;
        mMessage.setText(mMessageResId);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setMessage(String message) {
        mMessage.setText(message);
    }
}
