package com.thanhle.englishvocabulary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.dialog.LibraryListDialog.LibraryListDialogListener;
import com.thanhle.englishvocabulary.utils.SharePrefs;

import java.util.ArrayList;

public class LibraryListDialog extends AppDialog<LibraryListDialogListener> {
    public static final String TAG = LibraryListDialog.class.getSimpleName();
    private static final String EXTRA_LIST = "list";
    private ArrayList<LibraryTable> libraries = new ArrayList<LibraryTable>();
    private int mChoose;

    public interface LibraryListDialogListener {
        public void onLibraryCancel(AppDialog<?> f);

        public void onLibraryChoose(AppDialog<?> f, String library_code, String library_name);
    }

    public LibraryListDialog() {
        setCancelable(false);
    }

    public static LibraryListDialog newInstance(
            ArrayList<LibraryTable> libraries, LibraryListDialogListener listener) {
        LibraryListDialog dialog = new LibraryListDialog();
        dialog.setListener(listener);
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_LIST, libraries);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle arg0) {
        super.onSaveInstanceState(arg0);
        arg0.putParcelableArrayList(EXTRA_LIST, libraries);
    }

    @Override
    protected boolean isListenerOptional() {
        return true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            libraries = bundle.getParcelableArrayList(EXTRA_LIST);
        }
        if (savedInstanceState != null) {
            libraries = savedInstanceState.getParcelableArrayList(EXTRA_LIST);
        }
        // convert library to library name array only
        String[] names = new String[libraries.size()];
        int currentLibrary = 0;
        for (int i = 0; i < libraries.size(); i++) {
            names[i] = libraries.get(i).name;
            if (libraries.get(i).code.equals(SharePrefs.getInstance()
                    .getCurrentLibrary())) {
                currentLibrary = i;
            }
        }
        setCancelable(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity())
                .setTitle(R.string.word_library)
                .setSingleChoiceItems(names, currentLibrary,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int position) {
                                mChoose = position;
                            }
                        })
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (mChoose < libraries.size()
                                        && getListener() != null) {
                                    getListener().onLibraryChoose(
                                            LibraryListDialog.this,
                                            libraries.get(mChoose).code,libraries.get(mChoose).name );
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (getListener() != null)
                                    getListener().onLibraryCancel(
                                            LibraryListDialog.this);
                            }
                        });

        return builder.create();
    }
}
