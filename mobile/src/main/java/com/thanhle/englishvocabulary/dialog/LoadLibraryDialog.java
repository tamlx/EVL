package com.thanhle.englishvocabulary.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.ListLibraryLoadInDialogAdapter;
import com.thanhle.englishvocabulary.resource.DownloadLibraryInstallResource;

import java.util.ArrayList;

/**
 * Created by LaiXuanTam on 11/12/2015.
 */
public class LoadLibraryDialog extends AppDialog<LoadLibraryDialog.LoadLibraryDialogListenner> {


    private ArrayList<DownloadLibraryInstallResource> listLibrary = new ArrayList<DownloadLibraryInstallResource>();

    public interface LoadLibraryDialogListenner {
        public void result();
    }

    public LoadLibraryDialog() {
        setCancelable(false);
    }

    public static LoadLibraryDialog newInstance(ArrayList<DownloadLibraryInstallResource> listLibrary) {
        LoadLibraryDialog dialog = new LoadLibraryDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", listLibrary);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", listLibrary);
    }

    @Override
    protected boolean isListenerOptional() {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        listLibrary = bundle.getParcelableArrayList("list");
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_dialog_load_library, null);
        ListView lvListLibrary = (ListView) view.findViewById(R.id.lv_list_library_load);
        ListLibraryLoadInDialogAdapter adapter = new ListLibraryLoadInDialogAdapter(getActivity(), listLibrary);
        lvListLibrary.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity())
                .setView(view)
                .setTitle(getString(R.string.library_download_title));

        return builder.create();
    }
}
