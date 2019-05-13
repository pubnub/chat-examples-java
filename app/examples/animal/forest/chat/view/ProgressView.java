package animal.forest.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import animal.forest.chat.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressView extends RelativeLayout {

    @BindView(R.id.root)
    LinearLayout mRoot;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ButterKnife.bind(inflate(getContext(), R.layout.view_progress, this));
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    public void setEnabled(boolean enable) {
        if (enable)
            show();
        else
            hide();
    }

}
