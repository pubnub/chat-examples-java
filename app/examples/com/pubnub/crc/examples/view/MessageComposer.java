package com.pubnub.crc.examples.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pubnub.crc.examples.R;

public class MessageComposer extends RelativeLayout {

    private EditText mInput;
    private ImageView mSend;
    private ImageView mAttachment;

    private Listener mListener;

    public MessageComposer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View root = inflate(getContext(), R.layout.view_message_composer, this);
        mInput = root.findViewById(R.id.composer_edittext);
        mSend = root.findViewById(R.id.composer_send);
        mAttachment = root.findViewById(R.id.composer_attachment);

        mSend.setOnClickListener(v -> {
            mListener.onSentClick(mInput.getText().toString().trim());
            mInput.setText("");
        });
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {

        void onSentClick(String message);
    }

}
