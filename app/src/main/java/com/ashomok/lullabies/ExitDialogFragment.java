package com.ashomok.lullabies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ashomok.lullabies.services.MediaPlayerServiceTools;

/**
 * Created by iuliia on 7/10/16.
 */
public class ExitDialogFragment extends DialogFragment {

    public static ExitDialogFragment newInstance(int title) {
        ExitDialogFragment frag = new ExitDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_exit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                //stop music
                                MediaPlayerServiceTools mp = new MediaPlayerServiceTools(getActivity());
                                mp.handleStopRequest();
                                mp.destroy();

                                getActivity().finishAffinity();

                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //nothing
                            }
                        }
                )
                .create();
    }
}
