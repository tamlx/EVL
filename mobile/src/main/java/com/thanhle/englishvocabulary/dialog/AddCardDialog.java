package com.thanhle.englishvocabulary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.view.CardView;
import com.thanhle.englishvocabulary.view.FlipView;

public class AddCardDialog extends AppDialog<AddCardDialog.AddCardDialogListener> {
    public static final String TAG = AddCardDialog.class.getSimpleName();
    private static final String EXTRA_CARD = "card";
    private CardTable card;

    public interface AddCardDialogListener {
        public void onAddCardYes(AddCardDialog f, CardTable card);

        public void onAddCardCancel(AddCardDialog f);
    }

    public AddCardDialog() {
        setCancelable(false);
    }

    public static AddCardDialog newInstance(AddCardDialogListener listener) {
        AddCardDialog dialog = new AddCardDialog();
        dialog.setListener(listener);
        return dialog;
    }

    public void setCard(CardTable card) {
        this.card = card;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CARD, card);
    }

    @Override
    protected boolean isListenerOptional() {
        return false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(EXTRA_CARD);
        }
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_add_card_dialog, null);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setAnimationDuration(300);
        cardView.setDirection(FlipView.DIRECTION_HORIZONTAL);
        cardView.setPivot(FlipView.PIVOT_CENTER);
        cardView.fillCardData(card);
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity())
                .setTitle(R.string.add_card_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_card_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                if (getListener() != null)
                                    getListener().onAddCardYes(
                                            AddCardDialog.this, card);
                            }
                        })
                .setNegativeButton(R.string.add_card_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (getListener() != null)
                                    getListener().onAddCardCancel(AddCardDialog.this);
                            }
                        });

        return builder.create();
    }
}