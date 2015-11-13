package com.thanhle.englishvocabulary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;

/**
 * Created by LaiXuanTam on 10/8/2015.
 */
public class SearchLibraryDialog extends AppDialog<SearchLibraryDialog.SearchLibraryDialogListener> {

    public static final String TAG = SearchLibraryDialog.class.getSimpleName();
    private static final String EXTRA_LIST = "list";

    public interface SearchLibraryDialogListener {
        public void onSearchLibraryCancel();

        public void onSearchLibraryStart(String key);
    }

    public SearchLibraryDialog() {
        setCancelable(false);
    }

    public static SearchLibraryDialog newInstance(SearchLibraryDialogListener listener, String title, String result) {
        SearchLibraryDialog dialog = new SearchLibraryDialog();
        dialog.setListener(listener);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("result", result);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        final String title = bundle.getString("title");
        final String result = bundle.getString("result");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_search_library_dialog, null);
        final EditText edt_key_search = (EditText) view.findViewById(R.id.edt_key_library_search);
        TextView tv_result = (TextView) view.findViewById(R.id.tv_result_search_library);
        if (!"".equalsIgnoreCase(result)) {
            tv_result.setVisibility(View.VISIBLE);
            tv_result.setText(result);
        } else {
            edt_key_search.setVisibility(View.VISIBLE);
        }

        setCancelable(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity())
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (!"".equalsIgnoreCase(edt_key_search.getText().toString())) {

                                    getListener().onSearchLibraryStart(edt_key_search.getText().toString());
                                    edt_key_search.setText("");
                                }

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (getListener() != null)
                                    getListener().onSearchLibraryCancel();
                            }
                        });

        return builder.create();
    }
}

